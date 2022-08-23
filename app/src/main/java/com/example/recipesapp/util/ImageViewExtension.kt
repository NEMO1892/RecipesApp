package com.example.recipesapp.util

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadUrlImage(url: String) {
    Glide.with(context)
        .load(url)
        .into(this)
}
