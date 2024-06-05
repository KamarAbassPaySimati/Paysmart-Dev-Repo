package com.afrimax.paymaart.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class PhoneEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(
                s: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int
            ) {
                //
            }

            override fun afterTextChanged(s: Editable?) {
                // Ensure that text is not null and length is greater than 0
                s?.let {
                    var textWithoutSpaces = it.toString().replace(" ", "")

                    // Remove leading zeros
                    textWithoutSpaces = textWithoutSpaces.replaceFirst("^0+".toRegex(), "")

                    val formattedText = StringBuilder()
                    for (i in textWithoutSpaces.indices) {
                        formattedText.append(textWithoutSpaces[i])
                        if ((i == 1 || i == 4) && i < textWithoutSpaces.length - 1) {
                            formattedText.append(" ")
                        }
                    }
                    // Set the formatted text back to the EditText without triggering TextWatcher
                    if (formattedText.toString() != it.toString() && formattedText.length < 11) {
                        setText(formattedText)
                        setSelection(formattedText.length)
                    }
                }
            }
        })
    }
}
