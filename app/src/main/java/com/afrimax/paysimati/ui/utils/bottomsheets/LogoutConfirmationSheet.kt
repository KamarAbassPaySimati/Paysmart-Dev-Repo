package com.afrimax.paysimati.ui.utils.bottomsheets

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.R
import com.afrimax.paysimati.databinding.LogoutConfirmationSheetBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.intro.IntroActivity
import com.afrimax.paysimati.util.AuthCalls
import com.afrimax.paysimati.util.Constants
import com.afrimax.paysimati.util.PrefsManager
import com.amplifyframework.core.Amplify
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class LogoutConfirmationSheet: BottomSheetDialogFragment() {
    private lateinit var b: LogoutConfirmationSheetBinding
    private lateinit var authCalls: AuthCalls
    private lateinit var prefsManager: PrefsManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = LogoutConfirmationSheetBinding.inflate(inflater, container, false)
        setUpListeners()
        authCalls = AuthCalls()
        return b.root
    }

    private fun setUpListeners() {

        b.logoutConfirmationSheetCancelButton.setOnClickListener {
            dismiss()
        }

        b.logoutConfirmationSheetConfirmButton.setOnClickListener {
            onClickLogout()
        }
    }

    private fun onClickLogout() {
        val parentActivity = requireActivity() as BaseActivity
        parentActivity.lifecycleScope.launch {
            isCancelable = false
            b.logoutConfirmationSheetConfirmButton.isEnabled = false
            b.logoutConfirmationSheetConfirmButton.text = ""
            b.logoutConfirmationSheetConfirmButtonLoaderLottie.visibility = View.VISIBLE
            authCalls.initiateLogout(parentActivity)
            Amplify.Auth.signOut {
                clearPrefs()
                requireActivity().runOnUiThread {
                    isCancelable = true
                    b.logoutConfirmationSheetConfirmButton.isEnabled = true
                    b.logoutConfirmationSheetConfirmButton.text = getString(R.string.confirm)
                    b.logoutConfirmationSheetConfirmButtonLoaderLottie.visibility = View.GONE
                    dismiss()
                    requireActivity().finishAffinity()
                    startActivity(Intent(requireActivity(), IntroActivity::class.java))
                }
            }
        }
    }

    private fun clearPrefs() {
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences(Constants.USER_DATA_PREFS, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        const val TAG = "LogoutConfirmationSheet"
    }
}