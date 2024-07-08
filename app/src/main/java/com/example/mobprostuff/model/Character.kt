package com.example.mobprostuff.model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class Character(
    @Json(name="id")
    val id: Int,
    @Json(name="name")
    val name: String,
    @Json(name="description")
    val description: String,
    @Json(name="image_url")
    val imageUrl: String,
    @Json(name="user_id")
    val userId: String
)