package com.afrimax.paysimati.main.domain.usecase

import com.afrimax.paysimati.common.domain.repository.DownloadRepository
import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult
import javax.inject.Inject
import javax.inject.Named

class DownloadPdfFileUseCase @Inject constructor(
    private val downloadRepository: DownloadRepository,
    @Named("walletStatementPdfNamePrefix") private val walletStatementPdfNamePrefix: String
) {

    suspend operator fun invoke(fileUrl: String): GenericResult<String, Errors.Storage> {
        return downloadRepository.downloadPdf(
            fileUrl = fileUrl, fileName = walletStatementPdfNamePrefix
        )
    }
}