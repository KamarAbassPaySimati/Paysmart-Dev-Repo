package com.afrimax.paysimati.common.domain.utils

import android.util.Patterns

/**Extension function to check if a CharSequence is a valid email address.*/
fun CharSequence?.isValidEmail() =
    !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

/**The then extension function for the Boolean type allows conditional execution
 * of a given lambda expression if the Boolean value is true. */
inline fun Boolean.then(block: () -> Unit) {
    if (this) block()
}

