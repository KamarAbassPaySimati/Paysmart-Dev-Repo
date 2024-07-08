package com.afrimax.paymaart.data.model

data class SaveBasicDetailsSelfKycRequest(
    val email: String?,
    val phone_number: String?,
    val country_code: String?,
    val email_id: String?,
    val phone_number_id: String?,
    val profile_pic: String?,
    val public_profile: Boolean?
)
