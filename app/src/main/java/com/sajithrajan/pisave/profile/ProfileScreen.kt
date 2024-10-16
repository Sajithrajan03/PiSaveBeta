package com.sajithrajan.pisave.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun ProfileScreen(onNavigateToHome: () -> Unit) {
    val context = LocalContext.current
    var showSmsDialog by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                scanSmsForBankTransactions(context)
            } else {
                showSmsDialog = false
            }
        }
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(64.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        tint = Color.White
                    )
                    Column {
                        Text(
                            text = "John Doe",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "johndoe@example.com",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(16.dp))

            Button(
                onClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                        scanSmsForBankTransactions(context)
                    } else {
                        showSmsDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = "Scan SMS",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text("Scan SMS for Bank Transactions")
            }

            if (showSmsDialog) {
                AlertDialog(
                    onDismissRequest = { showSmsDialog = false },
                    title = { Text(text = "Permission Request") },
                    text = { Text("This app needs access to your SMS to scan for bank transactions. Do you want to grant permission?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                launcher.launch(Manifest.permission.READ_SMS)
                                showSmsDialog = false
                            }
                        ) {
                            Text("Allow")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showSmsDialog = false }) {
                            Text("Deny")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.padding(16.dp))

            // Navigate Back Button
            Button(onClick =  onNavigateToHome) {
                Text("Go Back")
            }
        }
    }
}

fun scanSmsForBankTransactions(context: Context) {
    // Your SMS scanning logic here
}
