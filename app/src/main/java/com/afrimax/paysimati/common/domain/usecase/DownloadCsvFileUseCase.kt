package com.afrimax.paysimati.common.domain.usecase

import com.afrimax.paysimati.common.domain.repository.DownloadRepository
import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult
import javax.inject.Inject

class DownloadCsvFileUseCase @Inject constructor(
    private val downloadRepository: DownloadRepository
) {

    suspend operator fun invoke(
        fileUrl: String, fileName: String
    ): GenericResult<String, Errors.Storage> {
        return downloadRepository.downloadCsv(fileUrl = fileUrl, fileName = fileName)
    }
}