package com.sajithrajan.pisave.ExpenseScreen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.sajithrajan.pisave.dataBase.ExpenseViewModel
import com.sajithrajan.pisave.dataBase.ReceiptEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachReceiptCard(viewModel: ExpenseViewModel, context: Context, expenseId: Int) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // ActivityResultLauncher to pick an image from the gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                // Store the selected image URI in Room DB
                viewModel.insertReceipt(ReceiptEntity(expenseId = expenseId, imageUri = uri.toString()))
            }
        }
    )

    // Check and request permission
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                galleryLauncher.launch("image/*")
            }
        }
    )

    fun openGallery() {
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                galleryLauncher.launch("image/*")
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clickable { openGallery() },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AttachFile,
                contentDescription = "Attach",
                modifier = Modifier.size(40.dp)
            )
            Text(text = "Add a photo of a receipt/warranty", fontSize = 14.sp)
        }
    }

    // Optionally, show a preview of the selected image
    if (selectedImageUri != null) {
        Text(
            text = "Selected Image: ${selectedImageUri!!.path}",
            modifier = Modifier.padding(16.dp),
            color = Color.Gray
        )
    }
}
