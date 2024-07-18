package com.afrimax.paymaart.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getFormattedAmount(amount: Double?): String{
    if (amount == null) {
        return "0.00"
    }
    return if (amount < 1000.00) {
        decimalFormatSmall.format(amount)
    } else {
        decimalFormatLarge.format(amount)
    }
}

fun getFormattedAmount(amount: Int?): String{
    if (amount == null) {
        return "0.00"
    }
    val newAmount = amount.toDouble()
    return if (newAmount < 1000) {
        decimalFormatSmall.format(amount)
    } else {
        decimalFormatLarge.format(amount)
    }
}

fun getFormattedAmount(amount: String?): String{
    if (amount == null) {
        return "0.00"
    }
    val newAmount = amount.toDouble()
    return if (newAmount < 1000) {
        decimalFormatSmall.format(amount)
    } else {
        decimalFormatLarge.format(amount)
    }
}

fun formatEpochTime(timestamp: Long?) : String {
    if (timestamp == null) {
        return "-"
    }
    val dateFormat = dateFormat
    val date = Date(timestamp * 1000)
    return dateFormat.format(date)
}

fun formatEpochTimeTwo(timestamp: Long?) : String {
    if (timestamp == null) {
        return "-"
    }
    val dateFormat = dateFormatTwo
    val date = Date(timestamp * 1000)
    return dateFormat.format(date)
}

val decimalFormatLarge: DecimalFormat
    get() = DecimalFormat("#,###.00")

val decimalFormatSmall: DecimalFormat
    get() = DecimalFormat("0.00")

val dateFormat: SimpleDateFormat
    get() = SimpleDateFormat("dd MMM yyyy, HH:mm 'hours'", Locale.getDefault())

val dateFormatTwo: SimpleDateFormat
    get() = SimpleDateFormat("dd MMM yy", Locale.getDefault())
