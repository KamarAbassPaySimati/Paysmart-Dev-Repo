package com.afrimax.paysimati.main.data.utils

fun mapToExportTimePeriod(selectedOption: Int): String? {
    return when (selectedOption) {
        1 -> TODAY
        2 -> YESTERDAY
        3 -> LAST_7_DAYS
        4 -> THIS_MONTH
        5 -> LAST_MONTH
        6 -> LAST_60_DAYS
        7 -> LAST_90_DAYS
        else -> null
    }
}