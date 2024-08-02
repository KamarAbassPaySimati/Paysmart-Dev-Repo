package com.afrimax.paymaart.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val DECIMAL_FORMAT_LONG = DecimalFormat("#,###.00")
private val DECIMAL_FORMAT_SHORT = DecimalFormat("0.00")
/**
    fun getFormattedAmount(amount: Double?): String{
        if (amount == null) {
            return "0.00"
        }
        return if (amount < 1000.00) {
            DECIMAL_FORMAT_SHORT.format(amount)
        } else {
            DECIMAL_FORMAT_LONG.format(amount)
        }
    }

    fun getFormattedAmount(amount: Int?): String{
        if (amount == null) {
            return "0.00"
        }
        val newAmount = amount.toDouble()
        return if (newAmount < 1000) {
            DECIMAL_FORMAT_SHORT.format(amount)
        } else {
            DECIMAL_FORMAT_LONG.format(amount)
        }
    }

    fun getFormattedAmount(amount: String?): String{
        if (amount == null) {
            return "0.00"
        }
        val newAmount = amount.toDouble()
        return if (newAmount < 1000) {
            DECIMAL_FORMAT_SHORT.format(amount)
        } else {
            DECIMAL_FORMAT_LONG.format(amount)
        }
    }

 */

fun <T> getFormattedAmount(amount: T?): String {
    if (amount == null) return "0.00"

    val newAmount: Double = when (amount) {
        is Double -> amount
        is Int -> amount.toDouble()
        is String -> amount.toDoubleOrNull() ?: 0.00
        else -> 0.00
    }

    return if (newAmount < 1000) {
        DECIMAL_FORMAT_SHORT.format(newAmount)
    } else {
        DECIMAL_FORMAT_LONG.format(newAmount)
    }
}


fun formatEpochTime(timestamp: Long?) : String {
    if (timestamp == null) {
        return "-"
    }
    val date = Date(timestamp * 1000)
    return dateFormat.format(date)
}

fun <T> formatEpochTimeTwo(timeStamp: T?): String {
    if (timeStamp == null) return ""
    val newTimeStamp = when (timeStamp) {
        is Long -> timeStamp
        is String -> timeStamp.toLongOrNull() ?: 0L
        else -> 0L
    }
    val date = Date(newTimeStamp * 1000)
    return dateFormatTwo.format(date)
}

//fun formatEpochTimeTwo(timestamp: Long?) : String {
//    if (timestamp == null) {
//        return "-"
//    }
//    val date = Date(timestamp * 1000)
//    return dateFormatTwo.format(date)
//}

val dateFormat: SimpleDateFormat
    get() = SimpleDateFormat("dd MMM yyyy, HH:mm 'hours'", Locale.getDefault())

val dateFormatTwo: SimpleDateFormat
    get() = SimpleDateFormat("dd MMM yy", Locale.getDefault())
