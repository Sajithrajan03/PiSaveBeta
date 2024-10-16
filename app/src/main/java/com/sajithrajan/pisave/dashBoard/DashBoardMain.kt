package com.sajithrajan.pisave

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sajithrajan.pisave.dashBoard.BudgetPanel
import com.sajithrajan.pisave.dashBoard.CategorySpendingScreen
import com.sajithrajan.pisave.dashBoard.ExpenseLineChart
import com.sajithrajan.pisave.dashBoard.TopExpensesScreen
import com.sajithrajan.pisave.dataBase.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashBoardMain(viewModel: ExpenseViewModel = viewModel()) {
   val expenseList by viewModel.getTotalExpensesForDay().observeAsState(listOf())
   var selectedChipIndex by remember { mutableStateOf(0) }
   LazyColumn(
      modifier = Modifier
         .fillMaxSize()
         .padding(16.dp)
   ) {
      // Add the BudgetPanel as the first item
      item {
         BudgetPanel(
            modifier = Modifier
               .padding(16.dp)
               .height(60.dp)
         )
      }

      // Add the "Analytics" text as another item
      item {
         Box(
            modifier = Modifier
               .fillMaxWidth()
               .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
         ) {
            Text(
               text = "Analytics",
               style = MaterialTheme.typography.headlineSmall,
               modifier = Modifier.padding(8.dp)
            )
         }
      }

      // Add the ExpenseLineChart or "No data available" text as an item
      item {
         Card(
            modifier = Modifier
               .fillMaxWidth()
               .padding(8.dp),
            elevation = CardDefaults.cardElevation(8.dp)
         ) {
            Box(
               contentAlignment = Alignment.Center,
               modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
               if (expenseList.isNotEmpty()) {
                  ExpenseLineChart(expenses = expenseList)
               } else {
                  Text("No data available", style = MaterialTheme.typography.bodyLarge)
               }
            }
         }
      }

      // Add the Tabs for "Top Categories" and "Top Expenses"
      item {
         Box(
            modifier = Modifier
               .fillMaxWidth()
               .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
         ) {
            Row(
               horizontalArrangement = Arrangement.spacedBy(16.dp),
               verticalAlignment = Alignment.CenterVertically
            ) {
               AssistChip(
                  onClick = { selectedChipIndex = 0 },
                  label = { Text("Top Categories") },

                  colors = AssistChipDefaults.assistChipColors(
                     containerColor = if (selectedChipIndex == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                     labelColor = if (selectedChipIndex == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                  ),
                  modifier = Modifier.padding(horizontal = 8.dp)
               )
               AssistChip(
                  onClick = { selectedChipIndex = 1 },
                  label = { Text("Top Expenses") },
                  colors = AssistChipDefaults.assistChipColors(
                     containerColor = if (selectedChipIndex == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                     labelColor = if (selectedChipIndex == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                  ),
                  modifier = Modifier.padding(horizontal = 8.dp)
               )
            }
         }
      }

      // Display content based on the selected tab
      item {
         when (selectedChipIndex) {
            0 -> CategorySpendingScreen(viewModel)
            1 -> TopExpensesScreen(viewModel)
         }
      }
   }
}