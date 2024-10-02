package com.sajithrajan.pisave.dataBase

sealed interface ExpenseEvent{
    object SaveExpense : ExpenseEvent
    data class SetExpenseTitle(val title: String): ExpenseEvent
    data class SetExpenseAmount(val amount: Double): ExpenseEvent
    data class SetExpenseCategory(val category: String): ExpenseEvent
    object ShowDialog: ExpenseEvent
    object HideDialog: ExpenseEvent
    data class SortExpense (val sortType: SortType): ExpenseEvent
    data class DeleteExpense (val expense: Expense): ExpenseEvent
}
 