package com.afrimax.paymaart.data.model

data class CreateUserResponse(
    val success_status: Boolean,
    val message: String,
    val paymaart_id: String,
)

data class CreateUserRequestBody(
    val first_name: String,
    val middle_name: String,
    val last_name: String,
    val country_code: String,
    val phone_number: String,
    val email: String,
    val email_otp_id: String,
    val phone_otp_id: String,
    val security_questions: List<SecurityQuestionAnswerModel>,
    val profile_pic: String,
    val public_profile: Boolean
)