package com.afrimax.paymaart.data.model

data class UpdatePinOrPasswordRequest(
    val old_password: String,
    val new_password: String,
    val confirm_password: String,
    val type: String
)

