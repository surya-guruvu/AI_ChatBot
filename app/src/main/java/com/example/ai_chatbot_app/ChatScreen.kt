package com.example.ai_chatbot_app

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ChatScreen(generativeAIViewModel: GenerativeAIViewModel) {
    var userInput by remember { mutableStateOf("") }
    val messages by generativeAIViewModel.messages.collectAsState()

    // Create the ActivityResultLauncher using rememberLauncherForActivityResult
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Get the data from the result and extract the recognized text
                val data = result.data
                val speechResult = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                userInput =
                    speechResult?.get(0).toString() // Update state with recognized text
            }
        }
    )

    fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
            )
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something...")
        }
        speechRecognizerLauncher.launch(intent)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Bottom
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().weight(1f).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(messages.size) {
                MessageView(messages[it])
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it },
                label = { Text("Type your message here") },
                modifier = Modifier.weight(1f),
                trailingIcon = {
                    IconButton(onClick = {
                        startVoiceRecognition()
                    }) {
                        Icon(imageVector = Icons.Filled.Mic, contentDescription = "Mic")
                    }
                }
            )
            IconButton(onClick = {
                if(userInput.isNotBlank()){
                    generativeAIViewModel.requestAssistant(userInput)
                    userInput = ""
                }
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
fun MessageView(message: Message){

    val alignment = when(message.sender){
        Sender.USER -> Alignment.CenterEnd
        Sender.ASSISTANT -> Alignment.CenterStart
    }

    Box(
        modifier = Modifier.fillMaxWidth()
            .padding(4.dp),
        contentAlignment = alignment


    ) {
        Text(text = message.message,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.background(
                when(message.sender){
                    Sender.USER -> MaterialTheme.colorScheme.primaryContainer
                    Sender.ASSISTANT -> MaterialTheme.colorScheme.secondaryContainer
                },
                shape = RoundedCornerShape(8.dp)
            )
                .padding(8.dp))

    }
}