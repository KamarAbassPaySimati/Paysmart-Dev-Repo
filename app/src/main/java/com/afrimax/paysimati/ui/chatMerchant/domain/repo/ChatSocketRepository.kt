package com.afrimax.paysimati.ui.chatMerchant.domain.repo

import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult
import com.afrimax.paysimati.data.model.chat.ChatMessage


interface ChatSocketRepository {
    suspend fun establishConnection(
        senderId: String, receiverId: String, onMessageReceived: (message: ChatMessage) -> Unit
    ): GenericResult<Unit, Errors.Network>

    fun sendTextMessage(
        message: String, senderId: String, receiverId: String
    ): GenericResult<Unit, Errors.Network>

    fun disconnect()
}