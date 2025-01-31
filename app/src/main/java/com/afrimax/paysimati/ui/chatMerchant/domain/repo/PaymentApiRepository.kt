package com.afrimax.paysimati.ui.chatMerchant.domain.repo

import com.afrimax.paysimati.common.core.parseDate
import com.afrimax.paysimati.common.data.utils.TokenProvider
import com.afrimax.paysimati.common.data.utils.safeApiCall
import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult
import com.afrimax.paysimati.common.presentation.utils.*
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.PreviousChatResponse
import com.afrimax.paysimati.data.model.chat.ChatMessage
import com.afrimax.paysimati.data.model.chat.PreviousChatsResult
import java.util.Date

interface PaymentApiRepository {
    suspend fun getPreviousChats(
        receiverId: String, senderId: String, page: Int
    ): GenericResult<PreviousChatsResult, Errors.Network>
}

class PaymentApiRepositoryImpl(
    private val tokenProvider: TokenProvider
) : PaymentApiRepository {

    override suspend fun getPreviousChats(
        receiverId: String, senderId: String, page: Int
    ): GenericResult<PreviousChatsResult, Errors.Network> {

        // Use safeApiCall to wrap the API call
        val apiCall = safeApiCall {
            ApiClient.apiService.getPreviousChat(
                header = tokenProvider.token(),
                page = page,
                receiverId = receiverId
            )
        }

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
                // Handle different errors based on the network error
                when (apiCall.error) {
                    Errors.Network.NO_RESPONSE -> GenericResult.Success(
                        PreviousChatsResult(totalRecords = 0, previousChats = arrayListOf())
                    )
                    else -> GenericResult.Error(apiCall.error)  // Return the error if it is not NO_RESPONSE
                }
            }
        }
    }

    // Maps the response to the PreviousChatsResult data class
    fun mapToPreviousChatResult(
        response: PreviousChatResponse,
        senderId: String
    ): PreviousChatsResult? {
        val chatList = mutableListOf<ChatMessage>()

        // Map the raw response chat messages to domain models
        val messages = response.chatMessages.orEmpty().mapNotNull { mapChatMessage(it, senderId) }

        // Add the mapped messages to the chat list
        chatList.addAll(messages)

        // Check if totalRecords is available and return a populated PreviousChatsResult
        return response.totalRecords?.let {
            PreviousChatsResult(
                totalRecords = it,
                previousChats = ArrayList(chatList)
            )
        }
    }

    // Maps individual chat messages
    private fun mapChatMessage(
        chat: PreviousChatResponse.ChatMessage?, senderId: String
    ): ChatMessage? {
        return chat?.let {
            val date = it.createdAt.parseDate()
            when (it.chatType) {
                TEXT_MESSAGE -> mapTextMessage(it, senderId, date)
                else -> null
            }
        }
    }

    // Maps a text message from the response to a domain model
    private fun mapTextMessage(
        chat: PreviousChatResponse.ChatMessage, senderId: String, date: Date?
    ): ChatMessage.TextMessage? {
        return if (chat.recordId != null && chat.receiverId != null && chat.content != null && chat.senderId != null && date != null) {
            ChatMessage.TextMessage(
                chatId = chat.recordId,
                receiverId = chat.receiverId,
                message = chat.content,
                chatCreatedTime = date,
                isAuthor = chat.senderId == senderId
            )
        } else null
    }
}
