package com.afrimax.paysimati.common.presentation.utils

object PhoneNumberFormatter {

    private val countryCodeMap = mapOf(
        "+91" to 10,  // India - Mobile and landline numbers
        "+44" to 10,  // United Kingdom - Mobile numbers are 10 digits; landline and other numbers are 10 digits
        "+1" to 10,   // United States/Canada - Mobile and landline numbers are 10 digits (including the country code)
        "+234" to 10, // Nigeria - Mobile numbers are typically 10 digits
        "+39" to 10,  // Italy - Mobile numbers are 10 digits
        "+265" to 9,  // Malawi - Mobile numbers are 9 digits
        "+27" to 9,  // South Africa - Mobile numbers are 9 digits
        "+46" to 9   // Sweden - Mobile numbers are 9 digits
    )


    fun format(countryCode: String?, phoneNumber: String?): String? {
        if (countryCode != null && phoneNumber != null) {
            val digits = phoneNumber.replace(" ", "")
                .replaceFirst("^0+".toRegex(), "")// Remove leading zeros

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
        return null
    }

    fun formatWholeNumber(phoneNumberWithCountryCode: String?): String? {
        if (phoneNumberWithCountryCode != null) {
            return when {
                phoneNumberWithCountryCode.startsWith("+91") -> formatWithCountryCode(
                    "+91", phoneNumberWithCountryCode, ::formatIndiaPhoneNumber
                )

                phoneNumberWithCountryCode.startsWith("+44") -> formatWithCountryCode(
                    "+44", phoneNumberWithCountryCode, ::formatUKPhoneNumber
                )

                phoneNumberWithCountryCode.startsWith("+1") -> formatWithCountryCode(
                    "+1", phoneNumberWithCountryCode, ::formatUSPhoneNumber
                )

                phoneNumberWithCountryCode.startsWith("+234") -> formatWithCountryCode(
                    "+234", phoneNumberWithCountryCode, ::formatNigeriaPhoneNumber
                )

                phoneNumberWithCountryCode.startsWith("+39") -> formatWithCountryCode(
                    "+39", phoneNumberWithCountryCode, ::formatItalyPhoneNumber
                )

                phoneNumberWithCountryCode.startsWith("+27") -> formatWithCountryCode(
                    "+27", phoneNumberWithCountryCode, ::formatSouthAfricaPhoneNumber
                )

                phoneNumberWithCountryCode.startsWith("+46") -> formatWithCountryCode(
                    "+46", phoneNumberWithCountryCode, ::formatSwedenPhoneNumber
                )

                //default is malawi
                else -> formatWithCountryCode(
                    "+265", phoneNumberWithCountryCode, ::formatMalawiPhoneNumber
                )
            }
        }
        return null
    }

    fun isValidPhoneNumber(countryCode: String, phoneNumber: String): Boolean {
        return countryCodeMap.getOrDefault(countryCode, -1) == phoneNumber.length
    }

    // ====================================================================
    //                        HELPER FUNCTIONS
    // ====================================================================

    private fun formatWithCountryCode(
        countryCode: String, phoneNumber: String, formatter: (String) -> String
    ): String {
        return "$countryCode ${formatter(phoneNumber.removePrefix(countryCode).trim())}"
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
            digits.length > 6 -> {
                "${digits.substring(0, 3)} ${digits.substring(3, 6)} ${digits.substring(6)}"
            }

            digits.length > 3 -> {
                "${digits.substring(0, 3)} ${digits.substring(3)}"
            }

            else -> {
                digits
            }
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