package com.afrimax.paysimati.ui.chatMerchant.ui

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.afrimax.paysimati.R
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable

@Composable
fun PrimaryLoader(modifier: Modifier = Modifier, context: Context) {
    AndroidView(
        factory = {
            // Create the LottieAnimationView programmatically
            LottieAnimationView(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER
                )
                setAnimation(R.raw.lottie_loader) // Your lottie animation file
                repeatCount = LottieDrawable.INFINITE // Loop the animation
                playAnimation() // Start the animation
            }
        }, modifier = modifier // Equivalent to 100dp width and height
    )
}