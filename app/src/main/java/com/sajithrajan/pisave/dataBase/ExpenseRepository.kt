package com.sajithrajan.pisave.dataBase

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDAO: ExpenseDAO, private val categoryDAO: CategoryDAO,private val transactionDao: TransactionDao) {

    suspend fun upsert(expense: Expense) = expenseDAO.upsertExpense(expense)

    fun getAllExpenses(): LiveData<List<Expense>> = expenseDAO.getAllExpenses()

    fun getExpensesOrderedByDate(): Flow<List<Expense>> = expenseDAO.getExpensesOrderedByDate()

    fun getExpensesOrderedByAmount(): Flow<List<Expense>> = expenseDAO.getExpensesOrderedByAmount()

    fun getExpensesOrderedByTitle(): Flow<List<Expense>> = expenseDAO.getExpensesOrderedByTitle()

    suspend fun deleteExpense(expense: Expense) = expenseDAO.deleteExpense(expense)

    fun getTopExpensesForCurrentDay(): LiveData<List<Expense>> {
        return expenseDAO.getTopExpensesForCurrentDay()
    }


    fun getTopExpensesForCurrentMonth(): LiveData<List<Expense>> {
        return expenseDAO.getTopExpensesForCurrentMonth()
    }


    fun getTopExpensesForCurrentYear(): LiveData<List<Expense>> {
        return expenseDAO.getTopExpensesForCurrentYear()
    }

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

    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun getAllTransactions(): List<TransactionEntity> {
        return transactionDao.getAllTransactions()
    }
    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteAllTransactions() {
        transactionDao.deleteAllTransactions()
    }
    suspend fun updateExpense(expense: Expense) {
        expenseDAO.updateExpense(expense)
    }
}

