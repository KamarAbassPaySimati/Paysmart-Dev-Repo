package com.afrimax.paymaart.ui.utils.interfaces

interface TransactionHistoryInterface {
    fun clearAllFilters()
    fun onFiltersApplied(time: Int, transactionType: String)
}