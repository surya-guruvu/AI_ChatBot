package com.example.ai_chatbot_app

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import android.Manifest
import androidx.compose.runtime.MutableState

private val contactsMap1 = mutableMapOf<String, String>()

@Composable
fun ContactScreen(context: Context){

    var contactList by remember { mutableStateOf(listOf<String>()) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        isGranted: Boolean ->
        if(isGranted){
            contactList = getContacts(context, contactsMap1)
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_CONTACTS
                    ) != PERMISSION_GRANTED
                ) {
                    launcher.launch(Manifest.permission.READ_CONTACTS)
                } else {
                    contactList = getContacts(context, contactsMap1)
                }
            }
        ) {
            Text(text = "Load Contacts")
        }

        Spacer(modifier = Modifier.height(20.dp))

        contactList.forEach { contact ->
            Text(
                text = contact,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun getContacts(context: Context,contactsMap: MutableMap<String, String>): MutableList<String> {

    val contacts = mutableListOf<String>()
    val resolver: ContentResolver = context.contentResolver
    val cursor: Cursor? = resolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null, null, null, null
    )

    cursor?.use {
        val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        while (cursor.moveToNext()) {
            val name = cursor.getString(nameIndex)
            val number = cursor.getString(numberIndex)
            contacts.add("$name: $number")

            contactsMap[name.lowercase()] = number
        }
    }
    return contacts
}
