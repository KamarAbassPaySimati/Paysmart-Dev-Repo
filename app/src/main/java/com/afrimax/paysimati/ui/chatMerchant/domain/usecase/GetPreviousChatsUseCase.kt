package com.afrimax.paysimati.ui.chatMerchant.domain.usecase

import com.afrimax.paysimati.common.data.repository.SharedPrefsRepository
import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult
import com.afrimax.paysimati.ui.chatMerchant.data.chat.PreviousChatsResult
import com.afrimax.paysimati.ui.chatMerchant.domain.repo.PaymentApiRepository
import javax.inject.Inject

class GetPreviousChatsUseCase @Inject constructor(
    private val paymentApiRepository: PaymentApiRepository,
    private val sharedPrefsRepository: SharedPrefsRepository
) {
    suspend operator fun invoke(
        receiverId: String, page: Int
    ): GenericResult<PreviousChatsResult, Errors> {

        val userPaysimatiId = sharedPrefsRepository.userPaySimatiId
        return if (userPaysimatiId != null) {
            paymentApiRepository.getPreviousChats(
                receiverId = receiverId, senderId = userPaysimatiId, page = page
            )
        } else {
            GenericResult.Error(Errors.Prefs.NO_SUCH_DATA)
        }
    }
}