package com.afrimax.paysimati.main.domain.usecase

import com.afrimax.paysimati.common.domain.repository.DownloadRepository
import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named

class DownloadPdfFileUseCase @Inject constructor(
    private val downloadRepository: DownloadRepository,
    @Named("walletStatementFileNamePrefix") private val walletStatementFileNamePrefix: String
) {

    suspend operator fun invoke(fileUrl: String): GenericResult<String, Errors.Storage> {
        val formatter = SimpleDateFormat(CHART_DATE_FORMAT, Locale.getDefault())
        val now = Calendar.getInstance().time
        val fileName = "$walletStatementFileNamePrefix-${formatter.format(now)}"

        return downloadRepository.downloadPdf(fileUrl = fileUrl, fileName = fileName)
    }

    companion object {
        const val CHART_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    }
}