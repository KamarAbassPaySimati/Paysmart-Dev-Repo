package com.afrimax.paysimati.data.model

data class GetSharedSecretRequest(
    val username: String
)

data class GetSharedSecretResponse(
    val mfa_code: String
)