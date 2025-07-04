package com.afrimax.paysimati.ui.chatMerchant.data.chat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatState(
    val receiverName: String,
    val receiverId: String,
    val receiverProfilePicture: String? = null,
    val receiverAddress:String?=null,
    val tillnumber:String?=null,
    val messageText: String = "",
    val realTimeMessages: ArrayList<ChatMessage> = ArrayList()
) : Parcelable