package com.sajithrajan.pisave.dataBase


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow


@Dao
interface ExpenseDAO{

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Upsert
    suspend fun upsertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getExpensesOrderedByDate(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses ORDER BY amount DESC")
    fun getExpensesOrderedByAmount(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses ORDER BY title DESC")
    fun getExpensesOrderedByTitle(): Flow<List<Expense>>

    @Query("SELECT category, SUM(amount) as totalAmount FROM expenses WHERE strftime('%m', date / 1000, 'unixepoch') = :month GROUP BY category ORDER BY totalAmount DESC")
    fun getCategoryWiseSpendingForMonth(month: String): LiveData<List<CategorySpendingTable>>

    @Query("SELECT category, SUM(amount) as totalAmount FROM expenses WHERE strftime('%Y', date / 1000, 'unixepoch') = :year GROUP BY category ORDER BY totalAmount DESC")
    fun getCategoryWiseSpendingForYear(year: String): LiveData<List<CategorySpendingTable>>

    @Query("SELECT category, SUM(amount) as totalAmount FROM expenses WHERE date(date / 1000, 'unixepoch') = :day GROUP BY category ORDER BY totalAmount DESC")
    fun getCategoryWiseSpendingForDay(day: String): LiveData<List<CategorySpendingTable>>

    @Query("SELECT * FROM expenses WHERE date(date / 1000, 'unixepoch') = date('now') ORDER BY amount DESC")
    fun getTopExpensesForCurrentDay(): LiveData<List<Expense>>

    // Top expenses for the current month
    @Query("SELECT * FROM expenses WHERE strftime('%Y-%m', date / 1000, 'unixepoch') = strftime('%Y-%m', 'now') ORDER BY amount DESC")
    fun getTopExpensesForCurrentMonth(): LiveData<List<Expense>>

    // Top expenses for the current year
    @Query("SELECT * FROM expenses WHERE strftime('%Y', date / 1000, 'unixepoch') = strftime('%Y', 'now') ORDER BY amount DESC")
    fun getTopExpensesForCurrentYear(): LiveData<List<Expense>>

    @Query("SELECT SUM(amount) as total, date FROM expenses GROUP BY date ORDER BY date ASC")
    fun getAllDailyExpenses(): LiveData<List<DailyExpense>>

    @Update
    suspend fun updateExpense(expense: Expense)
}




@Dao
interface CategoryDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): LiveData<List<Category>>

    @Query("SELECT * FROM categories WHERE categoryId = :categoryId")
    suspend fun getCategoryById(categoryId: Int): Category?
}

data class CategorySpendingTable(
    val category: String = "",
    val totalAmount: Double = 0.0,

)

data class CategorySpending(
    val category: String = "",
    val totalAmount: Double = 0.0,
    val percentage: Float = 0.0f,  // Added percentage
    val iconName: String = "",     // Added iconName
    val color: Int = 0
)
data class DailyExpense(
    val total: Double,
    val date: Long
)


@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    suspend fun getAllTransactions(): List<TransactionEntity>

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
}