package com.afrimax.paysimati.data.model

import com.google.gson.annotations.SerializedName

data class FcmTokenRequest(
    @SerializedName("notification_id") val notificationId: String
)