package com.afrimax.paysimati.common.presentation.utils

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class UiText : Parcelable {

    data class Dynamic(val value: String) : UiText()
    class Resource(@StringRes val id: Int, val args: Array<Parcelable> = arrayOf()) : UiText()

    fun asString(context: Context): String {
        return when (this) {
            is Dynamic -> value
            is Resource -> context.getString(id, *args)
        }
    }

    /**
     * Checks if the string value is empty.
     * Note: This method is applicable only for Dynamic values and does not apply to Resource values.
     */
    fun isEmpty(): Boolean {
        return when (this) {
            is Dynamic -> value.isEmpty()
            is Resource -> false // Resources should generally not be empty
        }
    }
}