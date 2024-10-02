package com.sajithrajan.pisave.ExpenseScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sajithrajan.pisave.dataBase.ExpenseEvent
import com.sajithrajan.pisave.dataBase.ExpenseState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseBottomSheet(
    state: ExpenseState,
    onEvent: (ExpenseEvent) -> Unit,
    sheetState: SheetState,
    scope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = {
            scope.launch { sheetState.hide() }
            onEvent(ExpenseEvent.HideDialog)
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = state.title,
                onValueChange = {
                    onEvent(ExpenseEvent.SetExpenseTitle(it))
                },
                placeholder = { Text(text = "Title") }
            )
            TextField(
                value = state.category,
                onValueChange = {
                    onEvent(ExpenseEvent.SetExpenseCategory(it))
                },
                placeholder = { Text(text = "Category") }
            )
            TextField(
                value = state.amount.toString(),
                onValueChange = {
                    onEvent(ExpenseEvent.SetExpenseAmount(it.toDoubleOrNull() ?: 0.0))
                },
                placeholder = { Text(text = "Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(onClick = {
                    onEvent(ExpenseEvent.SaveExpense)
                    scope.launch { sheetState.hide() }
                }) {
                    Text(text = "Save")
                }
            }
        }
    }
}