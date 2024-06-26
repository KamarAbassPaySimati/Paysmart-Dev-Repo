package com.afrimax.paymaart.data.model

data class UpdatePinPasswordRequest(
    val password: String,
    val confirm_password: String,
    val email_address: String,
    val type: String,
    val question_id: String,
    val answer: String
)