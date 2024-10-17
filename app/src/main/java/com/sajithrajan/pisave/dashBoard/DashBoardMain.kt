package com.sajithrajan.pisave

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sajithrajan.pisave.dashBoard.BudgetDialog
import com.sajithrajan.pisave.dashBoard.BudgetPanel
import com.sajithrajan.pisave.dashBoard.CategorySpendingScreen
import com.sajithrajan.pisave.dashBoard.ExpenseLineChart
import com.sajithrajan.pisave.dashBoard.TopExpensesScreen
import com.sajithrajan.pisave.dataBase.Budget
import com.sajithrajan.pisave.dataBase.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashBoardMain(viewModel: ExpenseViewModel = viewModel()) {
   val expenseList by viewModel.getTotalExpensesForDay().observeAsState(listOf())
   var selectedChipIndex by remember { mutableStateOf(0) }
   val todaySpent by viewModel.todaySpent.collectAsState(initial = 0.0)
   val monthSpent by viewModel.monthSpent.collectAsState(initial = 0.0)
   val currentMonth = SimpleDateFormat("MMMM", Locale.getDefault()).format(Date())
   val budget by viewModel.monthBudget.collectAsState()

   var showBudgetDialog by remember { mutableStateOf(false) }
   var dailyBudgetSliderValue by remember { mutableStateOf(100f) }
   var monthlyBudgetSliderValue by remember { mutableStateOf(1000f) }

   LaunchedEffect(currentMonth) {
      viewModel.getBudgetForMonth(currentMonth)
   }

   LazyColumn(
      modifier = Modifier
         .fillMaxSize()
         .padding(16.dp)
   ) {
      item {
         if (budget == null) {
            // Show button to set budget if the budget is null
            Box(
               modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
               contentAlignment = Alignment.Center
            ) {
               TextButton(onClick = { showBudgetDialog = true }) {
                  Text(text = "Set Monthly Budget")
               }
            }
         } else {
            BudgetPanel(
               todaySpending = todaySpent,
               monthlySpending = monthSpent,
               dailyBudget = budget?.dailyBudget ?: 0.0,
               monthBudget = budget?.monthlyBudget ?: 0.0
            )
         }
      }

      // Display the budget dialog if needed


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

      item {
         Card(
            modifier = Modifier
               .fillMaxWidth()
               .padding(8.dp),
            elevation = CardDefaults.cardElevation(8.dp)
         ) {
            Box(
               contentAlignment = Alignment.Center,
               modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp)
            ) {
               if (expenseList.isNotEmpty()) {
                  ExpenseLineChart(expenses = expenseList)
               } else {
                  Text("No data available", style = MaterialTheme.typography.bodyLarge)
               }
            }
         }
      }

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

      item {
         when (selectedChipIndex) {
            0 -> CategorySpendingScreen(viewModel)
            1 -> TopExpensesScreen(viewModel)
         }
      }

   }
   if (showBudgetDialog) {
      BudgetDialog(
         dailyBudgetSliderValue = dailyBudgetSliderValue,
         monthlyBudgetSliderValue = monthlyBudgetSliderValue,
         onDailyBudgetChange = { dailyBudgetSliderValue = it },
         onMonthlyBudgetChange = { monthlyBudgetSliderValue = it },
         onDismiss = { showBudgetDialog = false },
         onSave = {
            // Create a new Budget object
            val newBudget = Budget(
               month = currentMonth,
               dailyBudget = dailyBudgetSliderValue.toDouble(),
               monthlyBudget = monthlyBudgetSliderValue.toDouble()
            )

            // Save the new budget to the ViewModel
            viewModel.insertBudget(newBudget)

            // Update the local state to the newly inserted budget
//            budget = newBudget

            // Close the dialog
            showBudgetDialog = false
         }

      )
   }
}

