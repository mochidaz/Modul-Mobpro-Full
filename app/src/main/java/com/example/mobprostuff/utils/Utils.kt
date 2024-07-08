package com.example.mobprostuff.utils

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.canhub.cropper.CropImageView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

fun getCroppedImage(
    resolver: ContentResolver,
    result: CropImageView.CropResult
): Bitmap? {
    if (!result.isSuccessful) {
        Log.e("IMAGE", "Error: ${result.error}")
        return null
    }

    val uri = result.uriContent ?: return null

    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        MediaStore.Images.Media.getBitmap(resolver, uri)
    } else {
        val source = ImageDecoder.createSource(resolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}

fun Bitmap.toMultipartBody(): MultipartBody.Part {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 80, stream)
    val byteArray = stream.toByteArray()
    val requestBody = byteArray.toRequestBody(
        "image/jpg".toMediaTypeOrNull(), 0, byteArray.size)
    return MultipartBody.Part.createFormData(
        "image", "image.jpg", requestBody)
}
