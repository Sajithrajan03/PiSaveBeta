
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sajithrajan.pisave.dataBase.Expense
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun getExpenseIcon(category: String): ImageVector = when (category) {
    "Food" -> Icons.Default.Fastfood
    "Transport" -> Icons.Default.AirplanemodeActive
    "Entertainment" -> Icons.Default.Movie
    "Shopping" -> Icons.Default.ShoppingCart
    "Health" -> Icons.Default.Healing
    "Utilities" -> Icons.Default.Lightbulb
    "Others" -> Icons.Default.MoreHoriz
    else -> Icons.Default.Category
}

fun getCategoryColor(category: String): Color = when (category) {
    "Food" -> Color(0xFF80DEEA)
    "Transport" -> Color(0xFF90CAF9)
    "Entertainment" -> Color(0xFFF48FB1)
    "Shopping" -> Color(0xFFFFF176)
    "Health" -> Color(0xFFA5D6A7)
    "Utilities" -> Color(0xFFB39DDB)
    "Others" -> Color(0xFFFFCC80)
    else -> Color(0xFFB0BEC5)
}
fun formatDate(epochTime: Long): String {
    val sdf = SimpleDateFormat("MMMM dd", Locale.getDefault())
    return sdf.format(Date(epochTime))
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditTransactionDialog(
    expense: Expense,
    onDismiss: () -> Unit,
    onSave: (Expense) -> Unit
) {
    var title by remember { mutableStateOf(expense.title) }
    var amount by remember { mutableStateOf(expense.amount.toString()) }
    var selectedCategory by remember { mutableStateOf(expense.category) }
    var dateInMillis by remember { mutableStateOf(expense.date) }
    var note by remember { mutableStateOf(expense.note ?: "") }

    val categories = listOf("Food", "Transport", "Entertainment", "Shopping", "Health", "Utilities", "Others")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Expense details section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        // Background image with green tint overlay
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    alpha = 0.7f // Adjust for transparency
                                }
                                .background(Color(0xFF4CAF50).copy(alpha = 0.3f)) // Green tint
                        )

                        // Expense details overlay
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top right: Category
                            Text(
                                text = selectedCategory ?: "Category",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.align(Alignment.End)
                            )

                            // Center: Amount
                            Text(
                                text = "₹$amount",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            // Bottom left: Date
                            Text(
                                text = formatDate(dateInMillis),
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 14.sp,
                                modifier = Modifier.align(Alignment.Start)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Notes section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Notes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = { Text("Tap to add notes") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Split Expense section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { /* Handle split logic */ },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.Group, contentDescription = "Split", modifier = Modifier.size(40.dp))
                        Text(text = "Split expense with friends or family", fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Attach Receipt section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { /* Handle attach receipt logic */ },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.AttachFile, contentDescription = "Attach", modifier = Modifier.size(40.dp))
                        Text(text = "Add a photo of a receipt/warranty", fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category selection section
                Text(
                    text = "Select Category",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        val icon = getExpenseIcon(category)
                        val color = getCategoryColor(category)

                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            leadingIcon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = category,
                                    tint = color
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color.copy(alpha = 0.2f),
                                selectedLabelColor = color
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Other Info section (if applicable)
                Text(text = "Other Info", style = MaterialTheme.typography.bodyMedium)

                // Save and Cancel Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    TextButton(onClick = {
                        val updatedExpense = expense.copy(
                            title = title.trim(),
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            category = selectedCategory,
                            date = dateInMillis,
                            note = note
                        )
                        onSave(updatedExpense)
                        onDismiss()
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
