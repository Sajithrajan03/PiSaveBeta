
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.FunctionResponsePart
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.defineFunction
import com.sajithrajan.pisave.BuildConfig
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun LightControlChat() {
    // Mock function to simulate setting light values
    suspend fun setLightValues(
        brightness: Int,
        colorTemp: String
    ): JSONObject {
        return JSONObject().apply {
            put("brightness", brightness)
            put("colorTemperature", colorTemp)
        }
    }

    // Define the tool for controlling lights
    val lightControlTool = defineFunction(
        name = "setLightValues",
        description = "Set the brightness and color temperature of a room light.",
        Schema.int("brightness", "Light level from 0 to 100. Zero is off and 100 is full brightness."),
        Schema.str("colorTemperature", "Color temperature of the light fixture which can be `daylight`, `cool`, or `warm`.")
    ) { brightness, colorTemp ->
        setLightValues(brightness.toInt(), colorTemp)
    }

    // State to hold the response text and raw JSON
    var responseText by remember { mutableStateOf("Awaiting response...") }
    var rawJsonResponse by remember { mutableStateOf("No raw JSON response yet.") }

    val coroutineScope = rememberCoroutineScope()

    // Generative model setup
    val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey,
        tools = listOf(Tool(listOf(lightControlTool)))
    )

    // Coroutine to handle the light control chat interaction
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                // Start the chat and send the prompt
                val chat = generativeModel.startChat()
                val prompt = "Dim the lights so the room feels cozy and warm."

                // Send the message and get the response
                val response: GenerateContentResponse = chat.sendMessage(prompt)

                // Check if response has text or function calls
                response.text?.let {
                    responseText = it  // Display the AI-generated text
                } ?: run {
                    responseText = "No textual response from the model."
                }

                // Optionally handle function calls
                response.functionCalls.let { functionCallsList ->
                    functionCallsList.forEach { functionCall ->
                        println("Function Call: $functionCall")

                        val functionName = functionCall.name ?: throw IllegalStateException("Function call has no name")

                        val matchedFunction = generativeModel.tools?.flatMap { it.functionDeclarations }
                            ?.firstOrNull { it.name == functionName }
                            ?: throw IllegalStateException("Function not found: $functionName")

                        // Execute the function and handle the response
                        val apiResponse: JSONObject = matchedFunction.execute(functionCall)

                        // Send the API response back to the generative model
                        chat.sendMessage(
                            content(role = "function") {
                                part(FunctionResponsePart(functionName, apiResponse))
                            }
                        )
                    }
                }

                // Update rawJsonResponse for debugging purposes (assuming response can be serialized)
                rawJsonResponse = response.toString()  // Convert the response to a string format

            } catch (e: Exception) {
                responseText = "Error: ${e.message}"
                rawJsonResponse = "Error while fetching response: ${e.message}"
            }
        }
    }

    // Composable UI to display the raw JSON response and the processed response
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Display the model's response
        Text(
            text = responseText,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Display the raw JSON response
        Text(
            text = "Raw JSON Response: $rawJsonResponse",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
