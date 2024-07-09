package com.afrimax.paymaart.util

import java.text.DecimalFormat

fun getFormattedAmount(amount: Double): String{
    val decimalFormat = DecimalFormat("#,###.00")

    return if (amount == 0.00) {
        "0.00"
    } else {
        decimalFormat.format(amount)
    }
}