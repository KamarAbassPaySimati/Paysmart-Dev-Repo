package com.afrimax.paysimati.data.model

data class SaveInfoSimplifiedToFullRequest(
    val gender: String,
    val dob: String,
    val occupation: String,
    val employed_role: String,
    val employer_name: String,
    val self_employed_specify: String,
    val institute: String,
    val institute_specify: String,
    val occupation_specify: String,
    val industry: String,
    val occupation_town: String,
    val purpose_of_relation: String,
    val monthly_income: String,
    val monthly_withdrawal: String,
    val info_details_status: String,
)
