// ExpenseScreen.kt
package com.sajithrajan.pisave.ExpenseScreen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sajithrajan.pisave.dataBase.Expense
import com.sajithrajan.pisave.dataBase.ExpenseEvent
import com.sajithrajan.pisave.dataBase.ExpenseState
import com.sajithrajan.pisave.dataBase.ExpenseViewModel
import com.sajithrajan.pisave.dataBase.SortType
import com.sajithrajan.pisave.dataBase.TransactionEntity
import predefinedCategories

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(
    expenseList: List<Expense>,
    transactionList: List<TransactionEntity>,
    state: ExpenseState,
    onEvent: (ExpenseEvent) -> Unit
) {
    val viewModel: ExpenseViewModel = viewModel()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // Track which tab is selected: 0 for Expenses, 1 for Transactions
    var selectedTabIndex by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Tab Row to switch between "Expense" and "Transactions"
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 }
                ) {
                    Text("Expense", modifier = Modifier.padding(16.dp))
                }
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 }
                ) {
                    Text("Transactions", modifier = Modifier.padding(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sorting options (only show if viewing expenses)
            if (selectedTabIndex == 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SortType.entries.forEach { sortType ->
                        FilterChip(
                            selected = state.sortType == sortType,
                            onClick = {
                                onEvent(ExpenseEvent.SortExpense(sortType))
                            },
                            label = { Text(text = sortType.name) },
                            modifier = Modifier.padding(4.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Display the list based on the selected tab
            if (selectedTabIndex == 0) {
                ExpenseList(expenseList = expenseList, viewModel = viewModel, state = state, onEvent = onEvent)
            } else {
                TransactionList(transactionList = transactionList)
            }
        }

        FloatingActionButton(
            onClick = {
                onEvent(ExpenseEvent.ShowDialog)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Expense")
        }

        if (state.isAddingExpense) {
            AddExpenseBottomSheet(
                state = state,
                onEvent = onEvent,
                sheetState = sheetState,
                scope = scope,
                categories = predefinedCategories
            )
        }
    }
}

@Composable
fun TransactionList(transactionList: List<TransactionEntity>) {
    LazyColumn {
        items(transactionList) { transaction ->
            TransactionItem(transaction)
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Title: ${transaction.title}", fontWeight = FontWeight.Bold)
            Text(text = "Category: ${transaction.category}")
            Text(text = "Amount: ${transaction.currency} ${transaction.amount}")
            Text(text = "Date: ${transaction.date}")
        }
    }
}
