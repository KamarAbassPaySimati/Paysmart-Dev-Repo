package com.afrimax.paysimati.ui.chatMerchant.ui

import com.afrimax.paysimati.common.presentation.utils.UiText

sealed class ChatSideEffect {
    // Actions that can be performed on the UI
    data class ShowToast(val message: UiText) : ChatSideEffect()
    data class ShowSnack(val message: UiText) : ChatSideEffect()

    // Actions that can be used to invoke functions on the ViewModel
}
