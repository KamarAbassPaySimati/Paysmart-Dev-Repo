package com.afrimax.paysimati.ui.utils.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.R
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.DefaultResponse
import com.afrimax.paysimati.data.model.DeleteAccountReqRequest
import com.afrimax.paysimati.databinding.DeleteConfirmationBottomSheetBinding
import com.afrimax.paysimati.ui.delete.DeleteAccountActivity
import com.afrimax.paysimati.util.Constants
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteConfirmationBottomSheet: BottomSheetDialogFragment() {
    private lateinit var b: DeleteConfirmationBottomSheetBinding
    private lateinit var reasonList: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = DeleteConfirmationBottomSheetBinding.inflate(inflater, container, false)

        reasonList = requireArguments().getStringArrayList(Constants.DELETE_REASONS) ?: ArrayList()
        setUpListeners()

        return b.root
    }

    private fun setUpListeners() {

        b.deleteConfirmationSheetCancelButton.setOnClickListener {
            dismiss()
        }

        b.deleteConfirmationSheetConfirmButton.setOnClickListener {
            if (reasonList.isNotEmpty()) sendDeleteRequestApi()
        }

    }

    private fun sendDeleteRequestApi() {
        showButtonLoader(
            b.deleteConfirmationSheetConfirmButton,
            b.deleteConfirmationSheetConfirmButtonLoaderLottie
        )
        lifecycleScope.launch {
            val activity = requireActivity() as DeleteAccountActivity
            val idToken = activity.fetchIdToken()

            val sendDeleteRequestCall = ApiClient.apiService.deleteAccountRequest(
                idToken,
                DeleteAccountReqRequest(reasons = reasonList)
            )

            sendDeleteRequestCall.enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(
                    call: Call<DefaultResponse>, response: Response<DefaultResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        hideButtonLoader(
                            b.deleteConfirmationSheetConfirmButton,
                            b.deleteConfirmationSheetConfirmButtonLoaderLottie,
                            getString(R.string.delete)
                        )
                        dismiss()
                        DeleteRequestSuccessSheet().show(
                            requireActivity().supportFragmentManager, DeleteRequestSuccessSheet.TAG
                        )
                    } else {
                        val errorResponse: DefaultResponse = Gson().fromJson(
                            response.errorBody()!!.string(),
                            object : TypeToken<DefaultResponse>() {}.type
                        )
                        hideButtonLoader(
                            b.deleteConfirmationSheetConfirmButton,
                            b.deleteConfirmationSheetConfirmButtonLoaderLottie,
                            getString(R.string.delete)
                        )
                        activity.showToast(errorResponse.message)
                        dismiss()
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    hideButtonLoader(
                        b.deleteConfirmationSheetConfirmButton,
                        b.deleteConfirmationSheetConfirmButtonLoaderLottie,
                        getString(R.string.delete)
                    )
                    activity.showToast(getString(R.string.default_error_toast))
                    dismiss()
                }

            })
        }
    }

    private fun showButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView
    ) {
        actionButton.text = ""
        b.deleteConfirmationSheetConfirmButton.isEnabled = false
        b.deleteConfirmationSheetCancelButton.isEnabled = false
        loaderLottie.visibility = View.VISIBLE
        isCancelable = false

    }

    private fun hideButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView, buttonText: String
    ) {
        actionButton.text = buttonText
        b.deleteConfirmationSheetConfirmButton.isEnabled = true
        b.deleteConfirmationSheetCancelButton.isEnabled = true
        loaderLottie.visibility = View.GONE
        isCancelable = true
    }


    companion object {
        const val TAG = "DeleteConfirmationSheet"
    }
}