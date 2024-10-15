package com.sajithrajan.pisave.ExpenseScreen

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
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sajithrajan.pisave.dataBase.Expense
import com.sajithrajan.pisave.dataBase.ExpenseEvent
import com.sajithrajan.pisave.dataBase.ExpenseState
import com.sajithrajan.pisave.dataBase.ExpenseViewModel

fun getCategoryIcon(categoryName: String): ImageVector {
    return when (categoryName) {
        "Food" -> Icons.Filled.Fastfood
        "Travel" -> Icons.Filled.AirplanemodeActive
        "Shopping" -> Icons.Filled.ShoppingCart
        "Entertainment" -> Icons.Filled.Movie
        "Health" -> Icons.Filled.Healing
        else -> Icons.Filled.Category  // Default icon if category is unknown
    }
}

@Composable
fun ExpenseList(expenseList: List<Expense> ,
                viewModel: ExpenseViewModel,  // Pass the ViewModel so that ExpenseItem can fetch category names
                state: ExpenseState,
                onEvent: (ExpenseEvent) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        items(expenseList.size) { index ->
            ExpenseItem(
                expense = expenseList[index],
                viewModel = viewModel,  // Pass the ViewModel to each ExpenseItem
                onEvent = onEvent
            )
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense,
                viewModel: ExpenseViewModel,
                onEvent: (ExpenseEvent) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically  // Ensure everything is vertically centered
        ) {
            // Left side: Icon and category/note
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically  // Ensure vertical alignment here too
            ) {
                val icon = getCategoryIcon(expense.category ?: "Unknown")

                Icon(
                    imageVector = icon,
                    contentDescription = "Category Icon",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .size(25.dp)
                        .align(Alignment.CenterVertically) // Size of the Icon
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    verticalArrangement = if (expense.note != null) Arrangement.spacedBy(8.dp) else Arrangement.Top // No spacing if note is null
                ) {
                    Text(
                        text = expense.title ?: "Unknown",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSecondary,
                    )
                    expense.note?.let {
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
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
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 20.sp
                )
                Text(
                    text = RelativeDateText(epochTime = expense.date),  // Pass your epoch time here
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondary,
                )
            }

            // Delete button
            IconButton(
                onClick = {
                    onEvent(ExpenseEvent.DeleteExpense(expense))
                },
                modifier = Modifier.align(Alignment.CenterVertically)  // Align delete button vertically
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }

}
