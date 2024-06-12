package com.afrimax.paymaart.ui.utils.bottomsheets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paymaart.databinding.NatureOfPermitSheetBinding
import com.afrimax.paymaart.ui.utils.interfaces.KycYourIdentityInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class KycNatureOfPermitSheet : BottomSheetDialogFragment() {
    private lateinit var b: NatureOfPermitSheetBinding
    private lateinit var sheetCallback: KycYourIdentityInterface
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = NatureOfPermitSheetBinding.inflate(inflater, container, false)
        setUpListeners()

        return b.root
    }

    private fun setUpListeners() {

        b.kycNatureOfPermitSheetCloseButton.setOnClickListener {
            dismiss()
        }

        b.kycNatureOfPermitSheetOption1TV.setOnClickListener {
            onClickItem(b.kycNatureOfPermitSheetOption1TV.text.toString())
        }

        b.kycNatureOfPermitSheetOption2TV.setOnClickListener {
            onClickItem(b.kycNatureOfPermitSheetOption2TV.text.toString())
        }

        b.kycNatureOfPermitSheetOption3TV.setOnClickListener {
            onClickItem(b.kycNatureOfPermitSheetOption3TV.text.toString())
        }

        b.kycNatureOfPermitSheetOption4TV.setOnClickListener {
            onClickItem(b.kycNatureOfPermitSheetOption4TV.text.toString())
        }

    }

    private fun onClickItem(selectedItem: String) {
        sheetCallback.onNatureOfPermitSelected(selectedItem)
        dismiss()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as KycYourIdentityInterface
    }


    companion object {
        const val TAG = "KycNatureOfPermitSheet"
    }
}