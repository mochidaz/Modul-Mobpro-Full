package com.example.mobprostuff.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

import com.example.mobprostuff.model.Character

import com.example.mobprostuff.network.API
import com.example.mobprostuff.utils.toMultipartBody

class MainViewModel : ViewModel() {

    private val _characters = MutableStateFlow<MutableList<Character>?>(mutableListOf())
    val characters: MutableStateFlow<MutableList<Character>?> = _characters

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun fetchCharacters() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = API.retrofitService.getCharacters()
                Log.d("MainViewModel", response.data.toString())
                if (response.status == "success") {
                    _characters.value = response.data?.toMutableList()
                } else {
                    _error.value = response.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to fetch characters"
            } finally {
                _loading.value = false
            }
        }
    }

    fun addCharacter(name: String, description: String, imageUrl: Bitmap, userId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = API.retrofitService.addCharacter(name.toRequestBody("text/plain".toMediaTypeOrNull()),
                    description.toRequestBody("text/plain".toMediaTypeOrNull()),
                    imageUrl.toMultipartBody(), userId)
                if (response.status == "success") {
                    fetchCharacters()
                } else {
                    _error.value = response.message ?: "Failed to add character"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add character"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateCharacter(id: Int, name: String, description: String, imageUrl: Bitmap?, userId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = API.retrofitService.updateCharacter(
                    id,
                    name.toRequestBody("text/plain".toMediaTypeOrNull()),
                    description.toRequestBody("text/plain".toMediaTypeOrNull()),
                    imageUrl?.toMultipartBody(),
                    userId,
                )
                if (response.status == "success") {
                    fetchCharacters()
                } else {
                    _error.value = response.message ?: "Failed to update character"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update character"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteCharacter(id: Int, userId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = API.retrofitService.deleteCharacter(id, userId)
                if (response.status == "success") {
                    fetchCharacters()
                } else {
                    _error.value = response.message ?: "Failed to delete character"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete character"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = ""
    }
}