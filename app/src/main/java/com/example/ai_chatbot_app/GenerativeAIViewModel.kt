package com.example.ai_chatbot_app

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

enum class Sender{
    USER,
    ASSISTANT
}

data class Message(val sender: Sender, val message: String)

class  GenerativeAIViewModelFactory(
    private val alarmViewModel: AlarmViewModel,
    private val phoneCallViewModel: PhoneCallViewModel,
    private val spotifyViewModel: SpotifyViewModel
    ): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(GenerativeAIViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return  GenerativeAIViewModel(alarmViewModel,phoneCallViewModel,spotifyViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}

class GenerativeAIViewModel(
    private val alarmViewModel: AlarmViewModel,
    private val phoneCallViewModel: PhoneCallViewModel,
    private val spotifyViewModel: SpotifyViewModel
): ViewModel() {


    private val template = """
        Role: You are an AI assistant that interprets and responds to user inputs based on their type. Your responses should be clear, concise, and appropriately formatted. Follow the guidelines below:
        
        1. Handling Specific Requests
        When the user makes a specific actionable request (e.g., "book a cab," "set an alarm", "phone call someone", "play some song"), respond using the following format:
        
        Format: "Request: The request is to {action}."
        Examples:
        
        User: "book a cab"
        Assistant: "Request: The request is to book a cab."
        
        User: "Play some song"
        Assistant: "Request: The request is to play some song."
        
        User: "set an alarm for 10 pm"
        Assistant: "Request: The request is to set an alarm for 22:00."
        
        User: "set an alarm at morning 10 o'clock"
        Assistant: "Request: The request is to set an alarm for 10:00."
        
        User: "Call surya"
        Assistant: "Request: The request is to make a call to surya"
        
        User: "Call sai"
        Assistant: "Request: The request is to make a call to sai"
        
        2. Responding to General Questions
        If the user asks a general question or makes a non-actionable statement, respond with appropriate information based on the query.
        
        Example:
        
        User: "What’s the weather like?"
        Assistant: "The weather today is sunny with mild temperatures."
        3. Handling Invalid or Unserviceable Requests
        When the request is unclear, invalid, or cannot be fulfilled, respond with an error message explaining the issue:
        
        Format: "Err: [explanation]"
        Example:
        
        User: "set an alarm for 25:00"
        Assistant: "Err: The time is invalid; please specify a valid time."
        4. Dealing with Ambiguous Times
        For requests involving ambiguous times (e.g., missing AM/PM or unclear context), infer the most likely time, confirm it, and proceed.
        
        Example:
        
        User: "set an alarm for 10"
        Assistant: "Please mention AM or PM explicitly, e.g., 10 am/ 10 pm"
        Now, respond to the following:
        
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

        val userMessage = Message(Sender.USER, userInput)
        _messages.value += userMessage

        //Fetch AI response
        viewModelScope.launch {
            val prompt = template.replace("{user_input}",userInput)

            try {
                val response = generativeModel.generateContent(prompt)

                val assistantMessage = Message(Sender.ASSISTANT, response.text.toString())
                _messages.value += assistantMessage

                if (response.text?.contains("Request: The request is to set an alarm for") == true) {
                    setAlarm(response.text!!)
                }
                else if (response.text?.contains("Request: The request is to make a call to") == true) {
                    makePhoneCall(response.text!!)
                }
                else if (response.text?.contains("Request: The request is to play some song") == true) {
                    playSpotify()
                }

            }
            catch (e: IOException) {
                // Handle network error
                val errorMessage = Message(Sender.ASSISTANT, "Err: Please check your internet connection.")
                _messages.value += errorMessage
            } catch (e: Exception) {
                // Handle other possible exceptions
                Log.d("View Model",e.toString())
                val errorMessage = Message(Sender.ASSISTANT, "Err: Something went wrong. Please try again.")
                _messages.value += errorMessage
            }
        }
    }

    private fun makePhoneCall(response: String){

        var callerName = response.substringAfterLast(delimiter = "Request: The request is to make a call to ", missingDelimiterValue = "Delimiter Not found")
        callerName = callerName.substringBeforeLast(delimiter = ".", missingDelimiterValue = "Delimiter Not found")

        Log.d("Caller Name",callerName)

        phoneCallViewModel.setPhoneNumber("8688813171")

    }

    private fun playSpotify(){
        spotifyViewModel.setSpotifyPlay("play")
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