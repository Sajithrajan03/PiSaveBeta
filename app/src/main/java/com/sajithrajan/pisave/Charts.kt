package com.sajithrajan.pisave

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.common.components.Legends
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.sajithrajan.pisave.dataBase.Expense


private fun getColorForCategory(category: String): Color {
    return when (category) {
        // Merged color mapping from both original functions
        "Grocery" -> Color(0xFF5F0A87)
        "Entertainment" -> Color(0xFFF48FB1) // Using the color from the second function
        "Fuel" -> Color(0xFFEC9F05)
        "Shopping" -> Color(0xFFFFF176) // Using the color from the second function
        "Food" -> Color(0xFF80DEEA)
        "Transport" -> Color(0xFF90CAF9)
        "Health" -> Color(0xFFA5D6A7)
        "Utilities" -> Color(0xFFB39DDB)
        "Others" -> Color(0xFFFFCC80)
        else -> Color(0xFFB0BEC5) // Default color for categories not listed
    }
}


fun getDonutChartData(expenses: List<Expense>): List<PieChartData.Slice> {
    val slices = expenses.map { expense ->
        PieChartData.Slice(
            label = expense.category,
            value = expense.amount.toFloat(),
            color = getColorForCategory(expense.category) // Function to assign color based on category or title
        )
    }
    return slices
}

@Composable
fun DonutChartComposable(pieChartData: PieChartData) {
    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentAlignment = Alignment.Center
        )  {
            Legends(legendsConfig = DataUtils.getLegendsConfigFromPieChartData(pieChartData = pieChartData, gridSize = 3))
        }
        Box(
            modifier = Modifier
                .width(300.dp)
                .height(300.dp)
                .padding(0.dp)
                .background(color = Color.Transparent)
                ,contentAlignment = Alignment.Center


        ) {
            DonutPieChart(
                modifier = Modifier
                    .background(color = Color.Transparent)
                    .align(alignment = Alignment.Center),

                pieChartData = pieChartData,
                pieChartConfig = PieChartConfig(
                    labelVisible = true,
                    strokeWidth = 120f,
                    chartPadding = 5


                )
            )
        }
    }



}