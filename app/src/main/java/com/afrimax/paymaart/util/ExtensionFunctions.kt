package com.afrimax.paymaart.util

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

fun Context.getStringExt(resource: Int): String = ContextCompat.getString(this, resource)

fun Context.getDrawableExt(resource: Int): Drawable? = ContextCompat.getDrawable(this, resource)