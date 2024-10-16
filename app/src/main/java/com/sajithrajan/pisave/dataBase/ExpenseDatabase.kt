package com.sajithrajan.pisave.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Expense::class, Category::class,TransactionEntity::class,
        SplitExpenseEntity::class, ReceiptEntity::class],  // Include both Expense and Category entities
    version = 1
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDAO
    abstract fun categoryDao(): CategoryDAO  // Add DAO for Category entity
    abstract fun transactionDao(): TransactionDao
    abstract fun splitExpenseDao(): SplitExpenseDao
    abstract fun receiptDao(): ReceiptDao
}
