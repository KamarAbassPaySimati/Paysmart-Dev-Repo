package com.afrimax.paymaart.common.domain.utils

import android.util.Patterns

/**Extension function to check if a CharSequence is a valid email address.*/
fun CharSequence?.isValidEmail() =
    !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

