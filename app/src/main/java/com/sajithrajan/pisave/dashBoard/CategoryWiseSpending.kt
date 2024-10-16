package com.sajithrajan.pisave.dashBoard

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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.sajithrajan.pisave.dataBase.CategorySpending
import com.sajithrajan.pisave.dataBase.ExpenseViewModel

@Composable
fun SpendingProgressIndicator(progress: Float, color: Color) {
    LinearProgressIndicator(
        progress = {
            progress
        },
        modifier = Modifier.size(24.dp),  // Adjust size as per design
        color = color,

    )
}

// Function to get the appropriate icon for each category
@Composable
fun getCategoryIcon(category: String) = when (category) {
    "Food" -> Icons.Default.Fastfood
    "Transport" -> Icons.Default.Train
    "Entertainment" -> Icons.Default.Movie
    "Shopping" -> Icons.Default.ShoppingCart
    "Health" -> Icons.Default.LocalHospital
    "Utilities" -> Icons.Default.Lightbulb
    "Others" -> Icons.Default.MoreHoriz
    else -> Icons.Default.Folder  // Default icon if no match is found
}



@Composable
fun CategorySpendingCard(spending: CategorySpending) {
    val icon = getCategoryIcon(spending.iconName)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Icon and Category Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,  // Use the dynamic icon
                        contentDescription = "Category Icon",
                        tint=Color(spending.color),
                        modifier = Modifier.size(25.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = spending.category,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(spending.color)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "₹ ${spending.totalAmount}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = String.format("%.1f%%", spending.percentage),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Bottom Row: Linear Progress Indicator
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = spending.percentage / 100f,  // Convert percentage to 0-1 range
                color = Color(spending.color),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)  // Adjust the height as needed
            )
        }
    }
}



@Composable
fun PeriodSelectionChips(selectedPeriod: String, onPeriodSelected: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // FilterChip for "Day"
        FilterChip(
            onClick = { onPeriodSelected("day") },
            label = { Text("Day") },
            selected = selectedPeriod == "day",
            leadingIcon = if (selectedPeriod == "day") {
                {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Selected",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            } else {
                null
            }
        )

        // FilterChip for "Month"
        FilterChip(
            onClick = { onPeriodSelected("month") },
            label = { Text("Month") },
            selected = selectedPeriod == "month",
            leadingIcon = if (selectedPeriod == "month") {
                {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Selected",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            } else {
                null
            }
        )

        // FilterChip for "Year"
        FilterChip(
            onClick = { onPeriodSelected("year") },
            label = { Text("Year") },
            selected = selectedPeriod == "year",
            leadingIcon = if (selectedPeriod == "year") {
                {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Selected",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            } else {
                null
            }
        )
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
        PeriodSelectionChips(
            selectedPeriod = selectedPeriod,
            onPeriodSelected = { period ->
                selectedPeriod = period // Update selected period
            }
        )

        // Display list of spending categories
        spendings.forEach { spending ->
            // Calculate percentage
            val percentage = if (totalSpending > 0) (spending.totalAmount / totalSpending * 100).toFloat() else 0f

            // Display category card
            CategorySpendingCard(
                spending = CategorySpending(
                    category = spending.category,
                    totalAmount = spending.totalAmount,
                    percentage = percentage,
                    iconName = spending.category,
                    color = when (spending.category) {
                        "Food" -> Color(0xFF80DEEA) // Cyan
                        "Transport" -> Color(0xFF90CAF9) // Light Blue
                        "Entertainment" -> Color(0xFFF48FB1) // Pink
                        "Shopping" -> Color(0xFFFFF176) // Yellow
                        "Health" -> Color(0xFFA5D6A7) // Green
                        "Utilities" -> Color(0xFFB39DDB) // Lavender
                        "Others" -> Color(0xFFFFCC80) // Orange
                        else -> Color(0xFFB0BEC5) // Light Gray as default
                    }.toArgb()
                )
            )
        }
    }
}