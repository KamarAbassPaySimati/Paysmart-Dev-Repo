package com.afrimax.paysimati.data.model.chat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatState(
    val receiverName: String,
    val receiverId: String,
    val receiverProfilePicture: String? = null,
    val receiverCountryCode:String,
    val receiverPhoneNumber:String,
    val messageText: String = "",
    val realTimeMessages: ArrayList<ChatMessage> = ArrayList()
) : Parcelable