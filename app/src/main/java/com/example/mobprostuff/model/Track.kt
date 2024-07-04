package com.example.mobprostuff.model

import com.google.gson.annotations.SerializedName

data class Track(
    @SerializedName("album") val album: String?,
    @SerializedName("track_number") val trackNumber: String?,
    @SerializedName("arrangement_title") val arrangementTitle: String?,
    @SerializedName("translated_name") val translatedName: String?,
    @SerializedName("arrangement") val arrangement: String?,
    @SerializedName("source") val source: String?,
    @SerializedName("vocals") val vocals: String?,
    @SerializedName("lyrics") val lyrics: String?,
    @SerializedName("original_title") val originalTitle: String?,
    @SerializedName("guitar") val guitar: String?,
    @SerializedName("note") val note: String?,
    @SerializedName("from_") val from: String?,
    @SerializedName("genre") val genre: String?,
    @SerializedName("album_img") val albumImg: String?,
)
