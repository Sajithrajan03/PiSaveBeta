// ExpensesData.kt
package com.sajithrajan.pisave

import androidx.compose.runtime.mutableStateListOf
import com.sajithrajan.pisave.dataBase.Expense
import java.time.LocalDate


// Define your list of expenses here
val expenses = mutableStateListOf(
//    Expense(categoryIcon = Icons.Filled.Fastfood, category="Food",note = "Groceries", amount = 150.0, currency = "$", date = LocalDate.now().minusDays(2)),
//    Expense(categoryIcon = Icons.Filled.Movie, category="Entertainment",note = "Movie Tickets", amount = 45.0, currency = "$", date = LocalDate.now().minusDays(1)),
//    Expense(categoryIcon = Icons.Filled.DirectionsBusFilled,category="Transport", note = "Gas", amount = 60.0, currency = "$", date = LocalDate.now()),
//    Expense(categoryIcon = Icons.Filled.ShoppingBag, category="Shopping",note = "Shopping", amount = 80.0, currency = "$", date = LocalDate.now().minusDays(3))

    Expense(
        category = "Food",
        title = "Groceries",
        amount = 150.0,
        currency = "$",
        date = LocalDate.now().minusDays(2).toEpochDay(), // Convert LocalDate to Long (epoch days)
        note = null // or any relevant note
    ),
    Expense(
        category = "Entertainment",
        title = "Movie Tickets",
        amount = 45.0,
        currency = "$",
        date = LocalDate.now().minusDays(1).toEpochDay(),
        note = null
    ),
    Expense(
        category = "Transport",
        title = "Gas",
        amount = 60.0,
        currency = "$",
        date = LocalDate.now().toEpochDay(),
        note = null
    ),
    Expense(
        category = "Shopping",
        title = "Shopping",
        amount = 80.0,
        currency = "$",
        date = LocalDate.now().minusDays(3).toEpochDay(),
        note = null
    )
)
