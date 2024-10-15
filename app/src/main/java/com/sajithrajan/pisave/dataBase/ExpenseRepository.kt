package com.sajithrajan.pisave.dataBase

class ExpenseRepository(private val dao: ExpenseDAO) {

    // Retrieve expenses ordered by date
    fun getExpensesOrderedByDate() = dao.getExpensesOrderedByDate()

    // Retrieve expenses ordered by amount
    fun getExpensesOrderedByAmount() = dao.getExpensesOrderedByAmount()

    // Retrieve expenses ordered by title
    fun getExpensesOrderedByTitle() = dao.getExpensesOrderedByTitle()

    // Upsert an expense into the database
    suspend fun upsertExpense(expense: Expense) {
        dao.upsertExpense(expense)
    }

    // Delete an expense from the database
    suspend fun deleteExpense(expense: Expense) {
        dao.deleteExpense(expense)
    }
}