package com.afrimax.paysimati.common.domain.utils


sealed class Result<out D, out E : Issues> {
    data class Success<out D, out E : Issues>(val data: D) : Result<D, E>()
    data class Error<out D, out E : Issues>(val error: E) : Result<D, E>()
}