package com.afrimax.paysimati.data.model

import com.google.gson.annotations.SerializedName

data class GetAfrimaxPlansResponse(
    val message: String, val data: ArrayList<AfrimaxPlan>, val status: Boolean, val time: String
)

data class AfrimaxPlan(
    val id: Int,
    val title: String,
    @SerializedName("service_name") val serviceName: ArrayList<String>,
    val price: String,
    @SerializedName("billing_days_count") val billingDaysCount: Int,
    @SerializedName("speed_download") val speedDownload: Int,
    @SerializedName("speed_upload") val speedUpload: Int,
    @SerializedName("updated_at") val updatedAt: String,
    val viewType: String? = "",
    var isSelected: Boolean? = false
)
