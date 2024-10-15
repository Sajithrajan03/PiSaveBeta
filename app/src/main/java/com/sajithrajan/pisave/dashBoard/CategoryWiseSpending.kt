package com.sajithrajan.pisave.dashBoard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.sajithrajan.pisave.dataBase.CategorySpending
import com.sajithrajan.pisave.dataBase.ExpenseViewModel

@Composable
fun SpendingProgressIndicator(progress: Float, color: Color) {
    CircularProgressIndicator(
        progress = { progress },
        modifier = Modifier.size(24.dp),  // Adjust size as per design
        color = color,
        strokeWidth = 4.dp,
    )
}

@Composable
fun CategorySpendingCard(spending: CategorySpending) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
//        elevation = 4.dp,
//        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Icon and Category Name
            Row(
                modifier = Modifier.align(Alignment.CenterVertically),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,  // Placeholder, use unique icons
                    contentDescription = "Category Icon",
                    tint =Color(spending.color),  // Unique color per category
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = spending.category,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Right side: Amount spent and percentage
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "₹${spending.totalAmount}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${spending.percentage}%",
                    style = MaterialTheme.typography.bodySmall
                )

                // Progress Indicator at the bottom
                Spacer(modifier = Modifier.height(4.dp))
                SpendingProgressIndicator(
                    progress = spending.percentage / 100f,  // Convert percentage to 0-1 range
                    color = Color(spending.color)
                )
            }
        }
    }
}

@Composable
fun CategorySpendingList(spendings: List<CategorySpending>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(spendings) { spending ->
            CategorySpendingCard(spending)
        }
    }
}

@Composable
fun CategorySpendingScreen(viewModel: ExpenseViewModel) {
    var selectedPeriod by remember { mutableStateOf("month") }

    // Get the total spendings based on the selected period
    val spendings by when (selectedPeriod) {
        "day" -> viewModel.getCategoryWiseSpendingForDay("2024-10-14").observeAsState(emptyList())
        "month" -> viewModel.getCategoryWiseSpendingForMonth("10").observeAsState(emptyList())
        "year" -> viewModel.getCategoryWiseSpendingForYear("2024").observeAsState(emptyList())
        else -> viewModel.getCategoryWiseSpendingForMonth("10").observeAsState(emptyList())
    }

    // Calculate total spending for percentage calculations
    val totalSpending = spendings.sumOf { it.totalAmount }

    // Display the spending cards
    Column {
        // Toggle for period selection (Day/Month/Year)
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Button(onClick = { selectedPeriod = "day" }) { Text("Day") }
            Button(onClick = { selectedPeriod = "month" }) { Text("Month") }
            Button(onClick = { selectedPeriod = "year" }) { Text("Year") }
        }

        // Display list of spending categories
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(spendings) { spending ->
                // Calculate percentage
                val percentage = if (totalSpending > 0) (spending.totalAmount / totalSpending * 100).toFloat() else 0f

                // Assign icon and color based on the category
                val iconName = when (spending.category) {
                    "Food" -> "food_icon"
                    "Transport" -> "transport_icon"
                    "Shopping" -> "shopping_icon"
                    "Health" -> "health_icon"
                    else -> "default_icon"
                }
                val color = when (spending.category) {
                    "Food" -> Color.Green
                    "Transport" -> Color.Blue
                    "Shopping" -> Color.Red
                    "Health" -> Color.Magenta
                    else -> Color.Gray
                }

                // Display category card
                CategorySpendingCard(
                    spending = CategorySpending(
                        category = spending.category,
                        totalAmount = spending.totalAmount,
                        percentage = percentage,
                        iconName = iconName,
                        color = color.toArgb()
                    )
                )
            }
        }
    }
}
