package com.afrimax.paysimati.common.presentation.ui.text_field.basic

import com.afrimax.paysimati.common.presentation.utils.UiDrawable

sealed class BasicTextFieldIntent {
    data class SetInitialData(val title: String, val hint: String) : BasicTextFieldIntent()
    data class SetText(val text: String) : BasicTextFieldIntent()
    data class SetTitle(val title: String) : BasicTextFieldIntent()
    data class SetBackground(val background: UiDrawable) : BasicTextFieldIntent()
    data class ShowWarning(val warning: String) : BasicTextFieldIntent()
}