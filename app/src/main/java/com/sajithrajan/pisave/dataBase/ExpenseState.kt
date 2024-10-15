package com.sajithrajan.pisave.dataBase

data class ExpenseState(
    val title: String = "",
    val categoryId: Int = -1,  // Default to -1 until a category is selected
    val note: String = "",  // Added note field
    val amount: Double = 0.0,
    val date: Long = 0L,
    val category:String = "",
    val expenselist: List<Expense> = emptyList(),
    val isAddingExpense: Boolean = false,
    val sortType: SortType = SortType.DATE
)
