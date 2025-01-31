package com.afrimax.paysimati.data.model.chat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PreviousChatsResult(
    val totalRecords: Int, val previousChats: ArrayList<ChatMessage>
) : Parcelable
