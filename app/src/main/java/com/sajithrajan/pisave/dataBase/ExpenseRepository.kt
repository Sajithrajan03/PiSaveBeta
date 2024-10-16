package com.sajithrajan.pisave.dataBase

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ExpenseRepository(private val expenseDAO: ExpenseDAO, private val categoryDAO: CategoryDAO,private val transactionDao: TransactionDao,private val splitExpenseDao: SplitExpenseDao,private val receiptDao: ReceiptDao) {

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
    suspend fun insertSplitExpense(splitExpense: SplitExpenseEntity) {
        splitExpenseDao.insertSplitExpense(splitExpense)
    }

    // Function to get all split expenses for a specific expense
    suspend fun getSplitExpensesForExpense(expenseId: Int): List<SplitExpenseEntity> {
        return splitExpenseDao.getSplitExpensesForExpense(expenseId)
    }

    // Function to delete all split expenses for a specific expense
    suspend fun deleteSplitExpensesForExpense(expenseId: Int) {
        splitExpenseDao.deleteSplitExpensesForExpense(expenseId)
    }

    // Function to save multiple split expenses for an expense
    suspend fun saveSplitExpenses(expenseId: Int, splitData: Map<String, Double>) {
        // Delete existing split expenses for the given expense
        splitExpenseDao.deleteSplitExpensesForExpense(expenseId)

        // Insert each split expense entry
        splitData.forEach { (participant, amount) ->
            val splitExpense = SplitExpenseEntity(
                expenseId = expenseId,
                participantName = participant,
                amount = amount
            )
            splitExpenseDao.insertSplitExpense(splitExpense)
        }
    }
    suspend fun insertReceipt(receipt: ReceiptEntity) {
        withContext(Dispatchers.IO) {
            receiptDao.insertReceipt(receipt)
        }
    }

    // Function to get receipts for a specific expense ID
    suspend fun getReceiptsForExpense(expenseId: Int): List<ReceiptEntity> {
        return withContext(Dispatchers.IO) {
            receiptDao.getReceiptsForExpense(expenseId)
        }
    }
}

