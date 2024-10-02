package com.sajithrajan.pisave.dataBase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val title: String,
//    val categoryIcon: ImageVector?,
    val category: String,
    val note: String?,
    val amount: Double,
    val currency: String = "₹",
    val date: Long
)
