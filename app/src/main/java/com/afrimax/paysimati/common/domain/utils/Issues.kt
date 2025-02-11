package com.afrimax.paysimati.common.domain.utils

sealed interface Issues {

    /**Errors that can occur while performing network calls*/
    sealed class Network(val errorMessage: String) : Issues {
        data class NoResponse(val message: String) : Network(message)
        data class NoInternet(val message: String) : Network(message)
        data class InternalError(val message: String) : Network(message)
        data class RequestTimeOut(val message: String) : Network(message)
        data class PayloadTooLarge(val message: String) : Network(message)
        data class Unknown(val message: String) : Network(message)
        data class UnableToUpload(val message: String) : Network(message)
        data class FileNotFound(val message: String) : Network(message)
        data class ConflictFound(val message: String, val type: String? = null) : Network(message)
        data class UnAuthorized(val message: String) : Network(message)
        data class BadRequest(val message: String) : Network(message)
        data class MappingFailed(val message: String) : Network(message)
    }
}