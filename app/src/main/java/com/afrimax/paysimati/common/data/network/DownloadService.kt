package com.afrimax.paysimati.common.data.network


import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface DownloadService {

    @Streaming
    @GET
    suspend fun downloadCsvFile(@Url fileUrl: String): Response<ResponseBody>

    @Streaming
    @GET
    suspend fun downloadPdfFile(@Url fileUrl: String): Response<ResponseBody>
}