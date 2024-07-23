package com.afrimax.paymaart.data.model

import com.google.gson.annotations.SerializedName

data class ValidateAfrimaxIdResponse(
    val message: String, val data: AfrimaxUserData, val status: Boolean, val time: String
)

data class AfrimaxUserData(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    @SerializedName("last_online") val lastOnline: String,
    @SerializedName("last_update") val lastUpdate: String
)