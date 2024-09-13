package com.afrimax.paysimati.common.presentation.ui.text_field.basic

import android.os.Parcelable
import com.afrimax.paysimati.common.presentation.utils.UiDrawable
import com.afrimax.paysimati.common.presentation.utils.UiText
import kotlinx.parcelize.Parcelize

@Parcelize
data class BasicTextFieldState(
    val title: UiText,
    val text: UiText,
    val hint: UiText,
    val background: UiDrawable,
    val showWarning: Boolean,
    val warningText: UiText,
) : Parcelable