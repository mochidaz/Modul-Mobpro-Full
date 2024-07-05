package com.example.mobprostuff.network

import com.example.mobprostuff.model.Track
import com.google.gson.GsonBuilder
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://nazrin-dowse.kakashispiritnews.my.id/api/"

private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.MINUTES)
    .readTimeout(60, TimeUnit.MINUTES)
    .writeTimeout(60, TimeUnit.MINUTES)
    .build()

private val gson = GsonBuilder()
    .setLenient()
    .create()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create(gson))
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()

interface NazrinAPIService {
    @GET("search")
    @Streaming
    fun search(
        @Query("url") url: String,
        @Query("search_query") searchQuery: String
    ): Call<ResponseBody>
}

object NazrinAPI {
    val retrofitService: NazrinAPIService by lazy {
        retrofit.create(NazrinAPIService::class.java)
    }
}