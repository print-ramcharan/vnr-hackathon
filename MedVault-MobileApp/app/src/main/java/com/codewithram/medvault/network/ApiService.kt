package com.codewithram.medvault.network

import com.codewithram.medvault.network.models.PredictionResponse
import com.codewithram.medvault.network.models.SummaryResponse
import com.codewithram.medvault.network.models.ExtractionResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): Response<AuthResponse>

    // Route prediction calls through backend proxy mounted at /api/inference
    @POST("inference/predict")
    suspend fun predict(@Body body: Map<String, String>): Response<PredictionResponse>

    // The backend exposes summary endpoints under /api/summary (it proxies to the summary microservice).
    @Multipart
    @POST("summary/summarize")
    suspend fun summarize(@Part file: MultipartBody.Part): Response<SummaryResponse>

    @Multipart
    @POST("summary/extract_medicines")
    suspend fun extractPrescription(@Part file: MultipartBody.Part): Response<SummaryResponse>
}
