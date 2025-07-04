package com.afrimax.paysimati.ui.chatMerchant

import com.afrimax.paysimati.common.core.parseAmount
import com.afrimax.paysimati.common.core.parseDate
import com.afrimax.paysimati.common.presentation.utils.CHAT_TYPE_PAYMENT_MESSAGE
import com.afrimax.paysimati.common.presentation.utils.CHAT_TYPE_TEXT_MESSAGE
import com.afrimax.paysimati.common.presentation.utils.PAYMENT_CANCELLED_MESSAGE
import com.afrimax.paysimati.common.presentation.utils.PAYMENT_COMPLETED_MESSAGE
import com.afrimax.paysimati.common.presentation.utils.PAYMENT_REQUEST_MESSAGE
import com.afrimax.paysimati.common.presentation.utils.PAYMENT_SEND_MESSAGE
import com.afrimax.paysimati.common.presentation.utils.TEXT_MESSAGE
import com.afrimax.paysimati.ui.chatMerchant.data.chat.ChatMessage
import com.afrimax.paysimati.ui.chatMerchant.data.chat.ChatMessageResponse
import com.afrimax.paysimati.ui.chatMerchant.data.chat.PaymentStatusType
import com.afrimax.paysimati.ui.chatMerchant.data.chat.PreviousChatResponse
import com.afrimax.paysimati.ui.chatMerchant.data.chat.PreviousChatsResult
import java.util.Date
import java.util.UUID

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

private fun mapChatMessage(
    chat: PreviousChatResponse.ChatMessage?, senderId: String
): ChatMessage? {
    return chat?.let {
        val date = it.createdAt.parseDate()
        when (it.chatType) {
            TEXT_MESSAGE -> mapTextMessage(chat = it,senderId= senderId,date= date)

            PAYMENT_REQUEST_MESSAGE -> mapPaymentRequestMessage(
                chat = it, senderId = senderId, date = date
            )

            PAYMENT_COMPLETED_MESSAGE  -> mapPaymentCompletedMessage(
                it, senderId = senderId, date = date
            )

            PAYMENT_SEND_MESSAGE ->mapPaymentCompletedMessage(
                it, senderId = senderId, date = date
            )
            PAYMENT_CANCELLED_MESSAGE ->mapDelinedRequestMessage(
                it, senderId = senderId, date = date
            )



            else -> null
        }
    }
}

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


private fun mapDelinedRequestMessage(
    chat: PreviousChatResponse.ChatMessage, senderId: String, date: Date?
): ChatMessage.PaymentMessage? {
    return if ( chat.receiverId != null && chat.senderId != null && date != null && chat.transactionId != null) {
        ChatMessage.PaymentMessage(
            chatId = UUID.randomUUID().toString(),
            receiverId = chat.senderId,
            amount = chat.transactionAmount.parseAmount(),
            transactionId = chat.transactionId,
            paymentStatusType = PaymentStatusType.DECLINED,
            chatCreatedTime = date,
            note = chat.content,
            isAuthor = chat.senderId == senderId,
            tillnumber = chat.tillnumber
        )
    } else null
}


private fun mapPaymentRequestMessage(
    chat: PreviousChatResponse.ChatMessage, senderId: String, date: Date?
): ChatMessage.PaymentMessage? {
    return if ( chat.receiverId != null && chat.senderId != null && date != null && chat.transactionId != null) {
        ChatMessage.PaymentMessage(
            chatId = UUID.randomUUID().toString(),
            receiverId = chat.senderId,
            amount = chat.transactionAmount.parseAmount(),
            transactionId = chat.transactionId,
            paymentStatusType = PaymentStatusType.PENDING,
            chatCreatedTime = date,
            note = chat.content,
            isAuthor = chat.senderId == senderId,
            tillnumber = chat.tillnumber

        )
    } else null
}


private fun mapPaymentCompletedMessage(
    chat: PreviousChatResponse.ChatMessage, senderId: String, date: Date?
): ChatMessage.PaymentMessage? {
    return if (chat.tillnumber!=null &&chat.receiverId != null && chat.senderId != null && date != null && chat.transactionId != null) {
        ChatMessage.PaymentMessage(
            chatId = UUID.randomUUID().toString(),
            receiverId = chat.receiverId,
            amount = chat.transactionAmount.parseAmount(),
            transactionId = chat.transactionId,
            paymentStatusType = PaymentStatusType.RECEIVED,
            chatCreatedTime = date,
            note = chat.content,
            isAuthor = chat.senderId == senderId,
            tillnumber = chat.tillnumber,
        )
    } else null
}

fun mapToChatMessage(response: ChatMessageResponse): ChatMessage? {

    val chatCreatedTime = response.createdAt.parseDate()
    return when (response.type) {
        CHAT_TYPE_TEXT_MESSAGE -> {
            if (response.receiverId != null && chatCreatedTime != null) {
                ChatMessage.TextMessage(
                    chatId = UUID.randomUUID()
                        .toString(), //Ideally this should come from the backend
                    receiverId = response.receiverId,
                    message = response.message,
                    chatCreatedTime = chatCreatedTime,
                    isAuthor = false
                )
            } else {
                null
            }
        }



        else -> null
    }
}