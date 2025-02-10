package com.afrimax.paysimati.ui.chatMerchant.repository

import android.util.Log
import com.afrimax.paysimati.common.data.utils.TokenProvider
import com.afrimax.paysimati.common.data.utils.safeApiCall
import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult
import com.afrimax.paysimati.data.ApiService
import com.afrimax.paysimati.data.model.chat.PreviousChatsResult
import com.afrimax.paysimati.ui.chatMerchant.domain.repo.PaymentApiRepository
import com.afrimax.paysimati.ui.chatMerchant.mapToPreviousChatResult

class PaymentApiRepositoryImpl(
    private val paymentApiService: ApiService,
    private val tokenProvider: TokenProvider
) : PaymentApiRepository {
    override suspend fun getPreviousChats(
        receiverId: String, senderId: String, page: Int
    ): GenericResult<PreviousChatsResult, Errors.Network> {

        // Use safeApiCall to wrap the API call
        val apiCall = safeApiCall {
            paymentApiService.getPreviousChat(
                header = tokenProvider.token(),
                receiverId = receiverId,
                page = page
            )
        }
        Log.d("PaymentApiRepositoryImpl", "getPreviousChats: receiverId=$receiverId, page=$page")
        // Handle the result based on the response from safeApiCall
        return when (apiCall) {
            is GenericResult.Success -> {
                // Map the response to PreviousChatsResult if successful
                val response = mapToPreviousChatResult(apiCall.data, senderId)
                if (response != null) {
                    GenericResult.Success(response)  // Return Success with the data
                } else {
                    GenericResult.Error(Errors.Network.MAPPING_FAILED)  // Return Error if mapping fails
                }
            }
            is GenericResult.Error -> {
                Log.e("PaymentApiRepositoryImpl", "getPreviousChats: error=${apiCall.error}")
                // Handle different errors based on the network error
                when (apiCall.error) {
                    Errors.Network.NO_RESPONSE -> GenericResult.Success(
                        PreviousChatsResult(totalRecords = 0, previousChats = arrayListOf())
                    )
                    else -> GenericResult.Error(apiCall.error)
                // Return the error if it is not NO_RESPONSE
                }
            }
        }
    }

}