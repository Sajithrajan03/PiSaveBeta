package com.sajithrajan.pisave.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(
    entities = [Expense::class],
    version = 1
)
abstract class ExpenseDatabase:RoomDatabase() {
    abstract val dao: ExpenseDAO
    companion object {
        @Volatile
        private var INSTANCE: ExpenseDatabase? = null

        // Create a method to get the database instance
        fun getDatabase(context: Context): ExpenseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseDatabase::class.java,
                    "expense_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}