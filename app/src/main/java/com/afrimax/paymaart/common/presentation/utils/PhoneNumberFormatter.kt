package com.afrimax.paymaart.common.presentation.utils

object PhoneNumberFormatter {


    fun format(countryCode: String, phoneNumber: String): String {
        val digits =
            phoneNumber.replace(" ", "").replaceFirst("^0+".toRegex(), "")// Remove leading zeros

        return when (countryCode) {
            "+91" -> formatIndiaPhoneNumber(digits)
            "+44" -> formatUKPhoneNumber(digits)
            "+1" -> formatUSPhoneNumber(digits)
            "+234" -> formatNigeriaPhoneNumber(digits)
            "+39" -> formatItalyPhoneNumber(digits)
            "+265" -> formatMalawiPhoneNumber(digits)
            "+27" -> formatSouthAfricaPhoneNumber(digits)
            "+46" -> formatSwedenPhoneNumber(digits)
            else -> digits
        }
    }


    private fun formatIndiaPhoneNumber(digits: String): String {
        return if (digits.length > 5) {
            "${digits.substring(0, 5)} ${digits.substring(5)}"
        } else {
            digits
        }
    }

    private fun formatUKPhoneNumber(digits: String): String {
        return when {
            digits.length > 4 -> "${digits.substring(0, 4)} ${digits.substring(4)}"
            else -> digits
        }
    }

    private fun formatUSPhoneNumber(digits: String): String {
        return when {
            digits.length > 6 -> "${digits.substring(0, 3)} ${
                digits.substring(
                    3, 6
                )
            } ${digits.substring(6)}"

            digits.length > 3 -> "${digits.substring(0, 3)} ${digits.substring(3)}"
            else -> digits
        }
    }

    private fun formatNigeriaPhoneNumber(digits: String): String {
        return when {
            digits.length > 6 -> "${digits.substring(0, 3)} ${
                digits.substring(
                    3, 6
                )
            } ${digits.substring(6)}"

            digits.length > 3 -> "${digits.substring(0, 3)} ${digits.substring(3)}"
            else -> digits
        }
    }

    private fun formatItalyPhoneNumber(digits: String): String {
        return when {
            digits.length > 6 -> "${digits.substring(0, 3)} ${
                digits.substring(
                    3, 6
                )
            } ${digits.substring(6)}"

            digits.length > 3 -> "${digits.substring(0, 3)} ${digits.substring(3)}"
            else -> digits
        }
    }

    private fun formatMalawiPhoneNumber(digits: String): String {
        return when {
            digits.length > 5 -> "${digits.substring(0, 2)} ${
                digits.substring(
                    2, 5
                )
            } ${digits.substring(5)}"

            digits.length > 2 -> "${digits.substring(0, 2)} ${digits.substring(2)}"
            else -> digits
        }
    }

    private fun formatSouthAfricaPhoneNumber(digits: String): String {
        return when {
            digits.length >= 5 -> "${digits.substring(0, 2)} ${
                digits.substring(
                    2, 5
                )
            } ${digits.substring(5)}"

            digits.length > 2 -> "${digits.substring(0, 2)} ${digits.substring(2)}"
            else -> digits
        }
    }

    private fun formatSwedenPhoneNumber(digits: String): String {
        return when {
            digits.length >= 7 -> "${digits.substring(0, 2)} ${
                digits.substring(
                    2, 5
                )
            } ${digits.substring(5, 7)} ${digits.substring(7)}"

            digits.length >= 5 -> "${digits.substring(0, 2)} ${
                digits.substring(
                    2, 5
                )
            } ${digits.substring(5)}"

            digits.length > 2 -> "${digits.substring(0, 2)} ${digits.substring(2)}"
            else -> digits
        }
    }
}