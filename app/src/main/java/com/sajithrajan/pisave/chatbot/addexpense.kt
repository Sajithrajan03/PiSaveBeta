package com.sajithrajan.pisave.chatbot

import com.sajithrajan.pisave.dataBase.Expense
import com.sajithrajan.pisave.dataBase.ExpenseEvent

fun addtrans(expense: Expense,onEvent: (ExpenseEvent) -> Unit) {

    onEvent(ExpenseEvent.SetExpenseTitle(expense.title))
    onEvent(ExpenseEvent.SetExpenseAmount(expense.amount))
    onEvent(ExpenseEvent.SetExpenseCategory(expense.category))
    onEvent(ExpenseEvent.SaveExpense)

}