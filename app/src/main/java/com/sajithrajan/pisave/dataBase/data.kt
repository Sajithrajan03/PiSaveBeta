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
