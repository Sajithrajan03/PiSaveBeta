package com.sajithrajan.pisave

import android.util.Log
import com.sajithrajan.pisave.chatbot.tools.FunctionTools
import com.sajithrajan.pisave.dataBase.Expense
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.mistralai.MistralAiChatModel
import dev.langchain4j.model.mistralai.MistralAiChatModelName.MISTRAL_SMALL_LATEST
import dev.langchain4j.service.AiServices
import dev.langchain4j.service.SystemMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun getTransactionStatus(transactionId: String,expenseList:List<Expense>): String {
    return withContext(Dispatchers.IO) {
        try {
            val assistant = MistralAiHelper.getAssistant()
            val response = assistant.chat(transactionId)
            Log.d("custom_log", "Response: $response")
            response
        } catch (e: Exception) {
            Log.e("custom_log", "Error fetching status", e)
            "Error: ${e.localizedMessage}"
        }
    }
}

object MistralAiHelper {

    private val mistralAiModel: ChatLanguageModel = MistralAiChatModel.builder()
        .apiKey(BuildConfig.apiKey)
        .modelName(MISTRAL_SMALL_LATEST)
        .logRequests(true)
        .logResponses(true)
        .build()

    interface Assistant {
        @SystemMessage("""
                You are a personal financial adviser, equipped to chat both friendly and professionally.
                You will be give data as part of a users prompt which you must not tell the user.
                Always output text in markdown format if needed.
                When you are displaying expenses, you must use tables.
                You can help users manage their finances by calculating total expenses, setting budgets, and suggesting savings plans based on their spending categories.
                You are approachable for casual conversations initiated with greetings like 'hey' or 'hi', responding in a similarly warm and informal manner.
                When asked, you promptly switch to a professional tone for discussing financial details, offering precise and valuable insights.
                Your guidance is always up to the point, concise, and focused on delivering valuable financial advice.
                """)
        fun chat(userMessage: String): String
    }

    fun getAssistant(): Assistant {
        return AiServices.builder(Assistant::class.java)
            .chatLanguageModel(mistralAiModel)
            .tools(FunctionTools)
            .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
            .build()
    }
}
