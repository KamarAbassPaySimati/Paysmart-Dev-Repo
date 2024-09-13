package com.afrimax.paysimati.common.presentation.ui.text_field.phone

import com.afrimax.paysimati.common.presentation.utils.UiDrawable

sealed class PhoneFieldIntent {
    data class SetInitialData(val title: String, val hint: String) : PhoneFieldIntent()
    data class SetText(val text: String) : PhoneFieldIntent()
    data class SetTitle(val title: String) : PhoneFieldIntent()
    data class SetBackground(val background: UiDrawable) : PhoneFieldIntent()
    data class ShowWarning(val warning: String) : PhoneFieldIntent()
    data class SetCountryCodeList(val countryCodes: ArrayList<String>) : PhoneFieldIntent()
    data class ChangeCountryCode(val countryCode: String) : PhoneFieldIntent()
}