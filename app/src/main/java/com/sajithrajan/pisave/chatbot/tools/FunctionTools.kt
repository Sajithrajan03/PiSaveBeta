package com.sajithrajan.pisave.chatbot.tools

import com.sajithrajan.pisave.dataBase.ExpenseEvent
import com.sajithrajan.pisave.dataBase.ExpenseViewModel
import dev.langchain4j.agent.tool.Tool

object FunctionTools {

    private lateinit var expenseViewModel: ExpenseViewModel

    // Initialize ViewModel (called once at setup)
    fun initialize(viewModel: ExpenseViewModel) {
        expenseViewModel = viewModel
    }

    @Tool("Add a new expense with title, category, amount")
    fun addExpenseFromChatbot(title: String, category: String, amount: Double): String {
        // Dispatch events to the ViewModel for each part of the expense
        expenseViewModel.onEvent(ExpenseEvent.SetExpenseTitle(title))
        expenseViewModel.onEvent(ExpenseEvent.SetExpenseCategory(category))
        expenseViewModel.onEvent(ExpenseEvent.SetExpenseAmount(amount))

        // Optional note handling, if present
//        if (note != null) {
//            expenseViewModel.onEvent(ExpenseEvent.SetExpenseTitle(note)) // Assuming note handling through title event, adjust as needed
//        }

        // Simulate saving the expense
        expenseViewModel.onEvent(ExpenseEvent.SaveExpense)

        // Return success message
        return "Expense added: $title in category $category with amount $amount"
    }

}