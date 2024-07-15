package com.afrimax.paymaart.util

import java.text.DecimalFormat

fun getFormattedAmount(amount: Double): String{
    return if (amount < 1000.00) {
        decimalFormatSmall.format(amount)
    } else {
        decimalFormatLarge.format(amount)
    }
}

fun getFormattedAmount(amount: Int): String{
    val newAmount = amount.toDouble()
    return if (newAmount < 1000) {
        decimalFormatSmall.format(amount)
    } else {
        decimalFormatLarge.format(amount)
    }
}

fun getFormattedAmount(amount: String): String{
    val newAmount = amount.toDouble()
    return if (newAmount < 1000) {
        decimalFormatSmall.format(amount)
    } else {
        decimalFormatLarge.format(amount)
    }
}

val decimalFormatLarge: DecimalFormat
    get() = DecimalFormat("#,###.00")

val decimalFormatSmall: DecimalFormat
    get() = DecimalFormat("0.00")