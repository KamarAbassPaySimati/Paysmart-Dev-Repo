package com.afrimax.paymaart.common.presentation.ui.text_field.phone

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.afrimax.paymaart.R
import com.afrimax.paymaart.common.domain.utils.then
import com.afrimax.paymaart.common.presentation.utils.DrawableManager
import com.afrimax.paymaart.common.presentation.utils.PhoneNumberFormatter
import com.afrimax.paymaart.common.presentation.utils.getAttr
import com.afrimax.paymaart.common.presentation.utils.setOnItemSelectedListener
import com.afrimax.paymaart.common.presentation.utils.setOnTextChangedListener
import com.afrimax.paymaart.databinding.ComponentPhoneFieldBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class PhoneField @JvmOverloads constructor(
    private val cxt: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(cxt, attrs, defStyleAttr) {

    private var b: ComponentPhoneFieldBinding = ComponentPhoneFieldBinding.inflate(
        LayoutInflater.from(cxt), this
    )

    //XML attributes with default values
    private var titleText: String = cxt.getString(R.string.title)
    private var hintText: String = cxt.getString(R.string.hint)
    private var isWarningTextEnabled = true
    private var isOptionalField = false

    //Other variables
    private var countryCodes: ArrayList<String> = ArrayList<String>().apply { add("+265") }
    private var isTextWatcherEnabled = false
    private var isFocusListenerEnabled = false
    private var arrayAdapter: ArrayAdapter<String> =
        ArrayAdapter(cxt, R.layout.spinner_country_code, countryCodes)

    @Inject
    lateinit var drawableManager: DrawableManager

    init {
        // Obtain the styled attributes defined in XML for the component
        val tArray = cxt.obtainStyledAttributes(attrs, R.styleable.PhoneField, 0, 0)

        // Retrieve XML attributes from the TypedArray and assign default values if not found
        tArray.run {
            titleText = getAttr(R.styleable.PhoneField_titleText, titleText)
            hintText = getAttr(R.styleable.PhoneField_hintText, hintText)
            isWarningTextEnabled =
                getAttr(R.styleable.PhoneField_isWarningTextEnabled, isWarningTextEnabled)
            isOptionalField = getAttr(R.styleable.PhoneField_isOptionalField, isOptionalField)
        }

        // The post block ensures that the following code is executed only at runtime (not during layout preview)
        post {
            //Perform all the UI setup here
            setUpTitle()
            setUpTextField()
            setUpDropDown()

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
        b.phoneFieldTitleTV.text = titleText
        b.phoneFieldET.hint = hintText
        b.phoneFieldWarningTV.visibility = if (isWarningTextEnabled) VISIBLE else GONE
    }

    // ====================================================================
    //                        INITIAL SETUP
    // ====================================================================

    private fun setUpTitle() {
        b.phoneFieldTitleTV.apply {
            text = titleText
        }
    }

    private fun setUpTextField() {
        b.phoneFieldET.apply {
            setOnClickListener { _ ->
                setSelection(text.length)
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (isFocusListenerEnabled) {
                    b.phoneFieldBox.background = when {
                        b.phoneFieldWarningTV.isVisible -> drawableManager.errorDrawable
                        hasFocus -> drawableManager.focusDrawable
                        else -> drawableManager.notInFocusDrawable
                    }
                }
            }

            setOnTextChangedListener(onTextChanged = { txt, _, before, _ ->
                textFieldTextChangeListener(before, txt.toString())
            })
        }
    }

    private fun setUpDropDown() {
        b.phoneFieldCountryCodeSpinner.apply {
            adapter = arrayAdapter

            setOnItemSelectedListener(onItemSelected = { _, _, position, _ ->
                // Get the selected item
                val selectedCountryCode = adapter.getItem(position) as String

                b.phoneFieldET.filters = getPhoneNumberLength(selectedCountryCode)
                b.phoneFieldET.text?.clear()

                countryCode = selectedCountryCode
            })
        }
    }

    // ====================================================================
    //                        HELPER FUNCTIONS
    // ====================================================================

    private fun EditText.textFieldTextChangeListener(before: Int, txt: String) {
        if (isTextWatcherEnabled) {
            isTextWatcherEnabled = false
            if (before != 1) {
                b.phoneFieldBox.background = drawableManager.focusDrawable
                b.phoneFieldWarningTV.visibility = GONE
                val updatedText =
                    PhoneNumberFormatter.format(countryCode = countryCode, phoneNumber = txt) ?: txt
                setText(updatedText)
                setSelection(updatedText.length)
            } else {
                if (txt.isEmpty() && !isOptionalField) {
                    b.phoneFieldBox.background = drawableManager.errorDrawable
                    b.phoneFieldWarningTV.apply {
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
                b.phoneFieldBox.background = drawableManager.focusDrawable
                b.phoneFieldWarningTV.visibility = GONE
            }

            else -> {
                b.phoneFieldBox.background = drawableManager.notInFocusDrawable
                b.phoneFieldWarningTV.visibility = GONE
            }
        }
    }

    private fun getPhoneNumberLength(countryCode: String): Array<InputFilter.LengthFilter> {
        val maxLength = countryCodeMap.getOrDefault(countryCode, 9)
        return arrayOf(InputFilter.LengthFilter(maxLength))
    }

    // ====================================================================
    //                        PUBLIC METHODS & PROPERTIES
    // ====================================================================

    var text: String
        get() = b.phoneFieldET.text.toString().replace(" ", "")
        set(value) {
            isTextWatcherEnabled = false
            b.phoneFieldET.insertCustomText(value)
            isTextWatcherEnabled = true
        }

    var title: String
        get() = b.phoneFieldTitleTV.text.toString()
        set(value) {
            b.phoneFieldTitleTV.text = value
        }

    var countryCode: String = countryCodes[0]


    fun showWarning(warningText: String) {
        if (warningText.isNotEmpty()) {
            b.phoneFieldWarningTV.apply {
                visibility = VISIBLE
                text = warningText
            }
            b.phoneFieldBox.background = drawableManager.errorDrawable
        }
    }


    fun addTextChangeListener(
        afterTextChanged: (String) -> Unit = { _ -> },
        beforeTextChanged: (CharSequence, Int, Int, Int) -> Unit = { _, _, _, _ -> },
        onTextChanged: (CharSequence, Int, Int, Int) -> Unit = { _, _, _, _ -> }
    ) {
        b.phoneFieldET.apply {
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

    fun setCountryCodes(countryCodes: ArrayList<String>) {
        arrayAdapter.clear()
        arrayAdapter.addAll(countryCodes)
        arrayAdapter.notifyDataSetChanged()
    }


    companion object {

        //Along with phone number length required for space is also considered
        private val countryCodeMap = mapOf(
            "+91" to 10 + 1,  // India - Mobile and landline numbers
            "+44" to 10 + 1,  // United Kingdom - Mobile numbers are 10 digits; landline and other numbers are 10 digits
            "+1" to 10 + 2,   // United States/Canada - Mobile and landline numbers are 10 digits (including the country code)
            "+234" to 10 + 1, // Nigeria - Mobile numbers are typically 10 digits
            "+39" to 10 + 2,  // Italy - Mobile numbers are 10 digits
            "+265" to 9 + 2,  // Malawi - Mobile numbers are 9 digits
            "+27" to 9 + 2,  // South Africa - Mobile numbers are 9 digits
            "+46" to 9 + 3   // Sweden - Mobile numbers are 9 digits
        )
    }
}