package com.afrimax.paysimati.ui.utils.interfaces

interface KycYourInfoInterface {
    fun onIndustrySectorSelected(industrySector: String)
    fun onMonthlyIncomeSelected(monthlyIncome: String)
    fun onMonthlyWithdrawalSelected(monthlyWithdrawal: String)
    fun onBankSelected(bankName:String)
}