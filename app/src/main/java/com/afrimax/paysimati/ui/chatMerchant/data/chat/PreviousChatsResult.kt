package com.afrimax.paysimati.ui.chatMerchant.data.chat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PreviousChatsResult(
    val totalRecords: Int, val previousChats: ArrayList<ChatMessage>
) : Parcelable
