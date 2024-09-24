package com.afrimax.paysimati.ui.utils.bottomsheets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paysimati.databinding.KycBanksSheetBinding
import com.afrimax.paysimati.ui.utils.interfaces.KycYourInfoInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class KycBanksSheet: BottomSheetDialogFragment() {
    private lateinit var b: KycBanksSheetBinding
    private lateinit var sheetCallback: KycYourInfoInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = KycBanksSheetBinding.inflate(inflater, container, false)

        setUpListeners()

        return b.root
    }

    private fun setUpListeners() {

        b.kycBanksSheetCloseButton.setOnClickListener {
            dismiss()
        }

        b.kycBanksSheetOption1TV.setOnClickListener {
            onClickItem(b.kycBanksSheetOption1TV.text.toString())
        }

        b.kycBanksSheetOption2TV.setOnClickListener {
            onClickItem(b.kycBanksSheetOption2TV.text.toString())
        }

        b.kycBanksSheetOption3TV.setOnClickListener {
            onClickItem(b.kycBanksSheetOption3TV.text.toString())
        }

        b.kycBanksSheetOption4TV.setOnClickListener {
            onClickItem(b.kycBanksSheetOption4TV.text.toString())
        }

        b.kycBanksSheetOption5TV.setOnClickListener {
            onClickItem(b.kycBanksSheetOption5TV.text.toString())
        }

        b.kycBanksSheetOption6TV.setOnClickListener {
            onClickItem(b.kycBanksSheetOption6TV.text.toString())
        }

        b.kycBanksSheetOption7TV.setOnClickListener {
            onClickItem(b.kycBanksSheetOption7TV.text.toString())
        }

        b.kycBanksSheetOption8TV.setOnClickListener {
            onClickItem(b.kycBanksSheetOption8TV.text.toString())
        }
    }

    private fun onClickItem(bankName: String) {
        sheetCallback.onBankSelected(bankName)
        dismiss()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as KycYourInfoInterface
    }

    companion object {
        const val TAG = "kycBanksSheet"
    }
}