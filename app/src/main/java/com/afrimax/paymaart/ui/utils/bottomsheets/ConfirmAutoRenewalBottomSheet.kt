package com.afrimax.paymaart.ui.utils.bottomsheets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.UpdateAutoRenewalRequestBody
import com.afrimax.paymaart.databinding.ConfirmAutoRenewalBottomSheetBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.home.MembershipType
import com.afrimax.paymaart.ui.utils.interfaces.MembershipPlansInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmAutoRenewalBottomSheet(private val membershipType: MembershipType, private val autoRenew: Boolean) : BottomSheetDialogFragment() {
    private lateinit var binding: ConfirmAutoRenewalBottomSheetBinding
    private lateinit var sheetCallback: MembershipPlansInterface
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ConfirmAutoRenewalBottomSheetBinding.inflate(inflater, container, false)
        setupView()
        return binding.root
    }

    private fun setupView() {
        binding.confirmAutoRenewalConfirmButton.setOnClickListener {
            onConfirmClicked()
        }

        binding.confirmAutoRenewalCancelButton.setOnClickListener{
            dismiss()
            sheetCallback.onCancelClicked(membershipType = membershipType)
        }
    }

    private fun onConfirmClicked() {
        onConfirm()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as MembershipPlansInterface
    }

    companion object{
        const val TAG = "ConfirmAutoRenewalBottomSheet"
    }

    private fun onConfirm(){
        showLoader()
        val activity = activity as BaseActivity
        lifecycleScope.launch {
            val idToken = activity.fetchIdToken()
            val autoRenewalHandler = ApiClient.apiService.updateAutoRenewal(idToken, UpdateAutoRenewalRequestBody(autoRenew = autoRenew))

            autoRenewalHandler.enqueue(object : Callback<Unit>{
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful){
                        sheetCallback.onConfirm(membershipType = membershipType)
                    }else{

                        sheetCallback.onCancelClicked(membershipType = membershipType)
                        Toast.makeText(
                            requireContext(), getString(R.string.default_error_toast), Toast.LENGTH_LONG
                        ).show()
                    }
                    hideLoader()
                    dismiss()
                }

                override fun onFailure(call: Call<Unit>, throwable: Throwable) {
                    hideLoader()
                    dismiss()
                    Toast.makeText(
                        requireContext(), getString(R.string.default_error_toast), Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }

    private fun showLoader(){
        binding.confirmAutoRenewalConfirmButtonLottieLoader.visibility = View.VISIBLE
        binding.confirmAutoRenewalConfirmButton.apply {
            text = ""
            isEnabled = false
        }
    }

    private fun hideLoader() {
        binding.confirmAutoRenewalConfirmButtonLottieLoader.visibility = View.GONE
        binding.confirmAutoRenewalConfirmButton.apply {
            text = getString(R.string.confirm)
            isEnabled = true
        }
    }
}