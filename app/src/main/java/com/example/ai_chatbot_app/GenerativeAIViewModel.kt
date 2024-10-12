package com.example.ai_chatbot_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

class  GenerativeAIViewModelFactory(private val alarmViewModel: AlarmViewModel): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(GenerativeAIViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return  GenerativeAIViewModel(alarmViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}

class GenerativeAIViewModel(private val alarmViewModel: AlarmViewModel): ViewModel() {


    private val template = """
        You are an AI assistant that tailors responses based on the type of user input:
        
        For requests: If the user makes a specific request (e.g., "book a cab" or "set an alarm"), respond with:
        
        Format: "Request: The request is to {specific request}."
        Examples:
        
        User: "book a cab"
        Assistant: "Request: The request is to book a cab."
        User: "set an alarm for 10 pm"
        Assistant: "Request: The request is to set an alarm for 22:00."
        User: "set an alarm at morning 10 o'clock"
        Assistant: "Request: The request is to set an alarm for 10:00."
        For general questions: If the user asks a non-actionable question or makes a general statement, respond appropriately with relevant information.
        
        Example:
        
        User: "What’s the weather like?"
        Assistant: "The weather today is sunny with mild temperatures."
        For invalid or unserviceable requests: If the request is unclear or cannot be fulfilled, respond with:
        
        Format: "Err: [explanation]"
        Example:
        
        User: "set an alarm for 25:00"
        Assistant: "Err: The time is invalid; please specify a valid time."
        Handling ambiguous times for alarms: If the time provided is valid but lacks clarity (e.g., no AM/PM), you may choose the time based on context and confirm with the user.
        
        Example:
        
        User: "set an alarm for 10 o’clock"
        Assistant: "Request: The request is to set an alarm for 10:00."
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

            if(response.text?.contains("Request: The request is to set an alarm for") == true){
                setAlarm(response.text!!)
            }
        }
    }

    private fun setAlarm(response: String){
        val timeRegex = Regex("(\\d{1,2}:\\d{2})")
        val matchResult = timeRegex.find(response)

        val time = matchResult?.groups?.get(1)?.value

        time?.let {
            val (hh, mm) = time.split(":").map { it.toInt() }

            alarmViewModel.setAlarm(hh,mm,"message")
        }


        println("Extracted time: $time")
    }

}