package com.afrimax.paymaart.ui.kyc

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.afrimax.paymaart.BuildConfig
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.DefaultResponse
import com.afrimax.paymaart.data.model.GetUserKycDataResponse
import com.afrimax.paymaart.data.model.KycBankData
import com.afrimax.paymaart.data.model.KycSavePersonalDetailRequest
import com.afrimax.paymaart.data.model.KycUserData
import com.afrimax.paymaart.data.model.SaveInfoSimplifiedToFullRequest
import com.afrimax.paymaart.data.model.SaveNewInfoDetailsSelfKycRequest
import com.afrimax.paymaart.data.model.SelfKycDetailsResponse
import com.afrimax.paymaart.data.model.ViewUserData
import com.afrimax.paymaart.databinding.ActivityKycPersonalBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.utils.bottomsheets.KycBanksSheet
import com.afrimax.paymaart.ui.utils.bottomsheets.KycIndustrySectorSheet
import com.afrimax.paymaart.ui.utils.bottomsheets.KycMonthlyIncomeWithdrawalSheet
import com.afrimax.paymaart.ui.utils.interfaces.KycYourInfoInterface
import com.afrimax.paymaart.util.Constants
import com.airbnb.lottie.LottieAnimationView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.util.Date

//Should have been KycYourInfoActivity
class KycPersonalActivity : BaseActivity(), KycYourInfoInterface {
    private lateinit var b: ActivityKycPersonalBinding
    private lateinit var kycScope: String
    private lateinit var viewScope: String
    private lateinit var occupationResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var placesClient: PlacesClient
    private lateinit var placesList: ArrayList<MalawiPlace>
    private lateinit var townDistrictEditTextWatcher: TextWatcher
    private var unixTime = ""
    private var selectedCategory = ""
    private var selectedSubCategory = ""
    private var isCustomInstitute = false
    private lateinit var infoResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var nextScreenResultLauncher: ActivityResultLauncher<Intent>
    private var shouldReloadDocs = true
    private var sendEmail = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityKycPersonalBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.onboardKycPersonalActivity)) { v, insets ->
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
        kycScope = intent.getStringExtra(Constants.KYC_SCOPE) ?: ""
        viewScope = intent.getStringExtra(Constants.VIEW_SCOPE) ?: Constants.VIEW_SCOPE_EDIT
        sendEmail = intent.getBooleanExtra(Constants.KYC_SEND_EMAIL, true)

        Places.initialize(applicationContext, BuildConfig.PLACES_API_KEY)
        placesClient = Places.createClient(this)

        placesList = ArrayList()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val callbackIntent = Intent()
                callbackIntent.putExtra(Constants.KYC_SCOPE, kycScope)
                setResult(RESULT_CANCELED, callbackIntent)
                finish()
            }
        })

        occupationResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // There are no request codes
                    val data = result.data
                    populateOccupationDetails(data)

                    //Don't reload data from getUserKycDataApi
                    shouldReloadDocs = false
                } else if (result.resultCode == RESULT_CANCELED) {
                    //Don't reload data from getUserKycDataApi
                    shouldReloadDocs = false
                }
            }

        infoResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                //Don't reload data from getUserKycDataApi
                if (result.resultCode == RESULT_OK || result.resultCode == RESULT_CANCELED) shouldReloadDocs =
                    false
            }

        nextScreenResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data = result.data
                if ((result.resultCode == RESULT_OK || result.resultCode == RESULT_CANCELED) && data != null) {
                    kycScope = data.getStringExtra(Constants.KYC_SCOPE) ?: ""
                    viewScope = data.getStringExtra(Constants.VIEW_SCOPE) ?: Constants.VIEW_SCOPE_EDIT
                    sendEmail = data.getBooleanExtra(Constants.KYC_SEND_EMAIL, true)
                }
            }
    }

    private fun setUpLayout() {
        b.onboardKycPersonalActivityBankDetailsContainer.visibility = View.GONE
        when (kycScope) {
            Constants.KYC_MALAWI_FULL -> {
                b.onboardKycPersonalActivityBackButtonTV.text = getString(R.string.full_kyc)
            }

            Constants.KYC_MALAWI_SIMPLIFIED -> {
                b.onboardKycPersonalActivityBackButtonTV.text = getString(R.string.simplified_kyc)

                //Set default value for monthly income
                b.onboardKycPersonalActivityMonthlyIncomeTV.text =
                    getString(R.string.monthly_income_option6)
                b.onboardKycPersonalActivityMonthlyIncomeTV.background =
                    ContextCompat.getDrawable(this, R.color.defaultSelected)
                b.onboardKycPersonalActivityMonthlyIncomeTV.isEnabled = false

                //Set default value for monthly withdrawal
                b.onboardKycPersonalActivityMonthlyWithdrawalTV.text =
                    getString(R.string.monthly_income_option6)
                b.onboardKycPersonalActivityMonthlyWithdrawalTV.background =
                    ContextCompat.getDrawable(this, R.color.defaultSelected)
                b.onboardKycPersonalActivityMonthlyWithdrawalTV.isEnabled = false

                //Hide drop down icon from Monthly income and Monthly withdrawal fields
                b.onboardKycPersonalActivityMonthlyIncomeTV.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, 0, 0
                )
                b.onboardKycPersonalActivityMonthlyWithdrawalTV.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, 0, 0
                )
            }

            Constants.KYC_NON_MALAWI -> {
                b.onboardKycPersonalActivityBackButtonTV.text =
                    getString(R.string.non_malawi_full_kyc)
            }
        }

        when (viewScope) {
            Constants.VIEW_SCOPE_FILL -> {
                b.onboardKycPersonalActivityPB.max = 99
                b.onboardKycPersonalActivityPB.progress = 99
                b.onboardKycPersonalActivityProgressCountTV.text = getString(R.string.step3of3)
            }

            Constants.VIEW_SCOPE_EDIT -> {
                b.onboardKycPersonalActivityPB.max = 100
                b.onboardKycPersonalActivityPB.progress = 100
                b.onboardKycPersonalActivityProgressCountTV.text = getString(R.string.step4of4)
            }

            Constants.VIEW_SCOPE_UPDATE -> {
                b.onboardKycPersonalActivityPB.max = 100
                b.onboardKycPersonalActivityPB.progress = 100
                b.onboardKycPersonalActivityProgressCountTV.text = getString(R.string.step4of4)

                //hide skip button
                b.onboardKycPersonalActivitySkipButton.visibility = View.GONE
            }
        }
    }

    private fun populateOccupationDetails(data: Intent?) {
        if (data != null) {
            selectedCategory = data.getStringExtra(Constants.KYC_OCCUPATION_CATEGORY) ?: ""
            selectedSubCategory = data.getStringExtra(Constants.KYC_OCCUPATION_SUB_CATEGORY) ?: ""
            isCustomInstitute =
                data.getBooleanExtra(Constants.KYC_OCCUPATION_EDUCATION_IS_CUSTOM, false)

            val formattedText =
                if (selectedSubCategory.isNotEmpty()) "$selectedCategory - $selectedSubCategory"
                else selectedCategory
            b.onboardKycPersonalActivityOccupationTV.text = formattedText

            if (selectedCategory == getString(R.string.employed)) {
                //Show additional fields
                b.onboardKycPersonalActivityEmployerDetailsContainer.visibility = View.VISIBLE

                //Reset all the employer fields
                b.onboardKycPersonalActivityEmployerNameET.text.clear()
                b.onboardKycPersonalActivityEmployerNameET.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
                b.onboardKycPersonalActivityEmployerNameWarningTV.visibility = View.GONE

                b.onboardKycPersonalActivityIndustrySectorTV.text = ""

                b.onboardKycPersonalActivityTownDistrictET.text.clear()
                b.onboardKycPersonalActivityTownDistrictET.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
                b.onboardKycPersonalActivityTownDistrictWarningTV.visibility = View.GONE
            } else {
                //Hide additional fields
                b.onboardKycPersonalActivityEmployerDetailsContainer.visibility = View.GONE
            }

            //Hide warning text if any
            b.onboardKycPersonalActivityOccupationWarningTV.visibility = View.GONE
        }
    }

    private fun setUpListeners() {

        b.onboardKycPersonalActivityBackButtonIV.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.onboardKycPersonalActivityInfoButtonIV.setOnClickListener {
            val i = Intent(this, KycRegistrationGuideActivity::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
            infoResultLauncher.launch(i, options)
        }

        b.onboardKycPersonalActivityGenderRG.setOnCheckedChangeListener { _, _ ->
            b.onboardKycPersonalActivityGenderWarningTV.visibility = View.GONE
        }

        b.onboardKycPersonalActivityDOBTV.setOnClickListener {
            onClickDOB()
        }

        b.onboardKycPersonalActivityOccupationTV.setOnClickListener {
            onClickOccupation()
        }

        b.onboardKycPersonalActivityTownDistrictET.setOnItemClickListener { _, _, position, _ ->
            onTownFilled(position)
        }

        b.onboardKycPersonalActivityIndustrySectorTV.setOnClickListener {
            onClickIndustrySector()
        }

        b.onboardKycPersonalActivityBusinessRelationShip1CB.setOnCheckedChangeListener { _, _ ->
            validatePurposeOfBusiness()
        }

        b.onboardKycPersonalActivityBusinessRelationShip2CB.setOnCheckedChangeListener { _, _ ->
            validatePurposeOfBusiness()
        }

        b.onboardKycPersonalActivityBusinessRelationShip3CB.setOnCheckedChangeListener { _, _ ->
            validatePurposeOfBusiness()
        }

        b.onboardKycPersonalActivityBusinessRelationShip4CB.setOnCheckedChangeListener { _, _ ->
            validatePurposeOfBusiness()
        }

        b.onboardKycPersonalActivityMonthlyIncomeTV.setOnClickListener {
            onClickMonthlyIncome()
        }

        b.onboardKycPersonalActivityMonthlyWithdrawalTV.setOnClickListener {
            onClickMonthlyWithdrawal()

        }

        b.onboardKycPersonalActivityBankNameTV.setOnClickListener {
            onClickBankName()
        }

        b.onboardKycPersonalActivitySkipButton.setOnClickListener {
            when (viewScope) {
                Constants.VIEW_SCOPE_FILL -> {
                    val i = Intent(this, KycProgressActivity::class.java)
                    i.putExtra(Constants.KYC_SCOPE, kycScope)
                    i.putExtra(
                        Constants.ONBOARD_PROGRESS_SCOPE, Constants.ONBOARD_PROGRESS_SCOPE_FINAL
                    )
                    i.putExtra(Constants.VIEW_SCOPE, viewScope)
                    i.putExtra(Constants.KYC_SEND_EMAIL, sendEmail)
                    nextScreenResultLauncher.launch(i)
                }

                Constants.VIEW_SCOPE_EDIT -> {
                    val i = Intent(this, KycEditSuccessfulActivity::class.java)
                    i.putExtra(Constants.KYC_SCOPE, kycScope)
                    i.putExtra(Constants.VIEW_SCOPE, viewScope)
                    i.putExtra(Constants.KYC_SEND_EMAIL, sendEmail)
                    nextScreenResultLauncher.launch(i)
                }
            }
        }

        b.onboardKycPersonalActivitySaveAndContinueButton.setOnClickListener {
            validateFieldsForSaveAndContinue()
        }

        setupEditTextFocusListeners()
        setUpEditTextChangeListeners()
    }

    private fun onTownFilled(position: Int) {
        val malawiPlace = placesList[position]
        b.onboardKycPersonalActivityTownDistrictET.removeTextChangedListener(
            townDistrictEditTextWatcher
        )
        b.onboardKycPersonalActivityTownDistrictET.setText(malawiPlace.primaryText)
        b.onboardKycPersonalActivityTownDistrictET.setSelection(b.onboardKycPersonalActivityTownDistrictET.text.toString().length)
        b.onboardKycPersonalActivityTownDistrictET.addTextChangedListener(
            townDistrictEditTextWatcher
        )
    }

    private fun onClickBankName() {
        clearAllFocusedFields()
        b.onboardKycPersonalActivityBankNameTV.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)

        //Open Bottom sheet
        val bankSheet = KycBanksSheet()
        bankSheet.show(supportFragmentManager, KycBanksSheet.TAG)
    }

    private fun onClickMonthlyWithdrawal() {
        clearAllFocusedFields()
        b.onboardKycPersonalActivityMonthlyWithdrawalTV.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)

        //Open Bottom sheet
        val monthlyIncomeSheet = KycMonthlyIncomeWithdrawalSheet()
        val bundle = Bundle()
        bundle.putString(
            Constants.KYC_INCOME_STATUS_TYPE, Constants.KYC_INCOME_STATUS_MONTHLY_WITHDRAWAL
        )
        bundle.putString(
            Constants.KYC_SELECTED_MONTHLY_INCOME,
            b.onboardKycPersonalActivityMonthlyIncomeTV.text.toString()
        )
        monthlyIncomeSheet.arguments = bundle
        monthlyIncomeSheet.show(supportFragmentManager, KycMonthlyIncomeWithdrawalSheet.TAG)
    }

    private fun onClickMonthlyIncome() {
        clearAllFocusedFields()
        b.onboardKycPersonalActivityMonthlyIncomeTV.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)

        //Open Bottom sheet
        val monthlyIncomeSheet = KycMonthlyIncomeWithdrawalSheet()
        val bundle = Bundle()
        bundle.putString(
            Constants.KYC_INCOME_STATUS_TYPE, Constants.KYC_INCOME_STATUS_MONTHLY_INCOME

        )
        bundle.putString(
            Constants.KYC_SELECTED_MONTHLY_WITHDRAWAL,
            b.onboardKycPersonalActivityMonthlyWithdrawalTV.text.toString()
        )
        monthlyIncomeSheet.arguments = bundle
        monthlyIncomeSheet.show(supportFragmentManager, KycMonthlyIncomeWithdrawalSheet.TAG)
    }

    private fun onClickIndustrySector() {
        clearAllFocusedFields()
        b.onboardKycPersonalActivityIndustrySectorTV.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)

        //Open Bottom sheet
        val industrySectorSheet = KycIndustrySectorSheet()
        industrySectorSheet.show(supportFragmentManager, KycIndustrySectorSheet.TAG)
    }

    private fun onClickOccupation() {
        clearAllFocusedFields()
        b.onboardKycPersonalActivityOccupationTV.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)

        val i = Intent(this, KycOccupationActivity::class.java)
        i.putExtra(Constants.KYC_OCCUPATION_CATEGORY, selectedCategory)
        i.putExtra(Constants.KYC_OCCUPATION_SUB_CATEGORY, selectedSubCategory)
        occupationResultLauncher.launch(i)
    }

    private fun onClickDOB() {
        clearAllFocusedFields()
        b.onboardKycPersonalActivityDOBTV.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)

        openDatePicker()
    }

    private fun openDatePicker() {
        val cal = Calendar.getInstance()
        val calYear = cal[Calendar.YEAR]
        val calMonth = cal[Calendar.MONTH]
        val calDay = cal[Calendar.DAY_OF_MONTH]

        val monthsList = arrayOf(
            "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
        )

        val dpd = DatePickerDialog(this, R.style.date_picker_theme,{ _, year, monthOfYear, dayOfMonth ->
            val formattedDate = "$dayOfMonth-${monthsList[monthOfYear]}-$year"
            b.onboardKycPersonalActivityDOBTV.text = formattedDate

            //Hide warning text if any
            b.onboardKycPersonalActivityDOBWarningTV.visibility = View.GONE

            //Convert date to unix timestamp and store it
            val unixValue = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, monthOfYear)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                set(Calendar.HOUR, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis / 1000L
            unixTime = unixValue.toString()

        }, calYear, calMonth, calDay)
        dpd.datePicker.maxDate = System.currentTimeMillis()
        dpd.show()
    }

    private fun setUpEditTextChangeListeners() {
        configureEditTextChangeListener(
            b.onboardKycPersonalActivityEmployerNameET,
            b.onboardKycPersonalActivityEmployerNameWarningTV
        )
        configureTownTextChangeListener()
        configureOptionalEditTextChangeListener(
            b.onboardKycPersonalActivityAccountNumberET,
            b.onboardKycPersonalActivityAccountNumberWarningTV
        )
        configureOptionalEditTextChangeListener(
            b.onboardKycPersonalActivityAccountNameET,
            b.onboardKycPersonalActivityAccountNameWarningTV
        )
    }

    private fun configureTownTextChangeListener() {
        townDistrictEditTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                if (b.onboardKycPersonalActivityTownDistrictET.text.isEmpty()) {
                    b.onboardKycPersonalActivityTownDistrictWarningTV.visibility = View.VISIBLE
                    b.onboardKycPersonalActivityTownDistrictET.background =
                        ContextCompat.getDrawable(
                            this@KycPersonalActivity, R.drawable.bg_edit_text_error
                        )
                } else if (b.onboardKycPersonalActivityTownDistrictET.text.length >= 3 && !b.onboardKycPersonalActivityTownDistrictET.isPerformingCompletion) {
                    b.onboardKycPersonalActivityTownDistrictWarningTV.visibility = View.GONE
                    b.onboardKycPersonalActivityTownDistrictET.background =
                        ContextCompat.getDrawable(
                            this@KycPersonalActivity, R.drawable.bg_edit_text_focused
                        )
                    getTownsInMalawi(b.onboardKycPersonalActivityTownDistrictET.text.toString())
                } else {
                    b.onboardKycPersonalActivityTownDistrictWarningTV.visibility = View.GONE
                    b.onboardKycPersonalActivityTownDistrictET.background =
                        ContextCompat.getDrawable(
                            this@KycPersonalActivity, R.drawable.bg_edit_text_focused
                        )
                }
            }
        }
        b.onboardKycPersonalActivityTownDistrictET.addTextChangedListener(
            townDistrictEditTextWatcher
        )
    }

    private fun configureOptionalEditTextChangeListener(et: EditText, warningText: TextView) {
        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                if (et.text.isEmpty()) {
                    //Check whether other optional fields are empty or not
                    validateOptionalFields()
                } else {
                    warningText.visibility = View.GONE
                    et.background = ContextCompat.getDrawable(
                        this@KycPersonalActivity, R.drawable.bg_edit_text_focused
                    )
                }
            }
        })
    }

    private fun configureEditTextChangeListener(et: EditText, warningText: TextView) {
        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                if (et.text.isEmpty()) {
                    warningText.visibility = View.VISIBLE
                    et.background = ContextCompat.getDrawable(
                        this@KycPersonalActivity, R.drawable.bg_edit_text_error
                    )
                } else {
                    warningText.visibility = View.GONE
                    et.background = ContextCompat.getDrawable(
                        this@KycPersonalActivity, R.drawable.bg_edit_text_focused
                    )
                }
            }
        })
    }

    private fun setupEditTextFocusListeners() {
        configureEditTextFocusListener(b.onboardKycPersonalActivityEmployerNameET)
        configureEditTextFocusListener(b.onboardKycPersonalActivityTownDistrictET)
        configureEditTextFocusListener(b.onboardKycPersonalActivityAccountNumberET)
        configureEditTextFocusListener(b.onboardKycPersonalActivityAccountNameET)
    }

    private fun configureEditTextFocusListener(et: EditText) {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        et.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                clearAllFocusedFields()
                et.background = focusDrawable
            }
        }
    }

    private fun getTownsInMalawi(queryString: String) {
        val tempPlacesNamesList = ArrayList<String>()
        val token = AutocompleteSessionToken.newInstance()
        val request =
            FindAutocompletePredictionsRequest.builder().setSessionToken(token).setCountries("MW")
                .setTypesFilter(
                    mutableListOf(PlaceTypes.CITIES)
                ).setQuery(queryString).build()

        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            placesList.clear()
            tempPlacesNamesList.clear()

            val predictions = response.autocompletePredictions
            for (prediction in predictions) {
                tempPlacesNamesList.add(prediction.getFullText(null).toString())
                placesList.add(
                    MalawiPlace(
                        prediction.placeId,
                        prediction.getPrimaryText(null).toString(),
                        prediction.getFullText(null).toString()
                    )
                )
            }
            refreshTownSuggestions(tempPlacesNamesList)
        }.addOnFailureListener {//
            //
        }
    }

    private fun refreshTownSuggestions(array: ArrayList<String>) {
        val adapter = ArrayAdapter(this, R.layout.text_auto_complete, array)
        b.onboardKycPersonalActivityTownDistrictET.setAdapter(adapter)
        b.onboardKycPersonalActivityTownDistrictET.showDropDown()
    }

    private fun validateFieldsForSaveAndContinue() {
        var isValid = true
        var focusView: View? = null

        if (!validateGender()) {
            isValid = false
            focusView = b.onboardKycPersonalActivityGenderWarningTV
        }

        if (!validateDob()) {
            isValid = false
            if (focusView == null) focusView = b.onboardKycPersonalActivityDOBWarningTV
        }

        if (!validateOccupation()) {
            isValid = false
            if (focusView == null) focusView = b.onboardKycPersonalActivityOccupationTV
        } else if (b.onboardKycPersonalActivityOccupationTV.text.toString()
                .startsWith(getString(R.string.employed))
        ) {
            //In case Employed is selected
            if (!validateEmployerName()) {
                isValid = false
                if (focusView == null) focusView = b.onboardKycPersonalActivityEmployerNameWarningTV
            }

            if (!validateIndustrySector()) {
                isValid = false
                if (focusView == null) focusView =
                    b.onboardKycPersonalActivityIndustrySectorWarningTV
            }

            if (!validateTownDistrict()) {
                isValid = false
                if (focusView == null) focusView = b.onboardKycPersonalActivityTownDistrictWarningTV
            }
        }

        if (!validatePurposeOfBusiness()) {
            isValid = false
            if (focusView == null) focusView =
                b.onboardKycPersonalActivityBusinessRelationshipWarningTV
        }

        if (!validateMonthlyIncome()) {
            isValid = false
            if (focusView == null) focusView = b.onboardKycPersonalActivityMonthlyIncomeWarningTV
        }

        if (!validateMonthlyWithdrawal()) {
            isValid = false
            if (focusView == null) focusView =
                b.onboardKycPersonalActivityMonthlyWithdrawalWarningTV
        }

        if (!validateOptionalFields()) {
            isValid = false
            if (focusView == null) focusView = b.onboardKycPersonalActivityAccountNameWarningTV
        }

        if (isValid) {
            when (viewScope) {
                Constants.VIEW_SCOPE_FILL -> saveCustomerPersonalDetailsApi()
                Constants.VIEW_SCOPE_EDIT -> saveCustomerLatestPersonalDetailsApi()
                Constants.VIEW_SCOPE_UPDATE -> saveSimplifiedToFullInfoApi()
            }
        } else {
            focusView!!.parent.requestChildFocus(focusView, focusView)
        }
    }

    private fun validateGender(): Boolean {
        var isValid = true
        if (b.onboardKycPersonalActivityGenderRG.checkedRadioButtonId == -1) {
            //No item is selected
            isValid = false
            b.onboardKycPersonalActivityGenderWarningTV.visibility = View.VISIBLE
            b.onboardKycPersonalActivityGenderWarningTV.text = getString(R.string.required_field)
        } else {
            b.onboardKycPersonalActivityGenderWarningTV.visibility = View.GONE
        }
        return isValid
    }

    private fun validateDob(): Boolean {
        var isValid = true
        if (b.onboardKycPersonalActivityDOBTV.text.isEmpty()) {
            isValid = false
            b.onboardKycPersonalActivityDOBWarningTV.visibility = View.VISIBLE
            b.onboardKycPersonalActivityDOBWarningTV.text = getString(R.string.required_field)
            b.onboardKycPersonalActivityDOBTV.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        } else {
            b.onboardKycPersonalActivityDOBWarningTV.visibility = View.GONE
            b.onboardKycPersonalActivityDOBTV.background =
                if (viewScope == Constants.VIEW_SCOPE_UPDATE)
                    ContextCompat.getDrawable(this, R.color.defaultSelected)
                else
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }
        return isValid
    }

    private fun validateOccupation(): Boolean {
        var isValid = true
        if (b.onboardKycPersonalActivityOccupationTV.text.isEmpty()) {
            isValid = false
            b.onboardKycPersonalActivityOccupationWarningTV.visibility = View.VISIBLE
            b.onboardKycPersonalActivityOccupationWarningTV.text =
                getString(R.string.required_field)
            b.onboardKycPersonalActivityOccupationTV.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        } else {
            b.onboardKycPersonalActivityOccupationWarningTV.visibility = View.GONE
            b.onboardKycPersonalActivityOccupationTV.background =
                if (viewScope == Constants.VIEW_SCOPE_UPDATE)
                    ContextCompat.getDrawable(this, R.color.defaultSelected)
                else
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }
        return isValid
    }

    private fun validateEmployerName(): Boolean {
        var isValid = true
        if (b.onboardKycPersonalActivityEmployerNameET.text.isEmpty()) {
            isValid = false
            b.onboardKycPersonalActivityEmployerNameWarningTV.visibility = View.VISIBLE
            b.onboardKycPersonalActivityEmployerNameWarningTV.text =
                getString(R.string.required_field)
            b.onboardKycPersonalActivityEmployerNameET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        } else {
            b.onboardKycPersonalActivityOccupationWarningTV.visibility = View.GONE
            b.onboardKycPersonalActivityEmployerNameET.background =
                if (viewScope == Constants.VIEW_SCOPE_UPDATE)
                    ContextCompat.getDrawable(this, R.color.defaultSelected)
                else
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }
        return isValid
    }

    private fun validateIndustrySector(): Boolean {
        var isValid = true
        if (b.onboardKycPersonalActivityIndustrySectorTV.text.isEmpty()) {
            isValid = false
            b.onboardKycPersonalActivityIndustrySectorWarningTV.visibility = View.VISIBLE
            b.onboardKycPersonalActivityIndustrySectorWarningTV.text =
                getString(R.string.required_field)
            b.onboardKycPersonalActivityIndustrySectorTV.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        } else {
            b.onboardKycPersonalActivityIndustrySectorWarningTV.visibility = View.GONE
            b.onboardKycPersonalActivityIndustrySectorTV.background =
                if (viewScope == Constants.VIEW_SCOPE_UPDATE) ContextCompat.getDrawable(
                    this, R.color.defaultSelected
                ) else ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }
        return isValid
    }

    private fun validateTownDistrict(): Boolean {
        var isValid = true
        if (b.onboardKycPersonalActivityTownDistrictET.text.isEmpty()) {
            isValid = false
            b.onboardKycPersonalActivityTownDistrictWarningTV.visibility = View.VISIBLE
            b.onboardKycPersonalActivityTownDistrictWarningTV.text =
                getString(R.string.required_field)
            b.onboardKycPersonalActivityTownDistrictET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        } else {
            b.onboardKycPersonalActivityTownDistrictWarningTV.visibility = View.GONE
            b.onboardKycPersonalActivityTownDistrictET.background =
                if (viewScope == Constants.VIEW_SCOPE_UPDATE)
                    ContextCompat.getDrawable(this, R.color.defaultSelected)
                else
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }
        return isValid
    }

    private fun validatePurposeOfBusiness(): Boolean {
        var isValid = true
        var itemFilled = 0
        if (b.onboardKycPersonalActivityBusinessRelationShip1CB.isChecked) itemFilled++
        if (b.onboardKycPersonalActivityBusinessRelationShip2CB.isChecked) itemFilled++
        if (b.onboardKycPersonalActivityBusinessRelationShip3CB.isChecked) itemFilled++
        if (b.onboardKycPersonalActivityBusinessRelationShip4CB.isChecked) itemFilled++

        if (itemFilled <= 0) {
            isValid = false
            b.onboardKycPersonalActivityBusinessRelationshipWarningTV.visibility = View.VISIBLE
            b.onboardKycPersonalActivityBusinessRelationshipWarningTV.text =
                getString(R.string.required_field)
        } else {
            b.onboardKycPersonalActivityBusinessRelationshipWarningTV.visibility = View.GONE
        }
        return isValid
    }

    private fun validateMonthlyIncome(): Boolean {
        var isValid = true
        if (b.onboardKycPersonalActivityMonthlyIncomeTV.text.isEmpty()) {
            isValid = false
            b.onboardKycPersonalActivityMonthlyIncomeWarningTV.visibility = View.VISIBLE
            b.onboardKycPersonalActivityMonthlyIncomeWarningTV.text =
                getString(R.string.required_field)
            b.onboardKycPersonalActivityMonthlyIncomeTV.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        } else if (kycScope != Constants.KYC_MALAWI_SIMPLIFIED) {
            b.onboardKycPersonalActivityMonthlyIncomeWarningTV.visibility = View.GONE
            b.onboardKycPersonalActivityMonthlyIncomeTV.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }
        return isValid
    }

    private fun validateMonthlyWithdrawal(): Boolean {
        var isValid = true
        if (b.onboardKycPersonalActivityMonthlyWithdrawalTV.text.isEmpty()) {
            isValid = false
            b.onboardKycPersonalActivityMonthlyWithdrawalWarningTV.visibility = View.VISIBLE
            b.onboardKycPersonalActivityMonthlyWithdrawalWarningTV.text =
                getString(R.string.required_field)
            b.onboardKycPersonalActivityMonthlyWithdrawalTV.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        } else if (kycScope != Constants.KYC_MALAWI_SIMPLIFIED) {
            b.onboardKycPersonalActivityMonthlyWithdrawalWarningTV.visibility = View.GONE
            b.onboardKycPersonalActivityMonthlyWithdrawalTV.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }
        return isValid
    }

    private fun validateOptionalFields(): Boolean {
        val bankName = b.onboardKycPersonalActivityBankNameTV.text.toString()
        val accountNumber = b.onboardKycPersonalActivityAccountNumberET.text.toString()
        val accountName = b.onboardKycPersonalActivityAccountNameET.text.toString()

        //Either none of the fields are filled OR all the fields are filled
        val isValid =
            bankName.isEmpty() == accountNumber.isEmpty() && accountNumber.isEmpty() == accountName.isEmpty()

        if (!isValid) {
            if (bankName.isEmpty()) {
                b.onboardKycPersonalActivityBankNameWarningTV.visibility = View.VISIBLE
                b.onboardKycPersonalActivityBankNameWarningTV.text =
                    getString(R.string.required_field)
                b.onboardKycPersonalActivityBankNameTV.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
            }
            if (accountNumber.isEmpty()) {
                b.onboardKycPersonalActivityAccountNumberWarningTV.visibility = View.VISIBLE
                b.onboardKycPersonalActivityAccountNumberWarningTV.text =
                    getString(R.string.required_field)
                b.onboardKycPersonalActivityAccountNumberET.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
            }
            if (accountName.isEmpty()) {
                b.onboardKycPersonalActivityAccountNameWarningTV.visibility = View.VISIBLE
                b.onboardKycPersonalActivityAccountNameWarningTV.text =
                    getString(R.string.required_field)
                b.onboardKycPersonalActivityAccountNameET.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
            }
        } else {
            //Clear all error messages
            b.onboardKycPersonalActivityBankNameWarningTV.visibility = View.GONE
            b.onboardKycPersonalActivityAccountNumberWarningTV.visibility = View.GONE
            b.onboardKycPersonalActivityAccountNameWarningTV.visibility = View.GONE
            b.onboardKycPersonalActivityBankNameTV.background = if (viewScope == Constants.VIEW_SCOPE_UPDATE)
                ContextCompat.getDrawable(this, R.color.defaultSelected)
            else
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
            b.onboardKycPersonalActivityAccountNumberET.background = if (viewScope == Constants.VIEW_SCOPE_UPDATE)
                ContextCompat.getDrawable(this, R.color.defaultSelected)
            else
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
            b.onboardKycPersonalActivityAccountNameET.background = if (viewScope == Constants.VIEW_SCOPE_UPDATE)
                ContextCompat.getDrawable(this, R.color.defaultSelected)
            else
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }
        return isValid
    }

    /**We clear all the focused fields to unfocused except those fields have error warnings*/
    private fun clearAllFocusedFields() {
        val unfocusedDrawable = if (viewScope == Constants.VIEW_SCOPE_UPDATE)
            ContextCompat.getDrawable(this, R.color.defaultSelected)
        else
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)

        if (!b.onboardKycPersonalActivityDOBWarningTV.isVisible) b.onboardKycPersonalActivityDOBTV.background =
            unfocusedDrawable
        else b.onboardKycPersonalActivityDOBTV.background = errorDrawable

        if (!b.onboardKycPersonalActivityOccupationWarningTV.isVisible) b.onboardKycPersonalActivityOccupationTV.background =
            unfocusedDrawable
        else b.onboardKycPersonalActivityOccupationTV.background = errorDrawable

        if (!b.onboardKycPersonalActivityEmployerNameWarningTV.isVisible) b.onboardKycPersonalActivityEmployerNameET.background =
            unfocusedDrawable
        else b.onboardKycPersonalActivityEmployerNameET.background = errorDrawable

        if (!b.onboardKycPersonalActivityIndustrySectorWarningTV.isVisible) b.onboardKycPersonalActivityIndustrySectorTV.background =
            unfocusedDrawable
        else b.onboardKycPersonalActivityIndustrySectorTV.background = errorDrawable

        if (!b.onboardKycPersonalActivityTownDistrictWarningTV.isVisible) b.onboardKycPersonalActivityTownDistrictET.background =
            unfocusedDrawable
        else b.onboardKycPersonalActivityTownDistrictET.background = errorDrawable

        if (!b.onboardKycPersonalActivityMonthlyIncomeWarningTV.isVisible) {
            //Don't alter UI of Monthly income if kycScope is Simplified
            if (kycScope != Constants.KYC_MALAWI_SIMPLIFIED) b.onboardKycPersonalActivityMonthlyIncomeTV.background =
                unfocusedDrawable
        } else b.onboardKycPersonalActivityMonthlyIncomeTV.background = errorDrawable

        if (!b.onboardKycPersonalActivityMonthlyWithdrawalWarningTV.isVisible) {
            //Don't alter UI of Monthly withdrawal if kycScope is Simplified
            if (kycScope != Constants.KYC_MALAWI_SIMPLIFIED) b.onboardKycPersonalActivityMonthlyWithdrawalTV.background =
                unfocusedDrawable
        } else b.onboardKycPersonalActivityMonthlyWithdrawalTV.background = errorDrawable

        if (!b.onboardKycPersonalActivityBankNameWarningTV.isVisible) b.onboardKycPersonalActivityBankNameTV.background =
            if (viewScope == Constants.VIEW_SCOPE_UPDATE || viewScope == Constants.VIEW_SCOPE_EDIT)
                ContextCompat.getDrawable(this, R.color.defaultSelected)
            else
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        else b.onboardKycPersonalActivityBankNameTV.background = errorDrawable

        if (!b.onboardKycPersonalActivityAccountNumberWarningTV.isVisible) b.onboardKycPersonalActivityAccountNumberET.background =
            if (viewScope == Constants.VIEW_SCOPE_UPDATE || viewScope == Constants.VIEW_SCOPE_EDIT)
                ContextCompat.getDrawable(this, R.color.defaultSelected)
            else
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        else b.onboardKycPersonalActivityAccountNumberET.background = errorDrawable

        if (!b.onboardKycPersonalActivityAccountNameWarningTV.isVisible) b.onboardKycPersonalActivityAccountNameET.background =
            if (viewScope == Constants.VIEW_SCOPE_UPDATE || viewScope == Constants.VIEW_SCOPE_EDIT)
                ContextCompat.getDrawable(this, R.color.defaultSelected)
            else
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        else b.onboardKycPersonalActivityAccountNameET.background = errorDrawable
    }

    private fun getGender(): String {
        return when (b.onboardKycPersonalActivityGenderRG.checkedRadioButtonId) {
            R.id.onboardKycPersonalActivityGenderMaleRB -> getString(R.string.male)
            R.id.onboardKycPersonalActivityGenderFemaleRB -> getString(R.string.female)
            R.id.onboardKycPersonalActivityGenderUndisclosedRB -> getString(R.string.undisclosed)
            else -> ""
        }
    }

    private fun getPurposeOfRelation(): String {
        val listOfNames = mutableListOf<String>()
        if (b.onboardKycPersonalActivityBusinessRelationShip1CB.isChecked) listOfNames.add(
            getString(
                R.string.business_relationship1
            )
        )
        if (b.onboardKycPersonalActivityBusinessRelationShip2CB.isChecked) listOfNames.add(
            getString(
                R.string.business_relationship2
            )
        )
        if (b.onboardKycPersonalActivityBusinessRelationShip3CB.isChecked) listOfNames.add(
            getString(
                R.string.business_relationship3
            )
        )
        if (b.onboardKycPersonalActivityBusinessRelationShip4CB.isChecked) listOfNames.add(
            getString(
                R.string.business_relationship4
            )
        )

        return listOfNames.joinToString("\n")
    }

    private fun saveCustomerPersonalDetailsApi() {
        showButtonLoader(
            b.onboardKycPersonalActivitySaveAndContinueButton,
            b.onboardKycPersonalActivitySaveAndContinueButtonLoaderLottie
        )

        val gender = getGender()
        val dob = unixTime
        val occupation = selectedCategory

        //For employed
        var employedRole = ""
        var employerName = ""
        var industry = ""
        var townDistrict = ""

        //For self employed
        var selfEmployedSpecify = ""

        //For full time education
        var institute = ""
        var instituteSpecify = ""

        //For other
        var occupationSpecify = ""

        when (occupation) {
            getString(R.string.employed) -> {
                employedRole = selectedSubCategory
                employerName = b.onboardKycPersonalActivityEmployerNameET.text.toString()
                industry = b.onboardKycPersonalActivityIndustrySectorTV.text.toString()
                townDistrict = b.onboardKycPersonalActivityTownDistrictET.text.toString()
            }

            getString(R.string.self_employed) -> {
                selfEmployedSpecify = selectedSubCategory
            }

            getString(R.string.in_full_time_education) -> {
                if (isCustomInstitute) {
                    institute = getString(R.string.others)
                    instituteSpecify = selectedSubCategory
                } else {
                    institute = selectedSubCategory
                }
            }

            getString(R.string.others) -> {
                occupationSpecify = selectedSubCategory
            }
        }

        val purposeOfRelation = getPurposeOfRelation()
        val monthlyIncome = b.onboardKycPersonalActivityMonthlyIncomeTV.text.toString()
        val monthlyWithdrawal = b.onboardKycPersonalActivityMonthlyWithdrawalTV.text.toString()

        val bankName = b.onboardKycPersonalActivityBankNameTV.text.toString().ifEmpty { null }
        val accountNumber =
            b.onboardKycPersonalActivityAccountNumberET.text.toString().ifEmpty { null }
        val accountName = b.onboardKycPersonalActivityAccountNameET.text.toString().ifEmpty { null }

        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val saveInfoDetailsCall = ApiClient.apiService.saveCustomerPersonalDetails(
                "Bearer $idToken", KycSavePersonalDetailRequest(
                    gender = gender,
                    dob = dob,
                    occupation = occupation,
                    employed_role = employedRole,
                    employer_name = employerName,
                    self_employed_specify = selfEmployedSpecify,
                    institute = institute,
                    institute_specify = instituteSpecify,
                    occupation_specify = occupationSpecify,
                    industry = industry,
                    occupation_town = townDistrict,
                    purpose_of_relation = purposeOfRelation,
                    monthly_income = monthlyIncome,
                    monthly_withdrawal = monthlyWithdrawal,
                    bank_name = bankName,
                    account_number = accountNumber,
                    account_name = accountName,
                    info_details_status = Constants.KYC_STATUS_COMPLETED
                )
            )

            saveInfoDetailsCall.enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(
                    call: Call<DefaultResponse>, response: Response<DefaultResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        runOnUiThread {
                            hideButtonLoader(
                                b.onboardKycPersonalActivitySaveAndContinueButton,
                                b.onboardKycPersonalActivitySaveAndContinueButtonLoaderLottie,
                                getString(R.string.save_and_continue)
                            )
                            val i = Intent(
                                this@KycPersonalActivity,
                                KycProgressActivity::class.java
                            )
                            i.putExtra(Constants.ONBOARD_PROGRESS_SCOPE, Constants.ONBOARD_PROGRESS_SCOPE_FINAL)
                            i.putExtra(Constants.KYC_SCOPE, kycScope)
                            i.putExtra(Constants.VIEW_SCOPE, viewScope)
                            nextScreenResultLauncher.launch(i)
                        }
                    } else {
                        runOnUiThread {
                            showToast(getString(R.string.default_error_toast))
                            hideButtonLoader(
                                b.onboardKycPersonalActivitySaveAndContinueButton,
                                b.onboardKycPersonalActivitySaveAndContinueButtonLoaderLottie,
                                getString(R.string.save_and_continue)
                            )
                        }
                    }
                }

                override fun onFailure(
                    call: Call<DefaultResponse>, t: Throwable
                ) {
                    runOnUiThread {
                        showToast(getString(R.string.default_error_toast))
                        hideButtonLoader(
                            b.onboardKycPersonalActivitySaveAndContinueButton,
                            b.onboardKycPersonalActivitySaveAndContinueButtonLoaderLottie,
                            getString(R.string.save_and_continue)
                        )
                    }
                }

            })
        }
    }

    private fun saveCustomerLatestPersonalDetailsApi() {
        showButtonLoader(
            b.onboardKycPersonalActivitySaveAndContinueButton,
            b.onboardKycPersonalActivitySaveAndContinueButtonLoaderLottie
        )

        val gender = getGender()
        val dob = unixTime
        val occupation = selectedCategory

        //For employed
        var employedRole = ""
        var employerName = ""
        var industry = ""
        var townDistrict = ""

        //For self employed
        var selfEmployedSpecify = ""

        //For full time education
        var institute = ""
        var instituteSpecify = ""

        //For other
        var occupationSpecify = ""

        when (occupation) {
            getString(R.string.employed) -> {
                employedRole = selectedSubCategory
                employerName = b.onboardKycPersonalActivityEmployerNameET.text.toString()
                industry = b.onboardKycPersonalActivityIndustrySectorTV.text.toString()
                townDistrict = b.onboardKycPersonalActivityTownDistrictET.text.toString()
            }

            getString(R.string.self_employed) -> {
                selfEmployedSpecify = selectedSubCategory
            }

            getString(R.string.in_full_time_education) -> {
                if (isCustomInstitute) {
                    institute = getString(R.string.others)
                    instituteSpecify = selectedSubCategory
                } else {
                    institute = selectedSubCategory
                }
            }

            getString(R.string.others) -> {
                occupationSpecify = selectedSubCategory
            }
        }

        val purposeOfRelation = getPurposeOfRelation()
        val monthlyIncome = b.onboardKycPersonalActivityMonthlyIncomeTV.text.toString()
        val monthlyWithdrawal = b.onboardKycPersonalActivityMonthlyWithdrawalTV.text.toString()

        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val saveYourInfoCall = ApiClient.apiService.saveNewInfoDetailsSelfKyc(
                idToken, SaveNewInfoDetailsSelfKycRequest(
                    gender = gender,
                    dob = dob,
                    occupation = occupation,
                    employed_role = employedRole,
                    employer_name = employerName,
                    self_employed_specify = selfEmployedSpecify,
                    institute = institute,
                    institute_specify = instituteSpecify,
                    occupation_specify = occupationSpecify,
                    industry = industry,
                    occupation_town = townDistrict,
                    purpose_of_relation = purposeOfRelation,
                    monthly_income = monthlyIncome,
                    monthly_withdrawal = monthlyWithdrawal,
                    info_details_status = Constants.KYC_STATUS_COMPLETED,
                    sent_email = sendEmail
                )
            )

            saveYourInfoCall.enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(
                    call: Call<DefaultResponse>, response: Response<DefaultResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        //Email already sent, so stop sending email again for further  by making sendEmail valuew false
                        sendEmail = false

                        runOnUiThread {
                            hideButtonLoader(
                                b.onboardKycPersonalActivitySaveAndContinueButton,
                                b.onboardKycPersonalActivitySaveAndContinueButtonLoaderLottie,
                                getString(R.string.save_and_continue)
                            )
                            val i = Intent(
                                this@KycPersonalActivity, KycEditSuccessfulActivity::class.java
                            )
                            i.putExtra(Constants.KYC_SCOPE, kycScope)
                            i.putExtra(Constants.VIEW_SCOPE, viewScope)
                            i.putExtra(Constants.KYC_SEND_EMAIL, sendEmail)
                            nextScreenResultLauncher.launch(i)
                        }
                    } else {
                        runOnUiThread {
                            showToast(getString(R.string.default_error_toast))
                            hideButtonLoader(
                                b.onboardKycPersonalActivitySaveAndContinueButton,
                                b.onboardKycPersonalActivitySaveAndContinueButtonLoaderLottie,
                                getString(R.string.save_and_continue)
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    runOnUiThread {
                        showToast(getString(R.string.default_error_toast))
                        hideButtonLoader(
                            b.onboardKycPersonalActivitySaveAndContinueButton,
                            b.onboardKycPersonalActivitySaveAndContinueButtonLoaderLottie,
                            getString(R.string.save_and_continue)
                        )
                    }
                }

            })
        }
    }

    override fun onResume() {
        super.onResume()
        //Fetch Kyc Details
        when (viewScope) {
            Constants.VIEW_SCOPE_FILL -> {
                if (shouldReloadDocs) getCustomerKycDataApi()
                else shouldReloadDocs = true
            }

            Constants.VIEW_SCOPE_EDIT, Constants.VIEW_SCOPE_UPDATE -> {
                if (shouldReloadDocs) getCustomerLatestKycDataApi()
                else shouldReloadDocs = true
            }
        }
    }

    private fun getCustomerKycDataApi() {
        //Clear all prefilled data
        clearAllFields()

        //Hide main UI
        b.onboardKycPersonalActivityLoaderLottie.visibility = View.VISIBLE
        b.onboardKycPersonalActivityContentBox.visibility = View.GONE

        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val getUserKycDataCall =
                ApiClient.apiService.viewKyc(idToken)

            getUserKycDataCall.enqueue(object : Callback<GetUserKycDataResponse> {
                override fun onResponse(
                    call: Call<GetUserKycDataResponse>, response: Response<GetUserKycDataResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        populateKycInfoFields(body.data, body.bank_details)
                    } else {
                        runOnUiThread { showToast(getString(R.string.default_error_toast)) }
                    }
                }

                override fun onFailure(call: Call<GetUserKycDataResponse>, t: Throwable) {
                    runOnUiThread { showToast(getString(R.string.default_error_toast)) }
                }
            })
        }

    }

    private fun getCustomerLatestKycDataApi() {
        //Clear all prefilled data
        clearAllFields()

        //Hide main UI
        b.onboardKycPersonalActivityLoaderLottie.visibility = View.VISIBLE
        b.onboardKycPersonalActivityContentBox.visibility = View.GONE

        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val getUserKycDataCall = ApiClient.apiService.getSelfKycUserData("Bearer $idToken")

            getUserKycDataCall.enqueue(object : Callback<SelfKycDetailsResponse> {
                override fun onResponse(
                    call: Call<SelfKycDetailsResponse>, response: Response<SelfKycDetailsResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        populateLatestKycInfoFields(body.data)
                    } else {
                        runOnUiThread { showToast(getString(R.string.default_error_toast)) }
                    }
                }

                override fun onFailure(call: Call<SelfKycDetailsResponse>, t: Throwable) {
                    runOnUiThread { showToast(getString(R.string.default_error_toast)) }
                }
            })
        }

    }

    private fun saveSimplifiedToFullInfoApi() {
        showButtonLoader(
            b.onboardKycPersonalActivitySaveAndContinueButton,
            b.onboardKycPersonalActivitySaveAndContinueButtonLoaderLottie
        )

        val gender = getGender()
        val dob = unixTime
        val occupation = selectedCategory

        //For employed
        var employedRole = ""
        var employerName = ""
        var industry = ""
        var townDistrict = ""

        //For self employed
        var selfEmployedSpecify = ""

        //For full time education
        var institute = ""
        var instituteSpecify = ""

        //For other
        var occupationSpecify = ""

        when (occupation) {
            getString(R.string.employed) -> {
                employedRole = selectedSubCategory
                employerName = b.onboardKycPersonalActivityEmployerNameET.text.toString()
                industry = b.onboardKycPersonalActivityIndustrySectorTV.text.toString()
                townDistrict = b.onboardKycPersonalActivityTownDistrictET.text.toString()
            }

            getString(R.string.self_employed) -> {
                selfEmployedSpecify = selectedSubCategory
            }

            getString(R.string.in_full_time_education) -> {
                if (isCustomInstitute) {
                    institute = getString(R.string.others)
                    instituteSpecify = selectedSubCategory
                } else {
                    institute = selectedSubCategory
                }
            }

            getString(R.string.others) -> {
                occupationSpecify = selectedSubCategory
            }
        }

        val purposeOfRelation = getPurposeOfRelation()
        val monthlyIncome = b.onboardKycPersonalActivityMonthlyIncomeTV.text.toString()
        val monthlyWithdrawal = b.onboardKycPersonalActivityMonthlyWithdrawalTV.text.toString()

        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val saveYourInfoCall = ApiClient.apiService.saveInfoSimplifiedToFull(
                idToken, SaveInfoSimplifiedToFullRequest(
                    gender = gender,
                    dob = dob,
                    occupation = occupation,
                    employed_role = employedRole,
                    employer_name = employerName,
                    self_employed_specify = selfEmployedSpecify,
                    institute = institute,
                    institute_specify = instituteSpecify,
                    occupation_specify = occupationSpecify,
                    industry = industry,
                    occupation_town = townDistrict,
                    purpose_of_relation = purposeOfRelation,
                    monthly_income = monthlyIncome,
                    monthly_withdrawal = monthlyWithdrawal,
                    info_details_status = Constants.KYC_STATUS_COMPLETED,
                )
            )

            saveYourInfoCall.enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(
                    call: Call<DefaultResponse>, response: Response<DefaultResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        //Email already sent, so stop sending email again for further  by making sendEmail value false
                        sendEmail = false

                        runOnUiThread {
                            hideButtonLoader(
                                b.onboardKycPersonalActivitySaveAndContinueButton,
                                b.onboardKycPersonalActivitySaveAndContinueButtonLoaderLottie,
                                getString(R.string.save_and_continue)
                            )
                            val i = Intent(
                                this@KycPersonalActivity, KycEditSuccessfulActivity::class.java
                            )
                            i.putExtra(Constants.KYC_SCOPE, kycScope)
                            i.putExtra(Constants.VIEW_SCOPE, viewScope)
                            i.putExtra(Constants.KYC_SEND_EMAIL, sendEmail)
                            nextScreenResultLauncher.launch(i)
                        }
                    } else {
                        runOnUiThread {
                            showToast(getString(R.string.default_error_toast))
                            hideButtonLoader(
                                b.onboardKycPersonalActivitySaveAndContinueButton,
                                b.onboardKycPersonalActivitySaveAndContinueButtonLoaderLottie,
                                getString(R.string.save_and_continue)
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    runOnUiThread {
                        showToast(getString(R.string.default_error_toast))
                        hideButtonLoader(
                            b.onboardKycPersonalActivitySaveAndContinueButton,
                            b.onboardKycPersonalActivitySaveAndContinueButtonLoaderLottie,
                            getString(R.string.save_and_continue)
                        )
                    }
                }

            })
        }
    }

    private fun populateKycInfoFields(userData: KycUserData, bankDetails: KycBankData?) {
        var isValid = true

        val gender = userData.gender ?: ""
        val dob = userData.dob ?: ""
        val occupation = userData.occupation ?: ""

        val purposeOfRelation = userData.purpose_of_relation ?: ""
        val monthlyIncome = userData.monthly_income ?: ""
        val monthlyWithdrawal = userData.monthly_withdrawal ?: ""

        val infoDetailsStatus = userData.info_details_status

        if (gender.isEmpty() || dob.isEmpty() || occupation.isEmpty() || purposeOfRelation.isEmpty() || monthlyIncome.isEmpty() || monthlyWithdrawal.isEmpty()) isValid =
            false

        if (isValid && infoDetailsStatus == Constants.KYC_STATUS_COMPLETED) {
            //We assume if the status complete , then all the required fields will be provided from the backend

            //Select the gender
            setGender(gender)

            //Set DOB
            setDob(dob.toLong())

            //Set occupation
            setOccupation(userData)

            //Set Purpose of relations
            setPurposeOfRelations(purposeOfRelation)

            //Set monthly income if kyc not belongs to simplified category
            if (kycScope != Constants.KYC_MALAWI_SIMPLIFIED) b.onboardKycPersonalActivityMonthlyIncomeTV.text =
                monthlyIncome

            //Set monthly withdrawal if kyc not belongs to simplified category
            if (kycScope != Constants.KYC_MALAWI_SIMPLIFIED) b.onboardKycPersonalActivityMonthlyWithdrawalTV.text =
                monthlyWithdrawal

            //Set bank details
            if (bankDetails != null) setBankDetails(bankDetails)
        }

        //Hide main UI
        b.onboardKycPersonalActivitySV.scrollTo(0, 0)
        b.onboardKycPersonalActivityLoaderLottie.visibility = View.GONE
        b.onboardKycPersonalActivityContentBox.visibility = View.VISIBLE

    }

    private fun populateLatestKycInfoFields(data: ViewUserData){
        var isValid = true

        val gender = data.gender ?: ""
        val dob = data.dob.toString()
        val occupation = data.occupation ?: ""

        val purposeOfRelation = data.purpose_of_relation
        val monthlyIncome = data.monthly_income ?: ""
        val monthlyWithdrawal = data.monthly_withdrawal ?: ""

        if (gender.isEmpty() || dob.isEmpty() || occupation.isEmpty() || purposeOfRelation.isNullOrEmpty()) isValid =
            false

        if (isValid) {
            //Select the gender
            setGender(gender)
            //Set DOB
            setDob(dob.toLong())
            //Set occupation
            setLatestOccupation(data)
            //Set Purpose of relations
            setLatestPurposeOfRelations(data.purpose_of_relation)
            //Set monthly income if kyc not belongs to simplified category
            if (kycScope != Constants.KYC_MALAWI_SIMPLIFIED) b.onboardKycPersonalActivityMonthlyIncomeTV.text =
                monthlyIncome
            //Set monthly withdrawal if kyc not belongs to simplified category
            if (kycScope != Constants.KYC_MALAWI_SIMPLIFIED) b.onboardKycPersonalActivityMonthlyWithdrawalTV.text =
                monthlyWithdrawal
        }

        //Hide main UI
        b.onboardKycPersonalActivitySV.scrollTo(0, 0)
        b.onboardKycPersonalActivityLoaderLottie.visibility = View.GONE
        b.onboardKycPersonalActivityContentBox.visibility = View.VISIBLE
    }

    private fun setGender(gender: String) {
        when (gender) {
            getString(R.string.male) -> b.onboardKycPersonalActivityGenderMaleRB.isChecked = true
            getString(R.string.female) -> b.onboardKycPersonalActivityGenderFemaleRB.isChecked =
                true

            getString(R.string.undisclosed) -> b.onboardKycPersonalActivityGenderUndisclosedRB.isChecked =
                true
        }
        if (viewScope == Constants.VIEW_SCOPE_UPDATE) {
            b.onboardKycPersonalActivityGenderMaleRB.isEnabled = false
            b.onboardKycPersonalActivityGenderFemaleRB.isEnabled = false
            b.onboardKycPersonalActivityGenderUndisclosedRB.isEnabled = false
        }
    }

    private fun setDob(unixValue: Long) {
        unixTime = unixValue.toString()
        val unixTimeMillis = unixValue * 1000

        // Create a Date object from the Unix time
        val date = Date(unixTimeMillis)

        // Create a Calendar instance and set its time to the Date object
        val c = Calendar.getInstance()
        c.time = date

        // Extract year, month, and day
        val year = c[Calendar.YEAR]
        val monthOfYear = c[Calendar.MONTH]
        val dayOfMonth = c[Calendar.DAY_OF_MONTH]


        val monthsList = arrayOf(
            "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
        )

        val formattedDate = "$dayOfMonth-${monthsList[monthOfYear]}-$year"
        b.onboardKycPersonalActivityDOBTV.text = formattedDate

        if (viewScope == Constants.VIEW_SCOPE_UPDATE) {
            //Grey out
            b.onboardKycPersonalActivityDOBTV.background = ContextCompat.getDrawable(this, R.color.defaultSelected)
            b.onboardKycPersonalActivityDOBTV.isEnabled = false
        }
    }

    private fun setOccupation(userData: KycUserData) {
        val occupation = userData.occupation ?: ""

        //For employed
        val employedRole = userData.employed_role ?: ""
        val employerName = userData.employer_name ?: ""
        val industry = userData.industry ?: ""
        val townDistrict = userData.occupation_town ?: ""

        //For self employed
        val selfEmployedSpecify = userData.self_employed_specify ?: ""

        //For full time education
        val institute = userData.institute ?: ""
        val instituteSpecify = userData.institute_specify ?: ""

        //For other
        val occupationSpecify = userData.occupation_specify ?: ""

        //Hide additional employer fields by default
        b.onboardKycPersonalActivityEmployerDetailsContainer.visibility = View.GONE

        selectedCategory = occupation
        when (occupation) {
            getString(R.string.employed) -> {
                selectedSubCategory = employedRole

                //Show additional employer fields
                b.onboardKycPersonalActivityEmployerDetailsContainer.visibility = View.VISIBLE

                //set all the employer fields
                b.onboardKycPersonalActivityEmployerNameET.setText(employerName)
                b.onboardKycPersonalActivityEmployerNameET.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
                b.onboardKycPersonalActivityEmployerNameWarningTV.visibility = View.GONE

                b.onboardKycPersonalActivityIndustrySectorTV.text = industry

                b.onboardKycPersonalActivityTownDistrictET.setText(townDistrict)
                b.onboardKycPersonalActivityTownDistrictET.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
                b.onboardKycPersonalActivityTownDistrictWarningTV.visibility = View.GONE
            }

            getString(R.string.self_employed) -> {
                selectedSubCategory = selfEmployedSpecify
            }

            getString(R.string.in_full_time_education) -> {
                if (instituteSpecify.isNotEmpty()) {
                    isCustomInstitute = true

                    selectedSubCategory = instituteSpecify
                } else {
                    selectedSubCategory = institute
                }
            }

            getString(R.string.others) -> {
                selectedSubCategory = occupationSpecify
            }

        }

        val formattedText =
            if (selectedSubCategory.isNotEmpty()) "$selectedCategory - $selectedSubCategory"
            else selectedCategory
        b.onboardKycPersonalActivityOccupationTV.text = formattedText
    }

    private fun setPurposeOfRelations(purposeOfRelation: String) {
        val itemList = purposeOfRelation.split("\n")
        for (item in itemList) {
            when (item) {
                getString(R.string.business_relationship1) -> b.onboardKycPersonalActivityBusinessRelationShip1CB.isChecked =
                    true

                getString(R.string.business_relationship2) -> b.onboardKycPersonalActivityBusinessRelationShip2CB.isChecked =
                    true

                getString(R.string.business_relationship3) -> b.onboardKycPersonalActivityBusinessRelationShip3CB.isChecked =
                    true

                getString(R.string.business_relationship4) -> b.onboardKycPersonalActivityBusinessRelationShip4CB.isChecked =
                    true
            }
        }
    }

    private fun setBankDetails(bankDetails: KycBankData) {

        val bankName = bankDetails.bank_name ?: ""
        val accountNumber = bankDetails.account_number ?: ""
        val accountName = bankDetails.account_name ?: ""

        if (bankName.isNotEmpty() && accountNumber.isNotEmpty() && accountName.isNotEmpty()) {
            b.onboardKycPersonalActivityBankNameTV.text = bankName

            b.onboardKycPersonalActivityAccountNumberET.setText(accountNumber)
            b.onboardKycPersonalActivityAccountNumberET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
            b.onboardKycPersonalActivityAccountNumberWarningTV.visibility = View.GONE

            b.onboardKycPersonalActivityAccountNameET.setText(accountName)
            b.onboardKycPersonalActivityAccountNameET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
            b.onboardKycPersonalActivityAccountNameWarningTV.visibility = View.GONE
        }
    }

    private fun setLatestOccupation(userData: ViewUserData) {
        val occupation = userData.occupation ?: ""
        //For employed
        val employedRole = userData.employed_role ?: ""
        val employerName = userData.employer_name ?: ""
        val industry = userData.industry ?: ""
        val townDistrict = userData.occupation_town ?: ""
        //For self employed
        val selfEmployedSpecify = userData.self_employed_specify ?: ""
        //For full time education
        val institute = userData.institute ?: ""
        val instituteSpecify = userData.institute_specify ?: ""
        //For other
        val occupationSpecify = userData.occupation_specify ?: ""
        //Hide additional employer fields by default
        b.onboardKycPersonalActivityEmployerDetailsContainer.visibility = View.GONE
        selectedCategory = occupation
        when (occupation) {
            getString(R.string.employed) -> {
                selectedSubCategory = employedRole
                //Show additional employer fields
                b.onboardKycPersonalActivityEmployerDetailsContainer.visibility = View.VISIBLE
                //set all the employer fields
                b.onboardKycPersonalActivityEmployerNameET.setText(employerName)
                b.onboardKycPersonalActivityEmployerNameET.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
                b.onboardKycPersonalActivityEmployerNameWarningTV.visibility = View.GONE

                b.onboardKycPersonalActivityIndustrySectorTV.text = industry

                b.onboardKycPersonalActivityTownDistrictET.removeTextChangedListener(
                    townDistrictEditTextWatcher
                )
                b.onboardKycPersonalActivityTownDistrictET.setText(townDistrict)
                b.onboardKycPersonalActivityTownDistrictET.addTextChangedListener(
                    townDistrictEditTextWatcher
                )
            }
            getString(R.string.self_employed) -> {
                selectedSubCategory = selfEmployedSpecify
            }
            getString(R.string.in_full_time_education) -> {
                if (instituteSpecify.isNotEmpty()) {
                    isCustomInstitute = true

                    selectedSubCategory = instituteSpecify
                } else {
                    selectedSubCategory = institute
                }
            }
            getString(R.string.others) -> {
                selectedSubCategory = occupationSpecify
            }
        }

        val formattedText =
            if (selectedSubCategory.isNotEmpty()) "$selectedCategory - $selectedSubCategory"
            else selectedCategory
        b.onboardKycPersonalActivityOccupationTV.text = formattedText

        if (viewScope == Constants.VIEW_SCOPE_UPDATE) {
            //Grey out
            b.onboardKycPersonalActivityOccupationTV.background =
                ContextCompat.getDrawable(this, R.color.defaultSelected)
            b.onboardKycPersonalActivityOccupationTV.isEnabled = false

            b.onboardKycPersonalActivityEmployerNameET.background =
                ContextCompat.getDrawable(this, R.color.defaultSelected)
            b.onboardKycPersonalActivityEmployerNameET.isEnabled = false

            b.onboardKycPersonalActivityIndustrySectorTV.background =
                ContextCompat.getDrawable(this, R.color.defaultSelected)
            b.onboardKycPersonalActivityIndustrySectorTV.isEnabled = false

            b.onboardKycPersonalActivityTownDistrictET.background =
                ContextCompat.getDrawable(this, R.color.defaultSelected)
            b.onboardKycPersonalActivityTownDistrictET.isEnabled = false
        }
    }

    private fun setLatestPurposeOfRelations(purposeOfRelations: ArrayList<String>?){
        if (!purposeOfRelations.isNullOrEmpty()) {
            for (item in purposeOfRelations) {
                when (item) {
                    getString(R.string.business_relationship1) -> b.onboardKycPersonalActivityBusinessRelationShip1CB.isChecked = true
                    getString(R.string.business_relationship2) -> b.onboardKycPersonalActivityBusinessRelationShip2CB.isChecked = true
                    getString(R.string.business_relationship3) -> b.onboardKycPersonalActivityBusinessRelationShip3CB.isChecked = true
                    getString(R.string.business_relationship4) -> b.onboardKycPersonalActivityBusinessRelationShip4CB.isChecked = true
                }
            }
        }

        if (viewScope == Constants.VIEW_SCOPE_UPDATE) {
            b.onboardKycPersonalActivityBusinessRelationShip1CB.isEnabled = false
            b.onboardKycPersonalActivityBusinessRelationShip2CB.isEnabled = false
            b.onboardKycPersonalActivityBusinessRelationShip3CB.isEnabled = false
            b.onboardKycPersonalActivityBusinessRelationShip4CB.isEnabled = false
        }
    }

    private fun clearAllFields() {
        /**Unselect gender*/
        b.onboardKycPersonalActivityGenderRG.clearCheck()

        /**Clear Dob*/
        unixTime = ""
        b.onboardKycPersonalActivityDOBTV.text = ""
        //Hide any warnings
        b.onboardKycPersonalActivityDOBTV.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        b.onboardKycPersonalActivityDOBWarningTV.visibility = View.GONE

        /**Clear occupation*/
        selectedCategory = ""
        selectedSubCategory = ""

        //Hide additional employer fields by default
        b.onboardKycPersonalActivityEmployerDetailsContainer.visibility = View.GONE
        b.onboardKycPersonalActivityOccupationTV.text = ""

        //Clear all the employer fields
        b.onboardKycPersonalActivityEmployerNameET.setText("")
        b.onboardKycPersonalActivityIndustrySectorTV.text = ""
        b.onboardKycPersonalActivityTownDistrictET.setText("")

        //Hide any warnings
        b.onboardKycPersonalActivityOccupationWarningTV.visibility = View.GONE
        b.onboardKycPersonalActivityEmployerNameWarningTV.visibility = View.GONE
        b.onboardKycPersonalActivityIndustrySectorWarningTV.visibility = View.GONE
        b.onboardKycPersonalActivityTownDistrictWarningTV.visibility = View.GONE
        b.onboardKycPersonalActivityOccupationTV.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        b.onboardKycPersonalActivityEmployerNameET.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        b.onboardKycPersonalActivityIndustrySectorTV.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        b.onboardKycPersonalActivityTownDistrictET.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        /**Clear purpose of relations*/
        b.onboardKycPersonalActivityBusinessRelationShip1CB.isChecked = false
        b.onboardKycPersonalActivityBusinessRelationShip2CB.isChecked = false
        b.onboardKycPersonalActivityBusinessRelationShip3CB.isChecked = false
        b.onboardKycPersonalActivityBusinessRelationShip4CB.isChecked = false
        //Hide any warnings
        b.onboardKycPersonalActivityBusinessRelationshipWarningTV.visibility = View.GONE

        /**Clear monthly income if kyc not belongs to simplified category*/
        if (kycScope != Constants.KYC_MALAWI_SIMPLIFIED) {
            b.onboardKycPersonalActivityMonthlyIncomeTV.text = ""
            //Hide any warnings
            b.onboardKycPersonalActivityMonthlyIncomeWarningTV.visibility = View.GONE
            b.onboardKycPersonalActivityMonthlyIncomeTV.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }

        /**Clear monthly withdrawal if kyc not belongs to simplified category*/
        if (kycScope != Constants.KYC_MALAWI_SIMPLIFIED) {
            b.onboardKycPersonalActivityMonthlyWithdrawalTV.text = ""
            //Hide any warnings
            b.onboardKycPersonalActivityMonthlyWithdrawalWarningTV.visibility = View.GONE
            b.onboardKycPersonalActivityMonthlyWithdrawalTV.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }

        /**Clear Bank details*/
        b.onboardKycPersonalActivityBankNameTV.text = ""
        b.onboardKycPersonalActivityAccountNumberET.setText("")
        b.onboardKycPersonalActivityAccountNameET.setText("")

        //Hide any warnings
        b.onboardKycPersonalActivityBankNameWarningTV.visibility = View.GONE
        b.onboardKycPersonalActivityAccountNumberWarningTV.visibility = View.GONE
        b.onboardKycPersonalActivityAccountNameWarningTV.visibility = View.GONE
        b.onboardKycPersonalActivityBankNameTV.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        b.onboardKycPersonalActivityAccountNumberET.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        b.onboardKycPersonalActivityAccountNameET.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)


    }

    private fun showButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView
    ) {
        actionButton.text = ""
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        loaderLottie.visibility = View.VISIBLE
    }

    private fun hideButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView, buttonText: String
    ) {
        actionButton.text = buttonText
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        loaderLottie.visibility = View.GONE
    }

    override fun onIndustrySectorSelected(industrySector: String) {
        b.onboardKycPersonalActivityIndustrySectorTV.text = industrySector
        b.onboardKycPersonalActivityIndustrySectorWarningTV.visibility = View.GONE
    }

    override fun onMonthlyIncomeSelected(monthlyIncome: String) {
        b.onboardKycPersonalActivityMonthlyIncomeTV.text = monthlyIncome
        b.onboardKycPersonalActivityMonthlyIncomeWarningTV.visibility = View.GONE
    }

    override fun onMonthlyWithdrawalSelected(monthlyWithdrawal: String) {
        b.onboardKycPersonalActivityMonthlyWithdrawalTV.text = monthlyWithdrawal
        b.onboardKycPersonalActivityMonthlyWithdrawalWarningTV.visibility = View.GONE
    }

    override fun onBankSelected(bankName: String) {
        b.onboardKycPersonalActivityBankNameTV.text = bankName
        b.onboardKycPersonalActivityBankNameWarningTV.visibility = View.GONE
    }
}