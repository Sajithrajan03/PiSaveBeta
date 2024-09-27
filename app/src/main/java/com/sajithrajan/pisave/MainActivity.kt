package com.sajithrajan.pisave


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sajithrajan.pisave.ui.theme.PiSaveTheme
import java.time.LocalDate


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PiSaveTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val expenses = remember { mutableStateListOf(
        Expense(categoryIcon = R.drawable.ic_food, title = "Groceries", amount = 150.0, currency = "$", date = LocalDate.now().minusDays(2)),
        Expense(categoryIcon = R.drawable.ic_movie, title = "Movie Tickets", amount = 45.0, currency = "$", date = LocalDate.now().minusDays(1)),
        Expense(categoryIcon = R.drawable.ic_transport, title = "Gas", amount = 60.0, currency = "$", date = LocalDate.now()),
        Expense(categoryIcon = R.drawable.ic_shop, title = "Shopping", amount = 80.0, currency = "$", date = LocalDate.now().minusDays(3))
    ) }
    NavHost(navController = navController, startDestination = "expense_screen") {
        composable("expense_screen") {
            ExpenseScreen(navController = navController, expenses = expenses)
        }
        composable("chatbot_screen") {
            ChatBotScreen(expenses=expenses) // Pass any necessary arguments if needed
        }
    }
    // Button in ChatBotScreen to navigate to the chart screen
}



