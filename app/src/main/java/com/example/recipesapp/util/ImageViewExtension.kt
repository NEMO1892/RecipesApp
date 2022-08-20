package com.example.recipesapp.util

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadUrlImage(url: String) {
    Glide.with(context)
        .load(url)
        .into(this)
}

fun ImageView.loadUriImage(url: Uri?) {
    Glide.with(context)
        .load(url)
        .into(this)
}

fun ImageView.loadBitMap(bitmap: Bitmap) {
    Glide.with(context)
        .load(bitmap)
        .into(this)
}
