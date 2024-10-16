// ProfileScreen.kt
package com.sajithrajan.pisave

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.sajithrajan.pisave.dataBase.ExpenseViewModel
import com.sajithrajan.pisave.dataBase.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onNavigateToHome: () -> Unit,
    expenseViewModel: ExpenseViewModel
) {
    val context = LocalContext.current
    var isScanDisabled by remember { mutableStateOf(false) }
    val transactions by expenseViewModel.transactions.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                performSmsScan(expenseViewModel, context, { isScanDisabled = true }, coroutineScope)
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display Profile Information with Back Arrow
        ProfileInfo(name = "John Doe", email = "johndoe@example.com", onNavigateToHome = onNavigateToHome)

        Spacer(modifier = Modifier.padding(16.dp))

        Button(
            onClick = {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                    performSmsScan(expenseViewModel, context, { isScanDisabled = true }, coroutineScope)
                } else {
                    launcher.launch(Manifest.permission.READ_SMS)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isScanDisabled
        ) {
            Text("Scan SMS for Bank Transactions")
        }

        Spacer(modifier = Modifier.padding(16.dp))

        Button(
            onClick = {
                expenseViewModel.deleteAllTransactions()
                performSmsScan(expenseViewModel, context, { isScanDisabled = true }, coroutineScope)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Rescan SMS")
        }

        Spacer(modifier = Modifier.padding(16.dp))

        // List of Transactions
        Text(text = "Recent Transactions", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(transactions) { transaction ->
                TransactionItem(transaction)
            }
        }
    }
}

@Composable
fun ProfileInfo(name: String, email: String, onNavigateToHome: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onNavigateToHome() }) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Profile", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            Text(text = "Name: $name")
            Text(text = "Email: $email")
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Title: ${transaction.title}", fontWeight = FontWeight.Bold)
            Text(text = "Category: ${transaction.category}")
            Text(text = "Amount: ${transaction.currency} ${transaction.amount}")
            Text(text = "Date: ${transaction.date}")
        }
    }
}

private fun performSmsScan(
    expenseViewModel: ExpenseViewModel,
    context: Context,
    onCompletion: () -> Unit,
    coroutineScope: CoroutineScope
) {
    coroutineScope.launch {
        scanSmsForBankTransactions(context, expenseViewModel, onCompletion)
    }
}
