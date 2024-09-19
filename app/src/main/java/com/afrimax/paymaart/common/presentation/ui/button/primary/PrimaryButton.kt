package com.afrimax.paymaart.common.presentation.ui.button.primary

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.afrimax.paymaart.R
import com.afrimax.paymaart.common.domain.utils.then
import com.afrimax.paymaart.common.presentation.utils.getAttr
import com.afrimax.paymaart.databinding.ComponentPrimaryButtonBinding
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

    init {
        // Obtain the styled attributes defined in XML for the component
        val tArray = cxt.obtainStyledAttributes(attrs, R.styleable.PrimaryButton, 0, 0)

        // Retrieve XML attributes from the TypedArray and assign default values if not found
        tArray.run {
            buttonText = getAttr(R.styleable.PrimaryButton_buttonText, buttonText)
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
        b.primaryButton.text = buttonText
    }

    // ====================================================================
    //                        INITIAL SETUP
    // ====================================================================

    private fun setUpButton() {
        b.primaryButton.apply {
            text = buttonText
        }
    }

    fun setOnClickListener(listener: () -> Job?) {
        b.primaryButton.setOnClickListener {
            if (cxt is LifecycleOwner) {
                cxt.lifecycleScope.launch {
                    // Show the loader
                    b.primaryButtonLoader.visibility = VISIBLE
                    b.primaryButton.text = cxt.getString(R.string.empty_string)

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
                    b.primaryButton.text = buttonText

                }
            }
        }
    }
}