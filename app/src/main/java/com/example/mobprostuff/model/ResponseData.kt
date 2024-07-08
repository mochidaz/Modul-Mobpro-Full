package com.example.mobprostuff.model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class ResponseData (
    @Json(name="status")
    val status: String,
    @Json(name="message")
    val message: String,
    @Json(name="data")
    val data: List<Character>?
)