package com.sajithrajan.pisave.ExpenseScreen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sajithrajan.pisave.dataBase.Expense
import com.sajithrajan.pisave.dataBase.ExpenseEvent
import com.sajithrajan.pisave.dataBase.ExpenseState
import com.sajithrajan.pisave.dataBase.ExpenseViewModel
import com.sajithrajan.pisave.dataBase.SortType
import predefinedCategories


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(
    expenseList: List<Expense> ,
    state: ExpenseState,
    onEvent: (ExpenseEvent) -> Unit
) {
    val viewModel: ExpenseViewModel = viewModel()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp), // Add space between chips
                verticalAlignment = Alignment.CenterVertically
            ) {
                SortType.entries.forEach { sortType ->
                    FilterChip(
                        selected = state.sortType == sortType,
                        onClick = {
                            onEvent(ExpenseEvent.SortExpense(sortType))
                        },
                        label = { Text(text = sortType.name) },
                        modifier = Modifier.padding(4.dp), // Additional padding for better spacing
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
            ExpenseList(expenseList = expenseList,viewModel,state,onEvent )
        }


        FloatingActionButton(
            onClick = {
                onEvent(ExpenseEvent.ShowDialog )
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