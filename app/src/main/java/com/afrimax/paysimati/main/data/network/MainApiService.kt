package com.afrimax.paysimati.main.data.network

import com.afrimax.paysimati.main.data.model.FetchWalletStatementResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MainApiService {

    @GET("customer-user/transaction-history-pdf")
    suspend fun fetchWalletStatementPdfUrl(
        @Header("Authorization") header: String, @Query("timePeriod") timePeriod: String
    ): Response<FetchWalletStatementResponse>

    @GET("customer-user/transaction-history-csv")
    suspend fun fetchWalletStatementCsvUrl(
        @Header("Authorization") header: String, @Query("timePeriod") timePeriod: String
    ): Response<FetchWalletStatementResponse>
}