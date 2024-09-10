package com.afrimax.paymaart.common.presentation.ui.button.primary

import android.os.Parcelable
import com.afrimax.paymaart.common.presentation.utils.UiText
import kotlinx.parcelize.Parcelize

@Parcelize
data class PrimaryButtonState(
    val buttonText: UiText,
    val currentButtonText: UiText,
    val showLoader: Boolean = false,
) : Parcelable