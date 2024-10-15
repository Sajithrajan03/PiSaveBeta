package com.sajithrajan.pisave.dataBase


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

    @Query("SELECT category, SUM(amount) as totalAmount FROM expenses WHERE strftime('%m', date / 1000, 'unixepoch') = :month GROUP BY category")
    fun getCategoryWiseSpendingForMonth(month: String): LiveData<List<CategorySpendingTable>>

    @Query("SELECT category, SUM(amount) as totalAmount FROM expenses WHERE strftime('%Y', date / 1000, 'unixepoch') = :year GROUP BY category")
    fun getCategoryWiseSpendingForYear(year: String): LiveData<List<CategorySpendingTable>>

    @Query("SELECT category, SUM(amount) as totalAmount FROM expenses WHERE date(date / 1000, 'unixepoch') = :day GROUP BY category")
    fun getCategoryWiseSpendingForDay(day: String): LiveData<List<CategorySpendingTable>>

    @Query("SELECT SUM(amount) as total, date FROM expenses GROUP BY date ORDER BY date ASC")
    fun getAllDailyExpenses(): LiveData<List<DailyExpense>>
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
    val date: Long // Assuming the date is stored as a timestamp in the database
)

