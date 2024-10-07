package com.sajithrajan.pisave
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sajithrajan.pisave.dataBase.Expense
import com.sajithrajan.pisave.dataBase.ExpenseEvent
import com.sajithrajan.pisave.dataBase.ExpenseState
import dev.jeziellago.compose.markdowntext.MarkdownText
import java.util.Locale

@SuppressLint("UnrememberedMutableState")
@Composable
fun ChatBotScreen(
    bakingViewModel: BakingViewModel = viewModel(), // ViewModel
    expenseList: List<Expense>,
    state: ExpenseState,
    onEvent: (ExpenseEvent) -> Unit
) {
    val promptText = remember { mutableStateOf(TextFieldValue("")) }
    val conversationState by bakingViewModel.conversationList.collectAsState()
    val conversationList = conversationState.messages
    val uiState by bakingViewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(conversationList) { message ->
                ChatMessageCard(message = message)
            }

        }

        UserInputSection(
            promptText = promptText,
            onSendMessage = { message ->
                if (message.isNotBlank()) {
                    bakingViewModel.addMessageToConversation("You: $message")
                    bakingViewModel.sendPromptWithExpenses(expenseList, message)
                    promptText.value = TextFieldValue("")
                    keyboardController?.hide()
                }
            }
        )
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> {
                if (!conversationList.contains("AI: ${(uiState as UiState.Success).outputText}")) {
                    bakingViewModel.addMessageToConversation("AI: ${(uiState as UiState.Success).outputText}")
                }
            }
            is UiState.Error -> {
                if (!conversationList.contains("Error: ${(uiState as UiState.Error).errorMessage}")) {
                    bakingViewModel.addMessageToConversation("Error: ${(uiState as UiState.Error).errorMessage}")
                }
            }
            else -> Unit
        }
    }
}

@Composable
fun ChatMessageCard(message: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.startsWith("AI: ")) Arrangement.Start else Arrangement.End
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (message.startsWith("AI: ")) Color.Black else Color(0xFF0C6E63),
            ),
            modifier = Modifier.padding(5.dp)
        ) {
            MarkdownText(
                markdown = message, // AI's message
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp
                ),
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}

@Composable
fun UserInputSection(promptText: MutableState<TextFieldValue>, onSendMessage: (String) -> Unit) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            promptText.value = TextFieldValue(result?.get(0) ?: "")
        } else {
            promptText.value = TextFieldValue("")
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF26292D))
            .border(1.dp, Color(0xFF9860E4), MaterialTheme.shapes.large)
            .padding(vertical = 4.dp, horizontal = 10.dp)
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = 10.dp, alignment = Alignment.CenterHorizontally)



    ) {

        IconButton(onClick = {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Go on then, say something.")
            launcher.launch(intent) },
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "VolumeUp", tint = Color.White)

        }

        BasicTextField(
            value = promptText.value,
            onValueChange = { promptText.value = it },
            modifier = Modifier.weight(1f),
            textStyle = TextStyle(fontSize = MaterialTheme.typography.headlineSmall.fontSize, color = Color.White),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                    if (promptText.value.text.isEmpty()) {
                        Text("Ask anything ...")
                    }
                    innerTextField()
                }
            }
        )



        IconButton(
            onClick = { onSendMessage(promptText.value.text) },
            enabled = promptText.value.text.isNotEmpty(),
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)

        }
    }
}


// Example Expense class (used as part of the ViewModel)

// old code
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        // Display conversation history
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .weight(1f),
//            verticalArrangement = Arrangement.spacedBy(10.dp)
//
//        ) {
//            items(conversationList) { message ->
//                if (message.startsWith("AI: ")) {
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.Start
//                    ) {
//                        Card(
//                            colors = CardDefaults.cardColors(
//                                containerColor = Color.Black,
//                            ),
//                            modifier = Modifier
//                                .padding(5.dp)
//                        ) {
//                            MarkdownText(
//                                markdown = message, // AI's message
//                                style = TextStyle(
//                                    color = Color.White,
//                                    fontSize = 16.sp
//                                ),
//                                modifier = Modifier.padding(10.dp)
//                            )
//                        }
//                    }
//                }
//                else {
//
//                    // User's message aligned to the right
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.End // Align user messages to the end (right)
//                    ) {
//                        Card(
//                            colors = CardDefaults.cardColors(
//                                containerColor = Color(0xFF0C6E63),
//                            ),
//                            modifier = Modifier
//                                .padding(5.dp)
//                        ) {
//                            Text(
//                                text = message,
//                                modifier = Modifier
//                                    .padding(10.dp),
//                                style = TextStyle(
//                                    color = Color.White,
//                                    fontSize = 16.sp
//                                ),
//                            )
//                        }
//                    }
//                }
//            }
//        }
//
//        // Show loading indicator when waiting for AI response
//        if (uiState is UiState.Loading) {
//            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
//        }
//        if (uiState is UiState.Chart) {
//            DonutChartComposable(pieChartData = (uiState as UiState.Chart).pieData)
//        }
//
//        // User prompt input and send button
//        Row(modifier = Modifier.fillMaxWidth()
//            .clip(RoundedCornerShape(16.dp))
//            .background(
//                Color(0xFF26292D) // Background color
//            )// Clip content to rounded corners
//            .border(1.dp, Color(0xFF9860E4), MaterialTheme.shapes.large) // Add the border
//            .padding(vertical = 4.dp) // Inner padding
//            .padding(horizontal = 10.dp)
//            .padding(start=16.dp)// Horizontal padding for content
//            .height(60.dp)
//
//
//        ) {
//            BasicTextField(
//                value = promptText.value,
//                onValueChange = { promptText.value = it },
//                modifier = Modifier
//                    .weight(1f),
//                textStyle = TextStyle(
//                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
//                    color = Color.White
//                ),
//                decorationBox = { innerTextField ->
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize(),
//                        contentAlignment = Alignment.CenterStart
//                    ) {
//                        if (promptText.value.text.isEmpty()) {
//                            Text(
//                                text = "Ask anything ...",
//                            )
//                        }
//                        innerTextField()
//                    }
//                }
//            )
//
//            IconButton(
//                onClick = {
//                    val userPrompt = promptText.value.text
//
//                    if (userPrompt.isNotBlank()) {
//                        conversationList.add("You: $userPrompt")
//                        bakingViewModel.sendPromptWithExpenses(expenseList, userPrompt)
//                        promptText.value = TextFieldValue("")
//                    }
//                    keyboardController?.hide()
//                },
//                enabled = promptText.value.text.isNotEmpty(),
//                modifier = Modifier
//                    .align(Alignment.CenterVertically)
//                    .padding(start = 8.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.Send,
//                    contentDescription = "Send",
//                    tint = Color.White
//                )
//            }
//        }
//    }



