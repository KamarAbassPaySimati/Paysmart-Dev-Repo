package com.afrimax.paymaart.common.presentation.ui.text_field.verify_phone

import com.afrimax.paymaart.common.presentation.utils.UiDrawable

sealed class VerifyPhoneFieldIntent {
    data class SetInitialData(val title: String, val hint: String) : VerifyPhoneFieldIntent()
    data class SetText(val text: String) : VerifyPhoneFieldIntent()
    data class SetTitle(val title: String) : VerifyPhoneFieldIntent()
    data class SetBackground(val background: UiDrawable) : VerifyPhoneFieldIntent()
    data class ShowWarning(val warning: String) : VerifyPhoneFieldIntent()
    data class SetCountryCodeList(val countryCodes: ArrayList<String>) : VerifyPhoneFieldIntent()
    data class ChangeButtonLoaderStatus(val isVisible: Boolean) : VerifyPhoneFieldIntent()
    data class ChangeVerifiedStatus(val isVerified: Boolean) : VerifyPhoneFieldIntent()
    data class ChangeCountryCode(val countryCode: String) : VerifyPhoneFieldIntent()

}