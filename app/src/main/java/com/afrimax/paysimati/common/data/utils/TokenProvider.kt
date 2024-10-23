package com.afrimax.paysimati.common.data.utils

import com.afrimax.paysimati.common.domain.repository.AmplifyRepository
import com.afrimax.paysimati.common.domain.utils.GenericResult
import javax.inject.Inject

class TokenProvider @Inject constructor(
    private val amplifyRepository: AmplifyRepository
) {
    /**This function returns the Bearer token used for all the private APIs*/
    suspend fun token(): String {
        return when (val getBearerTokenCall = amplifyRepository.getBearerToken()) {
            is GenericResult.Success -> getBearerTokenCall.data
            is GenericResult.Error -> ""
        }
    }
}