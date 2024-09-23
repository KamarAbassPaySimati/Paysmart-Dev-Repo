package com.afrimax.paymaart.common.presentation.ui.text_field.phone

import android.os.Parcelable
import com.afrimax.paymaart.common.presentation.utils.UiDrawable
import com.afrimax.paymaart.common.presentation.utils.UiText
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhoneFieldState(
    val title: UiText,
    val text: UiText,
    val hint: UiText,
    val background: UiDrawable,
    val showWarning: Boolean,
    val warningText: UiText,
    val countryCodes: ArrayList<String>,
    val currentCountryCode: String
) : Parcelable