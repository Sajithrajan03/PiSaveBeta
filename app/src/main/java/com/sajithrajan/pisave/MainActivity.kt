package com.sajithrajan.pisave

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.sajithrajan.pisave.dataBase.AppDataBase
import com.sajithrajan.pisave.dataBase.Expense
import com.sajithrajan.pisave.dataBase.ExpenseRepository
import com.sajithrajan.pisave.dataBase.ExpenseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java,
            "app_database"
        ).build()

        val repository = ExpenseRepository(
            database.expenseDao(),
            database.categoryDao(),
            database.transactionDao(),
            database.splitExpenseDao(),
            database.receiptDao(),
            database.budgetDao()
        )

        val viewModel: ExpenseViewModel by viewModels {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
                        return ExpenseViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }

        prepopulateData(viewModel = viewModel)
        setContent {
            PiSaveTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}

private fun prepopulateData(viewModel: ExpenseViewModel) {
    CoroutineScope(Dispatchers.IO).launch {
        // Set the start date to September 17
        val startCalendar = Calendar.getInstance().apply {
            set(Calendar.MONTH, Calendar.SEPTEMBER)
            set(Calendar.DAY_OF_MONTH, 17)
        }

        // Set the end date to October 17
        val endCalendar = Calendar.getInstance().apply {
            set(Calendar.MONTH, Calendar.OCTOBER)
            set(Calendar.DAY_OF_MONTH, 17)
        }

        // List of categories for the expenses
        val categories = listOf("Food", "Transport", "Entertainment", "Shopping", "Health", "Utilities", "Others")

        // Generate expenses for each day from September 17 to October 17
        while (startCalendar.before(endCalendar) || startCalendar == endCalendar) {
            val dateInMillis = startCalendar.timeInMillis

            // Create an expense for each category
            categories.forEach { category ->
                val expense = Expense(
                    title = "$category Expense on ${startCalendar.get(Calendar.DAY_OF_MONTH)}",
                    amount = (50..500).random().toDouble(),
                    category = category,
                    date = dateInMillis,
                    note = "Sample note for $category on ${startCalendar.get(Calendar.DAY_OF_MONTH)}"
                )

                // Insert the expense using the ViewModel
                viewModel.addExpense(expense)
            }

            // Move to the next day
            startCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Refresh the spending data after inserting
        viewModel.fetchSpendingData()
    }
}