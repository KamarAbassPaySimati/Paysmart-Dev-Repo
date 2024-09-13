package com.afrimax.paysimati.common.presentation.ui.text_field.basic

import androidx.lifecycle.ViewModel
import com.afrimax.paysimati.R
import com.afrimax.paysimati.common.presentation.utils.UiDrawable
import com.afrimax.paysimati.common.presentation.utils.UiText
import com.afrimax.paysimati.common.presentation.utils.currentState
import com.afrimax.paysimati.common.presentation.utils.reduceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class BasicTextFieldViewModel @Inject constructor(
) : ViewModel(), ContainerHost<BasicTextFieldState, Unit> {

    private val initialState = BasicTextFieldState(
        title = UiText.Dynamic(""),
        text = UiText.Dynamic(""),
        hint = UiText.Dynamic(""),
        warningText = UiText.Dynamic(""),
        background = UiDrawable.Resource(R.drawable.bg_edit_text_unfocused),
        showWarning = false
    )

    override val container = container<BasicTextFieldState, Unit>(initialState)

    /** Public function exposed to UI components such as Activities, Fragments, and Bottom Sheets,
     * allowing them to perform operations on this ViewModel.
     * Do not call these functions or any functions declared within them directly from the ViewModel.
     * Instead, use side effects to invoke these functions from the UI components. */
    fun invoke(action: BasicTextFieldIntent): Job? {
        return when (action) {
            is BasicTextFieldIntent.SetInitialData -> setInitialData(
                title = action.title, hint = action.hint
            )

            is BasicTextFieldIntent.SetText -> setText(text = action.text)
            is BasicTextFieldIntent.SetBackground -> setBackground(background = action.background)
            is BasicTextFieldIntent.ShowWarning -> showWarning(warning = action.warning)
            is BasicTextFieldIntent.SetTitle -> setTitle(title = action.title)
        }
    }

    private fun setInitialData(title: String, hint: String): Job? {
        if (currentState.title.isEmpty() && currentState.hint.isEmpty()) {
            intent {
                reduce {
                    state.copy(
                        title = UiText.Dynamic(title), hint = UiText.Dynamic(hint)
                    )
                }
            }
        }
        return null
    }

    private fun setText(text: String): Job? {
        if (text.isEmpty()) {
            reduceState {
                copy(
                    text = UiText.Dynamic(text),
                    background = UiDrawable.Resource(R.drawable.bg_edit_text_error),
                    warningText = UiText.Resource(R.string.required_field),
                    showWarning = true
                )
            }
        } else {
            reduceState {
                copy(
                    text = UiText.Dynamic(text),
                    background = UiDrawable.Resource(R.drawable.bg_edit_text_focused),
                    showWarning = false
                )
            }
        }
        return null
    }

    private fun setBackground(background: UiDrawable): Job? {
        intent { reduce { state.copy(background = background) } }
        return null
    }

    private fun showWarning(warning: String): Job? {
        intent {
            reduce {
                state.copy(
                    warningText = UiText.Dynamic(warning),
                    background = UiDrawable.Resource(R.drawable.bg_edit_text_error),
                    showWarning = true
                )
            }
        }
        return null
    }

    private fun setTitle(title: String): Job? {
        intent { reduce { state.copy(title = UiText.Dynamic(title)) } }
        return null
    }
}
