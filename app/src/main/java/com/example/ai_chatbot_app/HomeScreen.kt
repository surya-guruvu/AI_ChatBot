package com.example.ai_chatbot_app

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(context: Context, navController: NavController){
    val text by remember { mutableStateOf("Hello, Welcome AI ChatBot!") }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = text)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            navController.navigate("chat")
        }) {
            Text(text = "Go to Chat Screen")
        }

        Button(onClick = {
            navController.navigate("contact")
        }) {
            Text(text = "Go to Contact Screen")
        }

        PhoneCallButton(context,"8688813171")
    }
}