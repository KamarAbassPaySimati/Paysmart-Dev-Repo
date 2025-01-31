    package com.afrimax.paysimati.common.domain.utils


    sealed class GenericResult<out D, out E : GenericError> {
        data class Success<out D, out E : GenericError>(val data: D) : GenericResult<D, E>()
        data class Error<out D, out E : GenericError>(val error: E) : GenericResult<D, E>()
    }