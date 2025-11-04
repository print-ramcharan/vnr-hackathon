package com.codewithram.medvault.network.models

data class PredictionResponse(
    val prediction: String,
    val confidence: Double = 0.0
)
