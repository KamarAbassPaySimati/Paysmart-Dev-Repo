package com.afrimax.paysimati.common.presentation.ui.button.primary

import androidx.lifecycle.ViewModel
import com.afrimax.paysimati.common.presentation.utils.UiText
import com.afrimax.paysimati.common.presentation.utils.currentState
import com.afrimax.paysimati.common.presentation.utils.reduceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class PrimaryButtonViewModel @Inject constructor() : ViewModel(),
    ContainerHost<PrimaryButtonState, Unit> {

    private val initialState = PrimaryButtonState(
        buttonText = UiText.Dynamic(""), currentButtonText = UiText.Dynamic("")
    )

    override val container = container<PrimaryButtonState, Unit>(initialState)

    /** Public function exposed to UI components such as Activities, Fragments, and Bottom Sheets,
     * allowing them to perform operations on this ViewModel.
     * Do not call these functions or any functions declared within them directly from the ViewModel.
     * Instead, use side effects to invoke these functions from the UI components. */
    fun invoke(action: PrimaryButtonIntent): Job? {
        return when (action) {
            is PrimaryButtonIntent.SetInitialData -> setInitialData(buttonText = action.buttonText)
            is PrimaryButtonIntent.ChangeButtonLoaderStatus -> changeButtonLoaderStatus(isVisible = action.isVisible)
        }
    }

    private fun setInitialData(buttonText: String): Job? {
        reduceState {
            copy(
                buttonText = UiText.Dynamic(buttonText),
                currentButtonText = UiText.Dynamic(buttonText)
            )
        }
        return null
    }

    private fun changeButtonLoaderStatus(isVisible: Boolean): Job? {
        if (isVisible) {
            reduceState {
                copy(
                    currentButtonText = UiText.Dynamic(""), showLoader = true
                )
            }
        } else {
            reduceState {
                copy(
                    currentButtonText = currentState.buttonText, showLoader = false
                )
            }
        }
        return null
    }

}
