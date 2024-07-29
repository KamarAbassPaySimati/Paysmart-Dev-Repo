package com.afrimax.paymaart.ui.paytoaffrimax

data class PayAfrimaxModel(
    val amount: String,
    val txnFee: String,
    val vat: String,
    val afrimaxId: String,
    val afrimaxName: String,
    val customerName: String,
    val customerId: String
)
