package com.afrimax.paymaart.ui.utils.interfaces

import com.afrimax.paymaart.ui.refundrequest.FilterParameter

interface RefundRequestSortFilterInterface {
    fun onSortParameterSelected(type: Int)
    fun onFilterParameterSelected(types: List<FilterParameter>)
}