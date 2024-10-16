package com.sajithrajan.pisave.ExpenseScreen


import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


fun RelativeDateText(epochTime: Long): String {
    val currentTime = System.currentTimeMillis()

    return when {
        // If it's today
        TimeUnit.MILLISECONDS.toDays(currentTime - epochTime) < 1 -> {
            "Today"
        }
        // If it's yesterday
        TimeUnit.MILLISECONDS.toDays(currentTime - epochTime) == 1L -> {
            "Yesterday"
        }
        // If it's within a week
        TimeUnit.MILLISECONDS.toDays(currentTime - epochTime) <= 5 -> {
            val daysAgo = TimeUnit.MILLISECONDS.toDays(currentTime - epochTime)
            "$daysAgo days ago"
        }
        // Else, display the short date format (e.g., "Sep 12")
        else -> {
            SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(epochTime))
        }
    }
}
