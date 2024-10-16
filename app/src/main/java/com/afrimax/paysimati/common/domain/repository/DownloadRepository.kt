package com.afrimax.paysimati.common.domain.repository

import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult

interface DownloadRepository {

    suspend fun downloadCsv(
        fileUrl: String, fileName: String
    ): GenericResult<String, Errors.Storage>

    suspend fun downloadPdf(
        fileUrl: String, fileName: String
    ): GenericResult<String, Errors.Storage>
}