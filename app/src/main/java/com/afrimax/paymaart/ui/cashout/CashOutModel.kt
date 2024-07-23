package com.afrimax.paymaart.ui.cashout

data class CashOutModel(
    val amount: String,
    val transactionFee: String,
    val vat: String,
    val receiverPaymaartId: String
)
