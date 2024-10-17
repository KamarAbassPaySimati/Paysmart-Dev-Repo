package com.afrimax.paysimati.main.ui.wallet_statement.choose_export_type

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paysimati.R
import com.afrimax.paysimati.databinding.SheetExportTypeBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Job

class ExportTypeSheet(
    private val downloadPdf: () -> Job?, private val downloadCsv: () -> Job?
) : BottomSheetDialogFragment() {

    private lateinit var b: SheetExportTypeBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheet = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view = View.inflate(context, R.layout.sheet_export_type, null)
        bottomSheet.setContentView(view)

        bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        return bottomSheet
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = SheetExportTypeBinding.inflate(inflater, container, false)

        setUpListeners()

        return b.root
    }


    private fun setUpListeners() {

        b.exportTypeSheetCloseButton.setOnClickListener {
            dismiss()
        }

        b.exportTypeSheetPdfIB.setOnClickListener {
            downloadPdf()
            dismiss()
        }

        b.exportTypeSheetCsvIB.setOnClickListener {
            downloadCsv()
            dismiss()
        }
    }

    companion object {
        const val TAG = "DashboardTimePeriodSheet"
    }
}