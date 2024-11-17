package com.example.ai_chatbot_app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED

fun phoneCall(context: Context,number: String){
    val callIntent = Intent(Intent.ACTION_CALL).apply {
        data = Uri.parse("tel:$number")
    }

    // Check if CALL_PHONE permission is granted
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PERMISSION_GRANTED) {
        context.startActivity(callIntent)
    } else {
        Toast.makeText(context, "Call permission not granted", Toast.LENGTH_SHORT).show()
    }
}



@Composable
fun PhoneCallButton(context: Context,number: String) {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            phoneCall(context,number)
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Button(
        onClick = {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CALL_PHONE
                ) != PERMISSION_GRANTED
            ) {
                launcher.launch(Manifest.permission.CALL_PHONE)
            } else {
                phoneCall(context,number)
            }
        }
    ) {
        Text("Make Call")
    }
}