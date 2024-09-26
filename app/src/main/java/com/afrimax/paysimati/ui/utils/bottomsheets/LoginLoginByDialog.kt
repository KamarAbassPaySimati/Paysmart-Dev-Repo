package com.afrimax.paysimati.ui.utils.bottomsheets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.afrimax.paysimati.databinding.DialogLoginLoginByBinding
import com.afrimax.paysimati.ui.utils.interfaces.LoginByDialogInterface
import com.afrimax.paysimati.util.Constants

class LoginLoginByDialog : DialogFragment() {

    private lateinit var b: DialogLoginLoginByBinding

    private lateinit var fragmentCallback: LoginByDialogInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = DialogLoginLoginByBinding.inflate(inflater, container, false)

        b.dialogLoginLoginByPhoneNumber.setOnClickListener {
            fragmentCallback.onSelection(Constants.SELECTION_PHONE_NUMBER)
            dismiss()
        }

        b.dialogLoginLoginByEmail.setOnClickListener {
            fragmentCallback.onSelection(Constants.SELECTION_EMAIL)
            dismiss()
        }

        b.dialogLoginLoginByPaymaartId.setOnClickListener {
            fragmentCallback.onSelection(Constants.SELECTION_PAYMAART_ID)
            dismiss()
        }

        return b.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentCallback = context as LoginByDialogInterface
    }

    companion object {
        const val TAG = "LoginLoginByDialog"
    }
}