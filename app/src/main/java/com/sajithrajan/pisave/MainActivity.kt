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
import com.sajithrajan.pisave.dataBase.ExpenseRepository
import com.sajithrajan.pisave.dataBase.ExpenseViewModel


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
            database.transactionDao()
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


        setContent {
            PiSaveTheme {
//                SideEffect {
//                    window.navigationBarColor = MaterialTheme.colorScheme.primary
//                }
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





