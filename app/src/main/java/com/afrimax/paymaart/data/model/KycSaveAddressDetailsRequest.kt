package com.afrimax.paymaart.data.model

data class KycSaveAddressDetailsRequest(
    val paymaart_id:String,
    val po_box_no: String,
    val house_number: String,
    val street_name: String,
    val landmark: String,
    val town_village_ta: String,
    val district: String,
    val citizen: String?,
    val intl_po_box_no: String?,
    val intl_house_number: String?,
    val intl_street_name: String?,
    val intl_landmark: String?,
    val intl_town_village_ta: String?,
    val intl_district: String?,
    val address_details_status: String
)