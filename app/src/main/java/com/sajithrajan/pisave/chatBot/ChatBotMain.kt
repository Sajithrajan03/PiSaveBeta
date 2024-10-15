package com.sajithrajan.pisave

//import com.sajithrajan.pisave.DonutChartComposable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sajithrajan.pisave.dataBase.Expense
import com.sajithrajan.pisave.dataBase.ExpenseEvent
import com.sajithrajan.pisave.dataBase.ExpenseState
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch

@Composable
fun ChatBotScreen(
//    bakingViewModel: BakingViewModel = viewModel(),
    expenseList: List<Expense>,
    state: ExpenseState,
    onEvent: (ExpenseEvent) -> Unit
) {
    val promptText = remember { mutableStateOf(TextFieldValue("")) }
    val conversationList = remember { mutableStateListOf<String>() }
//    val uiState by bakingViewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)

        ) {
            items(conversationList) { message ->
                if (message.startsWith("AI: ")) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Black,
                            ),
                            modifier = Modifier
                                .padding(5.dp)
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
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End // Align user messages to the end (right)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF0C6E63),
                            ),
                            modifier = Modifier
                                .padding(5.dp)
                        ) {
                            Text(
                                text = message,
                                modifier = Modifier
                                    .padding(10.dp),
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 16.sp
                                ),
                            )
                        }
                    }
                }
            }
        }

        // Show loading indicator when waiting for AI response
//        if (uiState is UiState.Loading) {
//            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
//        }
//        if (uiState is UiState.Chart) {
//            DonutChartComposable(pieChartData = (uiState as UiState.Chart).pieData)
//        }

        // User prompt input and send button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Color(0xFF26292D) // Background color
                )// Clip content to rounded corners
                .border(1.dp, Color(0xFF9860E4), MaterialTheme.shapes.large) // Add the border
                .padding(vertical = 4.dp) // Inner padding
                .padding(horizontal = 10.dp)
                .padding(start = 16.dp)// Horizontal padding for content
                .height(60.dp)


        ) {
            BasicTextField(
                value = promptText.value,
                onValueChange = { promptText.value = it },
                modifier = Modifier
                    .weight(1f),
                textStyle = TextStyle(
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    color = Color.White
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (promptText.value.text.isEmpty()) {
                            Text(
                                text = "Ask anything ...",
                            )
                        }
                        innerTextField()
                    }
                }
            )

            IconButton(
                onClick = {
                    val userPrompt = promptText.value.text

                    if (userPrompt.isNotBlank()) {
                        conversationList.add("You: $userPrompt")
//                        bakingViewModel.sendPromptWithExpenses(expenses, userPrompt)
                        coroutineScope.launch {
                            val response = getTransactionStatus(userPrompt,expenseList)
                            conversationList.add("AI: $response")
                            promptText.value = TextFieldValue("")
                        }

                    }
                    keyboardController?.hide()
                },
                enabled = promptText.value.text.isNotEmpty(),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }

//    LaunchedEffect(uiState) {
//        when (uiState) {
//            is UiState.Success -> conversationList.add("AI: ${(uiState as UiState.Success).outputText}")
//            is UiState.Error -> conversationList.add("Error: ${(uiState as UiState.Error).errorMessage}")
//            else -> Unit
//        }
//    }
}