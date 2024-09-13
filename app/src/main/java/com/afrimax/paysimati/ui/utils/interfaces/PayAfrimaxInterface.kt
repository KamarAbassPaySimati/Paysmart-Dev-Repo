package com.afrimax.paysimati.ui.utils.interfaces

import com.afrimax.paysimati.data.model.PayAfrimaxResponse

interface PayAfrimaxInterface {
    fun onPayAfrimax(data: PayAfrimaxResponse)
    fun onPayAfrimaxRejected(errorMessage: String)
}