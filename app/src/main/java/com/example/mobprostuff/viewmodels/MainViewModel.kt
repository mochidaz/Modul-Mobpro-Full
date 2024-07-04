package com.example.mobprostuff.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobprostuff.model.Track
import com.example.mobprostuff.network.NazrinAPI
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainViewModel : ViewModel() {
    private val _searchResults = MutableStateFlow<List<Track>>(emptyList())
    val searchResults: StateFlow<List<Track>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun search(url: String, searchQuery: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _error.value = null
            try {
                val response = NazrinAPI.retrofitService.search(url, searchQuery).execute()
                Log.d("MainViewModel", "Response: $response")
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        parseStreamedResponse(body)
                    }
                } else {
                    val errorMessage = "Error: ${response.code()} ${response.message()}"
                    Log.e("MainViewModel", errorMessage)
                    _error.value = errorMessage
                }
            } catch (e: IOException) {
                val errorMessage = "Network error: ${e.message}"
                Log.e("MainViewModel", errorMessage)
                _error.value = errorMessage
            } catch (e: HttpException) {
                val errorMessage = "HTTP error: ${e.code()} ${e.message}"
                Log.e("MainViewModel", errorMessage)
                _error.value = errorMessage
            } catch (e: Exception) {
                val errorMessage = "Error: ${e.message}"
                Log.e("MainViewModel", errorMessage)
                _error.value = errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun parseStreamedResponse(body: ResponseBody) {
        try {
            val reader = body.charStream().buffered()
            var line: String?
            val newResults = mutableListOf<Track>()
            while (reader.readLine().also { line = it } != null) {
                line?.let {
                    val jsonObject = JsonParser.parseString(it).asJsonObject
                    val dataJson = jsonObject.getAsJsonObject("data")
                    val track = Gson().fromJson(dataJson, Track::class.java)
                    Log.d("MainViewModel", "Track: $track")
                    newResults.add(track)
                }
                _searchResults.value = _searchResults.value + newResults
            }
        } catch (e: Exception) {
            val errorMessage = "Parsing error: ${e.message}"
            Log.e("MainViewModel", errorMessage)
            _error.value = errorMessage
        }
    }
}