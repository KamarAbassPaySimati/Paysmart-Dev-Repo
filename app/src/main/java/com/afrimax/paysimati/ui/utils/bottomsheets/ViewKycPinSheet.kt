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
import com.afrimax.paysimati.databinding.ViewKycPinBottomSheetBinding
import com.afrimax.paysimati.ui.utils.interfaces.ViewSelfKycInterface
import com.afrimax.paysimati.ui.viewkyc.ViewKycDetailsActivity
import com.afrimax.paysimati.util.AESCrypt
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class ViewKycPinSheet : BottomSheetDialogFragment() {
    private lateinit var b: ViewKycPinBottomSheetBinding

    private lateinit var sheetCallback: ViewSelfKycInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = ViewKycPinBottomSheetBinding.inflate(inflater, container, false)

        initViews()
        setUpListeners()

        setUpAutoAcceptPinField()

        return b.root
    }

    private fun initViews() {
        //
    }

    private fun setUpListeners() {

        b.viewKycPinSheetCloseButton.setOnClickListener {
            dismiss()
        }

    }


    private fun setUpAutoAcceptPinField() {
        b.viewKycPinSheetAPF.apply {
            onPinEntered {
                lifecycleScope.launch {
                    getKycDetailsApi()
                }
            }
        }
    }

    private suspend fun getKycDetailsApi() {


        val activity = requireActivity() as ViewKycDetailsActivity
        val idToken = activity.fetchIdToken()

        val encryptedPin = AESCrypt.encrypt(b.viewKycPinSheetAPF.text)
        val viewKycCall = safeApiCall {
            ApiClient.apiService.getSelfKycDetails(encryptedPin, idToken)
        }

        when (viewKycCall) {
            is GenericResult.Success -> {
                sheetCallback.onClickViewButton(viewKycCall.data.data)
                dismiss()
            }

            is GenericResult.Error -> {
                when (viewKycCall.error) {
                    Errors.Network.UNAUTHORIZED, Errors.Network.BAD_REQUEST -> {
                        b.viewKycPinSheetAPF.showWarning(getString(R.string.invalid_pin))
                    }

                    else -> b.viewKycPinSheetAPF.showWarning(getString(R.string.default_error_toast))
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as ViewSelfKycInterface
    }

    companion object {
        const val TAG = "ViewKycPinSheet"
    }
}