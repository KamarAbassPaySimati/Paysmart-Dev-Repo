package com.afrimax.paymaart.util

fun getInitials(name: String): String {
    if (name.isEmpty()) return ""
    return name.split(" ")
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString(" ")
}