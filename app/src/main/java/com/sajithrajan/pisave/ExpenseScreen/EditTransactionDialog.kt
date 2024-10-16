import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.sajithrajan.pisave.R
import com.sajithrajan.pisave.dataBase.Expense
import com.sajithrajan.pisave.dataBase.ExpenseViewModel
import com.sajithrajan.pisave.dataBase.ReceiptEntity
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
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
    onSave: (Expense) -> Unit,
    viewModel: ExpenseViewModel,
) {
    var title by remember { mutableStateOf(expense.title) }
    var amount by remember { mutableStateOf(expense.amount.toString()) }
    var selectedCategory by remember { mutableStateOf(expense.category) }
    var dateInMillis by remember { mutableStateOf(expense.date) }
    var note by remember { mutableStateOf(expense.note ?: "") }
    var showSplitDialog by remember { mutableStateOf(false) }
    var receiptUri by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val categories = listOf("Food", "Transport", "Entertainment", "Shopping", "Health", "Utilities", "Others")
    var splitResult by remember { mutableStateOf<Map<String, Double>?>(null) }

    LaunchedEffect(expense.id) {
        // Load split results and receipt information from the database
        val splits = viewModel.getSplitExpenses(expense.id)
        splitResult = splits.takeIf { it.isNotEmpty() }?.associate { it.participantName to it.amount }

        // Load receipt information
        val receipts = viewModel.getReceiptsForExpense(expense.id)
        receiptUri = receipts.firstOrNull()?.imageUri
    }

    // Function to handle image selection from the gallery
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            receiptUri = it.toString()
            viewModel.insertReceipt(ReceiptEntity(expenseId = expense.id, imageUri = it.toString()))
        }
    }

    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)
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
                    .padding(16.dp), verticalArrangement = Arrangement.SpaceBetween
                       , horizontalAlignment = Alignment.CenterHorizontally
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
                            .height(200.dp)
                    ) {
                        // Background image with green tint overlay
                        Image(painter = painterResource(id = R.drawable.atm),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .blur(
                                    radiusX = 2.dp, // Reduced blur effect for X
                                    radiusY = 2.dp, // Reduced blur effect for Y
                                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                                )

                                .graphicsLayer {
                                    alpha = 0.7f // Adjust for transparency
                                })

                        // Expense details overlay
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top right: Category chip
                            FilterChip(
                                selected = false,
                                onClick = { /* Handle chip click if needed */ },
                                label = {
                                    Text(
                                        text = selectedCategory ?: "Category",
                                        fontSize = 18.sp, // Larger font size
                                        fontWeight = FontWeight.Bold // Bold text
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = getExpenseIcon(selectedCategory ?: "Others"),
                                        contentDescription = selectedCategory ?: "Category",
                                        tint = getCategoryColor(selectedCategory ?: "Others")
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.8f
                                    ), selectedLabelColor = getCategoryColor(
                                        selectedCategory ?: "Others"
                                    )
                                ),
                                modifier = Modifier.align(Alignment.End)
                            )

                            // Center: Amount chip
                            AssistChip(

                                onClick = { /* Handle chip click if needed */ }, label = {
                                    Text(
                                        text = "₹$amount",
                                        fontSize = 40.sp, // Larger font size for emphasis
                                        fontWeight = FontWeight.Bold // Bold text
                                    )
                                }, colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    labelColor = MaterialTheme.colorScheme.onPrimary
                                ), modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            // Bottom left: Date chip
                            FilterChip(
                                selected = false,
                                onClick = { /* Handle chip click if needed */ },
                                label = {
                                    Text(
                                        text = formatDate(dateInMillis),
                                        fontSize = 18.sp, // Larger font size
                                        fontWeight = FontWeight.Bold // Bold text
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color.White.copy(alpha = 0.8f),
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.align(Alignment.Start)
                            )
                        }

                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Notes section

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    readOnly = false, // Allows editing the text
                    label = { Text("Tap to add notes") },
                    trailingIcon = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Note")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
//                            .clickable { noteExpanded = true }
                )


                Spacer(modifier = Modifier.height(8.dp))

                // Split Expense section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally // Center the row
                ) {
                    Card(
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { showSplitDialog = true },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = "Split",
                                modifier = Modifier.size(40.dp)
                            )
                            Text(text = "Split expense with friends or family", fontSize = 14.sp)
                        }
                    }

                    // Display split results if available
                    if (splitResult != null && splitResult!!.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier
                                // Set a width for the results column
                                .padding(16.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp), // Inner padding for content
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start
                        ) {
                            // Title for Split Details
                            Text(
                                text = "Split Details",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Display each split detail
                            splitResult?.forEach { (person, amount) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = person,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "₹${"%.2f".format(amount)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }

                            // Optional message if splitResult is empty

                        }

                    }
                    if (splitResult.isNullOrEmpty()) {
                        Text(
                            text = "No splits available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }

                if (showSplitDialog) {
                    SplitExpenseDialog(
                        totalAmount = expense.amount,
                        expenseId = expense.id,
                        context = LocalContext.current,
                        onDismiss = { showSplitDialog = false },
                        onSplitExpense = { result ->
                            splitResult = result
                            showSplitDialog = false
                        },
                        viewModel = viewModel
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Attach Receipt section
                Card(
                    modifier = Modifier
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable {
                                if (receiptUri != null) {
                                    // If receipt is already fetched, open it in full screen
                                    openImageInFullScreen(context, receiptUri!!)
                                } else {
                                    // Otherwise, launch the gallery to select an image
                                    launcher.launch("image/*")
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Attach",
                            modifier = Modifier.size(40.dp)
                        )
                        Text(text = "Add a photo of a receipt/warranty", fontSize = 14.sp)

                        // Display the selected image if available
                        receiptUri?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            AsyncImage(
                                model = it,
                                contentDescription = "Receipt Image",
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(Color.Gray, RoundedCornerShape(8.dp))
                                    .padding(4.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
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
                                    imageVector = icon, contentDescription = category, tint = color
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color.copy(alpha = 0.2f),
                                selectedLabelColor = color
                            )
                        )
                    }
                }


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

private fun copyUriToFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val tempFile = File(context.cacheDir, "temp_image.jpg")

        FileOutputStream(tempFile).use { outputStream ->
            inputStream?.copyTo(outputStream)
        }
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


fun openImageInFullScreen(context: Context, imageUri: String) {
    val uri = Uri.parse(imageUri)
    val tempFile = copyUriToFile(context, uri)

    if (tempFile != null && tempFile.exists()) {
        val fileUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            setDataAndType(fileUri, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // Check if there is an app that can handle the intent
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "No app available to open the image", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "Failed to open the image", Toast.LENGTH_SHORT).show()
    }
}


