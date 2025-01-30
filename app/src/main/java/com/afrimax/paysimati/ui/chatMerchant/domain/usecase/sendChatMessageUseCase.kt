package com.afrimax.paysimati.ui.chatMerchant.domain.usecase

import com.afrimax.paysimati.common.data.repository.SharedPrefsRepository
import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult
import com.afrimax.paysimati.ui.chatMerchant.domain.repo.ChatSocketRepository
import javax.inject.Inject

class sendChatMessageUseCase @Inject constructor(
    private val chatSocketRepository: ChatSocketRepository,
    private val sharedPrefsRepository: SharedPrefsRepository
) {
    operator fun invoke(message: String, receiverId: String): GenericResult<Unit, Errors> {
        val userPaysimatiId = sharedPrefsRepository.userPaySimatiId
        return if (userPaysimatiId != null) {
            chatSocketRepository.sendTextMessage(
                message = message, receiverId = receiverId, senderId = userPaysimatiId
            )
        } else {
            GenericResult.Error(Errors.Prefs.NO_SUCH_DATA)
        }
    }
}