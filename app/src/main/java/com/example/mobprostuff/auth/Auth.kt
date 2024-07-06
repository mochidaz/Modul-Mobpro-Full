package com.example.mobprostuff.auth

import android.content.Context
import androidx.credentials.GetCredentialResponse
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import androidx.datastore.dataStore
import com.example.mobprostuff.BuildConfig
import com.example.mobprostuff.R
import com.example.mobprostuff.model.User
import com.example.mobprostuff.network.UserDataStore
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
suspend fun signIn(context: Context, dataStore: UserDataStore) {
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val credentialManager = CredentialManager.create(context)
        Log.d("Auth - SIGN-IN", "Requesting credential")
        val result = credentialManager.getCredential(context, request)
        Log.d("Auth - SIGN-IN", "Credential received")
        handleSignIn(result, dataStore)
    } catch (e: NoCredentialException) {
        throw NoCredentialException(context.getString(R.string.no_credentials))
    } catch (e: Exception) {
        Log.e("Auth - SIGN-IN", "Error: ${e.message}")
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
private suspend fun handleSignIn(result: GetCredentialResponse, dataStore: UserDataStore) {
    val credential = result.credential

    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            val userName = googleId.displayName ?: ""
            val userEmail = googleId.id
            val photoUrl = googleId.profilePictureUri.toString()
            dataStore.saveData(User(userName, userEmail, photoUrl))
        } catch (e: Exception) {
            Log.e("Auth - HANDLE-SIGN-IN", "Error: ${e.message}")
        }
    } else {
        Log.e("Auth - HANDLE-SIGN-IN", "Error: Invalid credential type")
    }
}

suspend fun signOut(context: Context, dataStore: UserDataStore) {
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        dataStore.saveData(User())
    } catch (e: Exception) {
        Log.e("Auth - SIGN-OUT", "Error: ${e.message}")
    }
}