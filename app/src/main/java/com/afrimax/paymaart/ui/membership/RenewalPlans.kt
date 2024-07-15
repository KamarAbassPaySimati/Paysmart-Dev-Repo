package com.afrimax.paymaart.ui.membership

data class RenewalPlans(
    val membershipType: String,
    val referenceNumber: String,
    val planPrice: String?,
    val planValidity: String?
)
