package com.afrimax.paysimati.common.presentation.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class UiDrawable : Parcelable {

    data class Dynamic(val url: String) : UiDrawable()
    class Resource(@DrawableRes val id: Int) : UiDrawable()

    fun asDrawable(context: Context): Drawable? {
        return when (this) {
            is Resource -> ContextCompat.getDrawable(context, id)
            is Dynamic -> null
        }
    }

    fun asImage(context: Context, imageView: ShapeableImageView) {
        when (this) {
            is Dynamic -> Glide.with(context).load(url).into(imageView)
            is Resource -> Glide.with(context).load(id).into(imageView)
        }
    }
}