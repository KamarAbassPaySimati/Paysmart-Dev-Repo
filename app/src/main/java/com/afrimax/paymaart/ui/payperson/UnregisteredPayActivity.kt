package com.afrimax.paymaart.ui.payperson

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.model.IndividualSearchUserData
import com.afrimax.paymaart.databinding.ActivityUnregisteredPayBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.util.Constants

class UnregisteredPayActivity : BaseActivity() {

    private lateinit var b: ActivityUnregisteredPayBinding
    private lateinit var userData: IndividualSearchUserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityUnregisteredPayBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(b.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false
        wic.isAppearanceLightNavigationBars = true
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)

        initViews()
        setUpListeners()
    }

    private fun initViews() {
        userData = intent.parcelable<IndividualSearchUserData>(Constants.USER_DATA)!!
    }

    private fun setUpListeners() {

        b.unregisteredPayActivityBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.unregisteredPayActivityConfirmButton.setOnClickListener {
            validateFieldsForConfirm()
        }

        setupEditTextFocusListeners()
        setUpEditTextChangeListeners()
        setUpAllCapsEditTextFilters(b.unregisteredPayActivityLastNameET)
    }

    private fun setUpEditTextChangeListeners() {
        configureEditTextChangeListeners(
            b.unregisteredPayActivityFirstNameET, b.unregisteredPayActivityFirstNameWarningTV
        )
        configureEditTextChangeListeners(
            b.unregisteredPayActivityMiddleNameET, b.unregisteredPayActivityMiddleNameWarningTV
        )
        configureEditTextChangeListeners(
            b.unregisteredPayActivityLastNameET, b.unregisteredPayActivityLastNameWarningTV
        )
    }

    private fun configureEditTextChangeListeners(nameET: EditText, warningTV: TextView) {
        nameET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                if (nameET.text.isEmpty()) {
                    warningTV.visibility = View.VISIBLE
                    warningTV.text = getString(R.string.required_field)
                    nameET.background = ContextCompat.getDrawable(
                        this@UnregisteredPayActivity, R.drawable.bg_edit_text_error
                    )
                } else {
                    warningTV.visibility = View.GONE
                    nameET.background = ContextCompat.getDrawable(
                        this@UnregisteredPayActivity, R.drawable.bg_edit_text_focused
                    )
                }
            }
        })
    }


    private fun setupEditTextFocusListeners() {
        configureEditTextFocusListeners(
            b.unregisteredPayActivityFirstNameET, b.unregisteredPayActivityFirstNameWarningTV
        )
        configureEditTextFocusListeners(
            b.unregisteredPayActivityMiddleNameET, b.unregisteredPayActivityMiddleNameWarningTV
        )
        configureEditTextFocusListeners(
            b.unregisteredPayActivityLastNameET, b.unregisteredPayActivityLastNameWarningTV
        )
    }

    private fun configureEditTextFocusListeners(nameET: EditText, warningText: TextView) {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        nameET.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) nameET.background = focusDrawable
            else if (warningText.isVisible) nameET.background = errorDrawable
            else nameET.background = notInFocusDrawable

        }
    }


    private fun setUpAllCapsEditTextFilters(et: EditText) {
        et.filters += InputFilter.AllCaps()
    }

    private fun validateFieldsForConfirm() {
        var isValid = true
        var focusView: View? = null

        if (!validateNormalFields(
                b.unregisteredPayActivityFirstNameET, b.unregisteredPayActivityFirstNameWarningTV
            )
        ) {
            isValid = false
            focusView = b.unregisteredPayActivityFirstNameET
        }

        if (!validateNormalFields(
                b.unregisteredPayActivityMiddleNameET, b.unregisteredPayActivityMiddleNameWarningTV
            )
        ) {
            isValid = false
            if (focusView == null) focusView = b.unregisteredPayActivityMiddleNameET
        }

        if (!validateNormalFields(
                b.unregisteredPayActivityLastNameET, b.unregisteredPayActivityLastNameWarningTV
            )
        ) {
            isValid = false
            if (focusView == null) focusView = b.unregisteredPayActivityLastNameET
        }


        if (isValid) {
            val fullName =
                "${b.unregisteredPayActivityFirstNameET.text} ${b.unregisteredPayActivityMiddleNameET.text} ${b.unregisteredPayActivityLastNameET.text}"
            startActivity(Intent(
                this@UnregisteredPayActivity, PayPersonActivity::class.java
            ).apply {
                putExtra(Constants.USER_DATA, userData.copy(name = fullName))
            })
        } else {
            focusView!!.parent.requestChildFocus(focusView, focusView)
        }

    }


    private fun validateNormalFields(nameEditText: EditText, warningText: TextView): Boolean {
        var isValid = true
        if (nameEditText.text.isEmpty()) {
            warningText.visibility = View.VISIBLE
            nameEditText.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
            isValid = false
        } else {
            warningText.visibility = View.GONE
            nameEditText.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }
        return isValid
    }
}