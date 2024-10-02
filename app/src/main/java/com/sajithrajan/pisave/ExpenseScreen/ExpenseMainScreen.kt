package com.sajithrajan.pisave.ExpenseScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sajithrajan.pisave.dataBase.Expense
import com.sajithrajan.pisave.dataBase.ExpenseEvent
import com.sajithrajan.pisave.dataBase.ExpenseState
import com.sajithrajan.pisave.dataBase.SortType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(
    expenseList: List<Expense> ,
    state: ExpenseState,
    onEvent: (ExpenseEvent) -> Unit
) {

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                SortType.entries.forEach { sortType ->
                    Row(
                        modifier = Modifier.clickable {
                            onEvent(ExpenseEvent.SortExpense(sortType))
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = state.sortType == sortType,
                            onClick = {
                                onEvent(ExpenseEvent.SortExpense(sortType))
                            }
                        )
                        Text(text = sortType.name)
                    }
                }
            }
            ExpenseList(expenseList = expenseList,state,onEvent)
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
                scope = scope
            )
        }
    }
}