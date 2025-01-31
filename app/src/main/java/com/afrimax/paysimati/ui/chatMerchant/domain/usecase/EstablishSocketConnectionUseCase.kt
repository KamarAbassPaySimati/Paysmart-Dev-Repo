package com.afrimax.paysimati.ui.chatMerchant.domain.usecase

import com.afrimax.paysimati.common.data.repository.SharedPrefsRepository
import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult
import com.afrimax.paysimati.data.model.chat.ChatMessage
import com.afrimax.paysimati.ui.chatMerchant.domain.repo.ChatSocketRepository
import javax.inject.Inject

class EstablishSocketConnectionUseCase @Inject constructor(
    private val provideChatSocketRepository: ChatSocketRepository,
    private val sharedPrefsRepository: SharedPrefsRepository
) {
    suspend operator fun invoke(
        receiverId: String, onMessageReceived: (message: ChatMessage) -> Unit
    ): GenericResult<Unit, Errors> {
        val userPaysimatiId = sharedPrefsRepository.userPaySimatiId
        return if (userPaysimatiId != null) {
            provideChatSocketRepository.establishConnection(
                senderId = userPaysimatiId,
                receiverId = receiverId,
                onMessageReceived = onMessageReceived
            )
        } else {
            GenericResult.Error(Errors.Prefs.NO_SUCH_DATA)
        }
    }
}