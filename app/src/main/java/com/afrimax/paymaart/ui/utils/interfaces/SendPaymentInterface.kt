package com.afrimax.paymaart.ui.utils.interfaces

interface SendPaymentInterface {
    fun onPaymentSuccess(successData: Any?)
    fun onPaymentFailure(message: String)
}