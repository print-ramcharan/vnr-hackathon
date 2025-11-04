package com.codewithram.medvault.network.models

data class Medication(
    val name: String,
    val dosage: String?,
    val frequency: String?,
    val route: String?,
    val duration: String?
)

data class ExtractionResponse(
    val medications: List<Medication> = emptyList()
)
