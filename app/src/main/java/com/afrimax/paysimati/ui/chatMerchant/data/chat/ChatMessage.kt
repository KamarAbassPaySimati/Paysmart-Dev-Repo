package com.afrimax.paysimati.ui.chatMerchant.data.chat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date


@Parcelize
sealed class ChatMessage(
    val createdTime: Date,
) : Parcelable {

    @Parcelize
    data class TextMessage(
        val chatId: String,
        val receiverId: String,
        val message: String,
        val chatCreatedTime: Date,
        val isAuthor: Boolean
    ) : ChatMessage(createdTime = chatCreatedTime)

    @Parcelize
    data class PaymentMessage(
        val chatId: String,
        val receiverId: String,
        val amount: Double,
        val transactionId: String,
        val paymentStatusType: PaymentStatusType,
        val chatCreatedTime: Date,
        val note: String? = null,
        val isAuthor: Boolean,
        val tillnumber: String,
        val streetName: String? = null
    ) : ChatMessage(createdTime = chatCreatedTime)

}