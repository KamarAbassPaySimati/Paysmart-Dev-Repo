package com.afrimax.paymaart.data.model

import com.google.gson.annotations.SerializedName

data class RecaptchaRequestBody (
    val secret: String,
    val response: String
)

data class RecaptchaResponse(
    val success: Boolean,
    @SerializedName("challenge_ts")
    val challengeTs: String,
    @SerializedName("apk_package_name")
    val appPackageName: String,
)