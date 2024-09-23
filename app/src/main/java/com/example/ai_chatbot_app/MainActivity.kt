package com.example.ai_chatbot_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ai_chatbot_app.ui.theme.AI_ChatBot_APPTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.ai.client.generativeai.GenerativeModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApp()
        }
    }



}

@Composable
fun MyApp(viewModel: MyNewViewModel = viewModel()){
    var text by remember { mutableStateOf("Hello, Jetpack Compose!") }
    val viewModelText by viewModel.text.collectAsState()

    viewModel.fetchContent("Call surya")

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = text)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { text = "Button Clicked!" }) {
            Text("Click Me")
        }
        Counter()
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {viewModel.onButtonClick()}) {
            Text("Click Me")
        }
        Text(viewModelText)

        OpenTempActivity()
    }

}


//State hoisting
@Composable
fun Counter(){
    var count by remember { mutableIntStateOf(0) }
    val derivedCount by remember { derivedStateOf { count*2 } }
    CounterButton(count = count, onCountChange = {newCount -> count=newCount})
    Text("Derived Count: $derivedCount")
}

@Composable
fun CounterButton(count:Int,onCountChange:(Int)-> Unit){
    Button(onClick = {onCountChange(count+1)}) {
        Text("Count: $count")
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

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun AppPreview() {
    AI_ChatBot_APPTheme {
        MyApp()
    }
}
