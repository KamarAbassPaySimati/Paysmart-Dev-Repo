package com.afrimax.paysimati.common.domain.repository

import android.net.Uri
import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult

interface AmplifyRepository {

    suspend fun getBearerToken(): GenericResult<String, Errors.Network>

    suspend fun uploadFile(
        fileUri: Uri, objectKey: String
    ): GenericResult<String, Errors.Network>
}