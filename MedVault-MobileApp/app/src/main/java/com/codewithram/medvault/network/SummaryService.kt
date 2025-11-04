package com.codewithram.medvault.network

import com.codewithram.medvault.network.models.ExtractionResponse
import com.codewithram.medvault.network.models.SummaryResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SummaryService {
    // These endpoints are proxied by the MedVault backend. Keep paths matching backend (/api/summary/*)
    @Multipart
    @POST("summary/summarize")
    suspend fun summarize(@Part file: MultipartBody.Part): SummaryResponse

    @Multipart
    @POST("summary/extract_medicines")
    suspend fun extractMedicines(@Part file: MultipartBody.Part): ExtractionResponse
}
