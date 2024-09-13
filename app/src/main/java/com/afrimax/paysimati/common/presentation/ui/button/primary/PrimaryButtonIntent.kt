package com.afrimax.paysimati.common.presentation.ui.button.primary

sealed class PrimaryButtonIntent {
    // Define intents here
    data class SetInitialData(val buttonText: String) : PrimaryButtonIntent()
    data class ChangeButtonLoaderStatus(val isVisible: Boolean) : PrimaryButtonIntent()
}