package com.afrimax.paysimati.data.model

data class VerifyOtpForEditSelfKycRequest(
    val otp:String,
    val token:String,
    val question_id:String,
    val answer:String
)

data class VerifyOtpForEditSelfKycResponse(
    val success_status: Boolean,
    val record_id: String,
    val message: String,
)

data class VerifyOtpForEditSelfErrorResponse(
    val succes_status: String,
    val message: String,
    val type: String,
)