package com.afrimax.paysimati.main.ui.wallet_statement.wallet_statement

import com.afrimax.paysimati.common.presentation.utils.UiText

sealed class WalletStatementSideEffect {
    // Actions that can be performed on the UI 
    data class ShowToast(val message: UiText) : WalletStatementSideEffect()
    data class ShowSnack(val message: UiText) : WalletStatementSideEffect()

    // Actions that can be used to invoke functions on the ViewModel
}
