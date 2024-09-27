package com.sajithrajan.pisave

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.components.Legends
import co.yml.charts.common.model.PlotType
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.vertexai.type.Schema
import com.google.firebase.vertexai.type.defineFunction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class BakingViewModel : ViewModel() {


    private fun getColorForCategory(category: String): Color {
        return when (category) {
            "Groceries" -> Color(0xFF5F0A87)
            "Movie Tickets" -> Color(0xFF20BF55)
            "Gas" -> Color(0xFFEC9F05)
            "Shopping" -> Color(0xFFF53844)
            else -> Color(0xFFCCCCCC) // Default color for categories not listed
        }
    }
    // UI state handling using a StateFlow
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    fun getDonutChartData(expenses: List<Expense>): List<PieChartData.Slice> {
        val slices = expenses.map { expense ->
            PieChartData.Slice(
                label = expense.title,
                value = expense.amount.toFloat(),
                color = getColorForCategory(expense.title) // Function to assign color based on category or title
            )
        }
        return slices
    }

    @Composable
    fun DonutChartComposable(pieChartData: PieChartData) {
        Legends(legendsConfig = DataUtils.getLegendsConfigFromPieChartData(pieChartData = pieChartData, 2 ))
        DonutPieChart(
            modifier = Modifier
                .size(400.dp ,400.dp ),
            pieChartData = pieChartData,
            pieChartConfig = PieChartConfig(
                labelVisible = true,
                strokeWidth = 120f,
                backgroundColor = Color.Transparent,
                labelColor = Color.Gray,
                activeSliceAlpha = 0.9f,
                isAnimationEnable = true,
                showSliceLabels = true,
                animationDuration = 1000

            )
        )
    }

    val donutChartTool = defineFunction(
        name = "DonutChartComposable",
        description = "Plot the donut chart"
    ) {

    }

    // Initialize the generative AI model (Gemini in this case)
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    // Function to send prompt with expense list as a string
    fun sendPromptWithExpenses(expenses: List<Expense>, prompt: String) {
        _uiState.value = UiState.Loading

        val expenseListString = expenses.joinToString(separator = "\n") { expense ->
            "Title: ${expense.title}, Amount: ${expense.currency}${expense.amount}, Date: ${expense.date}"
        }

        val fullPrompt = "You are a personal financial adviser who enables the user to talk to their money and respond with some emojies. Keep your responses short while providing upto the point, concise and valuable insights. Below is the prompt given by the user:\n$prompt\n\nHere is the expense list:\n$expenseListString"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val classificationResponse = generativeModel.generateContent(
                    content {
                        text("classify whether the given prompt is a prompt to visualize expense data or a prompt which does not require data visualisation and the output must strictly be either one of these options (expense-chart, text-generation) based on the classification ie expense-chart for visualization and text-generation for everything else: $prompt")
                    }
                )

                when (classificationResponse.text?.trim()?.lowercase()) {
                    "expense-chart" -> {
                        // Generate and send the donut chart data to UI
                        val pieData = PieChartData(getDonutChartData(expenses), plotType = PlotType.Donut)
                        _uiState.value = UiState.Chart(pieData)  // Assume there's a Chart state
                    }
                    "text-generation" -> {
                        // Process the prompt as a regular text response
                        val response = generativeModel.generateContent(
                            content {
                                text(fullPrompt)
                            }
                        )
                        response.text?.let { outputContent ->
                            _uiState.value = UiState.Success(outputContent)
                        } ?: run {
                            _uiState.value = UiState.Error("No response from AI model.")
                        }
                    }
                    else -> {
                        // Fallback or error handling if the response is not as expected
                        _uiState.value = UiState.Error("Unexpected response from AI model.")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "An error occurred")
            }
        }
    }


}
