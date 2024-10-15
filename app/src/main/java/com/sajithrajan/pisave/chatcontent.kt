package com.sajithrajan.pisave

import co.yml.charts.ui.piechart.models.PieChartData

sealed class ChatContent {
    data class TextMessage(val message: String) : ChatContent()
    data class ChartMessage(val pieChartData: PieChartData) : ChatContent()
}