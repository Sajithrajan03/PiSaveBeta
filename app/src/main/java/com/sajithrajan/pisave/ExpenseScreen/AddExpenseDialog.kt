package com.sajithrajan.pisave.ExpenseScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember


import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sajithrajan.pisave.dataBase.Category
import com.sajithrajan.pisave.dataBase.ExpenseEvent
import com.sajithrajan.pisave.dataBase.ExpenseState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val todayMillis = System.currentTimeMillis()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = todayMillis)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val selectedDate = datePickerState.selectedDateMillis?.let { millis ->
                    getStartOfDay(millis)
                }
                onDateSelected(selectedDate)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

fun getStartOfDay(timeMillis: Long): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = timeMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseBottomSheet(
    state: ExpenseState,
    onEvent: (ExpenseEvent) -> Unit,
    sheetState: SheetState,
    scope: CoroutineScope,
    categories: List<Category>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(state.categoryId) }
    var showDatePicker by remember { mutableStateOf(false) }
    val todayMillis = System.currentTimeMillis()
    val selectedDate = state.date?.let { convertMillisToDate(it) } ?: convertMillisToDate(todayMillis)

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
                .padding(16.dp)
                .padding(bottom = 50.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title input with OutlinedTextField
            OutlinedTextField(
                value = state.title,
                onValueChange = {
                    onEvent(ExpenseEvent.SetExpenseTitle(it))
                },
                placeholder = { Text(text = "Title") },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            // Dropdown for Category selection
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = categories.firstOrNull { it.categoryId == selectedCategory }?.categoryName ?: "Select Category",
                    onValueChange = { /* Not editable */ },
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Open Category Dropdown")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().clickable { expanded = true }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            onClick = {
                                selectedCategory = category.categoryId
                                onEvent(ExpenseEvent.SetExpenseCategory(category.categoryName))
                                expanded = false
                            },
                            text = { Text(text = category.categoryName) }
                        )
                    }
                }
            }

            // Amount input with OutlinedTextField
            OutlinedTextField(
                value = if (state.amount == 0.0) "" else state.amount.toString(),
                onValueChange = {
                    val enteredAmount = it.toDoubleOrNull() ?: 0.0
                    onEvent(ExpenseEvent.SetExpenseAmount(enteredAmount))
                },
                placeholder = { if (state.amount == 0.0) Text(text = "Amount") },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Note input with OutlinedTextField
            OutlinedTextField(
                value = state.note ?: "",
                onValueChange = {
                    onEvent(ExpenseEvent.SetExpenseNote(it))
                },
                placeholder = { Text(text = "Note (Optional)") },
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth()
            )

            // Date input with OutlinedTextField and DatePicker
            OutlinedTextField(
                value = selectedDate,
                onValueChange = { /* Not editable */ },
                label = { Text("Date") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select date")
                    }
                },
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }
            )

            if (showDatePicker) {
                DatePickerModal(
                    onDateSelected = { selectedMillis ->
                        if (selectedMillis != null) {
                            onEvent(ExpenseEvent.SetExpenseDate(selectedMillis))
                        }
                        showDatePicker = false
                    },
                    onDismiss = { showDatePicker = false }
                )
            }

            // Save button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(onClick = {
                    onEvent(ExpenseEvent.SetExpenseTitle(state.title.trimEnd()))
                    onEvent(ExpenseEvent.SetExpenseNote(state.note.trimEnd()))
                    onEvent(ExpenseEvent.SaveExpense)
                    scope.launch { sheetState.hide() }
                }) {
                    Text(text = "Save")
                }
            }
        }
    }
}
