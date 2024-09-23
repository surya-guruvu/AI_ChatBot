package com.example.ai_chatbot_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyNewViewModel:ViewModel() {
    private var _text = MutableStateFlow("This text is from view model!")
    val text: StateFlow<String> = _text

    val template = """
    You are an AI chatbot assistant that serves some user needs. 
    When someone says "book a cab", you should respond with: "The Request is for Booking Cab".
    When someone says "set alarm at 10:00 AM", you should respond with: "The Request is to Set Alarm at 10:00 AM".
    
    User: {user_input}
    Assistant:
    """

    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = "AIzaSyAVlabW_rfKdRHumc8VaeA_AVtswKSOwMU"
    )

    fun onButtonClick(){
        _text.value = "This text is from view model, after button clicked"
    }

    fun fetchContent(userInput: String) {
        viewModelScope.launch {
            val prompt = template.replace("{user_input",userInput)
            val response = generativeModel.generateContent(prompt)
            // Update UI or handle response
            println(response.text)
        }
    }
}