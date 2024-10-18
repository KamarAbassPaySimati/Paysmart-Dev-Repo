package com.afrimax.paysimati.common.data.repository

import android.content.Context
import com.afrimax.paysimati.common.data.network.DownloadService
import com.afrimax.paysimati.common.data.utils.DownloadManager
import com.afrimax.paysimati.common.domain.repository.DownloadRepository
import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult

class DownloadRepositoryImpl(
    private val context: Context, private val downloadService: DownloadService
) : DownloadRepository {

    override suspend fun downloadCsv(
        fileUrl: String, fileName: String
    ): GenericResult<String, Errors.Storage> {

        val response = downloadService.downloadCsvFile(fileUrl = fileUrl).body()
        return if (response != null) {
            DownloadManager(context = context).saveFile(
                responseBody = response,
                fileName = fileName,
                mimeType = "text/csv",
                extension = "csv"
            )
        } else {
            GenericResult.Error(Errors.Storage.FILE_NOT_FOUND)
        }
    }

    override suspend fun downloadPdf(fileUrl: String,  fileName: String): GenericResult<String, Errors.Storage> {

        val response = downloadService.downloadPdfFile(fileUrl = fileUrl).body()
        return if (response != null) {
            DownloadManager(context = context).saveFile(
                responseBody = response,
                fileName = fileName,
                mimeType = "application/pdf",
                extension = "pdf"
            )
        } else {
            GenericResult.Error(Errors.Storage.FILE_NOT_FOUND)
        }
    }
}