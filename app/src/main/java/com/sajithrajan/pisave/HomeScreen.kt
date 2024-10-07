package com.sajithrajan.pisave

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.models.PieChartData
import com.sajithrajan.pisave.dataBase.Expense
import com.sajithrajan.pisave.dataBase.ExpenseEvent
import com.sajithrajan.pisave.dataBase.ExpenseState
import com.sajithrajan.pisave.ui.theme.NavyBlue
import java.time.LocalDate

@Composable
fun HomeScreen(
    expenseList: List<Expense>,
    state: ExpenseState,
    onEvent: (ExpenseEvent) -> Unit
){
    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = Color.Transparent),
        contentAlignment = Alignment.Center) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .align(alignment = Alignment.Center),

        ) {
            Surface (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),

                color = NavyBlue
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.8f)
                        .background(color = Color.Transparent)
                        .align(alignment = Alignment.CenterHorizontally),
                ) {
                    Text(
                        text = "Home",
                        fontSize =MaterialTheme.typography.displayLarge.fontSize,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(alignment = Alignment.Center),
                        color = Color.Black

                    )
                }

            }
            Box (
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Transparent),


            ) {
                Box (
                    modifier = Modifier
                        .fillMaxSize(0.8f)
                        .background(color = Color.Transparent)
                        .align(alignment = Alignment.Center)



                ) {
                    if (state.expenselist == null || state.expenselist.isEmpty()) {

                        val dummyExpense = Expense(
                            title = "No Expenses",
                            category = "General",
                            note = "No expenses recorded.",
                            amount = 1.0,
                            date = LocalDate.now().toEpochDay()
                        )

                        // Create a temporary list with the dummy expense
                        val tempExpenseList = expenseList + dummyExpense

                        // Generate pie data from the temporary list
                        val pieData = PieChartData(getDonutChartData(tempExpenseList), plotType = PlotType.Donut)
                        DonutChartComposable(pieChartData = pieData)
//                        Box(
//                            modifier = Modifier
//                            .fillMaxWidth()
//                            .height(100.dp)
//                            .background(color = Color.Transparent)
//                            .align(alignment = Alignment.Center),
//                        ) {
//                            Text(
//                                text = "No data",
//                                color = Color.White,
//                                fontWeight = FontWeight.Bold,
//                                textAlign = TextAlign.Center
//                            )
//                        }
//                        Box(
//                            modifier = Modifier
//                                .
//                                .background(color = Color.Transparent)
//                                .align(alignment = Alignment.Center),
//                        ) {
//                            // Display the donut chart
//                            D
//                        }



                    } else {
                        val pieData = PieChartData(getDonutChartData(state.expenselist), plotType = PlotType.Donut)
                        DonutChartComposable(pieChartData = pieData)
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun HomeScreenPreview() {
//    HomeScreen( state = state, onEvent = viewModel::onEvent , expenseList = state.expenselist)
//}