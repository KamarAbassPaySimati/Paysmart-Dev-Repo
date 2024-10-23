package com.afrimax.paysimati.main.ui.wallet_statement.wallet_statement

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afrimax.paysimati.common.domain.utils.GenericResult
import com.afrimax.paysimati.common.presentation.utils.UiText
import com.afrimax.paysimati.common.presentation.utils.VIEW_MODEL_STATE
import com.afrimax.paysimati.common.presentation.utils.asUiText
import com.afrimax.paysimati.common.presentation.utils.currentState
import com.afrimax.paysimati.common.presentation.utils.postSideEffect
import com.afrimax.paysimati.common.presentation.utils.reduceState
import com.afrimax.paysimati.main.domain.usecase.DownloadCsvFileUseCase
import com.afrimax.paysimati.main.domain.usecase.DownloadPdfFileUseCase
import com.afrimax.paysimati.main.domain.usecase.FetchWalletStatementCsvUrlUseCase
import com.afrimax.paysimati.main.domain.usecase.FetchWalletStatementPdfUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class WalletStatementViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fetchWalletStatementPdfUrlUseCase: FetchWalletStatementPdfUrlUseCase,
    private val fetchWalletStatementCsvUrlUseCase: FetchWalletStatementCsvUrlUseCase,
    private val downloadPdfFileUseCase: DownloadPdfFileUseCase,
    private val downloadCsvFileUseCase: DownloadCsvFileUseCase
) : ViewModel(), ContainerHost<WalletStatementState, WalletStatementSideEffect> {

    private val initialState = savedStateHandle[VIEW_MODEL_STATE] ?: WalletStatementState(
        selectedOption = 1
    )

    override val container =
        container<WalletStatementState, WalletStatementSideEffect>(initialState, savedStateHandle)

    /** Public function exposed to UI components such as Activities, Fragments, and Bottom Sheets,
     * allowing them to perform operations on this ViewModel.
     * Do not call these functions or any functions declared within them directly from the ViewModel.
     * Instead, use side effects to invoke these functions from the UI components. */
    operator fun invoke(action: WalletStatementIntent): Job? {
        return when (action) {
            is WalletStatementIntent.ExportPdfData -> exportPdfData()
            is WalletStatementIntent.ExportCsvData -> exportCsvData()
            is WalletStatementIntent.SetSelectedOption -> setSelectedOption(option = action.option)
        }
    }

    private fun exportPdfData(): Job {
        return viewModelScope.launch {
            val pdfUrl = fetchWalletStatementPdfUrl(timePeriodOption = currentState.selectedOption)

            pdfUrl?.let {
                when (val downloadCall = downloadPdfFileUseCase(fileUrl = it)) {
                    is GenericResult.Success -> postSideEffect {
                        WalletStatementSideEffect.ShowToast(
                            UiText.Dynamic("File downloaded successfully")
                        )
                    }

                    is GenericResult.Error -> postSideEffect {
                        WalletStatementSideEffect.ShowToast(downloadCall.error.asUiText())
                    }
                }
            }
        }
    }

    private fun exportCsvData(): Job {
        return viewModelScope.launch {
            val csvUrl = fetchWalletStatementCsvUrl(timePeriodOption = currentState.selectedOption)

            csvUrl?.let {
                when (val downloadCall = downloadCsvFileUseCase(fileUrl = it)) {
                    is GenericResult.Success -> postSideEffect {
                        WalletStatementSideEffect.ShowToast(
                            UiText.Dynamic("File downloaded successfully")
                        )
                    }

                    is GenericResult.Error -> postSideEffect {
                        WalletStatementSideEffect.ShowToast(downloadCall.error.asUiText())
                    }
                }
            }
        }
    }

    private fun setSelectedOption(option: Int): Job? {
        reduceState { copy(selectedOption = option) }
        return null
    }

    // ====================================================================
    //                        HELPER FUNCTIONS
    // ====================================================================

    private suspend fun fetchWalletStatementPdfUrl(timePeriodOption: Int): String? {
        val fetchUrlCall = fetchWalletStatementPdfUrlUseCase(timePeriodSelection = timePeriodOption)

        when (fetchUrlCall) {
            is GenericResult.Success -> return fetchUrlCall.data
            is GenericResult.Error -> postSideEffect { WalletStatementSideEffect.ShowToast((fetchUrlCall.error.asUiText())) }
        }

        return null
    }

    private suspend fun fetchWalletStatementCsvUrl(timePeriodOption: Int): String? {
        val fetchUrlCall = fetchWalletStatementCsvUrlUseCase(timePeriodSelection = timePeriodOption)

        when (fetchUrlCall) {
            is GenericResult.Success -> return fetchUrlCall.data
            is GenericResult.Error -> postSideEffect { WalletStatementSideEffect.ShowToast((fetchUrlCall.error.asUiText())) }
        }

        return null
    }
}
