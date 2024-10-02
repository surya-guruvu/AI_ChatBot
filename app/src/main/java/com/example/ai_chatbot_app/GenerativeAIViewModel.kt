package com.example.ai_chatbot_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class Sender{
    USER,
    ASSISTANT
}

data class Message(val sender: Sender, val message: String)

class GenerativeAIViewModel: ViewModel() {
    private val template = """
    You are an AI assistant that responds differently based on the type of user input:
    
    For requests: If the user makes a request (e.g., "book a cab" or "set an alarm"), respond with "Request: The request is to {specific request}." Ensure to format time correctly with AM/PM when applicable.
    
    For general questions: If the user asks a general question or makes a non-actionable statement, respond with a normal answer to the query.
    
    For example:
    
    User: "book a cab"
    
    Assistant: "Request: The request is to book a cab."
    
    User: "set an alarm for 10:00 AM"
    
    Assistant: "Request: The request is to set an alarm for 10:00 AM."
    
    User: "What is the weather like?"
    
    Assistant: "The weather today is sunny with mild temperatures."
    
    For invalid or unserviceable requests, respond with "Err:", followed by an apology and the reason why the request cannot be fulfilled.
    
    User: {user_input}
    Assistant:
    """.trimIndent()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> get()  = _messages //TypeCast

    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    fun requestAssistant(userInput: String){

        val userMessage = Message(Sender.USER, userInput);
        _messages.value += userMessage

        //Fetch AI response
        viewModelScope.launch {
            val prompt = template.replace("{user_input}",userInput)
            val response = generativeModel.generateContent(prompt)

            val assistantMessage = Message(Sender.ASSISTANT, response.text.toString())
            _messages.value += assistantMessage
        }
    }

}