package com.sajithrajan.pisave.dashBoard

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.sajithrajan.pisave.dataBase.DailyExpense
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpenseLineChart(expenses:List<DailyExpense>) {

    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())  // Example format "01 Jan"

    // Create a list of points using date (x-axis) and total (y-axis)
    val pointsData: List<Point> = expenses.mapIndexed { index, expense ->
        // For X-axis, you can use index or normalize the date
        val xValue = index.toFloat()  // Using index as X-axis value for now
        val yValue = expense.total.toFloat()  // Y-axis is the expense total

        Point(x = xValue, y = yValue)
    }

    // X-axis labels based on the formatted dates
    val xAxisLabels: List<String> = expenses.map { expense ->
        dateFormat.format(Date(expense.date))  // Convert long to formatted date
    }

    val xAxisData = AxisData.Builder()

        .axisStepSize(100.dp)
        .backgroundColor(Color.Transparent)
        .steps(pointsData.size - 1)
        .labelData { i -> xAxisLabels.getOrElse(i) { "" } } // Use formatted date labels
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .startPadding(20.dp)
        .build()


    val steps = 5
    val yAxisData = AxisData.Builder()
        .steps(steps)
        .backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            val maxYValue = expenses.maxOfOrNull { it.total.toFloat() } ?: 100f
            (i * maxYValue / steps).toString()  // Scale Y-axis based on max total
        }
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()

    // Line chart data
    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,  // Use the List<Point> here
                    lineStyle = LineStyle(
                        color = MaterialTheme.colorScheme.tertiary,
                        lineType = LineType.SmoothCurve(isDotted = false)
                    ),
                    intersectionPoint = IntersectionPoint(
                        color=MaterialTheme.colorScheme.tertiary
                    ),
                    selectionHighlightPoint = SelectionHighlightPoint(
                        color=MaterialTheme.colorScheme.primary
                    ),
                    shadowUnderLine = ShadowUnderLine(
                        alpha = 0.5f,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.inversePrimary,
                                Color.Transparent
                            )
                        )
                    ),
                    selectionHighlightPopUp = SelectionHighlightPopUp()
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(
            color = MaterialTheme.colorScheme.outline
        ),
        backgroundColor = MaterialTheme.colorScheme.surface
    )

    // LineChart Component
    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            ,
        lineChartData = lineChartData
    )
}