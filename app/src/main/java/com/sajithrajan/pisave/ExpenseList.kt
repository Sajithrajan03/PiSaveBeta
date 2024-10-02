//package com.sajithrajan.pisave
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material3.Button
//import androidx.compose.material3.FloatingActionButton
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.window.Dialog
//import com.sajithrajan.pisave.ExpenseScreen.ExpenseItem
//
//
//@Composable
//fun FilterItem(filter: String) {
//    // Composable for each filter item with rounded corners
//    Box(
//        modifier = Modifier
//            .clip(RoundedCornerShape(16.dp)) // Rounded corners
//            .background(MaterialTheme.colorScheme.primary) // Background color
//            .clickable { /* Handle click event if needed */ }
//            .padding(horizontal = 16.dp, vertical = 8.dp) // Padding for text inside the filter
//    ) {
//        Text(
//            text = filter,
//            color = Color.White, // Text color
//            fontSize = 16.sp,
//            fontWeight = FontWeight.Medium
//        )
//    }
//}
//
//
//@Composable
//fun ExpenseList(
//    expenses: MutableList<Expense>,
//    filters: List<String>,
//    onAddExpense: (Expense) -> Unit
//) {
//    // Control dialog visibility
//    var showDialog by remember { mutableStateOf(false) }
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        Text(
//            text = "Expenses",
//            fontWeight = FontWeight.Normal,
//            fontSize = 24.sp,
//            modifier = Modifier
//                .padding(16.dp)
//                .align(alignment = Alignment.CenterHorizontally)
//        )
//
//        // Filter Row
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp),
//            horizontalArrangement = Arrangement.SpaceEvenly // Evenly space the filters
//        ) {
//            filters.forEach { filter ->
//                FilterItem(filter = filter)
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // List of expenses
//        LazyColumn(
//
//            contentPadding = PaddingValues(8.dp)
//        ) {
//            items(expenses) { expense ->
//                ExpenseItem(expense = expense)
//            }
//        }
//
//        // Floating Action Button (+) at the bottom
//        Box(
//
//            contentAlignment = Alignment.BottomEnd,
//
//        ) {
//            FloatingActionButton(
//                onClick = {
//                    // Toggle dialog visibility
//                    showDialog = true
//                },
//                modifier = Modifier
//                    .padding(16.dp)
//                    .size(56.dp),
//                shape = CircleShape,
//                containerColor = MaterialTheme.colorScheme.primary
//            ) {
//                Icon(Icons.Default.Add, contentDescription = "Add Expense", tint = Color.White)
//            }
//        }
//
//        // Show the Add Expense Dialog
//        if (showDialog) {
//            AddExpenseDialog(
//                onAddExpense = { expense ->
//                    onAddExpense(expense)
//                    showDialog = false // Close dialog after adding expense
//                },
//                onDismiss = { showDialog = false } // Close dialog without adding
//            )
//        }
//    }
//}
//
//// Dialog to Add a New Expense
//@Composable
//fun AddExpenseDialog(onAddExpense: (Expense) -> Unit, onDismiss: () -> Unit) {
//    var title by remember { mutableStateOf("") }
//    var amount by remember { mutableStateOf("") }
//
//    Dialog(onDismissRequest = onDismiss) {
//        Surface(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            shape = RoundedCornerShape(8.dp)
//        ) {
//            Column(
//                modifier = Modifier
//                    .padding(16.dp)
//                    .fillMaxWidth()
//            ) {
//                Text(text = "Add New Expense", fontWeight = FontWeight.Bold, fontSize = 18.sp)
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                TextField(
//                    value = title,
//                    onValueChange = { title = it },
//                    label = { Text("Title") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                TextField(
//                    value = amount,
//                    onValueChange = { amount = it },
//                    label = { Text("Amount") },
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                    modifier = Modifier.fillMaxWidth()
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Row(
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Button(onClick = { onDismiss() }) {
//                        Text("Cancel")
//                    }
//                    Button(
//                        onClick = {
//                            if (title.isNotBlank() && amount.isNotBlank()) {
//
//
//                                onDismiss() // Close dialog after adding
//                            }
//                        }
//                    ) {
//                        Text("Add Expense")
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun ExpenseScreen(expenses:MutableList<Expense> ) {
//
//    val filters = remember {
//        mutableStateListOf("Day", "Week", "Month", "Year")
//    }
//
//
//    Column(modifier = Modifier
//        .fillMaxWidth()
//        .fillMaxHeight()
//        .background(Color(0xFF25304F))
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally) {
//        Button(onClick={}) {
//            Text("Go to Chatbot",
//                fontWeight = FontWeight.Normal,
//                fontSize = 14.sp,
//                modifier = Modifier
//                    .padding(6.dp)
//
//            )
//
//
//        }
//        ExpenseList(expenses = expenses, filters = filters, onAddExpense = { newExpense ->
//            expenses.add(newExpense) // Add new expense to the list
//        })
//
//
//        // Add a button to go to ChatBotScreen and pass the expenses list
//
//    }
//}
//
//
