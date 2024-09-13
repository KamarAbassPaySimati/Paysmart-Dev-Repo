package com.afrimax.paymaart.data.model

data class SaveNewAddressDetailsSelfKycRequest(
    val po_box_no: String,
    val house_number: String,
    val street_name: String,
    val landmark: String,
    val town_village_ta: String,
    val district: String,
    val citizen: String?,
    val intl_address: String?,
    val address_details_status: String,
    val sent_email: Boolean
)
