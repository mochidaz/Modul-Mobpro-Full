package com.example.mobprostuff

import androidx.annotation.DrawableRes

data class Sprite(
    val name: String,
    @DrawableRes val imageResId: Int,
)
