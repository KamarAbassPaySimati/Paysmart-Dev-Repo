package com.afrimax.paysimati.data.model

data class SaveNewIdentityDetailsSelfKycRequest(
    val id_document: String,
    val id_document_front: String,
    val id_document_back: String,
    val verification_document: String,
    val verification_document_front: String,
    val verification_document_back: String,
    val selfie: String,
    val nature_of_permit: String?,
    val ref_no: String?,
    val id_details_status: String,
    val sent_email: Boolean
)