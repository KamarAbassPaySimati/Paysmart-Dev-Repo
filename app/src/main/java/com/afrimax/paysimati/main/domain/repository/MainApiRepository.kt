package com.afrimax.paysimati.main.domain.repository

import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult

interface MainApiRepository {

    suspend fun fetchWalletStatementPdfUrl(timePeriodOption: Int): GenericResult<String, Errors.Network>
}