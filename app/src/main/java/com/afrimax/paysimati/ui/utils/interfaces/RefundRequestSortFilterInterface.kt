package com.afrimax.paysimati.ui.utils.interfaces

import com.afrimax.paysimati.ui.refundrequest.FilterParameter

interface RefundRequestSortFilterInterface {
    fun onSortParameterSelected(type: Int)
    fun onFilterParameterSelected(types: List<FilterParameter>)
}