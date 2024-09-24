package com.afrimax.paysimati.ui.utils.bottomsheets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paysimati.databinding.KycIndustrySectorSheetBinding
import com.afrimax.paysimati.ui.utils.interfaces.KycYourInfoInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class KycIndustrySectorSheet: BottomSheetDialogFragment() {
    private lateinit var b: KycIndustrySectorSheetBinding

    private lateinit var sheetCallback: KycYourInfoInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = KycIndustrySectorSheetBinding.inflate(inflater, container, false)

        setUpListeners()

        return b.root
    }

    private fun setUpListeners() {

        b.kycIndustrySectorSheetCloseButton.setOnClickListener {
            dismiss()
        }

        b.kycIndustrySectorSheetOption1TV.setOnClickListener {
            sheetCallback.onIndustrySectorSelected(b.kycIndustrySectorSheetOption1TV.text.toString())
            dismiss()
        }

        b.kycIndustrySectorSheetOption2TV.setOnClickListener {
            sheetCallback.onIndustrySectorSelected(b.kycIndustrySectorSheetOption2TV.text.toString())
            dismiss()
        }

        b.kycIndustrySectorSheetOption3TV.setOnClickListener {
            sheetCallback.onIndustrySectorSelected(b.kycIndustrySectorSheetOption3TV.text.toString())
            dismiss()
        }

        b.kycIndustrySectorSheetOption4TV.setOnClickListener {
            sheetCallback.onIndustrySectorSelected(b.kycIndustrySectorSheetOption4TV.text.toString())
            dismiss()
        }

        b.kycIndustrySectorSheetOption5TV.setOnClickListener {
            sheetCallback.onIndustrySectorSelected(b.kycIndustrySectorSheetOption5TV.text.toString())
            dismiss()
        }

        b.kycIndustrySectorSheetOption6TV.setOnClickListener {
            sheetCallback.onIndustrySectorSelected(b.kycIndustrySectorSheetOption6TV.text.toString())
            dismiss()
        }

        b.kycIndustrySectorSheetOption7TV.setOnClickListener {
            sheetCallback.onIndustrySectorSelected(b.kycIndustrySectorSheetOption7TV.text.toString())
            dismiss()
        }

        b.kycIndustrySectorSheetOption8TV.setOnClickListener {
            sheetCallback.onIndustrySectorSelected(b.kycIndustrySectorSheetOption8TV.text.toString())
            dismiss()
        }

        b.kycIndustrySectorSheetOption9TV.setOnClickListener {
            sheetCallback.onIndustrySectorSelected(b.kycIndustrySectorSheetOption9TV.text.toString())
            dismiss()
        }

        b.kycIndustrySectorSheetOption10TV.setOnClickListener {
            sheetCallback.onIndustrySectorSelected(b.kycIndustrySectorSheetOption10TV.text.toString())
            dismiss()
        }

        b.kycIndustrySectorSheetOption11TV.setOnClickListener {
            sheetCallback.onIndustrySectorSelected(b.kycIndustrySectorSheetOption11TV.text.toString())
            dismiss()
        }

        b.kycIndustrySectorSheetOption12TV.setOnClickListener {
            sheetCallback.onIndustrySectorSelected(b.kycIndustrySectorSheetOption12TV.text.toString())
            dismiss()
        }

        b.kycIndustrySectorSheetOption13TV.setOnClickListener {
            sheetCallback.onIndustrySectorSelected(b.kycIndustrySectorSheetOption13TV.text.toString())
            dismiss()
        }

        b.kycIndustrySectorSheetOption14TV.setOnClickListener {
            sheetCallback.onIndustrySectorSelected(b.kycIndustrySectorSheetOption14TV.text.toString())
            dismiss()
        }

        b.kycIndustrySectorSheetOption15TV.setOnClickListener {
            sheetCallback.onIndustrySectorSelected(b.kycIndustrySectorSheetOption15TV.text.toString())
            dismiss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as KycYourInfoInterface
    }


    companion object {
        const val TAG = "KycIndustrySectorSheet"
    }
}