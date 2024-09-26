package com.afrimax.paysimati.data.model

data class SendForgotOtpResponse(
    val message: String, val Encrypted_otp: String, val user_id: String
)