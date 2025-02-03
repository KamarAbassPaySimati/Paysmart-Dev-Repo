package com.afrimax.paysimati.ui.chatMerchant.domain.repo

import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult
import com.afrimax.paysimati.data.model.chat.PreviousChatsResult

interface PaymentApiRepository {
    suspend fun getPreviousChats(
        receiverId: String, senderId: String, page: Int
    ): GenericResult<PreviousChatsResult, Errors.Network>
}

