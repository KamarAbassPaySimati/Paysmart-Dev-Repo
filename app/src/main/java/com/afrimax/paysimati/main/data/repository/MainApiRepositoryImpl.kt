package com.afrimax.paysimati.main.data.repository

import android.content.Context
import com.afrimax.paysimati.common.data.utils.TokenProvider
import com.afrimax.paysimati.common.data.utils.safeApiCall
import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult
import com.afrimax.paysimati.main.data.network.MainApiService
import com.afrimax.paysimati.main.data.utils.mapToExportTimePeriod
import com.afrimax.paysimati.main.domain.repository.MainApiRepository

class MainApiRepositoryImpl(
    private val context: Context,
    private val mainApiService: MainApiService,
    private val tokenProvider: TokenProvider
) : MainApiRepository {

    override suspend fun fetchWalletStatementPdfUrl(timePeriodOption: Int): GenericResult<String, Errors.Network> {
        val request = mapToExportTimePeriod(timePeriodOption) ?: return GenericResult.Error(
            Errors.Network.BAD_REQUEST
        )

        val apiCall = safeApiCall {
            mainApiService.fetchWalletStatementPdfUrl(
                header = tokenProvider.token(), timePeriod = request
            )
        }

        return when (apiCall) {
            is GenericResult.Success -> {
                if (apiCall.data.s3Url != null) GenericResult.Success(apiCall.data.s3Url)
                else GenericResult.Error(Errors.Network.NO_RESPONSE)
            }

            is GenericResult.Error -> GenericResult.Error(apiCall.error)
        }

    }

    override suspend fun fetchWalletStatementCsvUrl(timePeriodOption: Int): GenericResult<String, Errors.Network> {
        val request = mapToExportTimePeriod(timePeriodOption) ?: return GenericResult.Error(
            Errors.Network.BAD_REQUEST
        )

        val apiCall = safeApiCall {
            mainApiService.fetchWalletStatementCsvUrl(
                header = tokenProvider.token(), timePeriod = request
            )
        }

        return when (apiCall) {
            is GenericResult.Success -> {
                if (apiCall.data.s3Url != null) GenericResult.Success(apiCall.data.s3Url)
                else GenericResult.Error(Errors.Network.NO_RESPONSE)
            }

            is GenericResult.Error -> GenericResult.Error(apiCall.error)
        }

    }
}