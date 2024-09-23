package com.afrimax.paymaart.common.presentation.ui.text_field.basic

import android.content.Context
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.Spanned
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.afrimax.paymaart.R
import com.afrimax.paymaart.common.domain.utils.then
import com.afrimax.paymaart.common.presentation.utils.DrawableManager
import com.afrimax.paymaart.common.presentation.utils.getAttr
import com.afrimax.paymaart.common.presentation.utils.setOnTextChangedListener
import com.afrimax.paymaart.databinding.ComponentBasicTextFieldBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BasicTextField @JvmOverloads constructor(
    private val cxt: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(cxt, attrs, defStyleAttr) {

    private var b: ComponentBasicTextFieldBinding = ComponentBasicTextFieldBinding.inflate(
        LayoutInflater.from(cxt), this
    )

    //XML attributes with default values
    private var isWarningTextEnabled: Boolean = true
    private var titleText: String = cxt.getString(R.string.title)
    private var hintText: String = cxt.getString(R.string.hint)
    private var digits: String? = null
    private var textTransformation: Int? = 0
    private var isOptionalField: Boolean = false

    private var isTextWatcherEnabled = false
    private var isFocusListenerEnabled = false

    @Inject
    lateinit var drawableManager: DrawableManager

    init {
        // Obtain the styled attributes defined in XML for the component
        val tArray = cxt.obtainStyledAttributes(attrs, R.styleable.BasicTextField, 0, 0)

        // Retrieve XML attributes from the TypedArray and assign default values if not found
        tArray.run {
            titleText = getAttr(R.styleable.BasicTextField_titleText, titleText)
            hintText = getAttr(R.styleable.BasicTextField_hintText, hintText)
            digits = getAttr(R.styleable.BasicTextField_android_digits, digits)
            textTransformation =
                getAttr(R.styleable.BasicTextField_textTransformation, textTransformation)
            isWarningTextEnabled =
                getAttr(R.styleable.BasicTextField_isWarningTextEnabled, isWarningTextEnabled)
            isOptionalField = getAttr(R.styleable.BasicTextField_isOptionalField, isOptionalField)
        }

        // The post block ensures that the following code is executed only at runtime (not during layout preview)
        post {
            //Perform all the UI setup here
            setUpTitle()
            setUpTextField()

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
        b.basicTextFieldTitleTV.text = titleText
        b.basicTextFieldET.hint = hintText
        b.basicTextFieldWarningTV.visibility = if (isWarningTextEnabled) VISIBLE else GONE
    }


    // ====================================================================
    //                        INITIAL SETUP
    // ====================================================================

    private fun setUpTitle() {
        b.basicTextFieldTitleTV.apply {
            text = titleText
        }
    }

    private fun setUpTextField() {
        b.basicTextFieldET.apply {
            hint = hintText

            setOnFocusChangeListener { _, hasFocus ->
                textFieldFocusListener(hasFocus)
            }

            setOnTextChangedListener(onTextChanged = { txt, _, before, _ ->
                textFieldTextChangeListener(before, txt.toString())
            })

            digits?.let { d ->
                filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
                    source?.filter { d.contains(it) }
                })
            }

            // Apply text transformation based on the attribute value
            when (textTransformation) {
                TEXT_CAPITAL_LETTERS -> filters += AllCaps()
                TEXT_SMALL_LETTERS -> filters += object : AllCaps() {
                    override fun filter(
                        source: CharSequence?,
                        start: Int,
                        end: Int,
                        dest: Spanned?,
                        dstart: Int,
                        dend: Int
                    ): CharSequence {
                        return source!!.filterNot { char -> char.isWhitespace() }.toString()
                            .lowercase()
                    }
                }
            }
        }
    }

    // ====================================================================
    //                        HELPER FUNCTIONS
    // ====================================================================

    private fun EditText.textFieldFocusListener(hasFocus: Boolean) {
        if (isFocusListenerEnabled) {
            background = when {
                b.basicTextFieldWarningTV.isVisible -> drawableManager.errorDrawable
                hasFocus -> drawableManager.focusDrawable
                else -> drawableManager.notInFocusDrawable
            }
        }
    }

    private fun EditText.textFieldTextChangeListener(before: Int, txt: String) {
        if (isTextWatcherEnabled) {
            isTextWatcherEnabled = false
            if (before != 1) {
                background = drawableManager.focusDrawable
                b.basicTextFieldWarningTV.visibility = GONE
                setText(txt)
                setSelection(txt.length)
            } else {
                if (txt.isEmpty() && !isOptionalField) {
                    background = drawableManager.errorDrawable
                    b.basicTextFieldWarningTV.apply {
                        visibility = VISIBLE
                        text = cxt.getString(R.string.required_field)
                    }
                }
            }

            isTextWatcherEnabled = true
        }
    }

    private fun EditText.insertCustomText(value: String) {
        setText(value)
        setSelection(value.length)
        when {
            isFocused -> {
                background = drawableManager.focusDrawable
                b.basicTextFieldWarningTV.visibility = GONE
            }

            else -> {
                background = drawableManager.notInFocusDrawable
                b.basicTextFieldWarningTV.visibility = GONE
            }
        }
    }

    // ====================================================================
    //                        PUBLIC PROPERTIES & METHODS
    // ====================================================================

    var text: String
        get() = b.basicTextFieldET.text.toString()
        set(value) {
            isTextWatcherEnabled = false
            b.basicTextFieldET.insertCustomText(value)
            isTextWatcherEnabled = true
        }

    var title: String
        get() = b.basicTextFieldTitleTV.text.toString()
        set(value) {
            b.basicTextFieldTitleTV.text = value
        }

    fun showWarning(warningText: String) {
        if (warningText.isNotEmpty()) {
            b.basicTextFieldWarningTV.apply {
                visibility = VISIBLE
                text = warningText
            }
            b.basicTextFieldET.background = drawableManager.errorDrawable
        }
    }

    fun addTextChangeListener(
        afterTextChanged: (String) -> Unit = { _ -> },
        beforeTextChanged: (CharSequence, Int, Int, Int) -> Unit = { _, _, _, _ -> },
        onTextChanged: (CharSequence, Int, Int, Int) -> Unit = { _, _, _, _ -> }
    ) {
        b.basicTextFieldET.apply {
            setOnTextChangedListener(afterTextChanged = { txt ->
                if (isTextWatcherEnabled) {
                    isTextWatcherEnabled = false
                    afterTextChanged(txt)
                    isTextWatcherEnabled = true
                }
            }, beforeTextChanged = { txt, start, count, after ->
                if (isTextWatcherEnabled) {
                    isTextWatcherEnabled = false
                    beforeTextChanged(txt, start, count, after)
                    isTextWatcherEnabled = true
                }
            }, onTextChanged = { txt, start, before, count ->
                if (isTextWatcherEnabled) {
                    isTextWatcherEnabled = false
                    onTextChanged(txt, start, before, count)
                    isTextWatcherEnabled = true
                }
            })
        }
    }

    companion object {
        const val TEXT_SMALL_LETTERS = 1
        const val TEXT_CAPITAL_LETTERS = 2
    }
}