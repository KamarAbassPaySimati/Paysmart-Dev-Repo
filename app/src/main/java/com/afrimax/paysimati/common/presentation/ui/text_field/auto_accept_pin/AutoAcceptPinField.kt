package com.afrimax.paysimati.common.presentation.ui.text_field.auto_accept_pin

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.R
import com.afrimax.paysimati.common.domain.utils.then
import com.afrimax.paysimati.common.presentation.utils.getAttr
import com.afrimax.paysimati.common.presentation.utils.setOnTextChangedListener
import com.afrimax.paysimati.databinding.ComponentAutoAcceptPinFieldBinding
import com.afrimax.paysimati.util.MaskPinTransformation
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AutoAcceptPinField @JvmOverloads constructor(
    private val cxt: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(cxt, attrs, defStyleAttr) {

    private var b: ComponentAutoAcceptPinFieldBinding = ComponentAutoAcceptPinFieldBinding.inflate(
        LayoutInflater.from(cxt), this
    )

    //XML attributes with default values
    private var isWarningTextEnabled: Boolean = true
    private var titleText: String = cxt.getString(R.string.title)
    private var titleTint: Int = ContextCompat.getColor(cxt, R.color.neutralGreyPrimaryText)

    private var isTextWatcherEnabled = false
    private var isFocusListenerEnabled = false

    init {
        // Obtain the styled attributes defined in XML for the component
        val tArray = cxt.obtainStyledAttributes(attrs, R.styleable.AutoAcceptPinField, 0, 0)

        // Retrieve XML attributes from the TypedArray and assign default values if not found
        tArray.run {
            titleText = getAttr(R.styleable.AutoAcceptPinField_titleText, titleText)
            titleTint = getAttr(R.styleable.AutoAcceptPinField_titleTint, titleTint)
            isWarningTextEnabled =
                getAttr(R.styleable.AutoAcceptPinField_isWarningTextEnabled, isWarningTextEnabled)
        }

        // The post block ensures that the following code is executed only at runtime (not during layout preview)
        post {
            //Perform all the UI setup here
            setUpTitle()

            //enable focus & text change listeners
            isTextWatcherEnabled = true
            isFocusListenerEnabled = true
        }

        // This block is executed when in layout editor mode (for design-time preview purposes)
        isInEditMode.then {
            showDisplayData()
        }

        // Always recycle the TypedArray after using it to free up resources
        tArray.recycle()
    }

    private fun showDisplayData() {
        b.autoAcceptPinFieldTitleTV.apply {
            text = titleText
            setTextColor(titleTint)
        }
        b.autoAcceptPinFieldWarningTV.visibility = if (isWarningTextEnabled) VISIBLE else GONE
    }

    // ====================================================================
    //                        INITIAL SETUP
    // ====================================================================

    private fun setUpTitle() {
        b.autoAcceptPinFieldTitleTV.apply {
            text = titleText
            setTextColor(titleTint)
        }
    }

    // ====================================================================
    //                        PUBLIC PROPERTIES & METHODS
    // ====================================================================

    var title: String
        get() = b.autoAcceptPinFieldTitleTV.text.toString()
        set(value) {
            b.autoAcceptPinFieldTitleTV.text = value
        }

    val text get() = b.autoAcceptPinFieldET.text.toString()

    fun onPinEntered(block: () -> Job?) {
        b.autoAcceptPinFieldET.apply {
            transformationMethod = MaskPinTransformation()

            setOnTextChangedListener(afterTextChanged = { text ->
                when (text.length) {
                    0 -> if (isWarningTextEnabled) {
                        b.autoAcceptPinFieldWarningTV.apply {
                            visibility = VISIBLE
                            this.text = cxt.getString(R.string.required_field)
                        }
                    }

                    6 -> {
                        val lifecycleOwner = findViewTreeLifecycleOwner()
                        lifecycleOwner?.let {
                            lifecycleOwner.lifecycleScope.launch {
                                //show loader
                                b.autoAcceptPinFieldWarningTV.visibility = GONE
                                b.autoAcceptPinFieldLoaderLottie.visibility = VISIBLE
                                this@AutoAcceptPinField.isClickable = false

                                block()?.join()

                                //hide loader
                                b.autoAcceptPinFieldLoaderLottie.visibility = GONE
                                this@AutoAcceptPinField.isClickable = true
                            }
                        }
                    }

                    else -> {
                        b.autoAcceptPinFieldWarningTV.visibility = GONE
                    }
                }
            })
        }
    }

    fun showWarning(warningText: String) {
        if (warningText.isNotEmpty()) {
            b.autoAcceptPinFieldWarningTV.apply {
                visibility = VISIBLE
                text = warningText
            }
        }
    }
}