package com.sajithrajan.pisave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.ui.piechart.models.PieChartData
import com.sajithrajan.pisave.BuildConfig.apiKey
import com.sajithrajan.pisave.dataBase.Expense
import dev.langchain4j.agent.tool.Tool
import dev.langchain4j.memory.ChatMemory
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.mistralai.MistralAiChatModel
import dev.langchain4j.model.mistralai.MistralAiChatModelName.MISTRAL_LARGE_LATEST
import dev.langchain4j.service.AiServices
import dev.langchain4j.service.SystemMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class ConversationState(
    val contents: List<ChatContent> = listOf(),
    val updateCount: Int = 0,  // This helps in ensuring state changes even if messages are the same
)

open class BakingViewModel() : ViewModel() {

    private val _conversationList = MutableStateFlow(ConversationState())
    val conversationList: StateFlow<ConversationState> = _conversationList
    private val chatMemory: ChatMemory = MessageWindowChatMemory.withMaxMessages(10)
    private var chartDisplayed = false


    fun addMessageToConversation(content: ChatContent) {
        if (content is ChatContent.ChartMessage && chartDisplayed) return

        val currentContents = _conversationList.value.contents
        if (content is ChatContent.ChartMessage) chartDisplayed = true
        _conversationList.value = ConversationState(currentContents + content)
    }

    fun visualizeChart(data: PieChartData) {
        if (!chartDisplayed) {
            addMessageToConversation(ChatContent.ChartMessage(data))
        }
    }
    // UI state handling using a StateFlow
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()



    class ToolManager(private val _uiState: MutableStateFlow<UiState>
    ) {
        @Tool("Visualize data using chart")
        fun visualize(
        ): Unit {

            _uiState.value = UiState.chart

        }
    }

    private val toolManager = ToolManager(_uiState)
    interface assisstant {
        @SystemMessage("""
                You are a personal financial adviser, equipped to chat both friendly and professionally.
                You will be given data as part of a users prompt which you MUST NOT TELL the user.
                Always output text in markdown format.
                When the user asks to DISPLAY/VIEW/VISUALIZE etc the expenses, you MUST use the visualize tool to display a donut chart.
                You can help users manage their finances by calculating total expenses, setting budgets, and suggesting savings plans based on their spending categories.
                You are approachable for casual conversations initiated with greetings like 'hey' or 'hi', responding in a similarly warm and informal manner.
                When asked, you promptly switch to a professional tone for discussing financial details, offering precise and valuable insights.
                """)

        fun chat(message: String):String
    }



    // Initialize the generative AI model (Gemini in this case)
    private val generativeModel = MistralAiChatModel.builder()
        .apiKey(apiKey)
        .modelName(MISTRAL_LARGE_LATEST)
        .logRequests(true)
        .logResponses(true)
        .build()
//    val database = db
//    val expenseDao =  database.dao// Assuming this is how you create an instance of ExpenseDAO
//    val expenseViewModel = ExpenseViewModel(expenseDao)

    private val agent: assisstant = AiServices.builder(assisstant::class.java)
        .chatLanguageModel(generativeModel)
        .tools(toolManager)
        .chatMemory(chatMemory)
        .build()

    // Function to send prompt with expense list as a string
    fun sendPromptWithExpenses(expenses: List<Expense>, prompt: String) {
        _uiState.value = UiState.Loading

        val expenseListString = expenses.joinToString(separator = "\n") { expense ->
            "Title: ${expense.title}, Amount: ${expense.currency}${expense.amount}, Date: ${expense.date}"
        }

        val fullPrompt = "Below is the prompt given by the user:\n$prompt\n\nHere is the expense list:\n$expenseListString\n\n"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = agent.chat(fullPrompt)
                if (_uiState.value !is UiState.chart) {
                    _uiState.value = UiState.Success(response)
                }


            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "An error occurred")
            }
        }

    }


}
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val classificationResponse = generativeModel.generate("Classify the given prompt to determine the appropriate response type. Return '1' for prompts that require data visualization (e.g., generating charts or graphs to display expense data), and return '2' for prompts that require only text generation (e.g., answering questions, providing advice). The classification must be strict, only returning '1' or '2'. The prompt is provided below:\n$prompt")
//
//                when (classificationResponse?.trim()?.lowercase()) {
//                    "1" -> {
//                        // Generate and send the donut chart data to UI
//                        val pieData = PieChartData(getDonutChartData(expenses), plotType = PlotType.Donut)
//
////                        Legends(legendsConfig = DataUtils.getLegendsConfigFromPieChartData(pieChartData =pieData, gridSize = 2))
//                    }
//                    "2" -> {
//                        // Process the prompt as a regular text response
//                        val response = generativeModel.generate(fullPrompt
//                        )
//
//                        response?.let { outputContent ->
//                            _uiState.value = UiState.Success(outputContent)
//                        } ?: run {
//                            _uiState.value = UiState.Error("No response from AI model.")
//                        }
//                    }
//
//                }
//            } catch (e: Exception) {
//                _uiState.value = UiState.Error(e.localizedMessage ?: "An error occurred")
//            }
//        }

//    // Method to add an expense
//    @Tool("Add a new expense record")
//    fun addExpense(
//        @P("Title or name  of the expense") title: String,
//        @P("Amount of the expense") amount: Double,
//        @P("Category of the expense") category: String,
//    ): String {
//        if (title.isBlank() || category.isBlank() || amount <= 0.0) {
//            return "failed to add expense`"
//        }
//        val newExpense = Expense(
//            title = title,
//            category = category,
//            note = null,
//            amount = amount,
//            date = LocalDate.now().toEpochDay()
//        )



