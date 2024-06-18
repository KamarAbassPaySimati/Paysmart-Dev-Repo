package com.afrimax.paymaart.ui.kyc

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.ActivityKycOccupationBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.utils.bottomsheets.KycOccupationEmployedSheet
import com.afrimax.paymaart.ui.utils.interfaces.KycOccupationEmployedInterface
import com.afrimax.paymaart.util.Constants

class KycOccupationActivity : BaseActivity(), KycOccupationEmployedInterface {
    private lateinit var b: ActivityKycOccupationBinding
    private lateinit var educationResultLauncher: ActivityResultLauncher<Intent>

    private var isCustomInstitute = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityKycOccupationBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.kycOccupationActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true

        initViews()
        setUpLayout()
        setUpListeners()
    }

    private fun initViews() {
        educationResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // There are no request codes
                    val data = result.data
                    populateEducationDetails(data)
                }
            }
    }

    private fun setUpLayout() {
        val selectedCategory = intent.getStringExtra(Constants.KYC_OCCUPATION_CATEGORY) ?: ""
        val selectedSubCategory = intent.getStringExtra(Constants.KYC_OCCUPATION_SUB_CATEGORY) ?: ""

        if (selectedCategory.isNotEmpty()) {
            when (selectedCategory) {
                getString(R.string.employed) -> {
                    b.kycOccupationActivityEmployedRB.isChecked = true
                    b.kycOccupationActivityEmployedSubText.visibility = View.VISIBLE
                    b.kycOccupationActivityEmployedSubText.text = selectedSubCategory
                }

                getString(R.string.self_employed) -> {
                    b.kycOccupationActivitySelfEmployedRB.isChecked = true
                    b.kycOccupationActivitySelfEmployedSubText.visibility = View.VISIBLE
                    b.kycOccupationActivitySelfEmployedET.visibility = View.VISIBLE
                    b.kycOccupationActivitySelfEmployedET.setText(selectedSubCategory)

                }

                getString(R.string.in_full_time_education) -> {
                    b.kycOccupationActivityEducationRB.isChecked = true
                    b.kycOccupationActivityEducationSubText.visibility = View.VISIBLE
                    b.kycOccupationActivityEducationSubText.text = selectedSubCategory
                }

                getString(R.string.seeking_employment) -> {
                    b.kycOccupationActivitySeekingRB.isChecked = true
                }

                getString(R.string.retired_pensioner) -> {
                    b.kycOccupationActivityRetiredRB.isChecked = true
                }

                getString(R.string.others) -> {
                    b.kycOccupationActivityOtherRB.isChecked = true
                    b.kycOccupationActivityOtherSubText.visibility = View.VISIBLE
                    b.kycOccupationActivityOtherET.visibility = View.VISIBLE
                    b.kycOccupationActivityOtherET.setText(selectedSubCategory)
                }
            }
        }
    }

    private fun populateEducationDetails(data: Intent?) {
        if (data != null) {
            clearRadioButtonChecks()

            val institute = data.getStringExtra(Constants.KYC_OCCUPATION_EDUCATION_TYPE) ?: ""
            isCustomInstitute =
                data.getBooleanExtra(Constants.KYC_OCCUPATION_EDUCATION_IS_CUSTOM, false)
            b.kycOccupationActivityEducationRB.isChecked = true
            b.kycOccupationActivityEducationSubText.visibility = View.VISIBLE
            b.kycOccupationActivityEducationSubText.text = institute
        }
    }

    private fun setUpListeners() {

        b.kycOccupationActivityBackButtonIV.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.kycOccupationActivityInfoButtonIV.setOnClickListener {
            val i = Intent(this, KycRegistrationGuideActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            startActivity(i, options)
        }

        b.kycOccupationActivityEmployedCL.setOnClickListener {
            onRadioButtonClick(Constants.KYC_OCCUPATION_EMPLOYED)
        }

        b.kycOccupationActivitySelfEmployedCL.setOnClickListener {
            onRadioButtonClick(Constants.KYC_OCCUPATION_SELF_EMPLOYED)
        }

        b.kycOccupationActivityEducationCL.setOnClickListener {
            onRadioButtonClick(Constants.KYC_OCCUPATION_EDUCATION)
        }

        b.kycOccupationActivitySeekingCL.setOnClickListener {
            onRadioButtonClick(Constants.KYC_OCCUPATION_SEEKING)
        }

        b.kycOccupationActivityRetiredCL.setOnClickListener {
            onRadioButtonClick(Constants.KYC_OCCUPATION_RETIRED)
        }

        b.kycOccupationActivityOtherCL.setOnClickListener {
            onRadioButtonClick(Constants.KYC_OCCUPATION_OTHER)
        }

        b.kycOccupationActivityNextButton.setOnClickListener {
            validateFieldsForNext()
        }

        setupEditTextFocusListeners()
        setUpEditTextChangeListeners()
    }

    private fun setUpEditTextChangeListeners() {
        configureEditTextChangeListeners(b.kycOccupationActivitySelfEmployedET)
        configureEditTextChangeListeners(b.kycOccupationActivityOtherET)
    }

    private fun configureEditTextChangeListeners(nameET: EditText) {
        nameET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                if (nameET.text.isEmpty()) {
                    nameET.background = ContextCompat.getDrawable(
                        this@KycOccupationActivity, R.drawable.bg_edit_text_error
                    )
                } else {
                    nameET.background = ContextCompat.getDrawable(
                        this@KycOccupationActivity, R.drawable.bg_edit_text_focused
                    )
                }
            }
        })
    }

    private fun setupEditTextFocusListeners() {
        configureEditTextFocusListeners(b.kycOccupationActivitySelfEmployedET)
        configureEditTextFocusListeners(b.kycOccupationActivityOtherET)
    }

    private fun configureEditTextFocusListeners(nameET: EditText) {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        nameET.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) nameET.background = focusDrawable
            else nameET.background = notInFocusDrawable
        }
    }

    private fun onRadioButtonClick(occupationType: String) {
        when (occupationType) {
            Constants.KYC_OCCUPATION_EMPLOYED -> {
                //Open Bottom sheet
                val occupationEmployedSheet = KycOccupationEmployedSheet()
                occupationEmployedSheet.show(supportFragmentManager, KycOccupationEmployedSheet.TAG)
            }

            Constants.KYC_OCCUPATION_SELF_EMPLOYED -> {
                clearRadioButtonChecks()
                b.kycOccupationActivitySelfEmployedRB.isChecked = true
                b.kycOccupationActivitySelfEmployedSubText.visibility = View.VISIBLE
                b.kycOccupationActivitySelfEmployedET.visibility = View.VISIBLE

            }

            Constants.KYC_OCCUPATION_EDUCATION -> {
//                val i = Intent(this, KycOccupationEducationActivity::class.java)
//                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
//                educationResultLauncher.launch(i, options)
            }

            Constants.KYC_OCCUPATION_SEEKING -> {
                clearRadioButtonChecks()
                b.kycOccupationActivitySeekingRB.isChecked = true
            }

            Constants.KYC_OCCUPATION_RETIRED -> {
                clearRadioButtonChecks()
                b.kycOccupationActivityRetiredRB.isChecked = true
            }

            Constants.KYC_OCCUPATION_OTHER -> {
                clearRadioButtonChecks()
                b.kycOccupationActivityOtherRB.isChecked = true
                b.kycOccupationActivityOtherSubText.visibility = View.VISIBLE
                b.kycOccupationActivityOtherET.visibility = View.VISIBLE
            }
        }
    }

    private fun clearRadioButtonChecks() {
        b.kycOccupationActivityEmployedRB.isChecked = false
        b.kycOccupationActivityEmployedSubText.visibility = View.GONE

        b.kycOccupationActivitySelfEmployedRB.isChecked = false
        b.kycOccupationActivitySelfEmployedSubText.visibility = View.GONE
        b.kycOccupationActivitySelfEmployedET.text.clear()
        b.kycOccupationActivitySelfEmployedET.visibility = View.GONE

        b.kycOccupationActivityEducationRB.isChecked = false
        b.kycOccupationActivityEducationSubText.visibility = View.GONE


        b.kycOccupationActivitySeekingRB.isChecked = false

        b.kycOccupationActivityRetiredRB.isChecked = false

        b.kycOccupationActivityOtherRB.isChecked = false
        b.kycOccupationActivityOtherSubText.visibility = View.GONE
        b.kycOccupationActivityOtherET.text.clear()
        b.kycOccupationActivityOtherET.visibility = View.GONE
    }

    private fun validateFieldsForNext() {
        var isValid = true
        var focusView: View? = null
        var selectedCategory = ""
        var selectedSubCategory = ""
        val callBackIntent = Intent()

        when (getSelectedRadioButton()) {
            Constants.KYC_OCCUPATION_EMPLOYED -> {
                if (b.kycOccupationActivityEmployedSubText.text.toString().isNotEmpty()) {
                    selectedCategory = getString(R.string.employed)
                    selectedSubCategory = b.kycOccupationActivityEmployedSubText.text.toString()
                }
            }

            Constants.KYC_OCCUPATION_SELF_EMPLOYED -> {
                if (b.kycOccupationActivitySelfEmployedET.text.toString().isEmpty()) {
                    isValid = false
                    focusView = b.kycOccupationActivitySelfEmployedET
                    b.kycOccupationActivitySelfEmployedET.background =
                        ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
                } else {
                    selectedCategory = getString(R.string.self_employed)
                    selectedSubCategory = b.kycOccupationActivitySelfEmployedET.text.toString()
                }
            }

            Constants.KYC_OCCUPATION_EDUCATION -> {
                if (b.kycOccupationActivityEducationSubText.text.toString().isNotEmpty()) {
                    selectedCategory = getString(R.string.in_full_time_education)
                    selectedSubCategory = b.kycOccupationActivityEducationSubText.text.toString()
                    callBackIntent.putExtra(
                        Constants.KYC_OCCUPATION_EDUCATION_IS_CUSTOM, isCustomInstitute
                    )
                }
            }

            Constants.KYC_OCCUPATION_SEEKING -> {
                selectedCategory = getString(R.string.seeking_employment)
            }

            Constants.KYC_OCCUPATION_RETIRED -> {
                selectedCategory = getString(R.string.retired_pensioner)
            }

            Constants.KYC_OCCUPATION_OTHER -> {
                if (b.kycOccupationActivityOtherET.text.toString().isEmpty()) {
                    isValid = false
                    focusView = b.kycOccupationActivityOtherET
                    b.kycOccupationActivityOtherET.background =
                        ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
                } else {
                    selectedCategory = getString(R.string.others)
                    selectedSubCategory = b.kycOccupationActivityOtherET.text.toString()
                }
            }
        }

        if (isValid) {
            callBackIntent.putExtra(Constants.KYC_OCCUPATION_CATEGORY, selectedCategory)
            callBackIntent.putExtra(Constants.KYC_OCCUPATION_SUB_CATEGORY, selectedSubCategory)
            setResult(RESULT_OK, callBackIntent)
            finish()
        } else {
            focusView!!.parent.requestChildFocus(focusView, focusView)
        }
    }

    private fun getSelectedRadioButton(): String {
        var selectedItem = ""
        when {
            b.kycOccupationActivityEmployedRB.isChecked -> selectedItem =
                Constants.KYC_OCCUPATION_EMPLOYED

            b.kycOccupationActivitySelfEmployedRB.isChecked -> selectedItem =
                Constants.KYC_OCCUPATION_SELF_EMPLOYED

            b.kycOccupationActivityEducationRB.isChecked -> selectedItem =
                Constants.KYC_OCCUPATION_EDUCATION

            b.kycOccupationActivitySeekingRB.isChecked -> selectedItem =
                Constants.KYC_OCCUPATION_SEEKING

            b.kycOccupationActivityRetiredRB.isChecked -> selectedItem =
                Constants.KYC_OCCUPATION_RETIRED

            b.kycOccupationActivityOtherRB.isChecked -> selectedItem =
                Constants.KYC_OCCUPATION_OTHER
        }
        return selectedItem

    }

    override fun onEmployedItemSelected(employedType: String) {
        clearRadioButtonChecks()
        b.kycOccupationActivityEmployedRB.isChecked = true

        //Set Employed type
        b.kycOccupationActivityEmployedSubText.visibility = View.VISIBLE
        b.kycOccupationActivityEmployedSubText.text = employedType
    }
}