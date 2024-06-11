package com.afrimax.paymaart.data.model

data class KycSaveCustomerPreferenceRequest(
    val kyc_type: String,
    val citizen: String,
    val paymaart_id: String,
)