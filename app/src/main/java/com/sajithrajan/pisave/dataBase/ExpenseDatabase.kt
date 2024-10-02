package com.sajithrajan.pisave.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
@Database(
    entities = [Expense::class],
    version = 1
)
abstract class ExpenseDatabase:RoomDatabase() {
    abstract val dao: ExpenseDAO
}