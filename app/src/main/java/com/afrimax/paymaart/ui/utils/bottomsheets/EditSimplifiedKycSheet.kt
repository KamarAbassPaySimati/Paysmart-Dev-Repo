package com.afrimax.paymaart.ui.utils.bottomsheets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.DefaultResponse
import com.afrimax.paymaart.databinding.EditSimplifiedKycBottomSheetBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.kyc.KycCustomerPersonalDetailsActivity
import com.afrimax.paymaart.util.Constants
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditSimplifiedKycSheet: BottomSheetDialogFragment() {
    private lateinit var b: EditSimplifiedKycBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = EditSimplifiedKycBottomSheetBinding.inflate(inflater, container, false)

        setUpListeners()

        return b.root
    }

    private fun setUpListeners() {

        b.editSimplifiedKycSheetCloseButton.setOnClickListener {
            dismiss()
        }

        b.editSimplifiedKycSheetUpgradeButton.setOnClickListener {
            //api call
            saveSelfPreferenceApi()

        }

        b.editSimplifiedKycSheetEditButton.setOnClickListener {
            //Continue with editing kyc
            dismiss()
            val i = Intent(requireContext(), KycCustomerPersonalDetailsActivity::class.java)
            i.putExtra(Constants.KYC_SCOPE, Constants.KYC_MALAWI_SIMPLIFIED)
            i.putExtra(Constants.VIEW_SCOPE, Constants.VIEW_SCOPE_EDIT)
            startActivity(i)
            requireActivity().finish()
        }

    }

    private fun saveSelfPreferenceApi() {
        showButtonLoader(
            b.editSimplifiedKycSheetUpgradeButton, b.editSimplifiedKycSheetUpgradeButtonLoaderLottie
        )

        lifecycleScope.launch {
            val activity = requireActivity() as BaseActivity
            val idToken = activity.fetchIdToken()

            val switchToFullApiCall = ApiClient.apiService.switchToFullKyc(idToken)

            switchToFullApiCall.enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(
                    call: Call<DefaultResponse>, response: Response<DefaultResponse>
                ) {
                    hideButtonLoader(
                        b.editSimplifiedKycSheetUpgradeButton,
                        b.editSimplifiedKycSheetUpgradeButtonLoaderLottie,
                        getString(
                            R.string.upgrade_to_full_kyc
                        )
                    )
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        dismiss()
                        val i = Intent(requireContext(), KycCustomerPersonalDetailsActivity::class.java)
                        i.putExtra(Constants.KYC_SCOPE, Constants.KYC_MALAWI_FULL)
                        i.putExtra(Constants.VIEW_SCOPE, Constants.VIEW_SCOPE_UPDATE)
                        startActivity(i)
                        activity.finish()
                    } else {
                        activity.showToast(getString(R.string.default_error_toast))
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    hideButtonLoader(
                        b.editSimplifiedKycSheetUpgradeButton,
                        b.editSimplifiedKycSheetUpgradeButtonLoaderLottie,
                        getString(
                            R.string.upgrade_to_full_kyc
                        )
                    )
                    activity.showToast(getString(R.string.default_error_toast))
                }


            })
        }
    }



    private fun showButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView
    ) {
        actionButton.text = ""
        b.editSimplifiedKycSheetCloseButton.isEnabled = false
        b.editSimplifiedKycSheetUpgradeButton.isEnabled = false
        b.editSimplifiedKycSheetEditButton.isEnabled = false
        loaderLottie.visibility = View.VISIBLE
        isCancelable = false

    }

    private fun hideButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView, buttonText: String
    ) {
        actionButton.text = buttonText
        b.editSimplifiedKycSheetCloseButton.isEnabled = true
        b.editSimplifiedKycSheetUpgradeButton.isEnabled = true
        b.editSimplifiedKycSheetEditButton.isEnabled = true
        loaderLottie.visibility = View.GONE
        isCancelable = true
    }


    companion object {
        const val TAG = "EditSimplifiedKycSheet"
    }
}