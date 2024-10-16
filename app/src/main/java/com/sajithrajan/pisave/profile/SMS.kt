package com.sajithrajan.pisave

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.sajithrajan.pisave.dataBase.ExpenseViewModel
import com.sajithrajan.pisave.dataBase.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

suspend fun scanSmsForBankTransactions(
    context: Context,
    expenseViewModel: ExpenseViewModel,
    onCompletion: () -> Unit
) {
    val smsUri = Uri.parse("content://sms/inbox")
    val projection = arrayOf("date", "body", "address")
    val cursor: Cursor? = context.contentResolver.query(smsUri, projection, null, null, null)

    cursor?.use {
        while (it.moveToNext()) {
            val smsDate = it.getLong(it.getColumnIndexOrThrow("date"))
            val smsBody = it.getString(it.getColumnIndexOrThrow("body"))

            // Parse SMS content
            val transaction = parseSmsForTransactionDetails(smsBody, smsDate)
            if (transaction != null) {
                // Check for duplicate before adding
                val isDuplicate = withContext(Dispatchers.IO) {
                    expenseViewModel.isDuplicateTransaction(transaction)
                }

                if (!isDuplicate) {
                    // Add to the ViewModel if not a duplicate
                    withContext(Dispatchers.IO) {
                        expenseViewModel.addTransaction(transaction)
                        expenseViewModel.uploadTransactionAsExpense(transaction)
                    }
                } else {
                    Log.d("SMS Scan", "Duplicate transaction detected: ${transaction.title}, Amount: ${transaction.amount}")
                }
            }
        }
    }

    cursor?.close()
    Log.d("SMS Scan", "SMS scanning completed.")
    onCompletion() // Notify that the scanning is complete
}

fun parseSmsForTransactionDetails(body: String, smsDate: Long): TransactionEntity? {
    try {
        // Example SMS format:
        // "Your a/c XXXXXXXXXX5088 debited for payee Mr DILIP P for Rs. 20.00 on 2024-08-12, ref 459193050990."
        val regex = Regex("debited for payee (.+?) for Rs. (\\d+\\.\\d{2}) on (\\d{4}-\\d{2}-\\d{2})")
        val matchResult = regex.find(body)

        if (matchResult != null) {
            val (payee, amountStr, dateStr) = matchResult.destructured
            val amount = amountStr.toDoubleOrNull() ?: 0.0
            val title = "Payment to $payee"
            val category = "Bank Transaction"
            val note = ""
            val date = convertDateStringToEpoch(dateStr)

            return TransactionEntity(
                title = title,
                category = category,
                note = note,
                amount = amount,
                currency = "₹",
                date = date
            )
        }
    } catch (e: Exception) {
        Log.e("SMS Parsing", "Failed to parse SMS: ${e.message}")
    }

    return null
}

fun convertDateStringToEpoch(dateStr: String): Long {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = sdf.parse(dateStr)
    return date?.time ?: System.currentTimeMillis()
}
