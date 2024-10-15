package com.sajithrajan.pisave.dataBase

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDAO: ExpenseDAO, private val categoryDAO: CategoryDAO) {

    suspend fun upsert(expense: Expense) = expenseDAO.upsertExpense(expense)

    fun getAllExpenses(): LiveData<List<Expense>> = expenseDAO.getAllExpenses()

    fun getExpensesOrderedByDate(): Flow<List<Expense>> = expenseDAO.getExpensesOrderedByDate()

    fun getExpensesOrderedByAmount(): Flow<List<Expense>> = expenseDAO.getExpensesOrderedByAmount()

    fun getExpensesOrderedByTitle(): Flow<List<Expense>> = expenseDAO.getExpensesOrderedByTitle()

    suspend fun deleteExpense(expense: Expense) = expenseDAO.deleteExpense(expense)

    suspend fun insertCategory(category: Category) = categoryDAO.insertCategory(category)

    fun getAllCategories(): LiveData<List<Category>> = categoryDAO.getAllCategories()

    suspend fun getCategoryById(categoryId: Int): Category? = categoryDAO.getCategoryById(categoryId)


    fun getCategoryWiseSpendingForDay(day: String): LiveData<List<CategorySpendingTable>> {
        return expenseDAO.getCategoryWiseSpendingForDay(day)
    }

    // Get category-wise spending for the current month
    fun getCategoryWiseSpendingForMonth(month: String): LiveData<List<CategorySpendingTable>> {
        return expenseDAO.getCategoryWiseSpendingForMonth(month)
    }

    // Get category-wise spending for the current year
    fun getCategoryWiseSpendingForYear(year: String): LiveData<List<CategorySpendingTable>> {
        return expenseDAO.getCategoryWiseSpendingForYear(year)
    }
    fun getAllDailyExpenses(): LiveData<List<DailyExpense>> {
        return expenseDAO.getAllDailyExpenses()
    }
}

