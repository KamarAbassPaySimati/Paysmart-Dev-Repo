package com.afrimax.paymaart.ui.utils.bottomsheets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.TransactionFilterBottomSheetBinding
import com.afrimax.paymaart.ui.utils.interfaces.TransactionHistoryInterface
import com.afrimax.paymaart.util.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TransactionHistorySheet: BottomSheetDialogFragment(){
    private lateinit var b: TransactionFilterBottomSheetBinding
    private lateinit var sheetCallback: TransactionHistoryInterface
    private var time = 60
    private lateinit var type: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = TransactionFilterBottomSheetBinding.inflate(inflater, container, false)
        time = requireArguments().getInt(Constants.TRANSACTION_FILTER_TIME)
        type = requireArguments().getString(Constants.TRANSACTION_FILTER_TYPE) ?: ""
        setUpLayout()
        setUpListeners()
        return b.root
    }

    private fun setUpLayout() {
        //Setup time filter
        when (time) {
            0 -> b.transactionHistorySheetTodayRB.isChecked = true
            1 -> b.transactionHistorySheetYesterdayRB.isChecked = true
            7 -> b.transactionHistorySheetLast7RB.isChecked = true
            30 -> b.transactionHistorySheetLast30RB.isChecked = true
            60 -> b.transactionHistorySheetLast60RB.isChecked = true
        }

        //Setup type filter
        val typeList = type.split(",")
        setTransactionTypeFilter(typeList)
    }

    private fun setUpListeners() {

        b.transactionHistorySheetTimePeriodTV.setOnClickListener {
            onClickTimePeriod()
        }

        b.transactionHistorySheetTransactionTypeTV.setOnClickListener {
            onClickTransactionType()
        }

        b.transactionHistorySheetClearAllButton.setOnClickListener {
            type = ""
            setTransactionTypeFilter(emptyList())
//            sheetCallback.clearAllFilters()
//            dismiss()
        }

        b.transactionHistorySheetApplyButton.setOnClickListener {
            sheetCallback.onFiltersApplied(getSelectedTime(), getSelectedTypes())
            dismiss()
        }
    }

    private fun onClickTimePeriod() {
        b.transactionHistorySheetRG.visibility = View.VISIBLE
        b.transactionHistorySheetTransactionTypeContainer.visibility = View.GONE

        b.transactionHistorySheetTimePeriodTV.background =
            ContextCompat.getDrawable(requireContext(), R.color.highlightedLight)
        b.transactionHistorySheetTransactionTypeTV.background =
            ContextCompat.getDrawable(requireContext(), R.color.transparent)

    }

    private fun onClickTransactionType() {
        b.transactionHistorySheetRG.visibility = View.GONE
        b.transactionHistorySheetTransactionTypeContainer.visibility = View.VISIBLE

        b.transactionHistorySheetTimePeriodTV.background =
            ContextCompat.getDrawable(requireContext(), R.color.transparent)
        b.transactionHistorySheetTransactionTypeTV.background =
            ContextCompat.getDrawable(requireContext(), R.color.highlightedLight)

    }

    private fun getSelectedTime(): Int {
        var selectedTime = 60

        val selectedRB = b.transactionHistorySheetRG.checkedRadioButtonId
        when (selectedRB) {
            R.id.transactionHistorySheetTodayRB -> selectedTime = 0
            R.id.transactionHistorySheetYesterdayRB -> selectedTime = 1
            R.id.transactionHistorySheetLast7RB -> selectedTime = 7
            R.id.transactionHistorySheetLast30RB -> selectedTime = 30
            R.id.transactionHistorySheetLast60RB -> selectedTime = 60
        }
        return selectedTime
    }

    private fun setTransactionTypeFilter(types: List<String>){
        for (type in types) {
            when (type) {
                CASH_IN -> b.transactionHistorySheetCashInCB.isChecked = true
                CASH_OUT -> b.transactionHistorySheetCashOutCB.isChecked = true
                AFRIMAX -> b.transactionHistorySheetPayAfrimaxCB.isChecked = true
                PAYMAART -> b.transactionHistorySheetPayPaymaartCB.isChecked = true
                INTEREST -> b.transactionHistorySheetInterestCB.isChecked = true
                PAY_IN -> b.transactionHistorySheetPayInCB.isChecked = true
                REFUND -> b.transactionHistorySheetRefundCB.isChecked = true
                PERSON -> b.transactionHistorySheetPayPersonCB.isChecked = true
            }
        }
    }

    private fun getSelectedTypes(): String {
        val typeList = ArrayList<String>()

        if (b.transactionHistorySheetCashInCB.isChecked) typeList.add(CASH_IN)
        if (b.transactionHistorySheetCashOutCB.isChecked) typeList.add(CASH_OUT)
        if (b.transactionHistorySheetPayAfrimaxCB.isChecked) typeList.add(AFRIMAX)
        if (b.transactionHistorySheetPayPaymaartCB.isChecked) typeList.add(PAYMAART)
        if (b.transactionHistorySheetPayInCB.isChecked) typeList.add(PAY_IN)
        if (b.transactionHistorySheetRefundCB.isChecked) typeList.add(REFUND)
        if (b.transactionHistorySheetInterestCB.isChecked) typeList.add(INTEREST)
        if (b.transactionHistorySheetPayPersonCB.isChecked) typeList.add(PERSON)

        return typeList.joinToString(",")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as TransactionHistoryInterface
    }

    companion object {
        const val TAG = "TransactionHistorySheet"
        const val CASH_IN = "cash_in"
        const val CASH_OUT = "cashout"
        const val AFRIMAX = "afrimax"
        const val PAY_IN = "pay_in"
        const val REFUND = "refund"
        const val PAYMAART = "paymaart"
        const val INTEREST = "interest"
        const val PERSON = "person"
    }
}