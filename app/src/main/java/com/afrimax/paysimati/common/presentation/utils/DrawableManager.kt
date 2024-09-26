package com.afrimax.paysimati.common.presentation.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.afrimax.paysimati.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DrawableManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val focusDrawable: Drawable? by lazy {
        ContextCompat.getDrawable(context, R.drawable.bg_edit_text_focused)
    }

    val errorDrawable: Drawable? by lazy {
        ContextCompat.getDrawable(context, R.drawable.bg_edit_text_error)
    }

    val notInFocusDrawable: Drawable? by lazy {
        ContextCompat.getDrawable(context, R.drawable.bg_edit_text_unfocused)
    }
}