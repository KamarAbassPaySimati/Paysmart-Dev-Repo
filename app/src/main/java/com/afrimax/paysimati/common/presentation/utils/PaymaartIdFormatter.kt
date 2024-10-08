package com.afrimax.paysimati.common.presentation.utils

object PaymaartIdFormatter {

    fun formatAgentId(id: String): String {
        return if (id.length == 9) {
            "${id.substring(0, 3)} ${id.substring(3, 7)} ${id.substring(7, 9)}"
        } else {
            id // Return the original string if it doesn't match the expected length
        }
    }

    fun formatCustomerId(id: String): String {
        return if (id.length == 11) {
            "${id.substring(0, 3)} ${id.substring(3, 7)} ${id.substring(7, 11)}"
        } else {
            id
        }
    }

    fun formatMerchantId(id: String): String {
        return if (id.length == 10) {
            "${id.substring(0, 3)} ${id.substring(3, 7)} ${id.substring(7, 10)}"
        } else if (id.length == 8) {
            //Merchant account of paymaart only has 8 chars
            "${id.substring(0, 3)} ${id.substring(3, 7)} ${id.substring(7, 8)}"
        } else {
            id
        }
    }

    fun formatAdminId(id: String): String {
        return if (id.length == 8) {
            "${id.substring(0, 4)} ${id.substring(4, 8)} ${id.substring(7, 11)}"
        } else {
            id
        }
    }

    fun formatId(id: String?): String? {
        return when {
            id == null -> null
            id.startsWith("AGT") -> formatAgentId(id)
            id.startsWith("CMR") -> formatCustomerId(id)
            id.startsWith("MCT") -> formatMerchantId(id)
            id.startsWith("PMT") -> formatAdminId(id)
            else -> id
        }
    }
}