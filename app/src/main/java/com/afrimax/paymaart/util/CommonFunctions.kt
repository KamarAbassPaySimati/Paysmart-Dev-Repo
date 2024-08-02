package com.afrimax.paymaart.util

import android.content.Context
import android.util.TypedValue

fun getInitials(name: String?): String {
    if (name.isNullOrBlank()) return ""
    return name.split(" ")
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
}


fun Context.toDp(value: Int): Int{
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value.toFloat(),
        this.resources.displayMetrics
    ).toInt()
}