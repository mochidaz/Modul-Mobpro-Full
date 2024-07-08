package com.example.mobprostuff.network

import com.example.mobprostuff.model.ResponseData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://char-api.kakashispiritnews.my.id/"

private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()

interface APIService {
    @Multipart
    @POST("character")
    suspend fun addCharacter(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part imageUrl: MultipartBody.Part,
        @Header("Authorization") userId: String,
    ): ResponseData

    @GET("character")
    suspend fun getCharacters(): ResponseData

    @GET("character/{id}")
    suspend fun getCharacter(
        @Path("id") id: Int
    ): ResponseData

    @GET("character/user/{user_id}")
    suspend fun getCharactersByUser(
        @Path("user_id") userId: Int
    ): ResponseData

    @Multipart
    @PATCH("character/{id}")
    suspend fun updateCharacter(
        @Path("id") id: Int,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part imageUrl: MultipartBody.Part?,
        @Header("Authorization") userId: String,
    ): ResponseData

    @DELETE("character/{id}")
    suspend fun deleteCharacter(
        @Path("id") id: Int,
        @Header("Authorization") userId: String,
    ): ResponseData
}

object API {
    val retrofitService: APIService by lazy {
        retrofit.create(APIService::class.java)
    }

    fun getCharacterImage(imageUrl: String): String {
        return "$BASE_URL$imageUrl"
    }
}