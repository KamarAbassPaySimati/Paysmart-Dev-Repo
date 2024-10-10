package com.afrimax.paysimati.ui.utils.interfaces

interface TransactionHistoryInterface {
    fun clearAllFilters()
    fun onFiltersApplied(time: Int, transactionType: String)
}