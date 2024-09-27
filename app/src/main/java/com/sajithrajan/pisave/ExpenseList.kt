package com.sajithrajan.pisave

import android.provider.CalendarContract
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import java.time.LocalDate
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController

data class Expense(
    val categoryIcon: Int, // Drawable resource id for the category icon
    val title: String,
    val amount: Double,
    val currency: String,
    val date: LocalDate
)

@Composable
fun ExpenseItem(expense: Expense) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xff0bdbb6)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = painterResource(id = expense.categoryIcon),
                    contentDescription = expense.title,
                    modifier = Modifier.size(40.dp),
                    colorFilter = ColorFilter.tint(Color(0x77000000))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = expense.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xccffffff)
                    )
                    Text(
                        text = "Date: ${expense.date}",
                        color = Color(0xccffffff),
                        fontSize = 14.sp,
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${expense.currency} ${expense.amount}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xeeffffff),
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun FilterItem(filter: String) {
    // Composable for each filter item with rounded corners
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp)) // Rounded corners
            .background(MaterialTheme.colorScheme.primary) // Background color
            .clickable { /* Handle click event if needed */ }
            .padding(horizontal = 16.dp, vertical = 8.dp) // Padding for text inside the filter
    ) {
        Text(
            text = filter,
            color = Color.White, // Text color
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}


@Composable
fun ExpenseList(
    expenses: MutableList<Expense>,
    filters: List<String>,
    onAddExpense: (Expense) -> Unit
) {
    // Control dialog visibility
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Expenses",
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            modifier = Modifier
                .padding(16.dp)
                .align(alignment = Alignment.CenterHorizontally)
        )

        // Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly // Evenly space the filters
        ) {
            filters.forEach { filter ->
                FilterItem(filter = filter)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List of expenses
        LazyColumn(

            contentPadding = PaddingValues(8.dp)
        ) {
            items(expenses) { expense ->
                ExpenseItem(expense = expense)
            }
        }

        // Floating Action Button (+) at the bottom
        Box(

            contentAlignment = Alignment.BottomEnd,

        ) {
            FloatingActionButton(
                onClick = {
                    // Toggle dialog visibility
                    showDialog = true
                },
                modifier = Modifier
                    .padding(16.dp)
                    .size(56.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense", tint = Color.White)
            }
        }

        // Show the Add Expense Dialog
        if (showDialog) {
            AddExpenseDialog(
                onAddExpense = { expense ->
                    onAddExpense(expense)
                    showDialog = false // Close dialog after adding expense
                },
                onDismiss = { showDialog = false } // Close dialog without adding
            )
        }
    }
}

// Dialog to Add a New Expense
@Composable
fun AddExpenseDialog(onAddExpense: (Expense) -> Unit, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Add New Expense", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = { onDismiss() }) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank() && amount.isNotBlank()) {
                                val newExpense = Expense(
                                    categoryIcon = R.drawable.ic_shop,
                                    title = title,
                                    amount = amount.toDouble(),
                                    currency = "$",
                                    date = LocalDate.now()
                                )
                                onAddExpense(newExpense)
                                onDismiss() // Close dialog after adding
                            }
                        }
                    ) {
                        Text("Add Expense")
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseScreen(navController: NavController ,expenses:MutableList<Expense> ) {

    val filters = remember {
        mutableStateListOf("Day", "Week", "Month", "Year")
    }


    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = {
            // Navigate to the chatbot screen
            navController.navigate("chatbot_screen")
        }) {
            Text("Go to Chatbot",
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(6.dp)

            )


        }
        ExpenseList(expenses = expenses, filters = filters, onAddExpense = { newExpense ->
            expenses.add(newExpense) // Add new expense to the list
        })


        // Add a button to go to ChatBotScreen and pass the expenses list

    }
}


