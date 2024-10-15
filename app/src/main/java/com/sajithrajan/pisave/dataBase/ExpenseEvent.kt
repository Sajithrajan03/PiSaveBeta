package com.sajithrajan.pisave.dataBase

sealed class ExpenseEvent {
    data class SetExpenseTitle(val title: String) : ExpenseEvent()
    data class SetExpenseAmount(val amount: Double) : ExpenseEvent()
    data class SetExpenseCategory(val category: String) : ExpenseEvent()
    data class SetExpenseNote(val note: String) : ExpenseEvent()
    data class SetExpenseDate(val date:Long) :ExpenseEvent()
    object SaveExpense : ExpenseEvent()
    object HideDialog : ExpenseEvent()
    object ShowDialog : ExpenseEvent()
    data class SortExpense(val sortType: SortType) : ExpenseEvent()
    data class DeleteExpense(val expense: Expense) : ExpenseEvent()
}
