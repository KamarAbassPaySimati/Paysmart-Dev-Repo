package com.afrimax.paymaart.ui.kyc

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
import com.afrimax.paymaart.data.model.KycSaveAddressDetailsRequest
import com.afrimax.paymaart.data.model.KycUserData
import com.afrimax.paymaart.data.model.SaveNewAddressDetailsSelfKycRequest
import com.afrimax.paymaart.data.model.SelfKycDetailsResponse
import com.afrimax.paymaart.data.model.ViewUserData
import com.afrimax.paymaart.databinding.ActivityOnboardKycAddressBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.util.Constants
import com.airbnb.lottie.LottieAnimationView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class KycAddressActivity : BaseActivity() {
    private lateinit var b: ActivityOnboardKycAddressBinding
    private lateinit var kycScope: String
    private lateinit var viewScope: String
    private lateinit var placesClient: PlacesClient
    private lateinit var placesList: ArrayList<MalawiPlace>
    private lateinit var streetEditTextWatcher: TextWatcher
    private lateinit var townEditTextWatcher: TextWatcher
    private lateinit var districtEditTextWatcher: TextWatcher
    private lateinit var intlStreetEditTextWatcher: TextWatcher
    private lateinit var infoResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var nextScreenResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var nationResultLauncher: ActivityResultLauncher<Intent>
    private var shouldReloadDocs = true
    private var sendEmail = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        b = ActivityOnboardKycAddressBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.onboardKycAddressActivity)) { v, insets ->
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
        viewScope = intent.getStringExtra(Constants.VIEW_SCOPE) ?: Constants.VIEW_SCOPE_FILL
        sendEmail = intent.getBooleanExtra(Constants.KYC_SEND_EMAIL, true)

        Places.initialize(applicationContext, BuildConfig.PLACES_API_KEY)
        placesClient = Places.createClient(this)

        placesList = ArrayList()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val callbackIntent = Intent()
                callbackIntent.putExtra(Constants.KYC_SCOPE, kycScope)
                callbackIntent.putExtra(Constants.VIEW_SCOPE, viewScope)
                callbackIntent.putExtra(Constants.KYC_SEND_EMAIL, sendEmail)
                setResult(RESULT_CANCELED, callbackIntent)
                finish()
            }
        })

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
                    viewScope = data.getStringExtra(Constants.VIEW_SCOPE) ?: Constants.VIEW_SCOPE_FILL
                    sendEmail = data.getBooleanExtra(Constants.KYC_SEND_EMAIL, true)
                }
            }

        nationResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                //Don't reload data from getUserKycDataApi
                shouldReloadDocs = false
                if (result.resultCode == RESULT_OK) {
                    // There are no request codes
                    val data = result.data
                    populateNationDetails(data)
                }
            }
    }

    private fun populateNationDetails(data: Intent?) {
        if (data != null) {
            val nation = data.getStringExtra(Constants.KYC_NATION) ?: ""
            b.onboardKycAddressActivityIntlNationalityTV.text = nation
            b.onboardKycAddressActivityIntlNationalityWarningTV.visibility = View.GONE
        }
    }

    private fun setUpLayout() {
        when (kycScope) {
            Constants.KYC_MALAWI_FULL -> {
                b.onboardKycAddressActivityBackButtonTV.text = getString(R.string.full_kyc)

                //hide additional fields
                b.onboardKycAddressActivityIntlNationalityContainer.visibility = View.GONE
                b.onboardKycAddressActivityIntlContainer.visibility = View.GONE
            }

            Constants.KYC_MALAWI_SIMPLIFIED -> {
                b.onboardKycAddressActivityBackButtonTV.text = getString(R.string.simplified_kyc)

                //hide additional fields
                b.onboardKycAddressActivityIntlNationalityContainer.visibility = View.GONE
                b.onboardKycAddressActivityIntlContainer.visibility = View.GONE
            }

            Constants.KYC_NON_MALAWI -> {
                b.onboardKycAddressActivityBackButtonTV.text =
                    getString(R.string.non_malawi_full_kyc)

                //Show additional fields
                b.onboardKycAddressActivityIntlNationalityContainer.visibility = View.VISIBLE
                b.onboardKycAddressActivityIntlContainer.visibility = View.VISIBLE
            }
        }
        when (viewScope) {
            Constants.VIEW_SCOPE_FILL -> {
                b.onboardKycAddressActivityPB.max = 99
                b.onboardKycAddressActivityPB.progress = 33
                b.onboardKycAddressActivityProgressCountTV.text = getString(R.string.step1of3)
            }

            Constants.VIEW_SCOPE_EDIT -> {
                b.onboardKycAddressActivityPB.max = 100
                b.onboardKycAddressActivityPB.progress = 50
                b.onboardKycAddressActivityProgressCountTV.text = getString(R.string.step2of4)
            }

            Constants.VIEW_SCOPE_UPDATE -> {
                b.onboardKycAddressActivityPB.max = 100
                b.onboardKycAddressActivityPB.progress = 50
                b.onboardKycAddressActivityProgressCountTV.text = getString(R.string.step2of4)

                //Hide skip button
                b.onboardKycAddressActivitySkipButton.visibility = View.GONE
            }
        }
    }


    private fun setUpListeners() {

        b.onboardKycAddressActivityBackButtonIV.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.onboardKycAddressActivityInfoButtonIV.setOnClickListener {
            val i = Intent(this, KycRegistrationGuideActivity::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
            infoResultLauncher.launch(i, options)
        }

        b.onboardKycAddressActivityIntlNationalityTV.setOnClickListener {
            onClickNationality()

        }

        b.onboardKycAddressActivityStreetNameET.setOnItemClickListener { _, _, position, _ ->
            onStreetNameFilled(position)
        }

        b.onboardKycAddressActivityTownET.setOnItemClickListener { _, _, position, _ ->
            onTownFilled(position)
        }

        b.onboardKycAddressActivityDistrictET.setOnItemClickListener { _, _, position, _ ->
            onDistrictFilled(position)
        }

        b.onboardKycAddressActivityIntlStreetNameET.setOnItemClickListener { _, _, position, _ ->
            onIntlStreetNameFilled(position)
        }

//        b.onboardKycAddressActivityIntlTownET.setOnItemClickListener { _, _, position, _ ->
//            onIntlTownFilled(position)
//        }
//
//        b.onboardKycAddressActivityIntlDistrictET.setOnItemClickListener { _, _, position, _ ->
//            onIntlDistrictFilled(position)
//        }


        b.onboardKycAddressActivitySkipButton.setOnClickListener {
            val i = Intent(this@KycAddressActivity, KycIdentityActivity::class.java)
            i.putExtra(Constants.VIEW_SCOPE, viewScope)
            i.putExtra(Constants.KYC_SCOPE, kycScope)
            i.putExtra(Constants.KYC_SEND_EMAIL, sendEmail)
            nextScreenResultLauncher.launch(i)
        }


        b.onboardKycAddressActivitySaveAndContinueButton.setOnClickListener {
            if (viewScope == Constants.VIEW_SCOPE_UPDATE) {
                //User can't edit address details if they are updating to full kyc, so save and continue work as skip
                val i = Intent(this@KycAddressActivity, KycIdentityActivity::class.java)
                i.putExtra(Constants.KYC_SCOPE, kycScope)
                i.putExtra(Constants.VIEW_SCOPE, viewScope)
                i.putExtra(Constants.KYC_SEND_EMAIL, sendEmail)
                nextScreenResultLauncher.launch(i)
            } else {
                validateFieldsForSaveAndContinue()
            }
        }

        setupEditTextFocusListeners()
        setUpEditTextChangeListeners()
    }

    private fun onClickNationality() {
        clearAllFocusedFields()
        b.onboardKycAddressActivityIntlNationalityTV.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)

        val i = Intent(this, KycNationalityActivity::class.java)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
        nationResultLauncher.launch(i, options)
    }

    private fun onStreetNameFilled(position: Int) {
        val malawiPlace = placesList[position]
        b.onboardKycAddressActivityStreetNameET.removeTextChangedListener(streetEditTextWatcher)
        b.onboardKycAddressActivityStreetNameET.setText(malawiPlace.primaryText)
        b.onboardKycAddressActivityStreetNameET.setSelection(b.onboardKycAddressActivityStreetNameET.text.toString().length)
        b.onboardKycAddressActivityStreetNameET.addTextChangedListener(streetEditTextWatcher)

        lifecycleScope.launch {
            val place = fetchExactPlace(malawiPlace.placeId)

            val addressComponents = place.addressComponents
            if (addressComponents != null) {
                //de-register Text Watchers
                b.onboardKycAddressActivityDistrictET.removeTextChangedListener(
                    districtEditTextWatcher
                )
                b.onboardKycAddressActivityTownET.removeTextChangedListener(townEditTextWatcher)
                //Clear fields
                b.onboardKycAddressActivityDistrictET.text.clear()
                b.onboardKycAddressActivityTownET.text.clear()
                for (component in addressComponents.asList()) {
                    when {
                        component.types.contains("administrative_area_level_2") -> b.onboardKycAddressActivityDistrictET.setText(
                            component.name
                        )

                        component.types.contains("locality") -> b.onboardKycAddressActivityTownET.setText(
                            component.name
                        )
                    }
                }
                //Validate the filled fields
                validateTown()
                validateDistrict()
                //Re-register Text Watchers
                b.onboardKycAddressActivityDistrictET.addTextChangedListener(districtEditTextWatcher)
                b.onboardKycAddressActivityTownET.addTextChangedListener(townEditTextWatcher)
            }

        }
    }

    private fun onTownFilled(position: Int) {
        val malawiPlace = placesList[position]
        b.onboardKycAddressActivityTownET.removeTextChangedListener(townEditTextWatcher)
        b.onboardKycAddressActivityTownET.setText(malawiPlace.primaryText)
        b.onboardKycAddressActivityTownET.setSelection(b.onboardKycAddressActivityTownET.text.toString().length)
        b.onboardKycAddressActivityTownET.addTextChangedListener(townEditTextWatcher)

        lifecycleScope.launch {
            val place = fetchExactPlace(malawiPlace.placeId)

            val addressComponents = place.addressComponents
            if (addressComponents != null) {
                //de-register Text Watchers
                b.onboardKycAddressActivityDistrictET.removeTextChangedListener(
                    districtEditTextWatcher
                )
                //Clear fields
                b.onboardKycAddressActivityDistrictET.text.clear()
                for (component in addressComponents.asList()) {
                    when {
                        component.types.contains("administrative_area_level_2") -> b.onboardKycAddressActivityDistrictET.setText(
                            component.name
                        )
                    }
                }
                //Validate the filled fields
                validateDistrict()
                //Re-register Text Watchers
                b.onboardKycAddressActivityDistrictET.addTextChangedListener(districtEditTextWatcher)
            }
        }
    }

    private fun onDistrictFilled(position: Int) {
        val malawiPlace = placesList[position]
        b.onboardKycAddressActivityDistrictET.removeTextChangedListener(districtEditTextWatcher)
        b.onboardKycAddressActivityDistrictET.setText(malawiPlace.primaryText)
        b.onboardKycAddressActivityDistrictET.setSelection(b.onboardKycAddressActivityDistrictET.text.toString().length)
        b.onboardKycAddressActivityDistrictET.addTextChangedListener(districtEditTextWatcher)
    }

    private fun onIntlStreetNameFilled(position: Int) {
        val malawiPlace = placesList[position]
        b.onboardKycAddressActivityIntlStreetNameET.removeTextChangedListener(
            intlStreetEditTextWatcher
        )
        b.onboardKycAddressActivityIntlStreetNameET.setText(malawiPlace.fullText)
        b.onboardKycAddressActivityIntlStreetNameET.setSelection(b.onboardKycAddressActivityIntlStreetNameET.text.toString().length)
        b.onboardKycAddressActivityIntlStreetNameET.addTextChangedListener(intlStreetEditTextWatcher)

        /**
            lifecycleScope.launch {
            val place = fetchExactPlace(malawiPlace.placeId)

            val addressComponents = place.addressComponents
            if (addressComponents != null) {
                //de-register Text Watchers
                b.onboardKycAddressActivityIntlDistrictET.removeTextChangedListener(
                    intlDistrictEditTextWatcher
                )
                b.onboardKycAddressActivityIntlTownET.removeTextChangedListener(
                    intlTownEditTextWatcher
                )
                //Clear fields
                b.onboardKycAddressActivityIntlDistrictET.text.clear()
                b.onboardKycAddressActivityIntlTownET.text.clear()
                for (component in addressComponents.asList()) {
                    when {
                        component.types.contains("administrative_area_level_2") -> b.onboardKycAddressActivityIntlDistrictET.setText(
                            component.name
                        )

                        component.types.contains("locality") -> b.onboardKycAddressActivityIntlTownET.setText(
                            component.name
                        )
                    }
                }
                //Validate the filled fields
                validateIntlTown()
                validateIntlDistrict()
                //Re-register Text Watchers
                b.onboardKycAddressActivityIntlDistrictET.addTextChangedListener(
                    intlDistrictEditTextWatcher
                )
                b.onboardKycAddressActivityIntlTownET.addTextChangedListener(intlTownEditTextWatcher)
            }
           }
         */
    }

    /**
        private fun onIntlTownFilled(position: Int) {
        val malawiPlace = placesList[position]
        b.onboardKycAddressActivityIntlTownET.removeTextChangedListener(intlTownEditTextWatcher)
        b.onboardKycAddressActivityIntlTownET.setText(malawiPlace.primaryText)
        b.onboardKycAddressActivityIntlTownET.setSelection(b.onboardKycAddressActivityIntlTownET.text.toString().length)
        b.onboardKycAddressActivityIntlTownET.addTextChangedListener(intlTownEditTextWatcher)

        lifecycleScope.launch {
            val place = fetchExactPlace(malawiPlace.placeId)

            val addressComponents = place.addressComponents
            if (addressComponents != null) {
                //de-register Text Watchers
                b.onboardKycAddressActivityIntlDistrictET.removeTextChangedListener(
                    intlDistrictEditTextWatcher
                )
                //Clear fields
                b.onboardKycAddressActivityIntlDistrictET.text.clear()
                for (component in addressComponents.asList()) {
                    when {
                        component.types.contains("administrative_area_level_2") -> b.onboardKycAddressActivityIntlDistrictET.setText(
                            component.name
                        )
                    }
                }
                //Validate the filled fields
                validateIntlDistrict()
                //Re-register Text Watchers
                b.onboardKycAddressActivityIntlDistrictET.addTextChangedListener(
                    intlDistrictEditTextWatcher
                )
            }
        }
       }

        private fun onIntlDistrictFilled(position: Int) {
            val malawiPlace = placesList[position]
            b.onboardKycAddressActivityIntlDistrictET.removeTextChangedListener(
                intlDistrictEditTextWatcher
            )
            b.onboardKycAddressActivityIntlDistrictET.setText(malawiPlace.primaryText)
            b.onboardKycAddressActivityIntlDistrictET.setSelection(b.onboardKycAddressActivityDistrictET.text.toString().length)
            b.onboardKycAddressActivityIntlDistrictET.addTextChangedListener(intlDistrictEditTextWatcher)
        }
     */


    /**Refresh the items in AutoCompleteTextView*/
    private fun refreshStreetNameSuggestions(array: ArrayList<String>) {
        val adapter = ArrayAdapter(this, R.layout.text_auto_complete, array)
        b.onboardKycAddressActivityStreetNameET.setAdapter(adapter)
        b.onboardKycAddressActivityStreetNameET.showDropDown()

    }

    /**Refresh the items in AutoCompleteTextView*/
    private fun refreshTownSuggestions(array: ArrayList<String>) {
        val adapter = ArrayAdapter(this, R.layout.text_auto_complete, array)
        b.onboardKycAddressActivityTownET.setAdapter(adapter)
        b.onboardKycAddressActivityTownET.showDropDown()

    }

    /**Refresh the items in AutoCompleteTextView*/
    private fun refreshDistrictSuggestions(array: ArrayList<String>) {
        val adapter = ArrayAdapter(this, R.layout.text_auto_complete, array)
        b.onboardKycAddressActivityDistrictET.setAdapter(adapter)
        b.onboardKycAddressActivityDistrictET.showDropDown()

    }

    private fun refreshIntlStreetNameSuggestions(array: ArrayList<String>) {
        val adapter = ArrayAdapter(this, R.layout.text_auto_complete, array)
        b.onboardKycAddressActivityIntlStreetNameET.setAdapter(adapter)
        b.onboardKycAddressActivityIntlStreetNameET.showDropDown()

    }

    /**
        private fun refreshIntlTownSuggestions(array: ArrayList<String>) {
        val adapter = ArrayAdapter(this, R.layout.text_auto_complete, array)
        b.onboardKycAddressActivityIntlTownET.setAdapter(adapter)
        b.onboardKycAddressActivityIntlTownET.showDropDown()
        }

        private fun refreshIntlDistrictSuggestions(array: ArrayList<String>) {
            val adapter = ArrayAdapter(this, R.layout.text_auto_complete, array)
            b.onboardKycAddressActivityIntlDistrictET.setAdapter(adapter)
            b.onboardKycAddressActivityIntlDistrictET.showDropDown()
        }
     */


    private fun setupEditTextFocusListeners() {
        configureNormalEditTextFocusListener(b.onboardKycAddressActivityPOBoxET)
        configureNormalEditTextFocusListener(b.onboardKycAddressActivityHouseNameET)
        configureRequiredEditTextFocusListener(
            b.onboardKycAddressActivityStreetNameET, b.onboardKycAddressActivityStreetNameWarningTV
        )
        configureNormalEditTextFocusListener(b.onboardKycAddressActivityLandmarkET)
        configureRequiredEditTextFocusListener(
            b.onboardKycAddressActivityTownET, b.onboardKycAddressActivityTownWarningTV
        )
        configureRequiredEditTextFocusListener(
            b.onboardKycAddressActivityDistrictET, b.onboardKycAddressActivityDistrictWarningTV
        )

        //Setup focus listeners for additional fields (Non malawi)
        configureRequiredEditTextFocusListener(
            b.onboardKycAddressActivityIntlStreetNameET,
            b.onboardKycAddressActivityIntlStreetNameWarningTV
        )
        /**
            configureRequiredEditTextFocusListener(
            b.onboardKycAddressActivityIntlPostalET, b.onboardKycAddressActivityIntlPostalWarningTV
            )
            configureRequiredEditTextFocusListener(
                b.onboardKycAddressActivityIntlHouseNameET,
                b.onboardKycAddressActivityIntlHouseNameWarningTV
            )
            configureRequiredEditTextFocusListener(
                b.onboardKycAddressActivityIntlLandmarkET,
                b.onboardKycAddressActivityIntlLandmarkWarningTV
            )
            configureRequiredEditTextFocusListener(
                b.onboardKycAddressActivityIntlTownET, b.onboardKycAddressActivityIntlTownWarningTV
            )
            configureRequiredEditTextFocusListener(
                b.onboardKycAddressActivityIntlDistrictET,
                b.onboardKycAddressActivityIntlDistrictWarningTV
            )
         */

    }

    private fun setUpEditTextChangeListeners() {
        configureStreetNameTextChangeListener()
        configureTownTextChangeListener()
        configureDistrictTextChangeListener()

        //Setup TextChangeListener for additional fields - Non malawi
        configureIntlStreetNameTextChangeListener()
        /**
            configureIntlTownTextChangeListener()
            configureIntlDistrictTextChangeListener()
            configureNormalEditTextChangeListener(
            b.onboardKycAddressActivityIntlPostalET, b.onboardKycAddressActivityIntlPostalWarningTV
            )
            configureNormalEditTextChangeListener(
                b.onboardKycAddressActivityIntlHouseNameET,
                b.onboardKycAddressActivityIntlHouseNameWarningTV
            )

            configureNormalEditTextChangeListener(
                b.onboardKycAddressActivityIntlLandmarkET,
                b.onboardKycAddressActivityIntlLandmarkWarningTV
            )
         */
    }

    /**
        private fun configureNormalEditTextChangeListener(et: EditText, warningText: TextView) {
        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //
            }

            override fun afterTextChanged(s: Editable?) {
                if (et.text.toString().isEmpty()) {
                    validateOptionalFields()
                } else {
                    et.background = ContextCompat.getDrawable(
                        this@KycAddressActivity, R.drawable.bg_edit_text_focused
                    )
                    warningText.visibility = View.GONE
                }
            }

        })
    }
     */

    private fun configureStreetNameTextChangeListener() {
        streetEditTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {

                if (b.onboardKycAddressActivityStreetNameET.text.isEmpty()) {
                    b.onboardKycAddressActivityStreetNameWarningTV.visibility = View.VISIBLE
                    b.onboardKycAddressActivityStreetNameET.background = ContextCompat.getDrawable(
                        this@KycAddressActivity, R.drawable.bg_edit_text_error
                    )
                    /** Following else if block becomes true If: enter at-least 3 characters and it was not done by the AutoCompleteTextView
                     *By default the AutoCompleteTextView will populate whatever is displayed in the drop down menu.
                     *In our case we are showing the fullText retrieved from Places API. But we don't wanna put all those
                     *text in a single text field. so we have to set the Text in a custom manner*/
                } else if (b.onboardKycAddressActivityStreetNameET.text.length >= 3 && !b.onboardKycAddressActivityStreetNameET.isPerformingCompletion) {
                    b.onboardKycAddressActivityStreetNameWarningTV.visibility = View.GONE
                    b.onboardKycAddressActivityStreetNameET.background = ContextCompat.getDrawable(
                        this@KycAddressActivity, R.drawable.bg_edit_text_focused
                    )
                    getStreetsInMalawi(b.onboardKycAddressActivityStreetNameET.text.toString())
                } else {
                    b.onboardKycAddressActivityStreetNameWarningTV.visibility = View.GONE
                    b.onboardKycAddressActivityStreetNameET.background = ContextCompat.getDrawable(
                        this@KycAddressActivity, R.drawable.bg_edit_text_focused
                    )
                }
            }
        }
        b.onboardKycAddressActivityStreetNameET.addTextChangedListener(streetEditTextWatcher)
    }

    private fun configureTownTextChangeListener() {
        townEditTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                if (b.onboardKycAddressActivityTownET.text.isEmpty()) {
                    b.onboardKycAddressActivityTownWarningTV.visibility = View.VISIBLE
                    b.onboardKycAddressActivityTownET.background = ContextCompat.getDrawable(
                        this@KycAddressActivity, R.drawable.bg_edit_text_error
                    )
                } else if (b.onboardKycAddressActivityTownET.text.length >= 3 && !b.onboardKycAddressActivityTownET.isPerformingCompletion) {
                    b.onboardKycAddressActivityTownWarningTV.visibility = View.GONE
                    b.onboardKycAddressActivityTownET.background = ContextCompat.getDrawable(
                        this@KycAddressActivity, R.drawable.bg_edit_text_focused
                    )
                    getTownsInMalawi(b.onboardKycAddressActivityTownET.text.toString())
                } else {
                    b.onboardKycAddressActivityTownWarningTV.visibility = View.GONE
                    b.onboardKycAddressActivityTownET.background = ContextCompat.getDrawable(
                        this@KycAddressActivity, R.drawable.bg_edit_text_focused
                    )
                }
            }
        }
        b.onboardKycAddressActivityTownET.addTextChangedListener(townEditTextWatcher)
    }

    private fun configureDistrictTextChangeListener() {
        districtEditTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                if (b.onboardKycAddressActivityDistrictET.text.isEmpty()) {
                    b.onboardKycAddressActivityDistrictWarningTV.visibility = View.VISIBLE
                    b.onboardKycAddressActivityDistrictET.background = ContextCompat.getDrawable(
                        this@KycAddressActivity, R.drawable.bg_edit_text_error
                    )
                } else if (b.onboardKycAddressActivityDistrictET.text.length >= 3 && !b.onboardKycAddressActivityDistrictET.isPerformingCompletion) {
                    b.onboardKycAddressActivityDistrictWarningTV.visibility = View.GONE
                    b.onboardKycAddressActivityDistrictET.background = ContextCompat.getDrawable(
                        this@KycAddressActivity, R.drawable.bg_edit_text_focused
                    )
                    getDistrictsInMalawi(b.onboardKycAddressActivityDistrictET.text.toString())
                } else {
                    b.onboardKycAddressActivityDistrictWarningTV.visibility = View.GONE
                    b.onboardKycAddressActivityDistrictET.background = ContextCompat.getDrawable(
                        this@KycAddressActivity, R.drawable.bg_edit_text_focused
                    )
                }
            }
        }
        b.onboardKycAddressActivityDistrictET.addTextChangedListener(districtEditTextWatcher)
    }

    private fun configureIntlStreetNameTextChangeListener() {
        intlStreetEditTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {

                if (b.onboardKycAddressActivityIntlStreetNameET.text.isEmpty()) {
//                    validateOptionalFields()
                    /** Following else if block becomes true If: enter at-least 3 characters and it was not done by the AutoCompleteTextView
                     *By default the AutoCompleteTextView will populate whatever is displayed in the drop down menu.
                     *In our case we are showing the fullText retrieved from Places API. But we don't wanna put all those
                     *text in a single text field. so we have to set the Text in a custom manner*/
                } else if (b.onboardKycAddressActivityIntlStreetNameET.text.length >= 3 && !b.onboardKycAddressActivityIntlStreetNameET.isPerformingCompletion) {
                    b.onboardKycAddressActivityIntlStreetNameWarningTV.visibility = View.GONE
                    b.onboardKycAddressActivityIntlStreetNameET.background =
                        ContextCompat.getDrawable(
                            this@KycAddressActivity, R.drawable.bg_edit_text_focused
                        )
                    getStreetsIntl(b.onboardKycAddressActivityIntlStreetNameET.text.toString())
                } else {
                    b.onboardKycAddressActivityIntlStreetNameWarningTV.visibility = View.GONE
                    b.onboardKycAddressActivityIntlStreetNameET.background =
                        ContextCompat.getDrawable(
                            this@KycAddressActivity, R.drawable.bg_edit_text_focused
                        )
                }
            }
        }
        b.onboardKycAddressActivityIntlStreetNameET.addTextChangedListener(intlStreetEditTextWatcher)
    }

    /**
        private fun configureIntlTownTextChangeListener() {
        intlTownEditTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                if (b.onboardKycAddressActivityIntlTownET.text.isEmpty()) {
                    validateOptionalFields()
                } else if (b.onboardKycAddressActivityIntlTownET.text.length >= 3 && !b.onboardKycAddressActivityIntlTownET.isPerformingCompletion) {
                    b.onboardKycAddressActivityIntlTownWarningTV.visibility = View.GONE
                    b.onboardKycAddressActivityIntlTownET.background = ContextCompat.getDrawable(
                        this@KycAddressActivity, R.drawable.bg_edit_text_focused
                    )
                    getTownsIntl(b.onboardKycAddressActivityIntlTownET.text.toString())
                } else {
                    b.onboardKycAddressActivityIntlTownWarningTV.visibility = View.GONE
                    b.onboardKycAddressActivityIntlTownET.background = ContextCompat.getDrawable(
                        this@KycAddressActivity, R.drawable.bg_edit_text_focused
                    )
                }
            }
        }
        b.onboardKycAddressActivityIntlTownET.addTextChangedListener(intlTownEditTextWatcher)
       }
     */

    /**
        private fun configureIntlDistrictTextChangeListener() {
        intlDistrictEditTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                if (b.onboardKycAddressActivityIntlDistrictET.text.isEmpty()) {
                    validateOptionalFields()
                } else if (b.onboardKycAddressActivityIntlDistrictET.text.length >= 3 && !b.onboardKycAddressActivityIntlDistrictET.isPerformingCompletion) {
                    b.onboardKycAddressActivityIntlDistrictWarningTV.visibility = View.GONE
                    b.onboardKycAddressActivityIntlDistrictET.background =
                        ContextCompat.getDrawable(
                            this@KycAddressActivity, R.drawable.bg_edit_text_focused
                        )
                    getDistrictsIntl(b.onboardKycAddressActivityIntlDistrictET.text.toString())
                } else {
                    b.onboardKycAddressActivityIntlDistrictWarningTV.visibility = View.GONE
                    b.onboardKycAddressActivityIntlDistrictET.background =
                        ContextCompat.getDrawable(
                            this@KycAddressActivity, R.drawable.bg_edit_text_focused
                        )
                }
            }
        }
        b.onboardKycAddressActivityIntlDistrictET.addTextChangedListener(intlDistrictEditTextWatcher)
       }
     */

    private fun configureNormalEditTextFocusListener(et: EditText) {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        et.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                clearAllFocusedFields()
                et.background = focusDrawable
            } else {
                et.background = notInFocusDrawable
            }
        }
    }

    private fun configureRequiredEditTextFocusListener(et: EditText, warningText: TextView) {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        et.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                clearAllFocusedFields()
                et.background = focusDrawable
            } else if (warningText.isVisible) et.background = errorDrawable
            else et.background = notInFocusDrawable

        }
    }

    private fun getStreetsInMalawi(queryString: String) {
        val tempPlacesNamesList = ArrayList<String>()
        val token = AutocompleteSessionToken.newInstance()
        val request =
            FindAutocompletePredictionsRequest.builder().setSessionToken(token).setCountries("MW")
                .setTypesFilter(
                    mutableListOf(PlaceTypes.ROUTE)
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
            refreshStreetNameSuggestions(tempPlacesNamesList)
        }.addOnFailureListener {//
            //
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

    private fun getDistrictsInMalawi(queryString: String) {
        val tempPlacesNamesList = ArrayList<String>()
        val token = AutocompleteSessionToken.newInstance()
        val request =
            FindAutocompletePredictionsRequest.builder().setSessionToken(token).setCountries("MW")
                .setTypesFilter(
                    mutableListOf(PlaceTypes.ADMINISTRATIVE_AREA_LEVEL_2)
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
            refreshDistrictSuggestions(tempPlacesNamesList)
        }.addOnFailureListener {//
            //
        }
    }

    private fun getStreetsIntl(queryString: String) {
        val tempPlacesNamesList = ArrayList<String>()
        val token = AutocompleteSessionToken.newInstance()
        val request =
            FindAutocompletePredictionsRequest.builder().setSessionToken(token).setTypesFilter(
                mutableListOf(PlaceTypes.ROUTE)
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
            refreshIntlStreetNameSuggestions(tempPlacesNamesList)
        }.addOnFailureListener {//
            //
        }
    }

    /**
        private fun getTownsIntl(queryString: String) {
        val tempPlacesNamesList = ArrayList<String>()
        val token = AutocompleteSessionToken.newInstance()
        val request =
            FindAutocompletePredictionsRequest.builder().setSessionToken(token).setTypesFilter(
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
            refreshIntlTownSuggestions(tempPlacesNamesList)
        }.addOnFailureListener {//
            //
        }
    }

    private fun getDistrictsIntl(queryString: String) {
        val tempPlacesNamesList = ArrayList<String>()
        val token = AutocompleteSessionToken.newInstance()
        val request =
            FindAutocompletePredictionsRequest.builder().setSessionToken(token).setTypesFilter(
                mutableListOf(PlaceTypes.ADMINISTRATIVE_AREA_LEVEL_2)
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
            refreshIntlDistrictSuggestions(tempPlacesNamesList)
        }.addOnFailureListener {//
            //
        }
    }
     */

    private suspend fun fetchExactPlace(placeId: String): Place {
        val request =
            FetchPlaceRequest.builder(placeId, mutableListOf(Place.Field.ADDRESS_COMPONENTS))
                .build()

        return suspendCoroutine { continuation ->
            placesClient.fetchPlace(request).addOnSuccessListener {
                continuation.resume(it.place)
            }
        }
    }

    private fun validateFieldsForSaveAndContinue() {
        var isValid = true
        var focusView: View? = null

        if (!validateStreetName()) {
            isValid = false
            focusView = b.onboardKycAddressActivityStreetNameWarningTV
        }

        if (!validateTown()) {
            isValid = false
            if (focusView == null) focusView = b.onboardKycAddressActivityTownWarningTV
        }

        if (!validateDistrict()) {
            isValid = false
            if (focusView == null) focusView = b.onboardKycAddressActivityDistrictWarningTV
        }

        //Only for non malawi kyc
        if (kycScope == Constants.KYC_NON_MALAWI) {
            if (!validateNationality()) {
                isValid = false
                if (focusView == null) focusView =
                    b.onboardKycAddressActivityIntlNationalityWarningTV
            }

            if (!validateInternationalAddress()) {
                isValid = false
                if (focusView == null) focusView = b.onboardKycAddressActivityIntlStreetNameWarningTV
            }
        }


        if (isValid) {
            when (viewScope) {
                Constants.VIEW_SCOPE_FILL -> saveCustomerAddressDetailsApi()
                Constants.VIEW_SCOPE_EDIT -> updateCustomerAddressDetailsApi()
            }
        } else {
            focusView!!.parent.requestChildFocus(focusView, focusView)
        }
    }

    private fun validateNationality(): Boolean {
        var isValid = true
        if (b.onboardKycAddressActivityIntlNationalityTV.text.isEmpty()) {
            isValid = false
            b.onboardKycAddressActivityIntlNationalityWarningTV.visibility = View.VISIBLE
            b.onboardKycAddressActivityIntlNationalityWarningTV.text =
                getString(R.string.required_field)
            b.onboardKycAddressActivityIntlNationalityTV.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        } else {
            b.onboardKycAddressActivityIntlNationalityWarningTV.visibility = View.GONE
            b.onboardKycAddressActivityIntlNationalityTV.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }
        return isValid
    }

    private fun validateStreetName(): Boolean {
        var isValid = true
        if (b.onboardKycAddressActivityStreetNameET.text.isEmpty()) {
            isValid = false
            b.onboardKycAddressActivityStreetNameWarningTV.visibility = View.VISIBLE
            b.onboardKycAddressActivityStreetNameWarningTV.text = getString(R.string.required_field)
            b.onboardKycAddressActivityStreetNameET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        } else {
            b.onboardKycAddressActivityStreetNameWarningTV.visibility = View.GONE
            b.onboardKycAddressActivityStreetNameET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }
        return isValid
    }

    private fun validateTown(): Boolean {
        var isValid = true
        if (b.onboardKycAddressActivityTownET.text.isEmpty()) {
            isValid = false
            b.onboardKycAddressActivityTownWarningTV.visibility = View.VISIBLE
            b.onboardKycAddressActivityTownWarningTV.text = getString(R.string.required_field)
            b.onboardKycAddressActivityTownET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        } else {
            b.onboardKycAddressActivityTownWarningTV.visibility = View.GONE
            b.onboardKycAddressActivityTownET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }
        return isValid
    }

    private fun validateDistrict(): Boolean {
        var isValid = true
        if (b.onboardKycAddressActivityDistrictET.text.isEmpty()) {
            isValid = false
            b.onboardKycAddressActivityDistrictWarningTV.visibility = View.VISIBLE
            b.onboardKycAddressActivityDistrictWarningTV.text = getString(R.string.required_field)
            b.onboardKycAddressActivityDistrictET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        } else {
            b.onboardKycAddressActivityDistrictWarningTV.visibility = View.GONE
            b.onboardKycAddressActivityDistrictET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }
        return isValid
    }

    private fun validateInternationalAddress(): Boolean{
        var isValid = true
        if (b.onboardKycAddressActivityIntlStreetNameET.text.isEmpty()) {
            isValid = false
            b.onboardKycAddressActivityIntlStreetNameWarningTV.visibility = View.VISIBLE
            b.onboardKycAddressActivityIntlStreetNameWarningTV.text = getString(R.string.required_field)
            b.onboardKycAddressActivityIntlStreetNameET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        } else {
            b.onboardKycAddressActivityIntlStreetNameWarningTV.visibility = View.GONE
            b.onboardKycAddressActivityIntlStreetNameET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }
        return isValid
    }

    /**
        private fun validateIntlTown(): Boolean {
        var isValid = true
        if (b.onboardKycAddressActivityIntlTownET.text.isEmpty()) {
            isValid = false
            b.onboardKycAddressActivityIntlTownWarningTV.visibility = View.VISIBLE
            b.onboardKycAddressActivityIntlTownWarningTV.text = getString(R.string.required_field)
            b.onboardKycAddressActivityIntlTownET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        } else {
            b.onboardKycAddressActivityIntlTownWarningTV.visibility = View.GONE
            b.onboardKycAddressActivityIntlTownET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }
        return isValid
    }

    private fun validateIntlDistrict(): Boolean {
        var isValid = true
        if (b.onboardKycAddressActivityIntlDistrictET.text.isEmpty()) {
            isValid = false
            b.onboardKycAddressActivityIntlDistrictWarningTV.visibility = View.VISIBLE
            b.onboardKycAddressActivityIntlDistrictWarningTV.text =
                getString(R.string.required_field)
            b.onboardKycAddressActivityIntlDistrictET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
        } else {
            b.onboardKycAddressActivityIntlDistrictWarningTV.visibility = View.GONE
            b.onboardKycAddressActivityIntlDistrictET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }
        return isValid
    }

    private fun validateOptionalFields(): Boolean {
        val intlPostal = b.onboardKycAddressActivityIntlPostalET.text.toString()
        val intlHouseName = b.onboardKycAddressActivityIntlHouseNameET.text.toString()
        val intlStreetName = b.onboardKycAddressActivityIntlStreetNameET.text.toString()
        val intLandmark = b.onboardKycAddressActivityIntlLandmarkET.text.toString()
        val intlTownName = b.onboardKycAddressActivityIntlTownET.text.toString()
        val intlDistrictName = b.onboardKycAddressActivityIntlDistrictET.text.toString()

        //Either none of the fields are filled OR all the fields are filled
        val isValid =
            intlPostal.isEmpty() == intlHouseName.isEmpty() && intlHouseName.isEmpty() == intlStreetName.isEmpty() && intlStreetName.isEmpty() == intLandmark.isEmpty() && intLandmark.isEmpty() == intlTownName.isEmpty() && intlTownName.isEmpty() == intlDistrictName.isEmpty()


        if (!isValid) {
            if (intlPostal.isEmpty()) {
                b.onboardKycAddressActivityIntlPostalWarningTV.visibility = View.VISIBLE
                b.onboardKycAddressActivityIntlPostalWarningTV.text =
                    getString(R.string.required_field)
                b.onboardKycAddressActivityIntlPostalET.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
            }
            if (intlHouseName.isEmpty()) {
                b.onboardKycAddressActivityIntlHouseNameWarningTV.visibility = View.VISIBLE
                b.onboardKycAddressActivityIntlHouseNameWarningTV.text =
                    getString(R.string.required_field)
                b.onboardKycAddressActivityIntlHouseNameET.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
            }
            if (intlStreetName.isEmpty()) {
                b.onboardKycAddressActivityIntlStreetNameWarningTV.visibility = View.VISIBLE
                b.onboardKycAddressActivityIntlStreetNameWarningTV.text =
                    getString(R.string.required_field)
                b.onboardKycAddressActivityIntlStreetNameET.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
            }
            if (intLandmark.isEmpty()) {
                b.onboardKycAddressActivityIntlLandmarkWarningTV.visibility = View.VISIBLE
                b.onboardKycAddressActivityIntlLandmarkWarningTV.text =
                    getString(R.string.required_field)
                b.onboardKycAddressActivityIntlLandmarkET.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
            }
            if (intlTownName.isEmpty()) {
                b.onboardKycAddressActivityIntlTownWarningTV.visibility = View.VISIBLE
                b.onboardKycAddressActivityIntlTownWarningTV.text =
                    getString(R.string.required_field)
                b.onboardKycAddressActivityIntlTownET.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
            }
            if (intlDistrictName.isEmpty()) {
                b.onboardKycAddressActivityIntlDistrictWarningTV.visibility = View.VISIBLE
                b.onboardKycAddressActivityIntlDistrictWarningTV.text =
                    getString(R.string.required_field)
                b.onboardKycAddressActivityIntlDistrictET.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)
            }
        } else {
            //Clear all error messages
            b.onboardKycAddressActivityIntlPostalWarningTV.visibility = View.GONE
            b.onboardKycAddressActivityIntlHouseNameWarningTV.visibility = View.GONE
            b.onboardKycAddressActivityIntlStreetNameWarningTV.visibility = View.GONE
            b.onboardKycAddressActivityIntlLandmarkWarningTV.visibility = View.GONE
            b.onboardKycAddressActivityIntlTownWarningTV.visibility = View.GONE
            b.onboardKycAddressActivityIntlDistrictWarningTV.visibility = View.GONE
            b.onboardKycAddressActivityIntlPostalET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
            b.onboardKycAddressActivityIntlHouseNameET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
            b.onboardKycAddressActivityIntlStreetNameET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
            b.onboardKycAddressActivityIntlLandmarkET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
            b.onboardKycAddressActivityIntlTownET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
            b.onboardKycAddressActivityIntlDistrictET.background =
                ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        }
        return isValid
    }
     */

    private fun saveCustomerAddressDetailsApi() {
        showButtonLoader(
            b.onboardKycAddressActivitySaveAndContinueButton,
            b.onboardKycAddressActivitySaveAndContinueButtonLoaderLottie
        )
        val poBoxNumber = b.onboardKycAddressActivityPOBoxET.text.toString()
        val houseName = b.onboardKycAddressActivityHouseNameET.text.toString()
        val streetName = b.onboardKycAddressActivityStreetNameET.text.toString()
        val landMark = b.onboardKycAddressActivityLandmarkET.text.toString()
        val townVillage = b.onboardKycAddressActivityTownET.text.toString()
        val district = b.onboardKycAddressActivityDistrictET.text.toString()
        //Additional fields
        val intlNationality = b.onboardKycAddressActivityIntlNationalityTV.text.toString().ifEmpty { null }
        val intlAddress = b.onboardKycAddressActivityIntlStreetNameET.text.toString().ifEmpty { null }

        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val saveAddressDetailsCall = ApiClient.apiService.saveCustomerAddressDetails(
                "Bearer $idToken", KycSaveAddressDetailsRequest(
                    po_box_no = poBoxNumber,
                    house_number = houseName,
                    street_name = streetName,
                    landmark = landMark,
                    town_village_ta = townVillage,
                    district = district,
                    citizen = intlNationality,
                    intl_address = intlAddress,
                    address_details_status = Constants.KYC_STATUS_COMPLETED
                )
            )

            saveAddressDetailsCall.enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(
                    call: Call<DefaultResponse>, response: Response<DefaultResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        runOnUiThread {
                            hideButtonLoader(
                                b.onboardKycAddressActivitySaveAndContinueButton,
                                b.onboardKycAddressActivitySaveAndContinueButtonLoaderLottie,
                                getString(R.string.save_and_continue)
                            )
                            val i = Intent(
                                this@KycAddressActivity,
                                KycIdentityActivity::class.java
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
                                b.onboardKycAddressActivitySaveAndContinueButton,
                                b.onboardKycAddressActivitySaveAndContinueButtonLoaderLottie,
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
                            b.onboardKycAddressActivitySaveAndContinueButton,
                            b.onboardKycAddressActivitySaveAndContinueButtonLoaderLottie,
                            getString(R.string.save_and_continue)
                        )
                    }
                }
            })
        }
    }

    private fun updateCustomerAddressDetailsApi() {
        showButtonLoader(
            b.onboardKycAddressActivitySaveAndContinueButton,
            b.onboardKycAddressActivitySaveAndContinueButtonLoaderLottie
        )

        val poBoxNumber = b.onboardKycAddressActivityPOBoxET.text.toString()
        val houseName = b.onboardKycAddressActivityHouseNameET.text.toString()
        val streetName = b.onboardKycAddressActivityStreetNameET.text.toString()
        val landMark = b.onboardKycAddressActivityLandmarkET.text.toString()
        val townVillage = b.onboardKycAddressActivityTownET.text.toString()
        val district = b.onboardKycAddressActivityDistrictET.text.toString()
        //Additional fields
        val intlNationality = b.onboardKycAddressActivityIntlNationalityTV.text.toString().ifEmpty { null }
        val intlAddress = b.onboardKycAddressActivityIntlStreetNameET.text.toString().ifEmpty { null }


        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val saveNewAddressCall = ApiClient.apiService.saveNewAddressDetailsSelfKyc(
                idToken, SaveNewAddressDetailsSelfKycRequest(
                    po_box_no = poBoxNumber,
                    house_number = houseName,
                    street_name = streetName,
                    landmark = landMark,
                    town_village_ta = townVillage,
                    district = district,
                    citizen = intlNationality,
                    intl_address = intlAddress,
                    address_details_status = Constants.KYC_STATUS_COMPLETED,
                    sent_email = sendEmail
                )
            )

            saveNewAddressCall.enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(
                    call: Call<DefaultResponse>, response: Response<DefaultResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        //Email already sent, so stop sending email again for further  by making sendEmail value false
                        sendEmail = false
                        runOnUiThread {
                            hideButtonLoader(
                                b.onboardKycAddressActivitySaveAndContinueButton,
                                b.onboardKycAddressActivitySaveAndContinueButtonLoaderLottie,
                                getString(R.string.save_and_continue)
                            )
                            val i = Intent(
                                this@KycAddressActivity, KycIdentityActivity::class.java
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
                                b.onboardKycAddressActivitySaveAndContinueButton,
                                b.onboardKycAddressActivitySaveAndContinueButtonLoaderLottie,
                                getString(R.string.save_and_continue)
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    runOnUiThread {
                        showToast(getString(R.string.default_error_toast))
                        hideButtonLoader(
                            b.onboardKycAddressActivitySaveAndContinueButton,
                            b.onboardKycAddressActivitySaveAndContinueButtonLoaderLottie,
                            getString(R.string.save_and_continue)
                        )
                    }
                }
            })
        }
    }


    override fun onResume() {
        super.onResume()
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
        b.onboardKycAddressActivityLoaderLottie.visibility = View.VISIBLE
        b.onboardKycAddressActivityContentBox.visibility = View.GONE

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
                        populateKycAddressFields(body.data)
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
        b.onboardKycAddressActivityLoaderLottie.visibility = View.VISIBLE
        b.onboardKycAddressActivityContentBox.visibility = View.GONE

        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val getUserKycDataCall = ApiClient.apiService.getSelfKycUserData("Bearer $idToken")

            getUserKycDataCall.enqueue(object : Callback<SelfKycDetailsResponse> {
                override fun onResponse(
                    call: Call<SelfKycDetailsResponse>, response: Response<SelfKycDetailsResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        populateLatestKycAddressFields(body.data)
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

    private fun populateKycAddressFields(data: KycUserData) {
        var isValid = true

        val poBox = data.po_box_no ?: ""
        val houseName = data.house_number ?: ""
        val streetName = data.street_name ?: ""
        val landmark = data.landmark ?: ""
        val townVillage = data.town_village_ta ?: ""
        val district = data.district ?: ""
        //Additional fields for Non malawi
        val intlNationality = data.citizen ?: ""
        val intlStreetName = data.intl_address ?: ""
        val addressDetailsStatus = data.address_details_status ?: ""

        if (streetName.isEmpty() || townVillage.isEmpty() || district.isEmpty()) isValid = false

        if (isValid && addressDetailsStatus == Constants.KYC_STATUS_COMPLETED) {
            //De-register Text Watchers
            b.onboardKycAddressActivityStreetNameET.removeTextChangedListener(streetEditTextWatcher)
            b.onboardKycAddressActivityTownET.removeTextChangedListener(townEditTextWatcher)
            b.onboardKycAddressActivityDistrictET.removeTextChangedListener(districtEditTextWatcher)
            b.onboardKycAddressActivityIntlStreetNameET.removeTextChangedListener(
                intlStreetEditTextWatcher
            )

            //Populate the fields
            b.onboardKycAddressActivityPOBoxET.setText(poBox)
            b.onboardKycAddressActivityHouseNameET.setText(houseName)
            b.onboardKycAddressActivityStreetNameET.setText(streetName)
            b.onboardKycAddressActivityLandmarkET.setText(landmark)
            b.onboardKycAddressActivityTownET.setText(townVillage)
            b.onboardKycAddressActivityDistrictET.setText(district)
            //Populate Additional fields
            b.onboardKycAddressActivityIntlNationalityTV.text = intlNationality
            b.onboardKycAddressActivityIntlStreetNameET.setText(intlStreetName)

            //Re-register TextWatchers
            b.onboardKycAddressActivityStreetNameET.addTextChangedListener(streetEditTextWatcher)
            b.onboardKycAddressActivityTownET.addTextChangedListener(townEditTextWatcher)
            b.onboardKycAddressActivityDistrictET.addTextChangedListener(districtEditTextWatcher)
            b.onboardKycAddressActivityIntlStreetNameET.addTextChangedListener(
                intlStreetEditTextWatcher
            )
        }

        b.onboardKycAddressActivitySV.scrollTo(0, 0)
        b.onboardKycAddressActivityLoaderLottie.visibility = View.GONE
        b.onboardKycAddressActivityContentBox.visibility = View.VISIBLE
    }

    private fun populateLatestKycAddressFields(data: ViewUserData) {
        var isValid = true

        val poBox = data.po_box_no ?: ""
        val houseName = data.house_number ?: ""
        val streetName = data.street_name ?: ""
        val landmark = data.landmark ?: ""
        val townVillage = data.town_village_ta ?: ""
        val district = data.district ?: ""
        //Additional fields for Non malawi
        val intlNationality = data.citizen ?: ""
        val intlStreetName = data.intl_address ?: ""
        val addressDetailsStatus = data.address_details_status ?: ""
        if (streetName.isEmpty() || townVillage.isEmpty() || district.isEmpty()) isValid = false
        if (isValid && addressDetailsStatus == Constants.KYC_STATUS_COMPLETED) {
            //De-register Text Watchers
            b.onboardKycAddressActivityStreetNameET.removeTextChangedListener(streetEditTextWatcher)
            b.onboardKycAddressActivityTownET.removeTextChangedListener(townEditTextWatcher)
            b.onboardKycAddressActivityDistrictET.removeTextChangedListener(districtEditTextWatcher)
            b.onboardKycAddressActivityIntlStreetNameET.removeTextChangedListener(
                intlStreetEditTextWatcher
            )
            //Populate the fields
            b.onboardKycAddressActivityPOBoxET.setText(poBox)
            b.onboardKycAddressActivityHouseNameET.setText(houseName)
            b.onboardKycAddressActivityStreetNameET.setText(streetName)
            b.onboardKycAddressActivityLandmarkET.setText(landmark)
            b.onboardKycAddressActivityTownET.setText(townVillage)
            b.onboardKycAddressActivityDistrictET.setText(district)
            //Populate Additional fields
            b.onboardKycAddressActivityIntlNationalityTV.text = intlNationality
            b.onboardKycAddressActivityIntlStreetNameET.setText(intlStreetName)

            if (viewScope == Constants.VIEW_SCOPE_UPDATE) {
                //Grey out fields
                b.onboardKycAddressActivityPOBoxET.isEnabled = false
                b.onboardKycAddressActivityPOBoxET.background =
                    ContextCompat.getDrawable(this, R.color.defaultSelected)

                b.onboardKycAddressActivityHouseNameET.isEnabled = false
                b.onboardKycAddressActivityHouseNameET.background =
                    ContextCompat.getDrawable(this, R.color.defaultSelected)

                b.onboardKycAddressActivityStreetNameET.isEnabled = false
                b.onboardKycAddressActivityStreetNameET.background =
                    ContextCompat.getDrawable(this, R.color.defaultSelected)

                b.onboardKycAddressActivityLandmarkET.isEnabled = false
                b.onboardKycAddressActivityLandmarkET.background =
                    ContextCompat.getDrawable(this, R.color.defaultSelected)

                b.onboardKycAddressActivityTownET.isEnabled = false
                b.onboardKycAddressActivityTownET.background =
                    ContextCompat.getDrawable(this, R.color.defaultSelected)

                b.onboardKycAddressActivityDistrictET.isEnabled = false
                b.onboardKycAddressActivityDistrictET.background =
                    ContextCompat.getDrawable(this, R.color.defaultSelected)
            }

            //Re-register TextWatchers
            b.onboardKycAddressActivityStreetNameET.addTextChangedListener(streetEditTextWatcher)
            b.onboardKycAddressActivityTownET.addTextChangedListener(townEditTextWatcher)
            b.onboardKycAddressActivityDistrictET.addTextChangedListener(districtEditTextWatcher)
            b.onboardKycAddressActivityIntlStreetNameET.addTextChangedListener(intlStreetEditTextWatcher)
        }

        b.onboardKycAddressActivitySV.scrollTo(0, 0)
        b.onboardKycAddressActivityLoaderLottie.visibility = View.GONE
        b.onboardKycAddressActivityContentBox.visibility = View.VISIBLE

    }

    private fun clearAllFields() {
        //Clear the fields
        b.onboardKycAddressActivityPOBoxET.setText("")
        b.onboardKycAddressActivityHouseNameET.setText("")
        b.onboardKycAddressActivityStreetNameET.setText("")
        b.onboardKycAddressActivityLandmarkET.setText("")
        b.onboardKycAddressActivityTownET.setText("")
        b.onboardKycAddressActivityDistrictET.setText("")

        //Clear additional fields - Non malawi
        b.onboardKycAddressActivityIntlNationalityTV.text = ""
        b.onboardKycAddressActivityIntlStreetNameET.setText("")


        //Hide any warnings
        b.onboardKycAddressActivityStreetNameWarningTV.visibility = View.GONE
        b.onboardKycAddressActivityTownWarningTV.visibility = View.GONE
        b.onboardKycAddressActivityDistrictWarningTV.visibility = View.GONE
        b.onboardKycAddressActivityStreetNameET.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        b.onboardKycAddressActivityTownET.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        b.onboardKycAddressActivityDistrictET.background =
            ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        //Hide any warnings in additional fields
        b.onboardKycAddressActivityIntlNationalityWarningTV.visibility = View.GONE
        b.onboardKycAddressActivityIntlStreetNameWarningTV.visibility = View.GONE
        b.onboardKycAddressActivityIntlStreetNameET.background = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
    }

    private fun clearAllFocusedFields() {
        val unfocusedDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_error)

        if (!b.onboardKycAddressActivityIntlNationalityWarningTV.isVisible) b.onboardKycAddressActivityIntlNationalityTV.background =
            unfocusedDrawable
        else b.onboardKycAddressActivityIntlNationalityTV.background = errorDrawable

        if (!b.onboardKycAddressActivityStreetNameWarningTV.isVisible) b.onboardKycAddressActivityStreetNameET.background =
            unfocusedDrawable
        else b.onboardKycAddressActivityStreetNameET.background = errorDrawable

        if (!b.onboardKycAddressActivityTownWarningTV.isVisible) b.onboardKycAddressActivityTownET.background =
            unfocusedDrawable
        else b.onboardKycAddressActivityTownET.background = errorDrawable

        if (!b.onboardKycAddressActivityDistrictWarningTV.isVisible) b.onboardKycAddressActivityDistrictET.background =
            unfocusedDrawable
        else b.onboardKycAddressActivityDistrictET.background = errorDrawable

        if (!b.onboardKycAddressActivityIntlStreetNameWarningTV.isVisible) b.onboardKycAddressActivityIntlStreetNameET.background =
            unfocusedDrawable
        else b.onboardKycAddressActivityIntlStreetNameET.background = errorDrawable
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
}