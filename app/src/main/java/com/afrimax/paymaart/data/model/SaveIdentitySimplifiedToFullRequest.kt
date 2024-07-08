package com.afrimax.paymaart.data.model

data class SaveIdentitySimplifiedToFullRequest(
    val id_document: String,
    val id_document_front: String,
    val id_document_back: String,
    val verification_document: String,
    val verification_document_front: String,
    val verification_document_back: String,
    val selfie: String,
    val id_details_status: String,
)
