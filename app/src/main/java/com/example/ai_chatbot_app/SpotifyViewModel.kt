package com.example.ai_chatbot_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SpotifyViewModel: ViewModel() {

    val clientId = "6a02699641df4c7bb6c33984745831f6"
    val redirectUri = "aichatbot://callback"

    private val _spotifyAccessToken = MutableLiveData<String?>()
    val spotifyAccessToken: LiveData<String?> get()= _spotifyAccessToken

    private val _spotifyPlay = MutableLiveData<String?>()
    val spotifyPlay: LiveData<String?> get()= _spotifyPlay

    private val _spotifyResume = MutableLiveData<String?>()
    val spotifyResume: LiveData<String?> get()= _spotifyResume

    private val _spotifyPause = MutableLiveData<String?>()
    val spotifyPause: LiveData<String?> get()= _spotifyPause

    fun setSpotifyAccessToken(token: String){
        _spotifyAccessToken.value = token
    }

    fun setSpotifyPlay(play: String){
        _spotifyPlay.value = play
    }

    fun resetSpotifyPlay(){
        _spotifyPlay.value = null
    }




}