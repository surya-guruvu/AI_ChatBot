package com.example.ai_chatbot_app

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse


class MainActivity : ComponentActivity() {

    private val alarmViewModel:AlarmViewModel by viewModels()
    private val phoneCallViewModel:PhoneCallViewModel by viewModels()
    private val spotifyViewModel:SpotifyViewModel by  viewModels()

    private val contactsMap = mutableMapOf<String, String>()
    private var contactList = mutableListOf<String>()



    private val clientId = BuildConfig.SPOTIFY_CLIENT_ID
    private val redirectUri = "aichatbot://callback"
    private var spotifyAppRemote: SpotifyAppRemote? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val builder = AuthorizationRequest.Builder(spotifyViewModel.clientId, AuthorizationResponse.Type.TOKEN, redirectUri)
        builder.setScopes(arrayOf("user-read-private","streaming","playlist-read","user-read-currently-playing","user-read-playback-state","user-read-recently-played"))
        val request = builder.build()

        // Launch the Spotify login activity
        val authIntent = AuthorizationClient.createLoginActivityIntent(this,request)
        spotifyAuthResultLauncher.launch(authIntent)


        setContent {
            MyApp(alarmViewModel,phoneCallViewModel,spotifyViewModel)
        }
    }





    override fun onStart() {
        super.onStart()

        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(false)
            .build()

        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("MainActivity", "Connected! Yay!")
                // Now you can start interacting with App Remote
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
                // Something went wrong when attempting to connect! Handle errors here
            }
        })
    }

    override fun onStop() {
        super.onStop()
        AuthorizationClient.clearCookies(applicationContext)
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }

    }


    @Composable
    fun MyApp(alarmViewModel: AlarmViewModel, phoneCallViewModel: PhoneCallViewModel, spotifyViewModel: SpotifyViewModel){
        val navController = rememberNavController()
        val context = LocalContext.current
        val alarmData by alarmViewModel.alarmData.observeAsState()
        val phoneNumber by phoneCallViewModel.phoneNumber.observeAsState()
        val spotifyPlay by spotifyViewModel.spotifyPlay.observeAsState()
        val spotifyAccessToken by spotifyViewModel.spotifyAccessToken.observeAsState()

        val contactLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) {
            isGranted: Boolean ->
            if(isGranted){
                contactList = getContacts(context, contactsMap)
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        LaunchedEffect(Unit) {
            contactLauncher.launch(android.Manifest.permission.READ_CONTACTS)
        }

        alarmData?.let {
            setSystemAlarm(context, it.hour, it.minutes, it.message)
            alarmViewModel.resetAlarm()
        }

        phoneNumber?.let {
            phoneCall(context,it)
            phoneCallViewModel.resetPhoneNumber()
        }

        spotifyPlay?.let {
            spotifyAccessToken?.let { it1 ->
                fetchRecentlyPlayedTracks(it1, object : TrackUrisCallback {
                    override fun onTrackUrisFetched(trackUris: List<String>) {
                        // Step 2: Select a random track from the list
                        if (trackUris.isNotEmpty()) {
                            val randomTrackUri = trackUris.random()

                            // Step 3: Play the selected random track
                            spotifyAppRemote?.let { it2 -> playTrack(randomTrackUri, it2) }
                        } else {
                            Log.e("MainActivity", "No recently played tracks found.")
                        }
                    }
                })
            }
            spotifyViewModel.resetSpotifyPlay()
        }

        val generativeAIViewModel:GenerativeAIViewModel = viewModel(
            factory = GenerativeAIViewModelFactory(alarmViewModel, phoneCallViewModel, spotifyViewModel)
        )

        NavHost(navController = navController, startDestination = "home") {
            composable("home") { HomeScreen(context,navController) }
            composable("chat") { ChatScreen(generativeAIViewModel) }
            composable("contact") { ContactScreen(context) }
        }

    }

    // Define the result launcher for starting the authorization activity
    private val spotifyAuthResultLauncher = registerForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        result ->
        if(result.resultCode == RESULT_OK){
            val response = AuthorizationClient.getResponse(result.resultCode, result.data)
            Log.d("SpotifyAuth", response.type.toString())
            handleAuthResponse(response)
        }
        else{
            Log.e("SpotifyAuth", "Authorization failed")
            Toast.makeText(this, "Spotify authorization failed. Please try again.", Toast.LENGTH_LONG).show()
        }
    }


    private fun handleAuthResponse(response: AuthorizationResponse) {
        when (response.type) {
            AuthorizationResponse.Type.TOKEN -> {
                spotifyViewModel.setSpotifyAccessToken(response.accessToken)
                Log.d("SpotifyAuth", "Access Token: ${response.accessToken}")
            }
            AuthorizationResponse.Type.ERROR -> {
                Log.e("SpotifyAuth", "Error: ${response.error}")
            }
            else -> {
                // Handle other cases
            }
        }
    }


}


