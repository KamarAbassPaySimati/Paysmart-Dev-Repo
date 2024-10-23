package com.afrimax.paysimati.common.presentation.ui.bar.basic_top

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.R
import com.afrimax.paysimati.common.domain.utils.then
import com.afrimax.paysimati.common.presentation.utils.getAttr
import com.afrimax.paysimati.databinding.ComponentBasicTopBarBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BasicTopBar @JvmOverloads constructor(
    private val cxt: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(cxt, attrs, defStyleAttr) {

    private val b: ComponentBasicTopBarBinding = ComponentBasicTopBarBinding.inflate(
        LayoutInflater.from(cxt), this
    )

    //XML attributes with default values
    private var titleText: String = cxt.getString(R.string.empty_string)
    private var leftIcon: Drawable? = ContextCompat.getDrawable(cxt, R.drawable.ic_arrow_back)
    private var rightFirstIcon: Drawable? = null
    private var rightSecondIcon: Drawable? = null
    private var bgTint: Int = ContextCompat.getColor(cxt, R.color.transparent)
    private var contentTint: Int = ContextCompat.getColor(cxt, R.color.neutralGreyPrimaryText)

    init {
        // Obtain the styled attributes defined in XML for the component
        val tArray = cxt.obtainStyledAttributes(attrs, R.styleable.BasicTopBar, 0, 0)

        // Retrieve XML attributes from the TypedArray and assign default values if not found
        tArray.run {
            titleText = getAttr(R.styleable.BasicTopBar_titleText, titleText)
            leftIcon = getAttr(R.styleable.BasicTopBar_leftIcon, leftIcon)
            rightFirstIcon = getAttr(R.styleable.BasicTopBar_rightFirstIcon, rightFirstIcon)
            rightSecondIcon = getAttr(R.styleable.BasicTopBar_rightSecondIcon, rightSecondIcon)
            bgTint = getAttr(R.styleable.BasicTopBar_bgTint, bgTint)
            contentTint = getAttr(R.styleable.BasicTopBar_contentTint, contentTint)
        }

        // The post block ensures that the following code is executed only at runtime (not during layout preview)
        post {
            //Perform all the UI setup here
            setUpBackground()
            setUpTitle()
            setUpLeftIcon()
            setUpRightFirstIcon()
            setUpRightSecondIcon()
        }

        // This block is executed when in layout editor mode (for design-time preview purposes)
        isInEditMode.then {
            showDisplayData()
        }

        // Always recycle the TypedArray after using it to free up resources
        tArray.recycle()
    }

    private fun showDisplayData() {
        b.basicTopBar.setBackgroundColor(bgTint)

        titleText.isNotBlank().then {
            b.basicTopBarTitleTV.apply {
                visibility = VISIBLE
                text = titleText
                setTextColor(contentTint)
            }
        }

        leftIcon?.let {
            b.basicTopBarLeftIB.apply {
                visibility = VISIBLE
                setColorFilter(contentTint)
                setImageDrawable(it)
            }
        }

        rightFirstIcon?.let {
            b.basicTopBarRightFirstIB.apply {
                visibility = VISIBLE
                setColorFilter(contentTint)
                setImageDrawable(it)
            }
        }

        rightSecondIcon?.let {
            b.basicTopBarRightSecondIB.apply {
                visibility = VISIBLE
                setColorFilter(contentTint)
                setImageDrawable(it)
            }
        }
    }

    // ====================================================================
    //                        INITIAL SETUP
    // ====================================================================

    private fun setUpBackground() {
        b.basicTopBar.apply {
            setBackgroundColor(bgTint)
        }
    }

    private fun setUpTitle() {
        b.basicTopBarTitleTV.apply {
            if (text.isBlank()) title = titleText
            setTextColor(contentTint)
        }
    }

    private fun setUpLeftIcon() {
        leftIcon?.let {
            b.basicTopBarLeftIB.apply {
                visibility = VISIBLE
                setColorFilter(contentTint)
                setImageDrawable(it)
            }
        }
    }

    private fun setUpRightFirstIcon() {
        rightFirstIcon?.let {
            b.basicTopBarRightFirstIB.apply {
                visibility = VISIBLE
                setColorFilter(contentTint)
                setImageDrawable(it)
            }
        }
    }

    private fun setUpRightSecondIcon() {
        rightSecondIcon?.let {
            b.basicTopBarRightSecondIB.apply {
                visibility = VISIBLE
                setColorFilter(contentTint)
                setImageDrawable(it)
            }
        }
    }

    // ====================================================================
    //                        PUBLIC PROPERTIES & METHODS
    // ====================================================================

    var title: String
        get() = b.basicTopBarTitleTV.text.toString()
        set(value) {
            value.isNotBlank().then {
                b.basicTopBarTitleTV.apply {
                    visibility = VISIBLE
                    text = value
                }
            }
        }


    fun onClickLeftIcon(block: () -> Job?) {
        if (cxt is LifecycleOwner) {
            b.basicTopBarLeftIB.setOnClickListener {
                cxt.lifecycleScope.launch {
                    block()?.join()
                }
            }
        }
    }

    fun onClickRightFirstIcon(block: () -> Job?) {
        if (cxt is LifecycleOwner) {
            b.basicTopBarRightFirstIB.setOnClickListener {
                cxt.lifecycleScope.launch {
                    block()?.join()
                }
            }
        }
    }

    fun onClickRightSecondIcon(block: () -> Job?) {
        if (cxt is LifecycleOwner) {
            b.basicTopBarRightSecondIB.setOnClickListener {
                cxt.lifecycleScope.launch {
                    block()?.join()
                }
            }
        }
    }
}