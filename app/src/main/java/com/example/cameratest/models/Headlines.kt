package com.example.cameratest.models

data class Headlines(
    val status: String,
    val totalResults: String,
    val articles: List<Articles>
)