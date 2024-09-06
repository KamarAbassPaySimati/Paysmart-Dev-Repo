package com.afrimax.paymaart.common.presentation.ui.text_field.phone

import android.content.Context
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.Spanned
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.afrimax.paymaart.R
import com.afrimax.paymaart.common.presentation.utils.PhoneNumberFormatter
import com.afrimax.paymaart.common.presentation.utils.UiDrawable
import com.afrimax.paymaart.common.presentation.utils.UiText
import com.afrimax.paymaart.common.presentation.utils.currentState
import com.afrimax.paymaart.common.presentation.utils.setOnItemSelectedListener
import com.afrimax.paymaart.common.presentation.utils.setOnTextChangedListener
import com.afrimax.paymaart.databinding.ComponentPhoneFieldBinding
import org.orbitmvi.orbit.viewmodel.observe

class PhoneField @JvmOverloads constructor(
    private val cxt: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(cxt, attrs, defStyleAttr), ViewModelStoreOwner {

    override val viewModelStore: ViewModelStore = ViewModelStore()

    private var b: ComponentPhoneFieldBinding = ComponentPhoneFieldBinding.inflate(
        LayoutInflater.from(cxt), this
    )
    private lateinit var vm: PhoneFieldViewModel

    private var isTextWatcherEnabled = false
    private var isFocusListenerEnabled = false
    private var isWarningTextEnabled = true

    private lateinit var arrayAdapter: ArrayAdapter<String>

    init {

        if (!isInEditMode) {
            require(cxt is ViewModelStoreOwner && cxt is LifecycleOwner)

            vm = ViewModelProvider(this)[PhoneFieldViewModel::class.java]
            vm.observe(cxt, state = ::observeState)

            //Perform all the UI setup here
            setUpTextField()
            setUpDropDown()
        }

        //enable focus & text change listeners
        isTextWatcherEnabled = true
        isFocusListenerEnabled = true

        initializeWithAttributes(attrs)

    }

    private fun initializeWithAttributes(attrs: AttributeSet?) {
        attrs.let { attributes ->
            val typedArray = cxt.obtainStyledAttributes(attributes, R.styleable.PhoneField, 0, 0)

            val titleText = typedArray.getString(R.styleable.PhoneField_titleText)
                ?: context.getString(R.string.title)
            val hintText = typedArray.getString(R.styleable.PhoneField_hintText)
                ?: context.getString(R.string.hint)
            val digits = typedArray.getString(R.styleable.PhoneField_android_digits)
            val textTransformation = typedArray.getInt(R.styleable.PhoneField_textTransformation, 0)
            isWarningTextEnabled =
                typedArray.getBoolean(R.styleable.PhoneField_isWarningTextEnabled, true)

            if (!isInEditMode) {
                if (!digits.isNullOrEmpty()) {
                    // Set the digits filter using the default android:digits attribute
                    b.phoneFieldET.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
                        source?.filter { digits.contains(it) }
                    })
                }

                // Apply text transformation based on the attribute value
                when (textTransformation) {
                    TEXT_CAPITAL_LETTERS -> b.phoneFieldET.filters += AllCaps()
                    TEXT_SMALL_LETTERS -> b.phoneFieldET.filters += object : AllCaps() {
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

                vm.invoke(
                    PhoneFieldIntent.SetInitialData(
                        title = titleText, hint = hintText
                    )
                )
            } else {
                b.phoneFieldTitleTV.text = titleText
                b.phoneFieldET.hint = hintText
                if (isWarningTextEnabled) b.phoneFieldWarningTV.visibility =
                    View.VISIBLE else b.phoneFieldWarningTV.visibility = View.GONE
            }

            typedArray.recycle()
        }
    }

    /**Observe changes in the State using Orbit StateFlow*/
    private fun observeState(state: PhoneFieldState) {
        modifyTitle(state.title)
        modifyDropDown(state.countryCodes)
        modifyTextField(state.text, state.hint)
        modifyBackground(state.background)
        modifyWarning(state.warningText, state.showWarning)
    }


    // ====================================================================
    //                        INITIAL SETUP
    // ====================================================================

    private fun setUpTextField() {
        val focusDrawable = UiDrawable.Resource(R.drawable.bg_edit_text_focused)
        val errorDrawable = UiDrawable.Resource(R.drawable.bg_edit_text_error)
        val notInFocusDrawable = UiDrawable.Resource(R.drawable.bg_edit_text_unfocused)

        b.phoneFieldET.apply {
            setOnClickListener { _ ->
                setSelection(text.length)
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (isFocusListenerEnabled) {
                    when {
                        vm.currentState.showWarning -> vm(
                            PhoneFieldIntent.SetBackground(errorDrawable)
                        )

                        hasFocus -> vm(PhoneFieldIntent.SetBackground(focusDrawable))
                        else -> vm(PhoneFieldIntent.SetBackground(notInFocusDrawable))
                    }
                }
            }

            setOnTextChangedListener(onTextChanged = { text, _, before, _ ->
                if (isTextWatcherEnabled) {
                    var updatedText = text.toString()

                    if (before != 1) updatedText = PhoneNumberFormatter.format(
                        countryCode = countryCode, phoneNumber = updatedText
                    )

                    vm(PhoneFieldIntent.SetText(text = updatedText))
                }
            })
        }
    }

    private fun setUpDropDown() {
        arrayAdapter =
            ArrayAdapter(cxt, R.layout.spinner_country_code, vm.currentState.countryCodes)
        b.phoneFieldCountryCodeSpinner.apply {
            adapter = arrayAdapter

            setOnItemSelectedListener(onItemSelected = { _, _, position, _ ->
                // Get the selected item
                val selectedCountryCode = adapter.getItem(position) as String

                b.phoneFieldET.filters = getPhoneNumberLength(selectedCountryCode)
                b.phoneFieldET.text?.clear()

                vm(PhoneFieldIntent.ChangeCountryCode(countryCode = selectedCountryCode))
            })
        }
    }

    // ====================================================================
    //                        STATE MODIFICATIONS
    // ====================================================================

    private fun modifyTitle(title: UiText) {
        b.phoneFieldTitleTV.apply {
            if (text.isEmpty()) text = title.asString(cxt)
        }
    }

    private fun modifyDropDown(countryCodes: ArrayList<String>) {
        val currentItemCount = b.phoneFieldCountryCodeSpinner.adapter.count
        if (countryCodes.size != currentItemCount) { //Avoid re rendering UI
            arrayAdapter.clear()
            arrayAdapter.addAll(countryCodes)
            arrayAdapter.notifyDataSetChanged()
        }
    }

    private fun modifyTextField(txt: UiText, hintText: UiText) {
        b.phoneFieldET.apply {
            if (hint.isNullOrEmpty()) hint = hintText.asString(cxt)

            val newText = txt.asString(cxt)
            if (newText != text.toString()) {
                isTextWatcherEnabled = false
                setText(newText)
                setSelection(newText.length)
                isTextWatcherEnabled = true
            }
        }
    }

    private fun modifyBackground(background: UiDrawable) {
        b.phoneFieldBox.apply {
            this.background = background.asDrawable(cxt)
        }
    }

    private fun modifyWarning(warningText: UiText, showWarning: Boolean) {
        if (isWarningTextEnabled) {
            if (showWarning) {
                b.phoneFieldWarningTV.apply {
                    visibility = View.VISIBLE
                    text = warningText.asString(cxt)
                }
            } else {
                b.phoneFieldWarningTV.apply {
                    visibility = View.GONE
                }
            }
        }
    }

    // ====================================================================
    //                        HELPER FUNCTIONS
    // ====================================================================


    private fun getPhoneNumberLength(countryCode: String): Array<InputFilter.LengthFilter> {
        val maxLength = countryCodeMap.getOrDefault(countryCode, 9)
        return arrayOf(InputFilter.LengthFilter(maxLength))
    }

    // ====================================================================
    //                        PUBLIC METHODS & PROPERTIES
    // ====================================================================

    val text: String get() = vm.currentState.text.asString(cxt)
    val countryCode: String get() = vm.currentState.currentCountryCode


    fun showWarning(warningText: String) {
        if (warningText.isNotEmpty()) {
            vm.invoke(PhoneFieldIntent.ShowWarning(warningText))
        }
    }

    fun addTextChangeListener(
        afterTextChanged: (String) -> Unit,
        beforeTextChanged: (CharSequence, Int, Int, Int) -> Unit = { _, _, _, _ -> },
        onTextChanged: (CharSequence, Int, Int, Int) -> Unit = { _, _, _, _ -> }
    ) {
        b.phoneFieldET.apply {
            setOnTextChangedListener(
                afterTextChanged = afterTextChanged,
                beforeTextChanged = beforeTextChanged,
                onTextChanged = onTextChanged
            )
        }
    }

    fun setText(text: String) {
        vm.invoke(PhoneFieldIntent.SetText(text))
    }


    fun setTitle(title: String) {
        vm.invoke(PhoneFieldIntent.SetTitle(title = title))
    }

    fun setCountryCodes(countryCodes: ArrayList<String>) {
        vm.invoke(PhoneFieldIntent.SetCountryCodeList(countryCodes = countryCodes))
    }


    companion object {
        const val TEXT_SMALL_LETTERS = 1
        const val TEXT_CAPITAL_LETTERS = 2

        //Along with phone number length required space is also considered
        private val countryCodeMap = mapOf(
            "+91" to 10 + 1,  // India - Mobile and landline numbers
            "+44" to 11 + 1,  // United Kingdom - Mobile numbers are 11 digits; landline and other numbers are 10 digits
            "+1" to 11 + 2,   // United States/Canada - Mobile and landline numbers are 11 digits (including the country code)
            "+234" to 11 + 1, // Nigeria - Mobile numbers are typically 11 digits
            "+39" to 10 + 2,  // Italy - Mobile numbers are 10 digits
            "+265" to 9 + 2,  // Malawi - Mobile numbers are 9 digits
            "+27" to 10 + 2,  // South Africa - Mobile numbers are 10 digits
            "+46" to 10 + 3   // Sweden - Mobile numbers are 10 digits
        )
    }

}