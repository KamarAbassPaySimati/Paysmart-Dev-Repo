package com.afrimax.paysimati.common.presentation.utils

object NameFormatter {

    fun shortName(name: String): String {
        val nameList = name.uppercase().split(" ")
        val sb = StringBuilder()
        nameList.forEach { item ->
            val letter = item.getOrNull(0)
            letter?.let { sb.append(letter) }
        }

        val shortName =
            if (sb.toString().length >= 3) sb.toString().substring(0, 3) else sb.toString()
        return shortName
    }
}