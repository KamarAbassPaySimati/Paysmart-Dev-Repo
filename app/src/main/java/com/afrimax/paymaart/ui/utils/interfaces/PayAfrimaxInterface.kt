package com.afrimax.paymaart.ui.utils.interfaces

import com.afrimax.paymaart.data.model.PayAfrimaxResponse

interface PayAfrimaxInterface {
    fun onPayAfrimax(data: PayAfrimaxResponse)
    fun onPayAfrimaxRejected(errorMessage: String)
}