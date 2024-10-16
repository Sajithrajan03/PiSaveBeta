package com.sajithrajan.pisave.dashBoard

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.sajithrajan.pisave.dataBase.Expense
import com.sajithrajan.pisave.dataBase.ExpenseViewModel

@Composable
fun TopExpensesScreen(viewModel: ExpenseViewModel) {
    var selectedPeriod by remember { mutableStateOf("month") }

    val topExpenses: LiveData<List<Expense>> = when (selectedPeriod) {
        "day" -> viewModel.getTopExpensesForCurrentDay()
        "month" -> viewModel.getTopExpensesForCurrentMonth()
        "year" -> viewModel.getTopExpensesForCurrentYear()
        else -> viewModel.getTopExpensesForCurrentMonth()
    }

    val topExpensesList by topExpenses.observeAsState(emptyList())
    val totalAmountSpent = topExpensesList.sumOf { it.amount }

    Column {
        PeriodSelectionChips(
            selectedPeriod = selectedPeriod,
            onPeriodSelected = { period -> selectedPeriod = period }
        )
        AssistChip(
            onClick = { Log.d("Assist chip", "Total amount clicked") },
            label = { Text("Total Spent: ₹${totalAmountSpent}") },
            leadingIcon = {
                Icon(
                    Icons.Filled.Money,
                    contentDescription = "Total Spent",
                    Modifier.size(AssistChipDefaults.IconSize)
                )
            },
            modifier = Modifier.padding(8.dp)
        )
        Column {
            topExpensesList.forEach { expense ->
                TopExpenseCard(expense = expense, totalAmount = totalAmountSpent)
            }
        }
    }
}

@Composable
fun TopExpenseCard(expense: Expense, totalAmount: Double) {
    val percentage = if (totalAmount > 0) (expense.amount / totalAmount * 100).toFloat() else 0f
    val icon = getExpenseIcon(expense.category)
    val color = getCategoryColor(expense.category)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Category Icon",
                        tint = color,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = expense.title,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${expense.amount}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = String.format("%.1f%%", percentage),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { percentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = color,
            )
        }
    }
}

@Composable
fun getExpenseIcon(category: String) = when (category) {
    "Food" -> Icons.Default.Fastfood
    "Transport" -> Icons.Default.Train
    "Entertainment" -> Icons.Default.Movie
    "Shopping" -> Icons.Default.ShoppingCart
    "Health" -> Icons.Default.LocalHospital
    "Utilities" -> Icons.Default.Lightbulb
    "Others" -> Icons.Default.MoreHoriz
    else -> Icons.Default.Money
}

fun getCategoryColor(category: String) = when (category) {
    "Food" -> Color(0xFF80DEEA)
    "Transport" -> Color(0xFF90CAF9)
    "Entertainment" -> Color(0xFFF48FB1)
    "Shopping" -> Color(0xFFFFF176)
    "Health" -> Color(0xFFA5D6A7)
    "Utilities" -> Color(0xFFB39DDB)
    "Others" -> Color(0xFFFFCC80)
    else -> Color(0xFFB0BEC5)
}
