package com.example.recipesapp.model

data class Images(
    val LARGE: LARGE,
    val REGULAR: REGULAR,
    val SMALL: SMALL,
    val THUMBNAIL: THUMBNAIL
)

data class LARGE(
    val height: Int,
    val url: String,
    val width: Int
)

data class REGULAR(
    val height: Int,
    val url: String,
    val width: Int
)

data class SMALL(
    val height: Int,
    val url: String,
    val width: Int
)

data class THUMBNAIL(
    val height: Int,
    val url: String,
    val width: Int
)