package com.afrimax.paysimati.data.model

import com.google.gson.annotations.SerializedName


data class ScanQrRequest(
    @SerializedName("encrypted_till_number")
    val encryptedtill:String
)
