package com.afrimax.paysimati.ui.utils.bottomsheets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paysimati.databinding.KycOccupationEmployedSheetBinding
import com.afrimax.paysimati.ui.utils.interfaces.KycOccupationEmployedInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class KycOccupationEmployedSheet: BottomSheetDialogFragment() {
    private lateinit var b: KycOccupationEmployedSheetBinding

    private lateinit var sheetCallback: KycOccupationEmployedInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = KycOccupationEmployedSheetBinding.inflate(inflater, container, false)

        setUpListeners()

        return b.root
    }

    private fun setUpListeners() {

        b.kycOccupationEmployedSheetCloseButton.setOnClickListener {
            dismiss()
        }

        b.kycOccupationEmployedSheetOption1TV.setOnClickListener {
            sheetCallback.onEmployedItemSelected(b.kycOccupationEmployedSheetOption1TV.text.toString())
            dismiss()
        }

        b.kycOccupationEmployedSheetOption2TV.setOnClickListener {
            sheetCallback.onEmployedItemSelected(b.kycOccupationEmployedSheetOption2TV.text.toString())
            dismiss()
        }

        b.kycOccupationEmployedSheetOption3TV.setOnClickListener {
            sheetCallback.onEmployedItemSelected(b.kycOccupationEmployedSheetOption3TV.text.toString())
            dismiss()
        }

        b.kycOccupationEmployedSheetOption4TV.setOnClickListener {
            sheetCallback.onEmployedItemSelected(b.kycOccupationEmployedSheetOption4TV.text.toString())
            dismiss()
        }

        b.kycOccupationEmployedSheetOption5TV.setOnClickListener {
            sheetCallback.onEmployedItemSelected(b.kycOccupationEmployedSheetOption5TV.text.toString())
            dismiss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as KycOccupationEmployedInterface
    }


    companion object {
        const val TAG = "KycOccupationEmployedSheet"
    }
}