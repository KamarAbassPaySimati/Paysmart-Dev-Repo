package com.afrimax.paysimati.data.model

data class SendOtpForEditSelfKycRequest(
    val first_name:String,
    val middle_name:String,
    val last_name:String,
    val type:String,
    val value:String,
    val country_code:String
)


data class SendOtpForEditSelfKycResponse(
    val success_status: Boolean,
    val token: String,
    val question: String,
    val question_id: String,
)