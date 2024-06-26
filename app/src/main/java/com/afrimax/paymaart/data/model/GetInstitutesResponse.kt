package com.afrimax.paymaart.data.model

data class GetInstitutesResponse(
    val success_status: Boolean,
    val institutionNames: List<String>
)
