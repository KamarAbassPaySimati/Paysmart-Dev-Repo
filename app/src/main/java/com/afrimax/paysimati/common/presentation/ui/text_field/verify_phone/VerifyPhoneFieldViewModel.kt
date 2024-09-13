package com.afrimax.paysimati.common.presentation.ui.text_field.verify_phone

import androidx.lifecycle.ViewModel
import com.afrimax.paysimati.R
import com.afrimax.paysimati.common.presentation.utils.UiDrawable
import com.afrimax.paysimati.common.presentation.utils.UiText
import com.afrimax.paysimati.common.presentation.utils.reduceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class VerifyPhoneFieldViewModel @Inject constructor(
) : ViewModel(), ContainerHost<VerifyPhoneFieldState, Unit> {

    private val initialState = VerifyPhoneFieldState(
        title = UiText.Dynamic(""),
        text = UiText.Dynamic(""),
        hint = UiText.Dynamic(""),
        background = UiDrawable.Resource(R.drawable.bg_edit_text_unfocused),
        showWarning = false,
        warningText = UiText.Dynamic(""),
        isPhoneVerified = false,
        showButtonLoader = false,
        countryCodes = ArrayList<String>().apply { add("+265") },
        currentCountryCode = "+265",
    )

    override val container =
        container<VerifyPhoneFieldState, Unit>(initialState)

    /** Public function exposed to UI components such as Activities, Fragments, and Bottom Sheets,
     * allowing them to perform operations on this ViewModel.
     * Do not call these functions or any functions declared within them directly from the ViewModel.
     * Instead, use side effects to invoke these functions from the UI components. */
    operator fun invoke(action: VerifyPhoneFieldIntent): Job? {
        return when (action) {
            is VerifyPhoneFieldIntent.SetInitialData -> setInitialData(
                title = action.title, hint = action.hint
            )

            is VerifyPhoneFieldIntent.SetTitle -> setTitle(title = action.title)
            is VerifyPhoneFieldIntent.SetText -> setText(text = action.text)
            is VerifyPhoneFieldIntent.ChangeButtonLoaderStatus -> changeButtonLoaderStatus(isVisible = action.isVisible)
            is VerifyPhoneFieldIntent.SetBackground -> setBackground(background = action.background)
            is VerifyPhoneFieldIntent.SetCountryCodeList -> setCountryCodes(countryCodes = action.countryCodes)
            is VerifyPhoneFieldIntent.ShowWarning -> showWarning(warning = action.warning)
            is VerifyPhoneFieldIntent.ChangeVerifiedStatus -> setVerifiedStatus(isVerified = action.isVerified)
            is VerifyPhoneFieldIntent.ChangeCountryCode -> changeCountryCode(countryCode = action.countryCode)
        }
    }

    private fun setInitialData(title: String, hint: String): Job? {
        reduceState { copy(title = UiText.Dynamic(title), hint = UiText.Dynamic(hint)) }
        return null
    }

    private fun setTitle(title: String): Job? {
        reduceState { copy(title = UiText.Dynamic(title)) }
        return null
    }

    private fun setText(text: String): Job? {
        if (text.isEmpty()) {
            reduceState {
                copy(
                    text = UiText.Dynamic(text),
                    background = UiDrawable.Resource(R.drawable.bg_edit_text_error),
                    warningText = UiText.Resource(R.string.required_field),
                    showWarning = true,
                    isPhoneVerified = false
                )
            }
        } else {
            reduceState {
                copy(
                    text = UiText.Dynamic(text),
                    background = UiDrawable.Resource(R.drawable.bg_edit_text_focused),
                    showWarning = false,
                    isPhoneVerified = false
                )
            }
        }
        return null
    }

    private fun changeButtonLoaderStatus(isVisible: Boolean): Job? {
        if (isVisible) reduceState {
            copy(
                showButtonLoader = true,
                background = UiDrawable.Resource(R.drawable.bg_edit_text_focused),
                showWarning = false
            )
        }
        else reduceState { copy(showButtonLoader = false) }

        return null
    }

    private fun setBackground(background: UiDrawable): Job? {
        reduceState { copy(background = background) }
        return null
    }

    private fun setCountryCodes(countryCodes: java.util.ArrayList<String>): Job? {
        reduceState { copy(countryCodes = countryCodes) }
        return null
    }

    private fun showWarning(warning: String): Job? {
        reduceState {
            copy(
                warningText = UiText.Dynamic(warning),
                background = UiDrawable.Resource(R.drawable.bg_edit_text_error),
                showWarning = true
            )
        }
        return null
    }

    private fun setVerifiedStatus(isVerified: Boolean): Job? {
        reduceState { copy(isPhoneVerified = isVerified) }
        return null
    }

    private fun changeCountryCode(countryCode: String): Job? {
        reduceState { copy(currentCountryCode = countryCode) }
        return null
    }
}
