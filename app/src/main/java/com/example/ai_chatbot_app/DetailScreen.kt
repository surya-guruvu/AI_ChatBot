package com.example.ai_chatbot_app

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

@Composable
fun DetailsScreen(navController: NavController){

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Details Screen")

        Button(onClick = {
            navController.popBackStack()
        }) {
            Text(text = "Go Back to Home")
        }
    }

}


@Composable
fun OpenTempActivity(){
    val context = LocalContext.current

    Button(onClick = {
        try {
            val intent = Intent(context, TempActivity::class.java)
            context.startActivity(intent)
        }
        catch (e: Exception){
            Log.e("MyApp", "Error starting TempActivity", e)
        }
    }) {
        Text("Open Temp Activity")
    }
}