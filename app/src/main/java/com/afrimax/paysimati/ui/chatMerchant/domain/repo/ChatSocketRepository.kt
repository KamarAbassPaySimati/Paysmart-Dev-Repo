package com.afrimax.paysimati.ui.chatMerchant.domain.repo

import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult
import com.afrimax.paysimati.ui.chatMerchant.data.chat.ChatMessage
import com.afrimax.paysimati.ui.chatMerchant.data.chat.PaymentStatusType


interface ChatSocketRepository {
    suspend fun establishConnection(
        senderId: String, receiverId: String, onMessageReceived: (message: ChatMessage) -> Unit
    ): GenericResult<Unit, Errors.Network>

    fun sendTextMessage(
        message: String, senderId: String, receiverId: String
    ): GenericResult<Unit, Errors.Network>

    fun disconnect()
}
