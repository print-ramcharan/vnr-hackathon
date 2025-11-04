package com.codewithram.medvault.network

import com.codewithram.medvault.network.models.PredictionResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ModelService {
    // Model predictions are exposed via the MedVault backend proxy at /api/inference/predict
    @POST("inference/predict")
    suspend fun predict(@Body body: Map<String, Any>): PredictionResponse
}
