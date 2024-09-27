package com.sajithrajan.pisave
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.yml.charts.common.components.Legends
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import dev.jeziellago.compose.markdowntext.MarkdownText


// Composable function for the chatbot screen
@Composable
fun ChatBotScreen(
    bakingViewModel: BakingViewModel = viewModel(), // ViewModel
    expenses: MutableList<Expense> // Passing the list of expenses
) {
    val promptText = remember { mutableStateOf(TextFieldValue("")) } // TextField state
    val conversationList = remember { mutableStateListOf<String>() } // Conversation history
    val uiState by bakingViewModel.uiState.collectAsState() // UI state from ViewModel

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Display conversation history
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            items(conversationList) { message ->
                if (message.startsWith("AI: ")) {
                    // Extract the markdown content from the message
                    val markdownContent = message
                    MarkdownText(markdown = markdownContent, modifier = Modifier.padding(vertical = 4.dp))
                } else {
                    Text(
                        text = message,
                        modifier = Modifier.padding(vertical = 4.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                HorizontalDivider(thickness = 1.dp, modifier = Modifier.fillMaxWidth(), color = Color.White)
            }
        }

        // Show loading indicator when waiting for AI response
        if (uiState is UiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        if (uiState is UiState.Chart) {
            DonutChartComposable(pieChartData = (uiState as UiState.Chart).pieData)
        }

        // User prompt input and send button
        Row(modifier = Modifier.fillMaxWidth()) {
            BasicTextField(
                value = promptText.value,
                onValueChange = { promptText.value = it },
                modifier = Modifier
                    .height(100.dp)
                    .weight(1f)
                    .padding(8.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small),
                textStyle = TextStyle(
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize ,color = MaterialTheme.colorScheme.onSurface),
            )

            Button(
                onClick = {
                    val userPrompt = promptText.value.text

                    if (userPrompt.isNotBlank()) {
                        // Add the user's prompt to the conversation
                        conversationList.add("You: $userPrompt")

                        // Combine the expenses into a formatted string without categoryIcon
                        //val expenseListString = expenses.joinToString(separator = "\n") { expense ->
                        //    "Title: ${expense.title}, Amount: ${expense.currency}${expense.amount}, Date: ${expense.date}"
                        //}

                        //val fullPrompt = "$userPrompt\n\nExpense List:\n$expenseListString"

                        // Call ViewModel function to handle the AI request
                        bakingViewModel.sendPromptWithExpenses(expenses, userPrompt)

                        // Clear the TextField input after sending the prompt
                        promptText.value = TextFieldValue("")
                    }
                },
                enabled = promptText.value.text.isNotEmpty(), // Button is enabled only when input is not empty
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 8.dp)
            ) {
                Text("Send")
            }
        }
    }

    // Add the AI response or error to the conversation list
    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> conversationList.add("AI: ${(uiState as UiState.Success).outputText}")
            is UiState.Error -> conversationList.add("Error: ${(uiState as UiState.Error).errorMessage}")
            else -> Unit
        }
    }
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
// Example Expense class (used as part of the ViewModel)



