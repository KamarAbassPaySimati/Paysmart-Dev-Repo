package com.afrimax.paysimati.data.model

data class KycSaveCustomerPreferenceRequest(
    val kyc_type: String,
    val citizen: String,
)