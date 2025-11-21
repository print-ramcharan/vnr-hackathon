package com.codewithram.medvault.network.models

data class SummaryResponse(
    val summary: String,
    val all_summaries: List<String>
)
