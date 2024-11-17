package com.example.ai_chatbot_app
import android.util.Log
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT

data class SpotifyUserResponse(
    val id: String,
    val href: String,
    val uri: String
)

data class Track(
    val uri: String
)

data class Item(
    val track: Track
)

data class UserRecentlyPlayed(
    val items: List<Item>
)



interface SpotifyAPIService {

    @GET("/v1/me")
    suspend fun getUserDetails(
        @Header("Authorization") accessToken: String
    ): Response<SpotifyUserResponse>

    @GET("/v1/me/player/recently-played")
    suspend fun getRecentlyPlayedTracks(
        @Header("Authorization") accessToken: String
    ): Response<UserRecentlyPlayed>

    @PUT("v1/me/player/pause")
    suspend fun pausePlayback(
        @Header("Authorization") accessToken: String
    ): Response<Void>

    @PUT("v1/me/player/play")
    suspend fun playPlayback(
        @Header("Authorization") accessToken: String
    ): Response<Void>

}

object RetrofitInstance {
    private const val BASE_URL = "https://api.spotify.com/"

    val api: SpotifyAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyAPIService::class.java)
    }
}

fun fetchUserProfile(accessToken: String) {
    CoroutineScope(Dispatchers.IO).launch {
        Log.d("Spotify", "$accessToken")
        val response = RetrofitInstance.api.getUserDetails("Bearer $accessToken")
        if (response.isSuccessful){
            val userProfile = response.body()
            val userId = userProfile?.id
            Log.d("Spotify", "User: $userId")
        }
        else{
            Log.e("Spotify", "Error fetching user profile, response code: ${response.code()}")
        }
    }
}

interface TrackUrisCallback {
    fun onTrackUrisFetched(trackUris: List<String>)
}

fun fetchRecentlyPlayedTracks(accessToken: String, callback: TrackUrisCallback) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = RetrofitInstance.api.getRecentlyPlayedTracks("Bearer $accessToken")

        if (response.isSuccessful) {
            val recentlyPlayedTracks = response.body()
            val trackUris = recentlyPlayedTracks?.items?.map { it.track.uri }
            Log.d("Spotify", "Recently played tracks: $trackUris")

            withContext(Dispatchers.Main){
                callback.onTrackUrisFetched(trackUris ?: emptyList())
            }
        }
        else {
            Log.e("Spotify", "Error fetching recently played tracks, response code: ${response.code()}")
        }
    }
}

fun playTrack(uri:String, spotifyAppRemote: SpotifyAppRemote){
    spotifyAppRemote.playerApi.play(uri)
}

fun pausePlayback(accessToken: String){
    CoroutineScope(Dispatchers.IO).launch {
        val response = RetrofitInstance.api.pausePlayback("Bearer $accessToken")
        if (response.isSuccessful) {
            Log.d("Spotify", "Playback paused")
        } else {
            Log.e("Spotify", "Error pausing playback, response code: ${response.code()}")
        }
    }
}

fun resumePlayback(accessToken: String){
    CoroutineScope(Dispatchers.IO).launch {
        val response = RetrofitInstance.api.playPlayback("Bearer $accessToken")
        if (response.isSuccessful) {
            Log.d("Spotify", "Playback resumed")
        } else {
            Log.e("Spotify", "Error resuming playback, response code: ${response.code()}")
        }
    }
}