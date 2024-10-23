package com.afrimax.paysimati.ui.utils.bottomsheets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.R
import com.afrimax.paysimati.common.data.utils.safeApiCall
import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.databinding.ViewWalletBalancePinBottomSheetBinding
import com.afrimax.paysimati.ui.home.HomeActivity
import com.afrimax.paysimati.ui.utils.interfaces.HomeInterface
import com.afrimax.paysimati.util.AESCrypt
import com.afrimax.paysimati.util.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class ViewWalletPinSheet : BottomSheetDialogFragment() {

    private lateinit var b: ViewWalletBalancePinBottomSheetBinding

    private lateinit var sheetCallback: HomeInterface

    private lateinit var viewWalletScope: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = ViewWalletBalancePinBottomSheetBinding.inflate(inflater, container, false)

        viewWalletScope = requireArguments().getString(Constants.VIEW_WALLET_SCOPE)
            ?: Constants.VIEW_WALLET_SIMPLE_SCOPE

        initViews()
        setUpListeners()

        setUpAutoAcceptPinField()

        return b.root
    }

    private fun initViews() {
        //
    }

    private fun setUpListeners() {

        b.viewWalletPinSheetCloseButton.setOnClickListener {
            dismiss()
        }
    }

    private fun setUpAutoAcceptPinField() {
        b.viewWalletPinSheetAPF.apply {
            onPinEntered {
                lifecycleScope.launch {
                    getWalletDetailsApi()
                }
            }
        }
    }


    private suspend fun getWalletDetailsApi() {


        val activity = requireActivity() as HomeActivity
        val idToken = activity.fetchIdToken()
        val encryptedPin = AESCrypt.encrypt(b.viewWalletPinSheetAPF.text)

        val viewWalletCall = safeApiCall {
            ApiClient.apiService.viewWallet(idToken, encryptedPin)
        }

        when (viewWalletCall) {
            is GenericResult.Success -> {
                sheetCallback.onClickViewBalance(viewWalletScope, viewWalletCall.data.data)
                dismiss()
            }

            is GenericResult.Error -> {
                when (viewWalletCall.error) {
                    Errors.Network.UNAUTHORIZED, Errors.Network.BAD_REQUEST -> {
                        b.viewWalletPinSheetAPF.showWarning(getString(R.string.invalid_pin))
                    }

                    else -> b.viewWalletPinSheetAPF.showWarning(getString(R.string.default_error_toast))
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as HomeInterface
    }

    companion object {
        const val TAG = "ViewWalletPinSheet"
    }
}