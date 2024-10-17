package com.sajithrajan.pisave.dataBase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
)
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val category:String,
    val note: String?,
    val amount: Double,
    val currency: String = "₹",
    val date: Long
)

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,
    val categoryName: String,
    val iconName: String
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val note: String?,
    val amount: Double,
    val currency: String = "₹",
    val date: Long
)

@Entity(tableName = "split_expenses")
data class SplitExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val expenseId: Int,
    val participantName: String,
    val amount: Double
)

@Entity(tableName = "receipts")
data class ReceiptEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val expenseId: Int,
    val imageUri: String
)

@Entity(tableName = "budget")
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val month: String, // Use "YYYY-MM" format to identify the month
    val dailyBudget: Double,
    val monthlyBudget: Double
)