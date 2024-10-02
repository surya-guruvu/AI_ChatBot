package com.example.ai_chatbot_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.example.ai_chatbot_app.ui.theme.AI_ChatBot_APPTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


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
fun MyApp(generativeAIViewModel: GenerativeAIViewModel = viewModel()){
    val navController = rememberNavController()
    val context = LocalContext.current


    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(context,navController) }
        composable("chat") { ChatScreen(generativeAIViewModel) }
    }
}


@Composable
fun HomeScreen(context: Context,navController: NavController){
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
    }

    AlarmScreen(context)
}

@Composable
fun AlarmScreen(context: Context,alarmViewModel: AlarmViewModel = viewModel()) {

    val alarmData by alarmViewModel.alarmData.observeAsState()

    alarmData?.let {
        setSystemAlarm(context,it.hour,it.minutes,it.message)
    }

    Column {
        // Button to set the alarm for a specific time
        Button(onClick = {
            alarmViewModel.setAlarm(23,45,"Testing Alarm")
        }) {
            Text("Set Alarm for 7:00 AM")
        }
    }
}

fun  setSystemAlarm(context: Context,hour: Int, minute: Int, message: String) {
    val alarmIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
        putExtra(AlarmClock.EXTRA_HOUR,hour)
        putExtra(AlarmClock.EXTRA_MINUTES,minute)
        putExtra(AlarmClock.EXTRA_MESSAGE,message)
        putExtra(AlarmClock.EXTRA_SKIP_UI,true)
    }

    context.startActivity(alarmIntent)
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
