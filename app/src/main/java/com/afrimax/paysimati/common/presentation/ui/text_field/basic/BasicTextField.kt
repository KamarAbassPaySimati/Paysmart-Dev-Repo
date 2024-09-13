package com.afrimax.paysimati.common.presentation.ui.text_field.basic

import android.content.Context
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.Spanned
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.afrimax.paysimati.R
import com.afrimax.paysimati.common.presentation.utils.UiDrawable
import com.afrimax.paysimati.common.presentation.utils.UiText
import com.afrimax.paysimati.common.presentation.utils.currentState
import com.afrimax.paysimati.common.presentation.utils.setOnTextChangedListener
import com.afrimax.paysimati.databinding.ComponentBasicTextFieldBinding

import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.viewmodel.observe

@AndroidEntryPoint
class BasicTextField @JvmOverloads constructor(
    private val cxt: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(cxt, attrs, defStyleAttr), ViewModelStoreOwner {

    override val viewModelStore: ViewModelStore = ViewModelStore()

    private var b: ComponentBasicTextFieldBinding = ComponentBasicTextFieldBinding.inflate(
        LayoutInflater.from(cxt), this
    )
    private lateinit var vm: BasicTextFieldViewModel

    private var isTextWatcherEnabled = false
    private var isFocusListenerEnabled = false
    private var isWarningTextEnabled = true


    init {

        if (!isInEditMode) {
            require(cxt is ViewModelStoreOwner && cxt is LifecycleOwner)

            vm = ViewModelProvider(this)[BasicTextFieldViewModel::class.java]
            vm.observe(cxt, state = ::observeState)

            //Perform all the UI setup here
            setUpFocusListener()
            setUpTextChangeListener()
        }

        //enable focus & text change listeners
        isTextWatcherEnabled = true
        isFocusListenerEnabled = true

        initializeWithAttributes(attrs)

    }

    private fun initializeWithAttributes(attrs: AttributeSet?) {
        attrs.let { attributes ->
            val typedArray =
                cxt.obtainStyledAttributes(attributes, R.styleable.BasicTextField, 0, 0)

            val titleText = typedArray.getString(R.styleable.BasicTextField_titleText)
                ?: context.getString(R.string.title)
            val hintText = typedArray.getString(R.styleable.BasicTextField_hintText)
                ?: context.getString(R.string.hint)
            val digits = typedArray.getString(R.styleable.BasicTextField_android_digits)
            val textTransformation =
                typedArray.getInt(R.styleable.BasicTextField_textTransformation, 0)
            isWarningTextEnabled =
                typedArray.getBoolean(R.styleable.BasicTextField_isWarningTextEnabled, true)

            if (!isInEditMode) {
                if (!digits.isNullOrEmpty()) {
                    // Set the digits filter using the default android:digits attribute
                    b.basicTextFieldET.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
                        source?.filter { digits.contains(it) }
                    })
                }

                // Apply text transformation based on the attribute value
                when (textTransformation) {
                    TEXT_CAPITAL_LETTERS -> b.basicTextFieldET.filters += AllCaps()
                    TEXT_SMALL_LETTERS -> b.basicTextFieldET.filters += object : AllCaps() {
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
                    BasicTextFieldIntent.SetInitialData(
                        title = titleText, hint = hintText
                    )
                )
            } else {
                b.basicTextFieldTitleTV.text = titleText
                b.basicTextFieldET.hint = hintText
                if (isWarningTextEnabled) b.basicTextFieldWarningTV.visibility =
                    View.VISIBLE else b.basicTextFieldWarningTV.visibility = View.GONE
            }

            typedArray.recycle()
        }
    }

    /**Observe changes in the State using Orbit StateFlow*/
    private fun observeState(state: BasicTextFieldState) {
        modifyTitle(state.title)
        modifyTextField(state.text, state.hint)
        modifyBackground(state.background)
        modifyWarning(state.warningText, state.showWarning)
    }

    private fun setUpFocusListener() {
        val focusDrawable = UiDrawable.Resource(R.drawable.bg_edit_text_focused)
        val errorDrawable = UiDrawable.Resource(R.drawable.bg_edit_text_error)
        val notInFocusDrawable = UiDrawable.Resource(R.drawable.bg_edit_text_unfocused)

        b.basicTextFieldET.apply {
            setOnFocusChangeListener { _, hasFocus ->
                if (isFocusListenerEnabled) {
                    when {
                        vm.currentState.showWarning -> vm.invoke(
                            BasicTextFieldIntent.SetBackground(
                                errorDrawable
                            )
                        )

                        hasFocus -> vm.invoke(BasicTextFieldIntent.SetBackground(focusDrawable))
                        else -> vm.invoke(BasicTextFieldIntent.SetBackground(notInFocusDrawable))
                    }
                }
            }
        }
    }

    private fun setUpTextChangeListener() {
        b.basicTextFieldET.apply {
            setOnTextChangedListener(afterTextChanged = { text ->
                if (isTextWatcherEnabled) vm.invoke(BasicTextFieldIntent.SetText(text = text))
            })
        }
    }

    private fun modifyTitle(title: UiText) {
        b.basicTextFieldTitleTV.apply {
            text = title.asString(cxt)
        }
    }

    private fun modifyTextField(txt: UiText, hintText: UiText) {
        b.basicTextFieldET.apply {
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
        b.basicTextFieldET.apply {
            this.background = background.asDrawable(cxt)
        }
    }

    private fun modifyWarning(warningText: UiText, showWarning: Boolean) {
        if (isWarningTextEnabled) {
            if (showWarning) {
                b.basicTextFieldWarningTV.apply {
                    visibility = View.VISIBLE
                    text = warningText.asString(cxt)
                }
            } else {
                b.basicTextFieldWarningTV.apply {
                    visibility = View.GONE
                }
            }
        }
    }

    fun showWarning(warningText: String) {
        if (warningText.isNotEmpty()) {
            vm.invoke(BasicTextFieldIntent.ShowWarning(warningText))
        }
    }

    fun addTextChangeListener(
        afterTextChanged: (String) -> Unit,
        beforeTextChanged: (CharSequence, Int, Int, Int) -> Unit = { _, _, _, _ -> },
        onTextChanged: (CharSequence, Int, Int, Int) -> Unit = { _, _, _, _ -> }
    ) {
        b.basicTextFieldET.apply {
            setOnTextChangedListener(
                afterTextChanged = afterTextChanged,
                beforeTextChanged = beforeTextChanged,
                onTextChanged = onTextChanged
            )
        }
    }

    fun setText(text: String) {
        vm.invoke(BasicTextFieldIntent.SetText(text))
    }

    fun getText(): String {
        return vm.currentState.text.asString(cxt)
    }

    fun setTitle(title: String) {
        vm.invoke(BasicTextFieldIntent.SetTitle(title = title))
    }

    companion object {
        const val TEXT_SMALL_LETTERS = 1
        const val TEXT_CAPITAL_LETTERS = 2
    }
}