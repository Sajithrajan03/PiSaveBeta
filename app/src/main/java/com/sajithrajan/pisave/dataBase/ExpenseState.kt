package com.sajithrajan.pisave.dataBase

data class ExpenseState(
    val expenselist: List<Expense> = emptyList(),
    val title: String = "",
    val amount: Double = 0.0,
    val date: Long = 0L,
//    val categoryIcon: ImageVector? = null,
    val category: String = "",
    val note: String? = null,
    val isAddingExpense: Boolean = false,
    val sortType: SortType = SortType.DATE
)