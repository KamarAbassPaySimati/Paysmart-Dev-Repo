package com.afrimax.paysimati.ui.chatMerchant.domain.usecase

import com.afrimax.paysimati.ui.chatMerchant.domain.repo.ChatSocketRepository
import javax.inject.Inject

class ShutDownChatSocketUseCase @Inject constructor(
    private val provideChatSocketRepository: ChatSocketRepository,
) {
    operator fun invoke() {
        provideChatSocketRepository.disconnect()
    }
}