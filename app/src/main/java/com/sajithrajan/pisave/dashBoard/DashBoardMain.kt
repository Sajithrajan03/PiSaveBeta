package com.sajithrajan.pisave

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sajithrajan.pisave.dashBoard.BudgetPanel
import com.sajithrajan.pisave.dashBoard.CategorySpendingScreen
import com.sajithrajan.pisave.dashBoard.ExpenseLineChart
import com.sajithrajan.pisave.dataBase.ExpenseViewModel

@Composable
fun DashBoardMain(viewModel: ExpenseViewModel = viewModel()) {
   val expenseList  by viewModel.getTotalExpensesForDay().observeAsState(listOf())

   Log.d("custom_log",expenseList.toString())
   Column(
      modifier = Modifier
         .fillMaxSize()
         .padding(16.dp)
   ) {
      BudgetPanel(
         modifier = Modifier
            .padding(16.dp)
            .height(60.dp)
      )
      Box(
         modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),  // Padding for vertical spacing
         contentAlignment = Alignment.Center
      ) {
         Text(
            text = "Analytics",
            style = MaterialTheme.typography.headlineSmall,  // Adjust text style based on your design
            modifier = Modifier.padding(8.dp)
         )
      }

         Box(
            contentAlignment = Alignment.Center,  // Align content horizontally and vertically centered
              // Ensures the Box takes up the full size of the Card
         ) {
            if (expenseList.isNotEmpty()) {
               ExpenseLineChart(expenses = expenseList)
            } else {
               Text("No data available", style = MaterialTheme.typography.bodyLarge)
            }
         }




      CategorySpendingScreen(viewModel)
   }
}
