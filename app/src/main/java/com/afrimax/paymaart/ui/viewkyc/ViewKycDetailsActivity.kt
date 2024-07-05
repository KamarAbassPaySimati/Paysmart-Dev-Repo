package com.afrimax.paymaart.ui.viewkyc

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.afrimax.paymaart.BuildConfig
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.model.ViewUserData
import com.afrimax.paymaart.databinding.ActivityViewKycDetailsBinding
import com.afrimax.paymaart.databinding.ContentViewKycReasonForRejectionBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.kyc.KycCustomerPersonalDetailsActivity
import com.afrimax.paymaart.ui.kyc.KycFullScreenPreviewActivity
import com.afrimax.paymaart.ui.kyc.KycProgressActivity
import com.afrimax.paymaart.ui.utils.bottomsheets.EditSimplifiedKycSheet
import com.afrimax.paymaart.ui.utils.bottomsheets.ViewKycPasswordSheet
import com.afrimax.paymaart.ui.utils.bottomsheets.ViewKycPinSheet
import com.afrimax.paymaart.ui.utils.interfaces.ViewSelfKycInterface
import com.afrimax.paymaart.util.Constants
import com.bumptech.glide.Glide
import jp.wasabeef.blurry.Blurry
import java.util.Calendar
import java.util.Date

class ViewKycDetailsActivity : BaseActivity(), ViewSelfKycInterface {
    private lateinit var b: ActivityViewKycDetailsBinding

    private lateinit var kycStatus: String
    private lateinit var kycType: String
    private lateinit var profilePicture: String
    private var publicProfile: Boolean = false
    private var isBlurred = false

    private var idFrontKey = ""
    private var idBackKey = ""
    private var verFrontKey = ""
    private var verBackKey = ""
    private var selfieKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityViewKycDetailsBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(b.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true

        initViews()
        setupLayout()
        setUpListeners()


    }

    private fun initViews() {
        kycStatus = intent.getStringExtra(Constants.KYC_STATUS) ?: getString(R.string.not_started)
        kycType = intent.getStringExtra(Constants.KYC_TYPE) ?: ""
        profilePicture = intent.getStringExtra(Constants.PROFILE_PICTURE) ?: ""
        publicProfile = intent.getBooleanExtra(Constants.PUBLIC_PROFILE, false)
    }

    private fun setupLayout() {
        val accountName = intent.getStringExtra(Constants.KYC_NAME) ?: ""
        val paymaartId = retrievePaymaartId() ?: ""

        //populate views
        val nameList = accountName.uppercase().split(" ")
        val shortName = "${nameList[0][0]}${nameList[1][0]}${nameList[2][0]}"
        if (publicProfile && profilePicture.isNotEmpty()){
            val profilePicUrl = BuildConfig.CDN_BASE_URL + profilePicture
            b.viewSelfKycActivityProfileIV.visibility = View.VISIBLE
            Glide
                .with(this)
                .load(profilePicUrl)
                .into(b.viewSelfKycActivityProfileIV)
            b.viewSelfKycActivityShortNameTV.visibility = View.GONE

        }
        b.viewSelfKycActivityShortNameTV.text = shortName
        b.viewSelfKycActivityNameTV.text = accountName
        b.viewSelfKycActivityPaymaartIdTV.text = paymaartId
        b.viewSelfKycActivityKycTypeTV.text = kycType
        setKycStatus()

        //Setup UI according to kycType
        when (kycType) {
            getString(R.string.malawi_full_kyc_registration), getString(R.string.malawi_simplified_kyc_registration) -> {
                //Only malawi address
                b.viewSelfKycActivityYourAddressHiddenContainer.viewKycYourAddressNationalityContainer.visibility =
                    View.GONE
                b.viewSelfKycActivityYourAddressHiddenContainer.viewKycYourAddressInternationalAddressContainer.visibility =
                    View.GONE
                b.viewSelfKycActivityYourAddressHiddenContainer.viewKycYourAddressMalawiAddressTitleTV.visibility =
                    View.GONE
            }

            getString(R.string.non_malawi_full_kyc_registration) -> {
                //Includes international address
                b.viewSelfKycActivityYourAddressHiddenContainer.viewKycYourAddressNationalityContainer.visibility =
                    View.VISIBLE
                b.viewSelfKycActivityYourAddressHiddenContainer.viewKycYourAddressInternationalAddressContainer.visibility =
                    View.VISIBLE
                b.viewSelfKycActivityYourAddressHiddenContainer.viewKycYourAddressMalawiAddressTitleTV.visibility =
                    View.VISIBLE
            }
        }
    }

    private fun setKycStatus() {
        val rejectionReasons =
            intent.getStringArrayListExtra(Constants.KYC_REJECTION_REASONS) ?: ArrayList<String>()
        b.viewSelfKycActivityKycStatusTV.text = kycStatus

        //Show default UI
        b.viewSelfKycActivityKycDetailsHolder.visibility = View.VISIBLE
        b.viewSelfKycActivityCompleteKycBox.visibility = View.GONE
        //Show default button
        b.viewSelfKycActivityEditButton.visibility = View.VISIBLE
        b.viewSelfKycActivityBlur2IV.visibility = View.VISIBLE
        b.viewSelfKycActivityCompleteKycButton.visibility = View.GONE

        when (kycStatus) {
            getString(R.string.not_started) -> {
                b.viewSelfKycActivityKycStatusTV.setTextColor(
                    ContextCompat.getColor(this, R.color.neutralGreyPrimaryText)
                )
                b.viewSelfKycActivityKycStatusTV.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_home_drawer_kyc_not_started)

                //Show UI accordingly
                b.viewSelfKycActivityCompleteKycBox.visibility = View.VISIBLE
                b.viewSelfKycActivityKycDetailsHolder.visibility = View.GONE
                //Show button accordingly
                b.viewSelfKycActivityCompleteKycButton.visibility = View.VISIBLE
                b.viewSelfKycActivityEditButton.visibility = View.GONE
                b.viewSelfKycActivityBlur2IV.visibility = View.GONE
            }

            getString(R.string.in_progress) -> {
                b.viewSelfKycActivityKycStatusTV.setTextColor(
                    ContextCompat.getColor(this, R.color.accentInformation)
                )
                b.viewSelfKycActivityKycStatusTV.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_home_drawer_kyc_in_progress)

                //Show UI accordingly
                b.viewSelfKycActivityCompleteKycBox.visibility = View.GONE
                b.viewSelfKycActivityKycDetailsHolder.visibility = View.VISIBLE
                //Show button accordingly
                b.viewSelfKycActivityCompleteKycButton.visibility = View.VISIBLE
                b.viewSelfKycActivityEditButton.visibility = View.GONE
            }

            getString(R.string.in_review) -> {
                b.viewSelfKycActivityKycStatusTV.setTextColor(
                    ContextCompat.getColor(this, R.color.accentWarning)
                )
                b.viewSelfKycActivityKycStatusTV.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_home_drawer_kyc_in_review)
            }

            getString(R.string.further_information_required) -> {
                b.viewSelfKycActivityKycStatusTV.setTextColor(
                    ContextCompat.getColor(this, R.color.accentNegative)
                )
                b.viewSelfKycActivityKycStatusTV.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_home_drawer_kyc_rejected)

                //show drop down for rejection reasons
                b.viewSelfKycActivityReasonForRejectionBox.visibility = View.VISIBLE
                for (i in 0..<rejectionReasons.size) addRejectionReason(
                    "${i + 1}.", rejectionReasons[i]
                )

            }

            getString(R.string.completed) -> {
                b.viewSelfKycActivityKycStatusTV.setTextColor(
                    ContextCompat.getColor(this, R.color.accentPositive)
                )
                b.viewSelfKycActivityKycStatusTV.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_home_drawer_kyc_completed)
            }
        }
    }


    private fun setUpListeners() {

        b.viewSelfKycActivityBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.viewSelfKycActivityReasonForRejectionBox.setOnClickListener {
            toggleKycStatusDropList()
        }

        b.viewSelfKycActivityBlur1IV.setOnClickListener {
            val loginMode = retrieveLoginMode() ?: ""
            when (loginMode) {
                Constants.SELECTION_PIN -> {
                    val viewKycPinSheet = ViewKycPinSheet()
                    viewKycPinSheet.isCancelable = false
                    viewKycPinSheet.show(supportFragmentManager, ViewKycPinSheet.TAG)
                }

                Constants.SELECTION_PASSWORD -> {
                    val viewKycPasswordSheet = ViewKycPasswordSheet()
                    viewKycPasswordSheet.isCancelable = false
                    viewKycPasswordSheet.show(supportFragmentManager, ViewKycPasswordSheet.TAG)
                }
            }
        }

        if (kycStatus == getString(R.string.not_started)) {
            b.viewSelfKycActivityCompleteKycButton.setOnClickListener {
                startActivity(Intent(this, KycProgressActivity::class.java))
                finish()
            }
        }

    }

    private fun enableClickListeners() {

        b.viewSelfKycActivityContactDetailsBox.setOnClickListener {
            toggleContactDetailsDropList()
        }

        b.viewSelfKycActivityYourAddressBox.setOnClickListener {
            toggleYourAddressDropList()
        }

        b.viewSelfKycActivityYourIdentityBox.setOnClickListener {
            toggleYourIdentityDropList()
        }

        b.viewSelfKycActivityYourInfoBox.setOnClickListener {
            toggleYourInfoDropList()
        }

        b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycIdDocumentFrontTV.setOnClickListener {
            onClickIdDocFront()
        }

        b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycIdDocumentBackTV.setOnClickListener {
            onClickIdDocBack()
        }

        b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycVerificationDocumentFrontTV.setOnClickListener {
            onClickVerificationDocFront()
        }

        b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycVerificationDocumentBackTV.setOnClickListener {
            onClickVerificationDocBack()
        }

        b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycLiveSelfieTV.setOnClickListener {
            onClickLiveSelfieDoc()
        }

        b.viewSelfKycActivityCompleteKycButton.setOnClickListener {
            startActivity(Intent(this, KycProgressActivity::class.java))
            finish()
        }

        b.viewSelfKycActivityEditButton.setOnClickListener {
            onClickEdit()
        }
    }

    private fun onClickEdit() {
        //Check if kyc is malawi simplified and the status is completed
        if (b.viewSelfKycActivityKycTypeTV.text.toString() == getString(R.string.malawi_simplified_kyc_registration) && b.viewSelfKycActivityKycStatusTV.text.toString() == getString(
                R.string.completed
            )
        ) {
            //show bottom sheet to upgrade to full kyc
            EditSimplifiedKycSheet().show(supportFragmentManager, EditSimplifiedKycSheet.TAG)
        } else {
            //Continue with editing screen
            val i = Intent(this@ViewKycDetailsActivity, KycCustomerPersonalDetailsActivity::class.java)
            i.putExtra(Constants.VIEW_SCOPE, Constants.VIEW_SCOPE_EDIT)
            when (b.viewSelfKycActivityKycTypeTV.text.toString()) {
                getString(R.string.malawi_full_kyc_registration) -> i.putExtra(
                    Constants.KYC_SCOPE, Constants.KYC_MALAWI_FULL
                )

                getString(R.string.malawi_simplified_kyc_registration) -> i.putExtra(
                    Constants.KYC_SCOPE, Constants.KYC_MALAWI_SIMPLIFIED
                )

                getString(R.string.non_malawi_full_kyc_registration) -> i.putExtra(
                    Constants.KYC_SCOPE, Constants.KYC_NON_MALAWI
                )
            }
            startActivity(i)
            finish()
        }
    }

    private fun onClickIdDocFront() {
        val i = Intent(this, KycFullScreenPreviewActivity::class.java)
        i.putExtra(Constants.KYC_MEDIA_IS_UPLOADED, true)
        i.putExtra(
            Constants.KYC_MEDIA_NAME,
            b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycIdDocumentFrontTV.text.toString()
        )
        if (idFrontKey.endsWith(".pdf")) {
            i.putExtra(Constants.KYC_MEDIA_TYPE, Constants.KYC_MEDIA_PDF_TYPE)
            i.putExtra(Constants.KYC_SELECTED_FILE_URI, BuildConfig.CDN_BASE_URL + idFrontKey)
        } else {
            i.putExtra(Constants.KYC_MEDIA_TYPE, Constants.KYC_MEDIA_IMAGE_TYPE)
            i.putExtra(Constants.KYC_CAPTURED_IMAGE_URI, idFrontKey)
        }
        startActivity(i)
    }

    private fun onClickIdDocBack() {
        val i = Intent(this, KycFullScreenPreviewActivity::class.java)
        i.putExtra(Constants.KYC_MEDIA_IS_UPLOADED, true)
        i.putExtra(
            Constants.KYC_MEDIA_NAME,
            b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycIdDocumentBackTV.text.toString()
        )
        if (idBackKey.endsWith(".pdf")) {
            i.putExtra(Constants.KYC_MEDIA_TYPE, Constants.KYC_MEDIA_PDF_TYPE)
            i.putExtra(Constants.KYC_SELECTED_FILE_URI, BuildConfig.CDN_BASE_URL + idBackKey)
        } else {
            i.putExtra(Constants.KYC_MEDIA_TYPE, Constants.KYC_MEDIA_IMAGE_TYPE)
            i.putExtra(Constants.KYC_CAPTURED_IMAGE_URI, idBackKey)
        }
        startActivity(i)
    }

    private fun onClickVerificationDocFront() {
        val i = Intent(this, KycFullScreenPreviewActivity::class.java)
        i.putExtra(Constants.KYC_MEDIA_IS_UPLOADED, true)
        i.putExtra(
            Constants.KYC_MEDIA_NAME,
            b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycVerificationDocumentFrontTV.text.toString()
        )
        if (verFrontKey.endsWith(".pdf")) {
            i.putExtra(Constants.KYC_MEDIA_TYPE, Constants.KYC_MEDIA_PDF_TYPE)
            i.putExtra(Constants.KYC_SELECTED_FILE_URI, BuildConfig.CDN_BASE_URL + verFrontKey)
        } else {
            i.putExtra(Constants.KYC_MEDIA_TYPE, Constants.KYC_MEDIA_IMAGE_TYPE)
            i.putExtra(Constants.KYC_CAPTURED_IMAGE_URI, verFrontKey)
        }
        startActivity(i)
    }

    private fun onClickVerificationDocBack() {
        val i = Intent(this, KycFullScreenPreviewActivity::class.java)
        i.putExtra(Constants.KYC_MEDIA_IS_UPLOADED, true)
        i.putExtra(
            Constants.KYC_MEDIA_NAME,
            b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycVerificationDocumentBackTV.text.toString()
        )
        if (verBackKey.endsWith(".pdf")) {
            i.putExtra(Constants.KYC_MEDIA_TYPE, Constants.KYC_MEDIA_PDF_TYPE)
            i.putExtra(Constants.KYC_SELECTED_FILE_URI, BuildConfig.CDN_BASE_URL + verBackKey)
        } else {
            i.putExtra(Constants.KYC_MEDIA_TYPE, Constants.KYC_MEDIA_IMAGE_TYPE)
            i.putExtra(Constants.KYC_CAPTURED_IMAGE_URI, verBackKey)
        }
        startActivity(i)
    }

    private fun onClickLiveSelfieDoc() {
        val i = Intent(this, KycFullScreenPreviewActivity::class.java)
        i.putExtra(Constants.KYC_MEDIA_IS_UPLOADED, true)
        i.putExtra(
            Constants.KYC_MEDIA_NAME,
            b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycLiveSelfieTV.text.toString()
        )
        if (selfieKey.endsWith(".pdf")) {
            i.putExtra(Constants.KYC_MEDIA_TYPE, Constants.KYC_MEDIA_PDF_TYPE)
            i.putExtra(Constants.KYC_SELECTED_FILE_URI, BuildConfig.CDN_BASE_URL + selfieKey)
        } else {
            i.putExtra(Constants.KYC_MEDIA_TYPE, Constants.KYC_MEDIA_IMAGE_TYPE)
            i.putExtra(Constants.KYC_CAPTURED_IMAGE_URI, selfieKey)
        }
        startActivity(i)
    }

    private fun addRejectionReason(number: String, reason: String) {
        val index = reason.indexOf(":")
        val part1 = reason.substring(0, index + 1).trim()
        val part2 = reason.substring(index + 1).trim()
        val child = ContentViewKycReasonForRejectionBinding.inflate(
            layoutInflater, b.viewSelfKycActivityReasonForRejectionHiddenContainer, false
        )
        child.viewKycRejectionNumberTV.text = number
        child.viewKycRejectionTitleTV.text = part1
        child.viewKycRejectionDescriptionTV.text = part2
        b.viewSelfKycActivityReasonForRejectionHiddenContainer.addView(child.root)

    }

    private fun toggleKycStatusDropList() {
        val transition = AutoTransition()
        transition.duration = 100
        if (b.viewSelfKycActivityReasonForRejectionHiddenContainer.isVisible) {
            TransitionManager.beginDelayedTransition(
                b.viewSelfKycActivityBaseCV, transition
            )
            b.viewSelfKycActivityReasonForRejectionHiddenContainer.visibility = View.GONE
            b.viewSelfKycActivityKycStatusExpandButton.animate().rotation(0f).setDuration(100)
        } else {
            TransitionManager.beginDelayedTransition(
                b.viewSelfKycActivityBaseCV, transition
            )
            b.viewSelfKycActivityReasonForRejectionHiddenContainer.visibility = View.VISIBLE
            b.viewSelfKycActivityKycStatusExpandButton.animate().rotation(180f).setDuration(100)
        }
    }

    private fun toggleContactDetailsDropList() {
        val transition = AutoTransition()
        transition.duration = 100
        if (b.viewSelfKycActivityContactDetailsHiddenContainer.root.isVisible) {
            TransitionManager.beginDelayedTransition(
                b.viewSelfKycActivityBaseCV, transition
            )
            b.viewSelfKycActivityContactDetailsHiddenContainer.root.visibility = View.GONE
            b.viewSelfKycActivityContactDetailsContainer.background =
                ContextCompat.getDrawable(this, R.drawable.bg_kyc_details_card)
            b.viewSelfKycActivityContactDetailsExpandButton.animate().rotation(0f).setDuration(100)
        } else {
            TransitionManager.beginDelayedTransition(
                b.viewSelfKycActivityBaseCV, transition
            )
            b.viewSelfKycActivityContactDetailsHiddenContainer.root.visibility = View.VISIBLE
            b.viewSelfKycActivityContactDetailsContainer.background =
                ContextCompat.getDrawable(this, R.drawable.bg_view_kyc_card)
            b.viewSelfKycActivityContactDetailsExpandButton.animate().rotation(180f)
                .setDuration(100)
        }
    }

    private fun toggleYourAddressDropList() {
        val transition = AutoTransition()
        transition.duration = 100

        if (b.viewSelfKycActivityYourAddressHiddenContainer.root.isVisible) {
            TransitionManager.beginDelayedTransition(
                b.viewSelfKycActivityBaseCV, transition
            )
            b.viewSelfKycActivityYourAddressHiddenContainer.root.visibility = View.GONE
            b.viewSelfKycActivityYourAddressContainer.background =
                ContextCompat.getDrawable(this, R.drawable.bg_kyc_details_card)
            b.viewSelfKycActivityYourAddressExpandButton.animate().rotation(0f).setDuration(100)
        } else {
            TransitionManager.beginDelayedTransition(
                b.viewSelfKycActivityBaseCV, transition
            )
            b.viewSelfKycActivityYourAddressHiddenContainer.root.visibility = View.VISIBLE
            b.viewSelfKycActivityYourAddressContainer.background =
                ContextCompat.getDrawable(this, R.drawable.bg_view_kyc_card)
            b.viewSelfKycActivityYourAddressExpandButton.animate().rotation(180f).setDuration(100)
        }
    }

    private fun toggleYourIdentityDropList() {
        val transition = AutoTransition()
        transition.duration = 100

        if (b.viewSelfKycActivityYourIdentityHiddenContainer.root.isVisible) {
            TransitionManager.beginDelayedTransition(
                b.viewSelfKycActivityBaseCV, transition
            )
            b.viewSelfKycActivityYourIdentityHiddenContainer.root.visibility = View.GONE
            b.viewSelfKycActivityYourIdentityContainer.background =
                ContextCompat.getDrawable(this, R.drawable.bg_kyc_details_card)
            b.viewSelfKycActivityYourIdentityExpandButton.animate().rotation(0f).setDuration(100)
        } else {
            TransitionManager.beginDelayedTransition(
                b.viewSelfKycActivityBaseCV, transition
            )
            b.viewSelfKycActivityYourIdentityHiddenContainer.root.visibility = View.VISIBLE
            b.viewSelfKycActivityYourIdentityContainer.background =
                ContextCompat.getDrawable(this, R.drawable.bg_view_kyc_card)
            b.viewSelfKycActivityYourIdentityExpandButton.animate().rotation(180f).setDuration(100)
        }
    }

    private fun toggleYourInfoDropList() {
        val transition = AutoTransition()
        transition.duration = 100

        if (b.viewSelfKycActivityYourInfoHiddenContainer.root.isVisible) {
            TransitionManager.beginDelayedTransition(
                b.viewSelfKycActivityBaseCV, transition
            )
            b.viewSelfKycActivityYourInfoHiddenContainer.root.visibility = View.GONE
            b.viewSelfKycActivityYourInfoContainer.background =
                ContextCompat.getDrawable(this, R.drawable.bg_kyc_details_card)
            b.viewSelfKycActivityYourInfoExpandButton.animate().rotation(0f).setDuration(100)
        } else {
            TransitionManager.beginDelayedTransition(
                b.viewSelfKycActivityBaseCV, transition
            )
            b.viewSelfKycActivityYourInfoHiddenContainer.root.visibility = View.VISIBLE
            b.viewSelfKycActivityYourInfoContainer.background =
                ContextCompat.getDrawable(this, R.drawable.bg_view_kyc_card)
            b.viewSelfKycActivityYourInfoExpandButton.animate().rotation(180f).setDuration(100)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && kycStatus != getString(R.string.not_started) && !isBlurred) {
            Blurry.with(this).radius(12).sampling(3)
                .capture(b.viewSelfKycActivityKycDetailsContainer)
                .into(b.viewSelfKycActivityBlur1IV)
            Blurry.with(this).radius(12).sampling(3).capture(b.viewSelfKycActivityBottomBar)
                .into(b.viewSelfKycActivityBlur2IV)
            isBlurred = true
        }
    }

    private fun populateMalawiFullKycDetails(data: ViewUserData) {
        populateContactDetails(data)
        populateAddressDetails(data)
        populateIdentityDetails(data)
        populateInfoDetails(data)

    }

    private fun populateInfoDetails(data: ViewUserData) {
        //Populate info details
        b.viewSelfKycActivityYourInfoHiddenContainer.viewKycInfoDetailsGenderTV.text =
            data.gender ?: "-"

        val unixTime = data.dob
        if (unixTime != null) {
            val c = Calendar.getInstance().apply { time = Date(data.dob * 1000) }

            val year = c[Calendar.YEAR]
            val monthOfYear = c[Calendar.MONTH]
            val dayOfMonth = c[Calendar.DAY_OF_MONTH]

            val monthsList = arrayOf(
                "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
            )

            val formattedDate = "$dayOfMonth ${monthsList[monthOfYear]} $year"
            b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoDobTV.text = formattedDate
        }
        b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoOccupationTV.text =
            data.occupation ?: "-"

        when (data.occupation) {
            getString(R.string.employed) -> {
                b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoOccupationDetailsContainer.visibility =
                    View.VISIBLE
                b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoEmployedDetailsContainer.visibility =
                    View.VISIBLE

                b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoOccupationDetailsTV.text =
                    data.employed_role ?: "-"
                b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoEmployerNameTV.text =
                    data.employer_name ?: "-"
                b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoIndustrySectorTV.text =
                    data.industry ?: "-"
                b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoTownDistrictTV.text =
                    data.occupation_town ?: "-"
            }

            getString(R.string.self_employed) -> {
                b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoOccupationDetailsContainer.visibility =
                    View.VISIBLE

                b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoOccupationDetailsTV.text =
                    data.self_employed_specify ?: "-"
            }

            getString(R.string.in_full_time_education) -> {
                b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoOccupationDetailsContainer.visibility =
                    View.VISIBLE

                val instituteDetails: String =
                    if (data.institute == getString(R.string.others)) "${data.institute} - ${data.institute_specify}"
                    else data.institute ?: "-"

                b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoOccupationDetailsTV.text =
                    instituteDetails

            }

            getString(R.string.others) -> {
                b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoOccupationDetailsContainer.visibility =
                    View.VISIBLE

                b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoOccupationDetailsTV.text =
                    data.occupation_specify ?: "-"
            }

            else -> {
                b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoOccupationDetailsContainer.visibility =
                    View.GONE
                b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoEmployedDetailsContainer.visibility =
                    View.GONE
            }
        }
        val purposeOfRelations = data.purpose_of_relation
        if (!purposeOfRelations.isNullOrEmpty()) {
            b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoRelationshipDashTV.visibility =
                View.GONE
            for (item in purposeOfRelations) {
                when (item) {
                    getString(R.string.business_relationship1) -> b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoRelationship1CB.visibility =
                        View.VISIBLE

                    getString(R.string.business_relationship2) -> b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoRelationship2CB.visibility =
                        View.VISIBLE

                    getString(R.string.business_relationship3) -> b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoRelationship3CB.visibility =
                        View.VISIBLE

                    getString(R.string.business_relationship4) -> b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoRelationship4CB.visibility =
                        View.VISIBLE
                }
            }
        } else {
            b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoRelationshipDashTV.visibility =
                View.VISIBLE
            b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoRelationship1CB.visibility =
                View.GONE
            b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoRelationship2CB.visibility =
                View.GONE
            b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoRelationship3CB.visibility =
                View.GONE
            b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoRelationship4CB.visibility =
                View.GONE
        }

        b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoMonthlyWithdrawalTV.text =
            data.monthly_withdrawal ?: "-"
        b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoMonthlyIncomeTV.text =
            data.monthly_income ?: "-"

//        val bankList = data.bank_details
//        if (!bankList.isNullOrEmpty()) {
//            val bankData = bankList[0]
//            b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoBankNameTV.text =
//                bankData.bank_name ?: "-"
//            b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoAccountNumberTV.text =
//                bankData.account_number ?: "-"
//            b.viewSelfKycActivityYourInfoHiddenContainer.viewKycYourInfoAccountNameTV.text =
//                bankData.account_name ?: "-"
//        }
    }

    private fun populateIdentityDetails(data: ViewUserData) {
        //Populate identity fields
        idFrontKey = data.id_document_front ?: ""
        idBackKey = data.id_document_back ?: ""
        verFrontKey = data.verification_document_front ?: ""
        verBackKey = data.verification_document_back ?: ""
        selfieKey = data.selfie ?: ""

        if (data.id_document == null || data.verification_document == null || data.selfie == null) {
            //Show dash
            b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycIdDocumentDashTV.visibility =
                View.VISIBLE
            b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycVerificationDocumentDashTV.visibility =
                View.VISIBLE
            b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycLiveSelfieDashTV.visibility =
                View.VISIBLE

            b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycIdDocumentContainer.visibility =
                View.GONE
            b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycVerificationDocumentContainer.visibility =
                View.GONE
            b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycLiveSelfieTV.visibility =
                View.GONE
        } else {
            b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycIdDocumentDashTV.visibility =
                View.GONE
            b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycVerificationDocumentDashTV.visibility =
                View.GONE
            b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycLiveSelfieDashTV.visibility =
                View.GONE

            b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycIdDocumentContainer.visibility =
                View.VISIBLE
            b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycVerificationDocumentContainer.visibility =
                View.VISIBLE
            b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycLiveSelfieTV.visibility =
                View.VISIBLE


            //We assume identity data are filled by user
            val idFront =
                if (idFrontKey.endsWith(".pdf")) "${data.id_document} Front.pdf" else "${data.id_document} Front.jpg"
            b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycIdDocumentFrontTV.text = idFront

            if (idBackKey.isNotEmpty()) {
                b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycIdDocumentBackTV.visibility =
                    View.VISIBLE
                val idBack =
                    if (idBackKey.endsWith(".pdf")) "${data.id_document} Back.pdf" else "${data.id_document} Back.jpg"
                b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycIdDocumentBackTV.text =
                    idBack
            } else {
                b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycIdDocumentBackTV.visibility =
                    View.GONE
            }

            val verFront =
                if (verFrontKey.endsWith(".pdf")) "${data.verification_document} Front.pdf" else "${data.verification_document} Front.jpg"
            b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycVerificationDocumentFrontTV.text =
                verFront

            if (verBackKey.isNotEmpty()) {
                b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycVerificationDocumentBackTV.visibility =
                    View.VISIBLE
                val verBack =
                    if (verBackKey.endsWith(".pdf")) "${data.verification_document} Back.pdf" else "${data.verification_document} Back.jpg"
                b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycVerificationDocumentBackTV.text =
                    verBack
            } else {
                b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycVerificationDocumentBackTV.visibility =
                    View.GONE
            }

            //Show visa/permit and ref_no if kyc type is non malawi and id doc is passport
            if (kycType == getString(R.string.non_malawi_full_kyc_registration) && data.id_document == getString(
                    R.string.passport
                )
            ) {
                b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycIdDocumentVisaTV.visibility =
                    View.VISIBLE
                b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycIdDocumentRefNoTV.visibility =
                    View.VISIBLE

                val visaPermit = "Type of Visa/Permit: ${data.nature_of_permit}"
                val refNo = "Visa/Permit reference number: ${data.ref_no}"

                b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycIdDocumentVisaTV.text =
                    visaPermit
                b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycIdDocumentRefNoTV.text =
                    refNo
            } else {
                b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycIdDocumentVisaTV.visibility =
                    View.GONE
                b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycIdDocumentRefNoTV.visibility =
                    View.GONE
            }

            val selfie = "Biometrics.jpg"
            b.viewSelfKycActivityYourIdentityHiddenContainer.viewKycLiveSelfieTV.text = selfie
        }
    }

    private fun populateAddressDetails(data: ViewUserData) {
        //Populate malawi address details
        val addressComponents = ArrayList<String>()
        if (!data.po_box_no.isNullOrEmpty()) addressComponents.add(data.po_box_no)
        if (!data.house_number.isNullOrEmpty()) addressComponents.add(data.house_number)
        if (!data.street_name.isNullOrEmpty()) addressComponents.add(data.street_name)
        if (!data.town_village_ta.isNullOrEmpty()) addressComponents.add(data.town_village_ta)
        if (!data.district.isNullOrEmpty()) addressComponents.add(data.district)

        val address = addressComponents.joinToString(separator = ", ").ifEmpty { "-" }
        b.viewSelfKycActivityYourAddressHiddenContainer.viewKycYourAddressMalawiAddressTV.text =
            address

        //Show additional fields in case of non malawi
        if (kycType == getString(R.string.non_malawi_full_kyc_registration)) {
            b.viewSelfKycActivityYourAddressHiddenContainer.viewKycYourAddressNationalityTV.text =
                data.citizen ?: "-"
            //All these implementation have been changed.
            //`intl_address` will contain the international address of the non malawian customer.
            /**
                val intlAddressComponents = ArrayList<String>()
                if (!data.intl_po_box_no.isNullOrEmpty()) intlAddressComponents.add(data.intl_po_box_no)
                if (!data.intl_house_number.isNullOrEmpty()) intlAddressComponents.add(data.intl_house_number)
                if (!data.intl_street_name.isNullOrEmpty()) intlAddressComponents.add(data.intl_street_name)
                if (!data.intl_town_village_ta.isNullOrEmpty()) intlAddressComponents.add(data.intl_town_village_ta)
                if (!data.intl_district.isNullOrEmpty()) intlAddressComponents.add(data.intl_district)
                val intlAddress = intlAddressComponents.joinToString(separator = ", ").ifEmpty { "-" }
             */
            val intlAddress = data.intl_address ?: ""
            b.viewSelfKycActivityYourAddressHiddenContainer.viewKycYourAddressInternationalAddressTV.text =
                intlAddress
        }
    }

    private fun populateContactDetails(data: ViewUserData) {
        val formattedNumber = StringBuilder(data.phone_number!!)
        formattedNumber.insert(2, ' ')
        formattedNumber.insert(6, ' ')
        val phone = "${data.country_code} $formattedNumber"
        b.viewSelfKycActivityContactDetailsHiddenContainer.viewKycContactDetailsEmailTV.text =
            data.email
        b.viewSelfKycActivityContactDetailsHiddenContainer.viewKycContactDetailsPhoneNumberTV.text =
            phone
    }

    override fun onClickViewButton(viewUserData: ViewUserData) {
        b.viewSelfKycActivityLockedContainer.visibility = View.GONE
        b.viewSelfKycActivityBlur1IV.visibility = View.GONE
        b.viewSelfKycActivityBlur2IV.visibility = View.GONE
        populateMalawiFullKycDetails(viewUserData)
        enableClickListeners()
    }
}