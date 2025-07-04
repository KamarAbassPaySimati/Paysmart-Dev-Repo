package com.afrimax.paysimati.common.presentation.ui.button.primary

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.R
import com.afrimax.paysimati.common.domain.utils.then
import com.afrimax.paysimati.common.presentation.utils.getAttr
import com.afrimax.paysimati.databinding.ComponentPrimaryButtonBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PrimaryButton @JvmOverloads constructor(
    private val cxt: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : FrameLayout(cxt, attrs, defStyleAttr) {

    private var b: ComponentPrimaryButtonBinding = ComponentPrimaryButtonBinding.inflate(
        LayoutInflater.from(cxt), this
    )

    //XML attributes with default values
    private var buttonText: String = cxt.getString(R.string.button)
    private var buttonIcon: Drawable? = null

    init {
        // Obtain the styled attributes defined in XML for the component
        val tArray = cxt.obtainStyledAttributes(attrs, R.styleable.PrimaryButton, 0, 0)

        // Retrieve XML attributes from the TypedArray and assign default values if not found
        tArray.run {
            buttonText = getAttr(R.styleable.PrimaryButton_buttonText, buttonText)
            buttonIcon = getAttr(R.styleable.PrimaryButton_buttonIcon, buttonIcon)
        }

        // The post block ensures that the following code is executed only at runtime (not during layout preview)
        post {
            //Perform all the UI setup here
            setUpButton()
        }

        // This block is executed when in layout editor mode (for design-time preview purposes)
        isInEditMode.then {
            showDisplayData()
        }

        // Always recycle the TypedArray after using it to free up resources
        tArray.recycle()

    }

    private fun showDisplayData() {
        b.primaryButtonTextTV.text = buttonText
        buttonIcon?.let {
            b.primaryButtonTextTV.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
        }
    }

    // ====================================================================
    //                        INITIAL SETUP
    // ====================================================================

    private fun setUpButton() {
        if (b.primaryButtonTextTV.text.isBlank()) text = buttonText
        buttonIcon?.let {
            b.primaryButtonTextTV.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
        }
    }

    // ====================================================================
    //                        PUBLIC PROPERTIES & METHODS
    // ====================================================================

    var text: String
        get() = b.primaryButtonTextTV.text.toString()
        set(value) {
            b.primaryButtonTextTV.text = value
            buttonText = value
        }

    var isButtonEnabled: Boolean
        get() = b.primaryButton.isEnabled
        set(value) {
            b.primaryButton.isEnabled = value
        }

    fun setOnClickListener(listener: () -> Job?) {
        b.primaryButton.setOnClickListener {
            val lifecycleOwner = if (cxt is LifecycleOwner) cxt else findViewTreeLifecycleOwner()
            lifecycleOwner?.let {
                lifecycleOwner.lifecycleScope.launch {
                    // Show the loader
                    b.primaryButtonLoader.visibility = VISIBLE
                    b.primaryButtonTextTV.text = cxt.getString(R.string.empty_string)

                    if (cxt is Activity) {
                        cxt.window.setFlags(
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        )
                    }

                    // Execute the listener and await its completion
                    listener()?.join()  // Wait for the API call to complete

                    if (cxt is Activity) cxt.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    // Hide the loader after execution
                    b.primaryButtonLoader.visibility = GONE
                    b.primaryButtonTextTV.text = buttonText

                }
            }
        }
    }
}