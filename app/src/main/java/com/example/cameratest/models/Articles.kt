package com.example.cameratest.models

data class Articles(
    val source: Source,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val content: String,
)