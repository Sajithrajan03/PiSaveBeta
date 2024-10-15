package com.sajithrajan.pisave

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
        "Grocery" -> Color(0xFF5F0A87)
        "Entertainment" -> Color(0xFF20BF55)
        "Fuel" -> Color(0xFFEC9F05)
        "Shopping" -> Color(0xFFF53844)
        else -> Color(0xFFCCCCCC) // Default color for categories not listed
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
                .height(100.dp)
        )  {
            Legends(legendsConfig = DataUtils.getLegendsConfigFromPieChartData(pieChartData = pieChartData, gridSize = 2))
        }
        Box(
            modifier = Modifier
                .width(300.dp)
                .height(300.dp)
                .background(color = Color.Transparent)


        ) {
            DonutPieChart(
                modifier = Modifier
                    .background(color = Color.Transparent)
                    .align(alignment = Alignment.Center),

                pieChartData = pieChartData,
                pieChartConfig = PieChartConfig(
                    labelVisible = true,
                    strokeWidth = 120f,
                    backgroundColor = Color.Transparent,
                    labelColor = Color.White,
                    activeSliceAlpha = 0.9f,
                    isAnimationEnable = true,
                    showSliceLabels = true,
                    animationDuration = 1000,
                    chartPadding = 20

                )
            )
        }
    }



}