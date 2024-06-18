package com.afrimax.paymaart.ui.utils.bottomsheets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.KycMonthlyIncomeWithdrawlSheetBinding
import com.afrimax.paymaart.ui.utils.interfaces.KycYourInfoInterface
import com.afrimax.paymaart.util.Constants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class KycMonthlyIncomeWithdrawalSheet: BottomSheetDialogFragment() {
    private lateinit var b: KycMonthlyIncomeWithdrawlSheetBinding
    private lateinit var sheetCallback: KycYourInfoInterface
    private lateinit var sheetType: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = KycMonthlyIncomeWithdrawlSheetBinding.inflate(inflater, container, false)

        sheetType = requireArguments().getString(Constants.KYC_INCOME_STATUS_TYPE) ?: ""

        /**WITHDRAWAL <= INCOME*/

        when (sheetType) {
            Constants.KYC_INCOME_STATUS_MONTHLY_INCOME -> {
                b.kycMonthlyIncomeWithdrawalSheetHeaderTV.text =
                    requireContext().getString(R.string.monthly_income)

                //Check whether already selected monthly withdrawal
                val selectedMonthlyWithdrawal =
                    requireArguments().getString(Constants.KYC_SELECTED_MONTHLY_WITHDRAWAL) ?: ""
                showRespectiveIncomes(selectedMonthlyWithdrawal)
            }

            Constants.KYC_INCOME_STATUS_MONTHLY_WITHDRAWAL -> {
                b.kycMonthlyIncomeWithdrawalSheetHeaderTV.text =
                    requireContext().getString(R.string.monthly_withdrawal)

                //Check whether already selected monthly withdrawal
                val selectedMonthlyIncome =
                    requireArguments().getString(Constants.KYC_SELECTED_MONTHLY_INCOME) ?: ""
                showRespectiveWithdrawals(selectedMonthlyIncome)
            }
        }

        setUpListeners()

        return b.root
    }

    private fun showRespectiveIncomes(selectedMonthlyWithdrawal: String) {
        when (selectedMonthlyWithdrawal) {
            getString(R.string.monthly_income_option1) -> {
                //User is withdrawing the least possible amount, so the income can be any of the possible value
                b.kycMonthlyIncomeWithdrawalSheetOption1TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption2TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption3TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption4TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption5TV.visibility = View.VISIBLE
            }

            getString(R.string.monthly_income_option2) -> {
                b.kycMonthlyIncomeWithdrawalSheetOption1TV.visibility = View.GONE
                b.kycMonthlyIncomeWithdrawalSheetOption2TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption3TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption4TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption5TV.visibility = View.VISIBLE
            }

            getString(R.string.monthly_income_option3) -> {
                b.kycMonthlyIncomeWithdrawalSheetOption1TV.visibility = View.GONE
                b.kycMonthlyIncomeWithdrawalSheetOption2TV.visibility = View.GONE
                b.kycMonthlyIncomeWithdrawalSheetOption3TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption4TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption5TV.visibility = View.VISIBLE
            }

            getString(R.string.monthly_income_option4) -> {
                b.kycMonthlyIncomeWithdrawalSheetOption1TV.visibility = View.GONE
                b.kycMonthlyIncomeWithdrawalSheetOption2TV.visibility = View.GONE
                b.kycMonthlyIncomeWithdrawalSheetOption3TV.visibility = View.GONE
                b.kycMonthlyIncomeWithdrawalSheetOption4TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption5TV.visibility = View.VISIBLE
            }

            getString(R.string.monthly_income_option5) -> {
                b.kycMonthlyIncomeWithdrawalSheetOption1TV.visibility = View.GONE
                b.kycMonthlyIncomeWithdrawalSheetOption2TV.visibility = View.GONE
                b.kycMonthlyIncomeWithdrawalSheetOption3TV.visibility = View.GONE
                b.kycMonthlyIncomeWithdrawalSheetOption4TV.visibility = View.GONE
                b.kycMonthlyIncomeWithdrawalSheetOption5TV.visibility = View.VISIBLE
            }
        }
    }

    private fun showRespectiveWithdrawals(selectedIncome: String) {
        when (selectedIncome) {
            getString(R.string.monthly_income_option1) -> {
                //User's income is the least possible amount, so user can only withdraw least amount
                b.kycMonthlyIncomeWithdrawalSheetOption1TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption2TV.visibility = View.GONE
                b.kycMonthlyIncomeWithdrawalSheetOption3TV.visibility = View.GONE
                b.kycMonthlyIncomeWithdrawalSheetOption4TV.visibility = View.GONE
                b.kycMonthlyIncomeWithdrawalSheetOption5TV.visibility = View.GONE
            }

            getString(R.string.monthly_income_option2) -> {
                b.kycMonthlyIncomeWithdrawalSheetOption1TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption2TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption3TV.visibility = View.GONE
                b.kycMonthlyIncomeWithdrawalSheetOption4TV.visibility = View.GONE
                b.kycMonthlyIncomeWithdrawalSheetOption5TV.visibility = View.GONE
            }

            getString(R.string.monthly_income_option3) -> {
                b.kycMonthlyIncomeWithdrawalSheetOption1TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption2TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption3TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption4TV.visibility = View.GONE
                b.kycMonthlyIncomeWithdrawalSheetOption5TV.visibility = View.GONE
            }

            getString(R.string.monthly_income_option4) -> {
                b.kycMonthlyIncomeWithdrawalSheetOption1TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption2TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption3TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption4TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption5TV.visibility = View.GONE
            }

            getString(R.string.monthly_income_option5) -> {
                b.kycMonthlyIncomeWithdrawalSheetOption1TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption2TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption3TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption4TV.visibility = View.VISIBLE
                b.kycMonthlyIncomeWithdrawalSheetOption5TV.visibility = View.VISIBLE
            }
        }
    }

    private fun setUpListeners() {

        b.kycMonthlyIncomeWithdrawalSheetCloseButton.setOnClickListener {
            dismiss()
        }

        b.kycMonthlyIncomeWithdrawalSheetOption1TV.setOnClickListener {
            onClickItem(b.kycMonthlyIncomeWithdrawalSheetOption1TV.text.toString())
        }

        b.kycMonthlyIncomeWithdrawalSheetOption2TV.setOnClickListener {
            onClickItem(b.kycMonthlyIncomeWithdrawalSheetOption2TV.text.toString())
        }

        b.kycMonthlyIncomeWithdrawalSheetOption3TV.setOnClickListener {
            onClickItem(b.kycMonthlyIncomeWithdrawalSheetOption3TV.text.toString())
        }

        b.kycMonthlyIncomeWithdrawalSheetOption4TV.setOnClickListener {
            onClickItem(b.kycMonthlyIncomeWithdrawalSheetOption4TV.text.toString())
        }

        b.kycMonthlyIncomeWithdrawalSheetOption5TV.setOnClickListener {
            onClickItem(b.kycMonthlyIncomeWithdrawalSheetOption5TV.text.toString())
        }
    }

    private fun onClickItem(selectedItem: String) {
        when (sheetType) {
            Constants.KYC_INCOME_STATUS_MONTHLY_INCOME -> {
                sheetCallback.onMonthlyIncomeSelected(selectedItem)
            }

            Constants.KYC_INCOME_STATUS_MONTHLY_WITHDRAWAL -> {
                sheetCallback.onMonthlyWithdrawalSelected(selectedItem)
            }
        }
        dismiss()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as KycYourInfoInterface
    }


    companion object {
        const val TAG = "kycMonthlyIncomeWithdrawalSheet"
    }
}