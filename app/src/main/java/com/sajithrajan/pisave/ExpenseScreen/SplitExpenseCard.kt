
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.sajithrajan.pisave.dataBase.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitExpenseDialog(
    totalAmount: Double,
    context: Context,
    onDismiss: () -> Unit,
    onSplitExpense: (Map<String, Double>) -> Unit,
    viewModel: ExpenseViewModel,
    expenseId : Int,
) {
    var showContactDialog by remember { mutableStateOf(true) }
    var selectedContacts by remember { mutableStateOf(emptyList<String>()) }
    var contacts by remember { mutableStateOf<List<String>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isSortedAscending by remember { mutableStateOf(true) }
    var currentPage by remember { mutableStateOf(0) } // 0 for contact selection, 1 for split configuration

    // State for splitting
    var splitAmounts by remember { mutableStateOf(mutableMapOf<String, String>()) } // Stores amount as a string for editing

    // Calculate total split amount
    val totalSplitAmount = splitAmounts.values.sumOf { it.toDoubleOrNull() ?: 0.0 }
    val remainingAmount = totalAmount - totalSplitAmount

    // Fetch contacts launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                contacts = fetchContacts(context)
            } else {
                showContactDialog = false
            }
        }
    )

    // Check and request permission
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            contacts = fetchContacts(context)
        } else {
            launcher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    // Filter and sort contacts based on the search query and sort order
    val filteredContacts = contacts
        .filter { it.contains(searchQuery, ignoreCase = true) }
        .sortedWith(if (isSortedAscending) String.CASE_INSENSITIVE_ORDER else String.CASE_INSENSITIVE_ORDER.reversed())

    if (showContactDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (currentPage == 0) {
                        // Contact selection page
                        Text(
                            text = "Select Contacts to Split Expense",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // "Proceed to Split" button
                        Button(
                            onClick = { currentPage = 1 },
                            modifier = Modifier.align(Alignment.End),
                            enabled = selectedContacts.isNotEmpty()
                        ) {
                            Text("Proceed to Split")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Search bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Search Contacts") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Sort button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isSortedAscending = !isSortedAscending },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(text = if (isSortedAscending) "Sort: A-Z" else "Sort: Z-A")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Contact List
                        LazyColumn {
                            items(filteredContacts) { contact ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (selectedContacts.contains(contact)) {
                                                selectedContacts = selectedContacts - contact
                                            } else {
                                                selectedContacts = selectedContacts + contact
                                            }
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedContacts.contains(contact),
                                        onCheckedChange = {
                                            if (it) {
                                                selectedContacts = selectedContacts + contact
                                            } else {
                                                selectedContacts = selectedContacts - contact
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = contact)
                                }
                            }
                        }
                    } else {
                        // Split configuration page
                        Text(
                            text = "Configure Split by Amount",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )




                        Spacer(modifier = Modifier.height(8.dp))

                        // Split input fields
                        val allParticipants = listOf("Me") + selectedContacts

                        val splitAmounts = remember { mutableStateMapOf<String, String>() }
                        var remainingAmount by remember { mutableStateOf(totalAmount) }

                        // Function to split equally
                        fun splitEqually() {
                            val equalAmount = totalAmount / allParticipants.size
                            allParticipants.forEach { participant ->
                                splitAmounts[participant] = equalAmount.toString()
                            }
                            remainingAmount = 0.0 // Since it is split equally, nothing is left to split
                        }

                        Column {
                            // Button for splitting equally
                            Button(
                                onClick = { splitEqually() },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Split Equally")
                            }

                            // Remaining amount text
                            Text(
                                text = "Amount left to split: ₹${"%.2f".format(remainingAmount)}",
                                fontSize = 16.sp,
                                color = if (remainingAmount == 0.0) Color.Green else Color.Red,
                                fontWeight = FontWeight.SemiBold
                            )

                            // LazyColumn to display each participant's amount
                            LazyColumn {
                                items(allParticipants) { participant ->
                                    OutlinedTextField(
                                        value = splitAmounts[participant] ?: "",
                                        onValueChange = { value ->
                                            // Update the value in the map
                                            splitAmounts[participant] = value
                                            val totalSplitAmount = splitAmounts.values.sumOf { it.toDoubleOrNull() ?: 0.0 }
                                            remainingAmount = totalAmount - totalSplitAmount
                                        },
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            keyboardType = KeyboardType.Number
                                        ),
                                        label = { Text("Amount for $participant") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Split button
                        Button(
                            onClick = {
                                val finalSplit = splitAmounts.mapValues { it.value.toDoubleOrNull() ?: 0.0 }
                                onSplitExpense(finalSplit)
                                viewModel.saveSplitExpenses(expenseId, finalSplit)
                                onDismiss()
                            },
                            modifier = Modifier.align(Alignment.End),
                            enabled = remainingAmount == 0.0 // Enable only if the remaining amount is zero
                        ) {
                            Text("Split Expense")
                        }

                        // Back button
                        TextButton(
                            onClick = { currentPage = 0 },
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Text("Back")
                        }
                    }
                }
            }
        }
    }
}

// Helper function to fetch contacts
fun fetchContacts(context: Context): List<String> {
    val contacts = mutableListOf<String>()
    val cursor = context.contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME),
        null, null, null
    )
    cursor?.use {
        while (it.moveToNext()) {
            val name = it.getString(
                it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            )
            contacts.add(name)
        }
    }
    return contacts
}
