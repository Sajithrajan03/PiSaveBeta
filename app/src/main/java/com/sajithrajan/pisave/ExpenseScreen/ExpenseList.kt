package com.sajithrajan.pisave.ExpenseScreen

import EditTransactionDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sajithrajan.pisave.dataBase.Expense
import com.sajithrajan.pisave.dataBase.ExpenseEvent
import com.sajithrajan.pisave.dataBase.ExpenseState
import com.sajithrajan.pisave.dataBase.ExpenseViewModel

@Composable
fun getExpenseIcon(category: String): ImageVector = when (category) {
    "Food" -> Icons.Default.Fastfood
    "Transport" -> Icons.Default.AirplanemodeActive
    "Entertainment" -> Icons.Default.Movie
    "Shopping" -> Icons.Default.ShoppingCart
    "Health" -> Icons.Default.Healing
    "Utilities" -> Icons.Default.Lightbulb
    "Others" -> Icons.Default.MoreHoriz
    else -> Icons.Default.Category
}

fun getCategoryColor(category: String): Color = when (category) {
    "Food" -> Color(0xFF80DEEA)
    "Transport" -> Color(0xFF90CAF9)
    "Entertainment" -> Color(0xFFF48FB1)
    "Shopping" -> Color(0xFFFFF176)
    "Health" -> Color(0xFFA5D6A7)
    "Utilities" -> Color(0xFFB39DDB)
    "Others" -> Color(0xFFFFCC80)
    else -> Color(0xFFB0BEC5)
}

@Composable
fun ExpenseList(
    expenseList: List<Expense>,
    viewModel: ExpenseViewModel,
    state: ExpenseState,
    onEvent: (ExpenseEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(expenseList.size) { index ->
            ExpenseItem(
                expense = expenseList[index],
                viewModel = viewModel,
                onEvent = onEvent
            )
        }
    }
}

@Composable
fun ExpenseItem(
    expense: Expense,
    viewModel: ExpenseViewModel,
    onEvent: (ExpenseEvent) -> Unit
) {
    val icon = getExpenseIcon(expense.category ?: "Others")
    val color = getCategoryColor(expense.category ?: "Others")
    var showEditDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
        .clickable { showEditDialog = true },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Icon and category/note
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Category Icon",
                    tint = color,
                    modifier = Modifier.size(25.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    verticalArrangement = if (expense.note != null) Arrangement.spacedBy(8.dp) else Arrangement.Top
                ) {
                    Text(
                        text = expense.title ?: "Unknown",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                        Text(
                            text = expense.category,
                            fontSize = 14.sp,
                            color = color
                        )

                }
            }

            // Right side: Amount and date
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${expense.currency} ${expense.amount}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp
                )
                Text(
                    text = RelativeDateText(epochTime = expense.date),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Delete button
            IconButton(
                onClick = {
                    onEvent(ExpenseEvent.DeleteExpense(expense))
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }

    if (showEditDialog) {
        EditTransactionDialog(
            expense = expense,
            onDismiss = { showEditDialog = false },
            onSave = { updatedExpense ->
                viewModel.updateExpense(updatedExpense)
                showEditDialog = false
            }
        )
    }
}
