package com.afrimax.paymaart.data.model

import com.google.gson.annotations.SerializedName

data class FcmTokenRequest(
    @SerializedName("notification_id") val notificationId: String
)