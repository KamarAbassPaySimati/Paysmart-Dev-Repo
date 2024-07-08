package com.afrimax.paymaart.ui.kyc

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.RadioButton
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
import androidx.lifecycle.lifecycleScope
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.DefaultResponse
import com.afrimax.paymaart.data.model.GetUserKycDataResponse
import com.afrimax.paymaart.data.model.KycSaveIdentityDetailRequest
import com.afrimax.paymaart.data.model.KycUserData
import com.afrimax.paymaart.data.model.SaveIdentitySimplifiedToFullRequest
import com.afrimax.paymaart.data.model.SaveNewIdentityDetailsSelfKycRequest
import com.afrimax.paymaart.data.model.SelfKycDetailsResponse
import com.afrimax.paymaart.data.model.ViewUserData
import com.afrimax.paymaart.databinding.ActivityOnboardKycIdentityBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.utils.bottomsheets.KycNatureOfPermitSheet
import com.afrimax.paymaart.ui.utils.bottomsheets.KycNumberOfReferenceSheet
import com.afrimax.paymaart.ui.utils.interfaces.KycYourIdentityInterface
import com.afrimax.paymaart.util.Constants
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KycIdentityActivity : BaseActivity(), KycYourIdentityInterface {
    private lateinit var b: ActivityOnboardKycIdentityBinding
    private lateinit var kycScope: String
    private lateinit var viewScope: String
    private lateinit var identityDocsResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var infoResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraPermissionCheckLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var nextScreenResultLauncher: ActivityResultLauncher<Intent>
    private var shouldReloadDocs = true
    private var natureOfPermit = ""
    private var numberOfReference = ""
    private var sendEmail = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityOnboardKycIdentityBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.onboardKycIdentityActivity)) { v, insets ->
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

        identityDocsResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    populateIdentityDetails(data)

                    //Don't reload data from getUserKycDataApi
                    shouldReloadDocs = false
                } else if (result.resultCode == RESULT_CANCELED) {
                    //By default RESULT_CANCELED is returned if the user press back button
                    //Don't reload data from getUserKycDataApi
                    shouldReloadDocs = false
                }
            }

        //Declare Activity Result Contract for permissions
        cameraPermissionCheckLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { multiplePermissionsMap ->
                if (!multiplePermissionsMap.containsValue(false)) {
                    //If all permissions are granted then open Live Selfie Activity to shoot photo
                    onClickSelfieButton()
                } else {
                    showToast("Please allow camera permissions to continue")
                }
            }

        infoResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                //Don't reload data from getUserKycDataApi
                if (result.resultCode == RESULT_OK || result.resultCode == RESULT_CANCELED) shouldReloadDocs = false
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
    }

    private fun setUpLayout() {
        when (kycScope) {
            Constants.KYC_MALAWI_FULL -> {
                b.onboardKycIdentityActivityBackButtonTV.text = getString(R.string.full_kyc)

                //Show relevant Id doc fields
                b.onboardKycIdentityActivityIdDocNationalIdRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityIdDocPassportRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityIdDocDriverLicenseRBContainer.visibility = View.GONE
                b.onboardKycIdentityActivityIdDocTrafficRegisterCardRBContainer.visibility = View.GONE
                b.onboardKycIdentityActivityIdDocBirthCertificateRBContainer.visibility = View.GONE
                b.onboardKycIdentityActivityIdDocStudentIdRBContainer.visibility = View.GONE
                b.onboardKycIdentityActivityIdDocEmployeeIdRBContainer.visibility = View.GONE
                b.onboardKycIdentityActivityIdDocRefugeeIdRBContainer.visibility = View.GONE
                b.onboardKycIdentityActivityIdDocAsylumIdRBContainer.visibility = View.GONE

                //Show relevant Verification doc
                b.onboardKycIdentityActivityVerDocBirthCertificateRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityVerDocDriverLicenseRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityVerDocEmployerLetterRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityVerDocInstitutionLetterRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityVerDocTrafficRegisterCardRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityVerDocReligiousInstitutionLetterRBContainer.visibility = View.GONE
                b.onboardKycIdentityActivityVerDocNationalIdRBContainer.visibility = View.GONE
            }

            Constants.KYC_MALAWI_SIMPLIFIED -> {
                b.onboardKycIdentityActivityBackButtonTV.text = getString(R.string.simplified_kyc)

                //Show relevant Id doc fields
                b.onboardKycIdentityActivityIdDocDriverLicenseRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityIdDocTrafficRegisterCardRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityIdDocBirthCertificateRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityIdDocStudentIdRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityIdDocEmployeeIdRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityIdDocNationalIdRBContainer.visibility = View.GONE
                b.onboardKycIdentityActivityIdDocPassportRBContainer.visibility = View.GONE
                b.onboardKycIdentityActivityIdDocRefugeeIdRBContainer.visibility = View.GONE
                b.onboardKycIdentityActivityIdDocAsylumIdRBContainer.visibility = View.GONE

                //Show relevant Verification doc
                b.onboardKycIdentityActivityVerDocEmployerLetterRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityVerDocInstitutionLetterRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityVerDocReligiousInstitutionLetterRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityVerDocBirthCertificateRBContainer.visibility = View.GONE
                b.onboardKycIdentityActivityVerDocDriverLicenseRBContainer.visibility = View.GONE
                b.onboardKycIdentityActivityVerDocTrafficRegisterCardRBContainer.visibility = View.GONE
                b.onboardKycIdentityActivityVerDocNationalIdRBContainer.visibility = View.GONE
            }

            Constants.KYC_NON_MALAWI -> {
                b.onboardKycIdentityActivityBackButtonTV.text = getString(R.string.non_malawi_full_kyc)
                //Show relevant Id doc fields
                b.onboardKycIdentityActivityIdDocPassportRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityIdDocRefugeeIdRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityIdDocAsylumIdRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityIdDocDriverLicenseRBContainer.visibility = View.GONE
                b.onboardKycIdentityActivityIdDocTrafficRegisterCardRBContainer.visibility = View.GONE
                b.onboardKycIdentityActivityIdDocBirthCertificateRBContainer.visibility = View.GONE
                b.onboardKycIdentityActivityIdDocStudentIdRBContainer.visibility = View.GONE
                b.onboardKycIdentityActivityIdDocEmployeeIdRBContainer.visibility = View.GONE
                b.onboardKycIdentityActivityIdDocNationalIdRBContainer.visibility = View.GONE

                //Show relevant Verification doc
                b.onboardKycIdentityActivityVerDocDriverLicenseRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityVerDocNationalIdRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityVerDocEmployerLetterRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityVerDocInstitutionLetterRBContainer.visibility = View.VISIBLE
                b.onboardKycIdentityActivityVerDocReligiousInstitutionLetterRBContainer.visibility = View.GONE
                b.onboardKycIdentityActivityVerDocBirthCertificateRBContainer.visibility = View.GONE
                b.onboardKycIdentityActivityVerDocTrafficRegisterCardRBContainer.visibility = View.GONE
            }
        }
        when (viewScope) {
            Constants.VIEW_SCOPE_FILL -> {
                b.onboardKycIdentityActivityPB.max = 99
                b.onboardKycIdentityActivityPB.progress = 66
                b.onboardKycIdentityActivityProgressCountTV.text = getString(R.string.step2of3)
            }

            Constants.VIEW_SCOPE_EDIT -> {
                b.onboardKycIdentityActivityPB.max = 100
                b.onboardKycIdentityActivityPB.progress = 75
                b.onboardKycIdentityActivityProgressCountTV.text = getString(R.string.step3of4)
            }

            Constants.VIEW_SCOPE_UPDATE -> {
                b.onboardKycIdentityActivityPB.max = 100
                b.onboardKycIdentityActivityPB.progress = 75
                b.onboardKycIdentityActivityProgressCountTV.text = getString(R.string.step3of4)

                //Hide skip button
                b.onboardKycIdentityActivitySkipButton.visibility = View.GONE
            }
        }
    }

    private fun populateIdentityDetails(data: Intent?) {
        if (data != null) {
            val identityType = data.getStringExtra(Constants.KYC_IDENTITY_TYPE) ?: ""
            val kycDocumentFrontUrl = data.getStringExtra(Constants.KYC_DOCUMENT_FRONT_URL) ?: ""
            val kycDocumentBackUrl = data.getStringExtra(Constants.KYC_DOCUMENT_BACK_URL) ?: ""
            val kycSelfieUrl = data.getStringExtra(Constants.KYC_SELFIE_URL) ?: ""

            when (identityType) {
                Constants.KYC_IDENTITY_ID_NATIONAL_ID -> {
                    clearIdDocumentCheck()
                    populateSingleDocument(
                        b.onboardKycIdentityActivityIdDocumentStatusTV,
                        b.onboardKycIdentityActivityIdDocNationalIdRB,
                        kycDocumentFrontUrl,
                        kycDocumentBackUrl
                    )
                }

                Constants.KYC_IDENTITY_ID_PASSPORT -> {
                    clearIdDocumentCheck()
                    populateSingleDocument(
                        b.onboardKycIdentityActivityIdDocumentStatusTV,
                        b.onboardKycIdentityActivityIdDocPassportRB,
                        kycDocumentFrontUrl,
                        kycDocumentBackUrl
                    )
                    //show additional fields
                    if (kycScope == Constants.KYC_NON_MALAWI) {
                        b.onboardKycIdentityActivityIdDocPassportStatusContainer.visibility =
                            View.VISIBLE
                        b.onboardKycIdentityActivityIdDocPassportNatureOfPermitTV.visibility =
                            View.VISIBLE
                        b.onboardKycIdentityActivityIdDocPassportNumberOfReferencesTV.visibility =
                            View.VISIBLE
                        checkAndMarkSubmittedForPassport()
                    }
                }

                Constants.KYC_IDENTITY_ID_REFUGEE_ID -> {
                    clearIdDocumentCheck()
                    populateSingleDocument(
                        b.onboardKycIdentityActivityIdDocumentStatusTV,
                        b.onboardKycIdentityActivityIdDocRefugeeIdRB,
                        kycDocumentFrontUrl,
                        kycDocumentBackUrl
                    )
                }

                Constants.KYC_IDENTITY_ID_ASYLUM_ID -> {
                    clearIdDocumentCheck()
                    populateSingleDocument(
                        b.onboardKycIdentityActivityIdDocumentStatusTV,
                        b.onboardKycIdentityActivityIdDocAsylumIdRB,
                        kycDocumentFrontUrl,
                        kycDocumentBackUrl
                    )
                }

                Constants.KYC_IDENTITY_ID_DRIVER_LICENSE -> {
                    clearIdDocumentCheck()
                    populateSingleDocument(
                        b.onboardKycIdentityActivityIdDocumentStatusTV,
                        b.onboardKycIdentityActivityIdDocDriverLicenseRB,
                        kycDocumentFrontUrl,
                        kycDocumentBackUrl
                    )
                }

                Constants.KYC_IDENTITY_ID_TRAFFIC_CARD -> {
                    clearIdDocumentCheck()
                    populateSingleDocument(
                        b.onboardKycIdentityActivityIdDocumentStatusTV,
                        b.onboardKycIdentityActivityIdDocTrafficRegisterCardRB,
                        kycDocumentFrontUrl,
                        kycDocumentBackUrl
                    )
                }

                Constants.KYC_IDENTITY_ID_BIRTH_CERTIFICATE -> {
                    clearIdDocumentCheck()
                    populateSingleDocument(
                        b.onboardKycIdentityActivityIdDocumentStatusTV,
                        b.onboardKycIdentityActivityIdDocBirthCertificateRB,
                        kycDocumentFrontUrl,
                        kycDocumentBackUrl
                    )
                }

                Constants.KYC_IDENTITY_ID_STUDENT_ID -> {
                    clearIdDocumentCheck()
                    populateSingleDocument(
                        b.onboardKycIdentityActivityIdDocumentStatusTV,
                        b.onboardKycIdentityActivityIdDocStudentIdRB,
                        kycDocumentFrontUrl,
                        kycDocumentBackUrl
                    )
                }

                Constants.KYC_IDENTITY_ID_EMPLOYEE_ID -> {
                    clearIdDocumentCheck()
                    populateSingleDocument(
                        b.onboardKycIdentityActivityIdDocumentStatusTV,
                        b.onboardKycIdentityActivityIdDocEmployeeIdRB,
                        kycDocumentFrontUrl,
                        kycDocumentBackUrl
                    )
                }

                Constants.KYC_IDENTITY_VER_DRIVER_LICENSE -> {
                    clearVerificationDocumentCheck()
                    populateSingleDocument(
                        b.onboardKycIdentityActivityVerificationDocumentStatusTV,
                        b.onboardKycIdentityActivityVerDocDriverLicenseRB,
                        kycDocumentFrontUrl,
                        kycDocumentBackUrl
                    )
                }

                Constants.KYC_IDENTITY_VER_NATIONAL_ID -> {
                    clearVerificationDocumentCheck()
                    populateSingleDocument(
                        b.onboardKycIdentityActivityVerificationDocumentStatusTV,
                        b.onboardKycIdentityActivityVerDocNationalIdRB,
                        kycDocumentFrontUrl,
                        kycDocumentBackUrl
                    )
                }

                Constants.KYC_IDENTITY_VER_TRAFFIC_CARD -> {
                    clearVerificationDocumentCheck()
                    populateSingleDocument(
                        b.onboardKycIdentityActivityVerificationDocumentStatusTV,
                        b.onboardKycIdentityActivityVerDocTrafficRegisterCardRB,
                        kycDocumentFrontUrl,
                        kycDocumentBackUrl
                    )
                }

                Constants.KYC_IDENTITY_VER_BIRTH_CERTIFICATE -> {
                    clearVerificationDocumentCheck()
                    populateSingleDocument(
                        b.onboardKycIdentityActivityVerificationDocumentStatusTV,
                        b.onboardKycIdentityActivityVerDocBirthCertificateRB,
                        kycDocumentFrontUrl,
                        kycDocumentBackUrl
                    )
                }

                Constants.KYC_IDENTITY_VER_EMPLOYER_LETTER -> {
                    clearVerificationDocumentCheck()
                    populateSingleDocument(
                        b.onboardKycIdentityActivityVerificationDocumentStatusTV,
                        b.onboardKycIdentityActivityVerDocEmployerLetterRB,
                        kycDocumentFrontUrl,
                        kycDocumentBackUrl
                    )
                }

                Constants.KYC_IDENTITY_VER_INSTITUTION_LETTER -> {
                    clearVerificationDocumentCheck()
                    populateSingleDocument(
                        b.onboardKycIdentityActivityVerificationDocumentStatusTV,
                        b.onboardKycIdentityActivityVerDocInstitutionLetterRB,
                        kycDocumentFrontUrl,
                        kycDocumentBackUrl
                    )
                }

                Constants.KYC_IDENTITY_VER_RELIGIOUS_INSTITUTION_LETTER -> {
                    clearVerificationDocumentCheck()
                    populateSingleDocument(
                        b.onboardKycIdentityActivityVerificationDocumentStatusTV,
                        b.onboardKycIdentityActivityVerDocReligiousInstitutionLetterRB,
                        kycDocumentFrontUrl,
                        kycDocumentBackUrl
                    )
                }

                Constants.KYC_IDENTITY_LIVE_SELFIE -> {
                    b.onboardKycIdentityActivitySelfieStatusTV.text = getString(R.string.submitted)
                    b.onboardKycIdentityActivitySelfieStatusTV.setTextColor(ContextCompat.getColor(this, R.color.accentPositive))

                    //Store the url
                    if (kycSelfieUrl.isNotEmpty())
                        b.onboardKycIdentityActivitySelfieTV.setTag(R.string.key_selfie_url, kycSelfieUrl)
                }

            }
        }
    }

    private fun populateSingleDocument(
        statusTV: TextView,
        radioButton: RadioButton,
        kycDocumentFrontUrl: String,
        kycDocumentBackUrl: String
    ) {
        //Change the status to submitted
        statusTV.text = getString(R.string.submitted)
        statusTV.setTextColor(
            ContextCompat.getColor(
                this, R.color.accentPositive
            )
        )

        //Store obtained urls as tag
        radioButton.isChecked = true
        if (kycDocumentFrontUrl.isNotEmpty()) radioButton.setTag(
            R.string.key_document_front_url, kycDocumentFrontUrl
        )
        if (kycDocumentBackUrl.isNotEmpty()) radioButton.setTag(
            R.string.key_document_back_url, kycDocumentBackUrl
        )
    }


    private fun clearIdDocumentCheck() {
        b.onboardKycIdentityActivityIdDocNationalIdRB.isChecked = false
        b.onboardKycIdentityActivityIdDocNationalIdRB.setTag(R.string.key_document_front_url, null)
        b.onboardKycIdentityActivityIdDocNationalIdRB.setTag(R.string.key_document_back_url, null)

        b.onboardKycIdentityActivityIdDocPassportRB.isChecked = false
        b.onboardKycIdentityActivityIdDocPassportRB.setTag(R.string.key_document_front_url, null)
        b.onboardKycIdentityActivityIdDocPassportRB.setTag(R.string.key_document_back_url, null)

        b.onboardKycIdentityActivityIdDocRefugeeIdRB.isChecked = false
        b.onboardKycIdentityActivityIdDocRefugeeIdRB.setTag(R.string.key_document_front_url, null)
        b.onboardKycIdentityActivityIdDocRefugeeIdRB.setTag(R.string.key_document_back_url, null)

        b.onboardKycIdentityActivityIdDocAsylumIdRB.isChecked = false
        b.onboardKycIdentityActivityIdDocAsylumIdRB.setTag(R.string.key_document_front_url, null)
        b.onboardKycIdentityActivityIdDocAsylumIdRB.setTag(R.string.key_document_back_url, null)

        b.onboardKycIdentityActivityIdDocDriverLicenseRB.isChecked = false
        b.onboardKycIdentityActivityIdDocDriverLicenseRB.setTag(
            R.string.key_document_front_url, null
        )
        b.onboardKycIdentityActivityIdDocDriverLicenseRB.setTag(
            R.string.key_document_back_url, null
        )

        b.onboardKycIdentityActivityIdDocTrafficRegisterCardRB.isChecked = false
        b.onboardKycIdentityActivityIdDocTrafficRegisterCardRB.setTag(
            R.string.key_document_front_url, null
        )
        b.onboardKycIdentityActivityIdDocTrafficRegisterCardRB.setTag(
            R.string.key_document_back_url, null
        )

        b.onboardKycIdentityActivityIdDocBirthCertificateRB.isChecked = false
        b.onboardKycIdentityActivityIdDocBirthCertificateRB.setTag(
            R.string.key_document_front_url, null
        )
        b.onboardKycIdentityActivityIdDocBirthCertificateRB.setTag(
            R.string.key_document_back_url, null
        )

        b.onboardKycIdentityActivityIdDocStudentIdRB.isChecked = false
        b.onboardKycIdentityActivityIdDocStudentIdRB.setTag(R.string.key_document_front_url, null)
        b.onboardKycIdentityActivityIdDocStudentIdRB.setTag(R.string.key_document_back_url, null)

        b.onboardKycIdentityActivityIdDocEmployeeIdRB.isChecked = false
        b.onboardKycIdentityActivityIdDocEmployeeIdRB.setTag(R.string.key_document_front_url, null)
        b.onboardKycIdentityActivityIdDocEmployeeIdRB.setTag(R.string.key_document_back_url, null)

        //Hide additional fields for non malawi
        b.onboardKycIdentityActivityIdDocPassportStatusContainer.visibility = View.GONE
        b.onboardKycIdentityActivityIdDocPassportNatureOfPermitTV.visibility = View.GONE
        b.onboardKycIdentityActivityIdDocPassportNumberOfReferencesTV.visibility = View.GONE

        //Reset fields related to passport
        natureOfPermit = ""
        numberOfReference = ""
    }

    private fun clearVerificationDocumentCheck() {
        b.onboardKycIdentityActivityVerDocDriverLicenseRB.isChecked = false
        b.onboardKycIdentityActivityVerDocDriverLicenseRB.setTag(
            R.string.key_document_front_url, null
        )
        b.onboardKycIdentityActivityVerDocDriverLicenseRB.setTag(
            R.string.key_document_back_url, null
        )

        b.onboardKycIdentityActivityVerDocNationalIdRB.isChecked = false
        b.onboardKycIdentityActivityVerDocNationalIdRB.setTag(R.string.key_document_front_url, null)
        b.onboardKycIdentityActivityVerDocNationalIdRB.setTag(R.string.key_document_back_url, null)

        b.onboardKycIdentityActivityVerDocTrafficRegisterCardRB.isChecked = false
        b.onboardKycIdentityActivityVerDocTrafficRegisterCardRB.setTag(
            R.string.key_document_front_url, null
        )
        b.onboardKycIdentityActivityVerDocTrafficRegisterCardRB.setTag(
            R.string.key_document_back_url, null
        )

        b.onboardKycIdentityActivityVerDocBirthCertificateRB.isChecked = false
        b.onboardKycIdentityActivityVerDocBirthCertificateRB.setTag(
            R.string.key_document_front_url, null
        )
        b.onboardKycIdentityActivityVerDocBirthCertificateRB.setTag(
            R.string.key_document_back_url, null
        )

        b.onboardKycIdentityActivityVerDocEmployerLetterRB.isChecked = false
        b.onboardKycIdentityActivityVerDocEmployerLetterRB.setTag(
            R.string.key_document_front_url, null
        )
        b.onboardKycIdentityActivityVerDocEmployerLetterRB.setTag(
            R.string.key_document_back_url, null
        )

        b.onboardKycIdentityActivityVerDocInstitutionLetterRB.isChecked = false
        b.onboardKycIdentityActivityVerDocInstitutionLetterRB.setTag(
            R.string.key_document_front_url, null
        )
        b.onboardKycIdentityActivityVerDocInstitutionLetterRB.setTag(
            R.string.key_document_back_url, null
        )

        b.onboardKycIdentityActivityVerDocReligiousInstitutionLetterRB.isChecked = false
        b.onboardKycIdentityActivityVerDocReligiousInstitutionLetterRB.setTag(
            R.string.key_document_front_url, null
        )
        b.onboardKycIdentityActivityVerDocReligiousInstitutionLetterRB.setTag(
            R.string.key_document_back_url, null
        )
    }

    private fun setUpListeners() {

        b.onboardKycIdentityActivityBackButtonIV.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.onboardKycIdentityActivityInfoButtonIV.setOnClickListener {
            val i = Intent(this, KycRegistrationGuideActivity::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
            infoResultLauncher.launch(i, options)
        }

        b.onboardKycIdentityActivityIdDocNationalIdRBContainer.setOnClickListener {
            onClickRadioButton(
                b.onboardKycIdentityActivityIdDocNationalIdRB, Constants.KYC_IDENTITY_ID_NATIONAL_ID
            )
        }

        b.onboardKycIdentityActivityIdDocPassportTV.setOnClickListener {
            onClickRadioButton(
                b.onboardKycIdentityActivityIdDocPassportRB, Constants.KYC_IDENTITY_ID_PASSPORT
            )
        }

        b.onboardKycIdentityActivityIdDocPassportNatureOfPermitTV.setOnClickListener {
            //Open Bottom sheet
            val natureOfPermitSheet = KycNatureOfPermitSheet()
            natureOfPermitSheet.show(supportFragmentManager, KycNatureOfPermitSheet.TAG)
        }

        b.onboardKycIdentityActivityIdDocPassportNumberOfReferencesTV.setOnClickListener {
            //Open Bottom sheet
            val numberOfReferenceSheet = KycNumberOfReferenceSheet()
            val bundle = Bundle()
            bundle.putString(
                Constants.KYC_NUMBER_OF_REFERENCES, numberOfReference
            )
            numberOfReferenceSheet.arguments = bundle
            numberOfReferenceSheet.show(supportFragmentManager, KycNumberOfReferenceSheet.TAG)
        }

        b.onboardKycIdentityActivityIdDocRefugeeIdRBContainer.setOnClickListener {
            onClickRadioButton(
                b.onboardKycIdentityActivityIdDocRefugeeIdRB, Constants.KYC_IDENTITY_ID_REFUGEE_ID
            )
        }

        b.onboardKycIdentityActivityIdDocAsylumIdRBContainer.setOnClickListener {
            onClickRadioButton(
                b.onboardKycIdentityActivityIdDocAsylumIdRB, Constants.KYC_IDENTITY_ID_ASYLUM_ID
            )
        }

        b.onboardKycIdentityActivityIdDocDriverLicenseRBContainer.setOnClickListener {
            onClickRadioButton(
                b.onboardKycIdentityActivityIdDocDriverLicenseRB,
                Constants.KYC_IDENTITY_ID_DRIVER_LICENSE
            )
        }

        b.onboardKycIdentityActivityIdDocTrafficRegisterCardRBContainer.setOnClickListener {
            onClickRadioButton(
                b.onboardKycIdentityActivityIdDocTrafficRegisterCardRB,
                Constants.KYC_IDENTITY_ID_TRAFFIC_CARD
            )
        }

        b.onboardKycIdentityActivityIdDocBirthCertificateRBContainer.setOnClickListener {
            onClickRadioButton(
                b.onboardKycIdentityActivityIdDocBirthCertificateRB,
                Constants.KYC_IDENTITY_ID_BIRTH_CERTIFICATE
            )
        }

        b.onboardKycIdentityActivityIdDocStudentIdRBContainer.setOnClickListener {
            onClickRadioButton(
                b.onboardKycIdentityActivityIdDocStudentIdRB, Constants.KYC_IDENTITY_ID_STUDENT_ID
            )
        }

        b.onboardKycIdentityActivityIdDocEmployeeIdRBContainer.setOnClickListener {
            onClickRadioButton(
                b.onboardKycIdentityActivityIdDocEmployeeIdRB, Constants.KYC_IDENTITY_ID_EMPLOYEE_ID
            )
        }

        b.onboardKycIdentityActivitySelfieTV.setOnClickListener {
            checkPermissionsAndProceed()
        }

        b.onboardKycIdentityActivityVerDocDriverLicenseRBContainer.setOnClickListener {
            onClickRadioButton(
                b.onboardKycIdentityActivityVerDocDriverLicenseRB,
                Constants.KYC_IDENTITY_VER_DRIVER_LICENSE
            )
        }

        b.onboardKycIdentityActivityVerDocNationalIdRBContainer.setOnClickListener {
            onClickRadioButton(
                b.onboardKycIdentityActivityVerDocNationalIdRB,
                Constants.KYC_IDENTITY_VER_NATIONAL_ID
            )
        }

        b.onboardKycIdentityActivityVerDocTrafficRegisterCardRBContainer.setOnClickListener {
            onClickRadioButton(
                b.onboardKycIdentityActivityVerDocTrafficRegisterCardRB,
                Constants.KYC_IDENTITY_VER_TRAFFIC_CARD
            )
        }

        b.onboardKycIdentityActivityVerDocBirthCertificateRBContainer.setOnClickListener {
            onClickRadioButton(
                b.onboardKycIdentityActivityVerDocBirthCertificateRB,
                Constants.KYC_IDENTITY_VER_BIRTH_CERTIFICATE
            )
        }

        b.onboardKycIdentityActivityVerDocEmployerLetterRBContainer.setOnClickListener {
            onClickRadioButton(
                b.onboardKycIdentityActivityVerDocEmployerLetterRB,
                Constants.KYC_IDENTITY_VER_EMPLOYER_LETTER
            )
        }

        b.onboardKycIdentityActivityVerDocInstitutionLetterRBContainer.setOnClickListener {
            onClickRadioButton(
                b.onboardKycIdentityActivityVerDocInstitutionLetterRB,
                Constants.KYC_IDENTITY_VER_INSTITUTION_LETTER
            )
        }

        b.onboardKycIdentityActivityVerDocReligiousInstitutionLetterRBContainer.setOnClickListener {
            onClickRadioButton(
                b.onboardKycIdentityActivityVerDocReligiousInstitutionLetterRB,
                Constants.KYC_IDENTITY_VER_RELIGIOUS_INSTITUTION_LETTER
            )
        }

        b.onboardKycIdentityActivitySkipButton.setOnClickListener {
            val i = Intent(this, KycPersonalActivity::class.java)
            i.putExtra(Constants.KYC_SCOPE, kycScope)
            i.putExtra(Constants.VIEW_SCOPE, viewScope)
            i.putExtra(Constants.KYC_SEND_EMAIL, sendEmail)
            nextScreenResultLauncher.launch(i)
        }

        b.onboardKycIdentityActivitySaveAndContinueButton.setOnClickListener {
            validateFieldsForSaveAndContinue()
        }


    }

    private fun onClickSelfieButton() {
        val selfieUrl = b.onboardKycIdentityActivitySelfieTV.getTag(R.string.key_selfie_url)

        val intent = Intent(this, KycLiveSelfieActivity::class.java)
        if (selfieUrl != null) intent.putExtra(
            Constants.KYC_SELFIE_URL, selfieUrl as String
        )
        identityDocsResultLauncher.launch(intent)
    }

    /**
     *  Unselect the clicked radio button,
     *  Because Radio Button will be checked based on whether the user fill the data
     *  in the KycCaptureUploadScreen. It is handled in populateIdentityDetails() function.
     */
    private fun onClickRadioButton(radioButton: RadioButton, radioButtonId: String) {

        val frontDocUrl = radioButton.getTag(R.string.key_document_front_url)
        val backDocUrl = radioButton.getTag(R.string.key_document_back_url)

        val intent = Intent(this, KycCaptureUploadActivity::class.java)
        intent.putExtra(Constants.KYC_IDENTITY_TYPE, radioButtonId)
        intent.putExtra(Constants.KYC_SCOPE, kycScope)
        if (frontDocUrl != null) intent.putExtra(
            Constants.KYC_DOCUMENT_FRONT_URL, frontDocUrl as String
        )
        if (backDocUrl != null) intent.putExtra(
            Constants.KYC_DOCUMENT_BACK_URL, backDocUrl as String
        )
        identityDocsResultLauncher.launch(intent)
    }

    private fun validateFieldsForSaveAndContinue() {
        if (validateIdDocument() && validateSelfie() && validateVerificationDocument()) {
            when (viewScope) {
                Constants.VIEW_SCOPE_FILL -> saveCustomerIdentityDetailsApi()
                Constants.VIEW_SCOPE_EDIT -> saveCustomerNewIdentityDetailsApi()
                Constants.VIEW_SCOPE_UPDATE -> saveSimplifiedToFullIdentityApi()
            }

        } else {
            showToast("Please complete pending tasks")
        }
    }

    private fun validateIdDocument(): Boolean {
        var isValid = true
        val selectedId = getSelectedIdDocument()
        when (selectedId) {
            getString(R.string.national_id), getString(R.string.driver_s_licence), getString(R.string.traffic_register_card) -> {
                //National id require both front and back
                if (getIdDocumentFront().isEmpty() || getIdDocumentBack().isEmpty()) isValid = false
            }

            getString(R.string.birth_certificate), getString(R.string.student_id), getString(
                R.string.employee_id
            ), getString(R.string.refugee_id), getString(R.string.asylum_id) -> {
                if (getIdDocumentFront().isEmpty()) isValid = false
            }

            //2 additional fields are required for passport if it is non malawi
            getString(R.string.passport) -> {
                if (kycScope == Constants.KYC_NON_MALAWI && (getIdDocumentFront().isEmpty() || natureOfPermit.isEmpty() || numberOfReference.isEmpty())) isValid =
                    false
                else if (getIdDocumentFront().isEmpty()) isValid = false

            }

            else -> isValid = false
        }

        return isValid

    }

    private fun validateSelfie(): Boolean {
        return getSelfie().isNotEmpty()
    }

    private fun validateVerificationDocument(): Boolean {
        var isValid = true
        val selectedId = getSelectedVerificationDocument()
        when (selectedId) {
            //both front and back
            getString(R.string.driver_s_licence), getString(R.string.traffic_register_card), getString(
                R.string.national_id
            ) -> {
                if (getVerificationDocumentFront().isEmpty() || getVerificationDocumentBack().isEmpty()) isValid =
                    false
            }

            //only front side
            getString(R.string.birth_certificate), getString(R.string.employer_letter), getString(
                R.string.institution_letter
            ), getString(
                R.string.religious_institution_district_commissioner_letter
            ) -> {
                if (getVerificationDocumentFront().isEmpty()) isValid = false
            }

            else -> isValid = false
        }

        return isValid
    }

    private fun getSelectedVerificationDocument(): String {
        var selectedId = ""
        when {
            b.onboardKycIdentityActivityVerDocDriverLicenseRB.isChecked -> selectedId =
                getString(R.string.driver_s_licence)

            b.onboardKycIdentityActivityVerDocNationalIdRB.isChecked -> selectedId =
                getString(R.string.national_id)

            b.onboardKycIdentityActivityVerDocTrafficRegisterCardRB.isChecked -> selectedId =
                getString(R.string.traffic_register_card)

            b.onboardKycIdentityActivityVerDocBirthCertificateRB.isChecked -> selectedId =
                getString(R.string.birth_certificate)

            b.onboardKycIdentityActivityVerDocEmployerLetterRB.isChecked -> selectedId =
                getString(R.string.employer_letter)

            b.onboardKycIdentityActivityVerDocInstitutionLetterRB.isChecked -> selectedId =
                getString(R.string.institution_letter)

            b.onboardKycIdentityActivityVerDocReligiousInstitutionLetterRB.isChecked -> selectedId =
                getString(R.string.religious_institution_district_commissioner_letter)
        }
        return selectedId
    }

    private fun getSelectedIdDocument(): String {
        var selectedId = ""
        when {
            b.onboardKycIdentityActivityIdDocNationalIdRB.isChecked -> selectedId =
                getString(R.string.national_id)

            b.onboardKycIdentityActivityIdDocPassportRB.isChecked -> selectedId =
                getString(R.string.passport)

            b.onboardKycIdentityActivityIdDocRefugeeIdRB.isChecked -> selectedId =
                getString(R.string.refugee_id)

            b.onboardKycIdentityActivityIdDocAsylumIdRB.isChecked -> selectedId =
                getString(R.string.asylum_id)

            b.onboardKycIdentityActivityIdDocDriverLicenseRB.isChecked -> selectedId =
                getString(R.string.driver_s_licence)

            b.onboardKycIdentityActivityIdDocTrafficRegisterCardRB.isChecked -> selectedId =
                getString(R.string.traffic_register_card)

            b.onboardKycIdentityActivityIdDocBirthCertificateRB.isChecked -> selectedId =
                getString(R.string.birth_certificate)

            b.onboardKycIdentityActivityIdDocStudentIdRB.isChecked -> selectedId =
                getString(R.string.student_id)

            b.onboardKycIdentityActivityIdDocEmployeeIdRB.isChecked -> selectedId =
                getString(R.string.employee_id)
        }
        return selectedId
    }

    private fun getIdDocumentFront(): String {
        val selectedId = getSelectedIdDocument()
        return when (selectedId) {
            getString(R.string.national_id) -> {
                b.onboardKycIdentityActivityIdDocNationalIdRB.getTag(R.string.key_document_front_url) as String?
                    ?: ""
            }

            getString(R.string.passport) -> {
                b.onboardKycIdentityActivityIdDocPassportRB.getTag(R.string.key_document_front_url) as String?
                    ?: ""
            }

            getString(R.string.refugee_id) -> {
                b.onboardKycIdentityActivityIdDocRefugeeIdRB.getTag(R.string.key_document_front_url) as String?
                    ?: ""
            }

            getString(R.string.asylum_id) -> {
                b.onboardKycIdentityActivityIdDocAsylumIdRB.getTag(R.string.key_document_front_url) as String?
                    ?: ""
            }

            getString(R.string.driver_s_licence) -> {
                b.onboardKycIdentityActivityIdDocDriverLicenseRB.getTag(R.string.key_document_front_url) as String?
                    ?: ""
            }

            getString(R.string.traffic_register_card) -> {
                b.onboardKycIdentityActivityIdDocTrafficRegisterCardRB.getTag(R.string.key_document_front_url) as String?
                    ?: ""
            }

            getString(R.string.birth_certificate) -> {
                b.onboardKycIdentityActivityIdDocBirthCertificateRB.getTag(R.string.key_document_front_url) as String?
                    ?: ""
            }

            getString(R.string.student_id) -> {
                b.onboardKycIdentityActivityIdDocStudentIdRB.getTag(R.string.key_document_front_url) as String?
                    ?: ""
            }

            getString(R.string.employee_id) -> {
                b.onboardKycIdentityActivityIdDocEmployeeIdRB.getTag(R.string.key_document_front_url) as String?
                    ?: ""
            }

            else -> ""
        }
    }

    private fun getIdDocumentBack(): String {
        val selectedId = getSelectedIdDocument()
        return when (selectedId) {
            getString(R.string.national_id) -> {
                b.onboardKycIdentityActivityIdDocNationalIdRB.getTag(R.string.key_document_back_url) as String?
                    ?: ""
            }

            getString(R.string.passport) -> {
                b.onboardKycIdentityActivityIdDocPassportRB.getTag(R.string.key_document_back_url) as String?
                    ?: ""
            }

            getString(R.string.driver_s_licence) -> {
                b.onboardKycIdentityActivityIdDocDriverLicenseRB.getTag(R.string.key_document_back_url) as String?
                    ?: ""
            }

            getString(R.string.traffic_register_card) -> {
                b.onboardKycIdentityActivityIdDocTrafficRegisterCardRB.getTag(R.string.key_document_back_url) as String?
                    ?: ""
            }

            else -> ""
        }
    }

    private fun getVerificationDocumentFront(): String {
        val selectedId = getSelectedVerificationDocument()
        return when (selectedId) {
            getString(R.string.driver_s_licence) -> {
                b.onboardKycIdentityActivityVerDocDriverLicenseRB.getTag(R.string.key_document_front_url) as String?
                    ?: ""
            }

            getString(R.string.national_id) -> {
                b.onboardKycIdentityActivityVerDocNationalIdRB.getTag(R.string.key_document_front_url) as String?
                    ?: ""
            }

            getString(R.string.traffic_register_card) -> {
                b.onboardKycIdentityActivityVerDocTrafficRegisterCardRB.getTag(R.string.key_document_front_url) as String?
                    ?: ""
            }

            getString(R.string.birth_certificate) -> {
                b.onboardKycIdentityActivityVerDocBirthCertificateRB.getTag(R.string.key_document_front_url) as String?
                    ?: ""

            }

            getString(R.string.employer_letter) -> {
                b.onboardKycIdentityActivityVerDocEmployerLetterRB.getTag(R.string.key_document_front_url) as String?
                    ?: ""

            }

            getString(R.string.institution_letter) -> {
                b.onboardKycIdentityActivityVerDocInstitutionLetterRB.getTag(R.string.key_document_front_url) as String?
                    ?: ""
            }

            getString(R.string.religious_institution_district_commissioner_letter) -> {
                b.onboardKycIdentityActivityVerDocReligiousInstitutionLetterRB.getTag(R.string.key_document_front_url) as String?
                    ?: ""
            }

            else -> ""
        }
    }

    private fun getVerificationDocumentBack(): String {
        val selectedId = getSelectedVerificationDocument()
        return when (selectedId) {
            getString(R.string.driver_s_licence) -> {
                b.onboardKycIdentityActivityVerDocDriverLicenseRB.getTag(R.string.key_document_back_url) as String?
                    ?: ""
            }

            getString(R.string.national_id) -> {
                b.onboardKycIdentityActivityVerDocNationalIdRB.getTag(R.string.key_document_back_url) as String?
                    ?: ""
            }

            getString(R.string.traffic_register_card) -> {
                b.onboardKycIdentityActivityVerDocTrafficRegisterCardRB.getTag(R.string.key_document_back_url) as String?
                    ?: ""
            }

            else -> ""
        }
    }

    private fun getSelfie(): String {
        val selfieUrl = b.onboardKycIdentityActivitySelfieTV.getTag(R.string.key_selfie_url)
        return if (selfieUrl != null) selfieUrl as String else ""
    }

    private fun saveCustomerIdentityDetailsApi() {
        showButtonLoader(
            b.onboardKycIdentityActivitySaveAndContinueButton,
            b.onboardKycIdentityActivitySaveAndContinueButtonLoaderLottie
        )

        val idDocument = getSelectedIdDocument()
        val idDocFront = getIdDocumentFront()
        val idDocBack = getIdDocumentBack()
        val verificationDoc = getSelectedVerificationDocument()
        val verificationDocFront = getVerificationDocumentFront()
        val verificationDocBack = getVerificationDocumentBack()
        val selfie = getSelfie()

        lifecycleScope.launch {
            val idToken = fetchIdToken()

            val saveIdentityDetailsCall =
                ApiClient.apiService.saveCustomerIdentityDetails(
                    "Bearer $idToken", KycSaveIdentityDetailRequest(
                        id_document = idDocument,
                        id_document_front = idDocFront,
                        id_document_back = idDocBack,
                        verification_document = verificationDoc,
                        verification_document_front = verificationDocFront,
                        verification_document_back = verificationDocBack,
                        selfie = selfie,
                        nature_of_permit = natureOfPermit.ifEmpty { null },
                        ref_no = numberOfReference.ifEmpty { null },
                        id_details_status = Constants.KYC_STATUS_COMPLETED
                    )
                )

            saveIdentityDetailsCall.enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(
                    call: Call<DefaultResponse>, response: Response<DefaultResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        runOnUiThread {
                            hideButtonLoader(
                                b.onboardKycIdentityActivitySaveAndContinueButton,
                                b.onboardKycIdentityActivitySaveAndContinueButtonLoaderLottie,
                                getString(R.string.save_and_continue)
                            )
                            val i = Intent(
                                this@KycIdentityActivity,
                                KycPersonalActivity::class.java
                            )
                            i.putExtra(Constants.KYC_SCOPE, kycScope)
                            i.putExtra(Constants.VIEW_SCOPE, viewScope)
                            nextScreenResultLauncher.launch(i)
                        }
                    } else {
                        runOnUiThread {
                            showToast(getString(R.string.default_error_toast))
                            hideButtonLoader(
                                b.onboardKycIdentityActivitySaveAndContinueButton,
                                b.onboardKycIdentityActivitySaveAndContinueButtonLoaderLottie,
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
                            b.onboardKycIdentityActivitySaveAndContinueButton,
                            b.onboardKycIdentityActivitySaveAndContinueButtonLoaderLottie,
                            getString(R.string.save_and_continue)
                        )
                    }
                }
            })
        }
    }

    private fun saveCustomerNewIdentityDetailsApi() {
        showButtonLoader(
            b.onboardKycIdentityActivitySaveAndContinueButton,
            b.onboardKycIdentityActivitySaveAndContinueButtonLoaderLottie
        )

        val idDocument = getSelectedIdDocument()
        val idDocFront = getIdDocumentFront()
        val idDocBack = getIdDocumentBack()
        val verificationDoc = getSelectedVerificationDocument()
        val verificationDocFront = getVerificationDocumentFront()
        val verificationDocBack = getVerificationDocumentBack()
        val selfie = getSelfie()

        lifecycleScope.launch {
            val idToken = fetchIdToken()

            val saveYourIdentityCall = ApiClient.apiService.saveNewIdentityDetailsSelfKyc(
                idToken, SaveNewIdentityDetailsSelfKycRequest(
                    id_document = idDocument,
                    id_document_front = idDocFront,
                    id_document_back = idDocBack,
                    verification_document = verificationDoc,
                    verification_document_front = verificationDocFront,
                    verification_document_back = verificationDocBack,
                    selfie = selfie,
                    nature_of_permit = natureOfPermit.ifEmpty { null },
                    ref_no = numberOfReference.ifEmpty { null },
                    id_details_status = Constants.KYC_STATUS_COMPLETED,
                    sent_email = sendEmail
                )
            )

            saveYourIdentityCall.enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(
                    call: Call<DefaultResponse>, response: Response<DefaultResponse>
                ) {

                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        //Email already sent, so stop sending email again for further  by making sendEmail value false
                        sendEmail = false

                        runOnUiThread {
                            hideButtonLoader(
                                b.onboardKycIdentityActivitySaveAndContinueButton,
                                b.onboardKycIdentityActivitySaveAndContinueButtonLoaderLottie,
                                getString(R.string.save_and_continue)
                            )
                            val i = Intent(
                                this@KycIdentityActivity, KycPersonalActivity::class.java
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
                                b.onboardKycIdentityActivitySaveAndContinueButton,
                                b.onboardKycIdentityActivitySaveAndContinueButtonLoaderLottie,
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
                            b.onboardKycIdentityActivitySaveAndContinueButton,
                            b.onboardKycIdentityActivitySaveAndContinueButtonLoaderLottie,
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
        b.onboardKycIdentityActivityLoaderLottie.visibility = View.VISIBLE
        b.onboardKycIdentityActivityContentBox.visibility = View.GONE

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
                        populateKycIdentityFields(body.data)
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
        b.onboardKycIdentityActivityLoaderLottie.visibility = View.VISIBLE
        b.onboardKycIdentityActivityContentBox.visibility = View.GONE

        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val getUserKycDataCall = ApiClient.apiService.getSelfKycUserData("Bearer $idToken")

            getUserKycDataCall.enqueue(object : Callback<SelfKycDetailsResponse> {
                override fun onResponse(
                    call: Call<SelfKycDetailsResponse>, response: Response<SelfKycDetailsResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        populateLatestKycIdentityFields(body.data)
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

    private fun saveSimplifiedToFullIdentityApi() {
        showButtonLoader(
            b.onboardKycIdentityActivitySaveAndContinueButton,
            b.onboardKycIdentityActivitySaveAndContinueButtonLoaderLottie
        )

        val idDocument = getSelectedIdDocument()
        val idDocFront = getIdDocumentFront()
        val idDocBack = getIdDocumentBack()
        val verificationDoc = getSelectedVerificationDocument()
        val verificationDocFront = getVerificationDocumentFront()
        val verificationDocBack = getVerificationDocumentBack()
        val selfie = getSelfie()

        lifecycleScope.launch {
            val idToken = fetchIdToken()

            val saveYourIdentityCall = ApiClient.apiService.saveIdentitySimplifiedToFull(
                idToken, SaveIdentitySimplifiedToFullRequest(
                    id_document = idDocument,
                    id_document_front = idDocFront,
                    id_document_back = idDocBack,
                    verification_document = verificationDoc,
                    verification_document_front = verificationDocFront,
                    verification_document_back = verificationDocBack,
                    selfie = selfie,
                    id_details_status = Constants.KYC_STATUS_COMPLETED,
                )
            )
            saveYourIdentityCall.enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(
                    call: Call<DefaultResponse>, response: Response<DefaultResponse>
                ) {

                    hideButtonLoader(
                        b.onboardKycIdentityActivitySaveAndContinueButton,
                        b.onboardKycIdentityActivitySaveAndContinueButtonLoaderLottie,
                        getString(R.string.save_and_continue)
                    )
                    val body = response.body()
                    if (body != null && response.isSuccessful) {

                        val i = Intent(
                            this@KycIdentityActivity, KycPersonalActivity::class.java
                        )
                        i.putExtra(Constants.KYC_SCOPE, kycScope)
                        i.putExtra(Constants.VIEW_SCOPE, viewScope)
                        nextScreenResultLauncher.launch(i)

                    } else {
                        showToast(getString(R.string.default_error_toast))
                        hideButtonLoader(
                            b.onboardKycIdentityActivitySaveAndContinueButton,
                            b.onboardKycIdentityActivitySaveAndContinueButtonLoaderLottie,
                            getString(R.string.save_and_continue)
                        )
                    }
                }
                override fun onFailure(
                    call: Call<DefaultResponse>, t: Throwable
                ) {
                    runOnUiThread {
                        showToast(getString(R.string.default_error_toast))
                        hideButtonLoader(
                            b.onboardKycIdentityActivitySaveAndContinueButton,
                            b.onboardKycIdentityActivitySaveAndContinueButtonLoaderLottie,
                            getString(R.string.save_and_continue)
                        )
                    }
                }
            })
        }
    }

    private fun populateKycIdentityFields(data: KycUserData) {

        var isValid = true

        val idDocument = data.id_document ?: ""
        val idDocFront = data.id_document_front ?: ""
        val idDocBack = data.id_document_back ?: ""
        val selfie = data.selfie ?: ""
        val verificationDoc = data.verification_document ?: ""
        val verificationDocFront = data.verification_document_front ?: ""
        val verificationDocBack = data.verification_document_back ?: ""
        val idDetailsStatus = data.id_details_status ?: ""

        if (idDocument.isEmpty() || idDocFront.isEmpty() || selfie.isEmpty() || verificationDoc.isEmpty() || verificationDocFront.isEmpty()) isValid =
            false

        if (isValid && idDetailsStatus == Constants.KYC_STATUS_COMPLETED) {
            //We assume if the status complete , then all the required fields will be provided from the backend

            //Change the status to submitted
            b.onboardKycIdentityActivityIdDocumentStatusTV.text = getString(R.string.submitted)
            b.onboardKycIdentityActivityIdDocumentStatusTV.setTextColor(
                ContextCompat.getColor(
                    this, R.color.accentPositive
                )
            )

            //Store id document details as tags
            when (idDocument) {
                getString(R.string.national_id) -> {
                    b.onboardKycIdentityActivityIdDocNationalIdRB.isChecked = true
                    b.onboardKycIdentityActivityIdDocNationalIdRB.setTag(
                        R.string.key_document_front_url, idDocFront
                    )
                    b.onboardKycIdentityActivityIdDocNationalIdRB.setTag(
                        R.string.key_document_back_url, idDocBack
                    )
                }

                getString(R.string.passport) -> {
                    b.onboardKycIdentityActivityIdDocPassportRB.isChecked = true
                    b.onboardKycIdentityActivityIdDocPassportRB.setTag(
                        R.string.key_document_front_url, idDocFront
                    )
                    //show additional fields
                    if (kycScope == Constants.KYC_NON_MALAWI) {
                        //For non malawi passport , 2 fields are required
                        b.onboardKycIdentityActivityIdDocPassportRB.setTag(
                            R.string.key_document_back_url, idDocBack
                        )

                        natureOfPermit = data.nature_of_permit ?: ""
                        numberOfReference = data.ref_no ?: ""
                        b.onboardKycIdentityActivityIdDocPassportStatusContainer.visibility =
                            View.VISIBLE
                        b.onboardKycIdentityActivityIdDocPassportNatureOfPermitTV.visibility =
                            View.VISIBLE
                        b.onboardKycIdentityActivityIdDocPassportNumberOfReferencesTV.visibility =
                            View.VISIBLE
                        checkAndMarkSubmittedForPassport()
                    }
                }

                getString(R.string.refugee_id) -> {
                    b.onboardKycIdentityActivityIdDocRefugeeIdRB.isChecked = true
                    b.onboardKycIdentityActivityIdDocRefugeeIdRB.setTag(
                        R.string.key_document_front_url, idDocFront
                    )
                }

                getString(R.string.asylum_id) -> {
                    b.onboardKycIdentityActivityIdDocAsylumIdRB.isChecked = true
                    b.onboardKycIdentityActivityIdDocAsylumIdRB.setTag(
                        R.string.key_document_front_url, idDocFront
                    )
                }

                getString(R.string.driver_s_licence) -> {
                    b.onboardKycIdentityActivityIdDocDriverLicenseRB.isChecked = true
                    b.onboardKycIdentityActivityIdDocDriverLicenseRB.setTag(
                        R.string.key_document_front_url, idDocFront
                    )
                    b.onboardKycIdentityActivityIdDocDriverLicenseRB.setTag(
                        R.string.key_document_back_url, idDocBack
                    )
                }

                getString(R.string.traffic_register_card) -> {
                    b.onboardKycIdentityActivityIdDocTrafficRegisterCardRB.isChecked = true
                    b.onboardKycIdentityActivityIdDocTrafficRegisterCardRB.setTag(
                        R.string.key_document_front_url, idDocFront
                    )
                    b.onboardKycIdentityActivityIdDocTrafficRegisterCardRB.setTag(
                        R.string.key_document_back_url, idDocBack
                    )
                }

                getString(R.string.birth_certificate) -> {
                    b.onboardKycIdentityActivityIdDocBirthCertificateRB.isChecked = true
                    b.onboardKycIdentityActivityIdDocBirthCertificateRB.setTag(
                        R.string.key_document_front_url, idDocFront
                    )
                }

                getString(R.string.student_id) -> {
                    b.onboardKycIdentityActivityIdDocStudentIdRB.isChecked = true
                    b.onboardKycIdentityActivityIdDocStudentIdRB.setTag(
                        R.string.key_document_front_url, idDocFront
                    )
                }

                getString(R.string.employee_id) -> {
                    b.onboardKycIdentityActivityIdDocEmployeeIdRB.isChecked = true
                    b.onboardKycIdentityActivityIdDocEmployeeIdRB.setTag(
                        R.string.key_document_front_url, idDocFront
                    )
                }
            }

            //Change the status to submitted
            b.onboardKycIdentityActivitySelfieStatusTV.text = getString(R.string.submitted)
            b.onboardKycIdentityActivitySelfieStatusTV.setTextColor(
                ContextCompat.getColor(
                    this, R.color.accentPositive
                )
            )

            //Store selfie details
            b.onboardKycIdentityActivitySelfieTV.setTag(
                R.string.key_selfie_url, selfie
            )


            //Change the status to submitted
            b.onboardKycIdentityActivityVerificationDocumentStatusTV.text =
                getString(R.string.submitted)
            b.onboardKycIdentityActivityVerificationDocumentStatusTV.setTextColor(
                ContextCompat.getColor(
                    this, R.color.accentPositive
                )
            )

            //Store verification document details as tags
            when (verificationDoc) {
                getString(R.string.driver_s_licence) -> {
                    b.onboardKycIdentityActivityVerDocDriverLicenseRB.isChecked = true
                    b.onboardKycIdentityActivityVerDocDriverLicenseRB.setTag(
                        R.string.key_document_front_url, verificationDocFront
                    )
                    b.onboardKycIdentityActivityVerDocDriverLicenseRB.setTag(
                        R.string.key_document_back_url, verificationDocBack
                    )
                }

                getString(R.string.national_id) -> {
                    b.onboardKycIdentityActivityVerDocNationalIdRB.isChecked = true
                    b.onboardKycIdentityActivityVerDocNationalIdRB.setTag(
                        R.string.key_document_front_url, verificationDocFront
                    )
                    b.onboardKycIdentityActivityVerDocNationalIdRB.setTag(
                        R.string.key_document_back_url, verificationDocBack
                    )
                }

                getString(R.string.traffic_register_card) -> {
                    b.onboardKycIdentityActivityVerDocTrafficRegisterCardRB.isChecked = true
                    b.onboardKycIdentityActivityVerDocTrafficRegisterCardRB.setTag(
                        R.string.key_document_front_url, verificationDocFront
                    )
                    b.onboardKycIdentityActivityVerDocTrafficRegisterCardRB.setTag(
                        R.string.key_document_back_url, verificationDocBack
                    )
                }

                getString(R.string.birth_certificate) -> {
                    b.onboardKycIdentityActivityVerDocBirthCertificateRB.isChecked = true
                    b.onboardKycIdentityActivityVerDocBirthCertificateRB.setTag(
                        R.string.key_document_front_url, verificationDocFront
                    )
                }

                getString(R.string.employer_letter) -> {
                    b.onboardKycIdentityActivityVerDocEmployerLetterRB.isChecked = true
                    b.onboardKycIdentityActivityVerDocEmployerLetterRB.setTag(
                        R.string.key_document_front_url, verificationDocFront
                    )
                }

                getString(R.string.institution_letter) -> {
                    b.onboardKycIdentityActivityVerDocInstitutionLetterRB.isChecked = true
                    b.onboardKycIdentityActivityVerDocInstitutionLetterRB.setTag(
                        R.string.key_document_front_url, verificationDocFront
                    )
                }

                getString(R.string.religious_institution_district_commissioner_letter) -> {
                    b.onboardKycIdentityActivityVerDocReligiousInstitutionLetterRB.isChecked = true
                    b.onboardKycIdentityActivityVerDocReligiousInstitutionLetterRB.setTag(
                        R.string.key_document_front_url, verificationDocFront
                    )
                }
            }
        }

        b.onboardKycIdentityActivitySV.scrollTo(0, 0)
        b.onboardKycIdentityActivityLoaderLottie.visibility = View.GONE
        b.onboardKycIdentityActivityContentBox.visibility = View.VISIBLE
    }

    private fun populateLatestKycIdentityFields(data: ViewUserData) {
        var isValid = true

        val idDocument = data.id_document ?: ""
        val idDocFront = data.id_document_front ?: ""
        val idDocBack = data.id_document_back ?: ""
        val selfie = data.selfie ?: ""
        val verificationDoc = data.verification_document ?: ""
        val verificationDocFront = data.verification_document_front ?: ""
        val verificationDocBack = data.verification_document_back ?: ""
        val idDetailsStatus = data.id_details_status ?: ""

        if (idDocument.isEmpty() || idDocFront.isEmpty() || selfie.isEmpty() || verificationDoc.isEmpty() || verificationDocFront.isEmpty()) isValid =
            false

        if (isValid && idDetailsStatus == Constants.KYC_STATUS_COMPLETED) {
            //We assume if the status complete , then all the required fields will be provided from the backend

            //Change the status to submitted
            b.onboardKycIdentityActivityIdDocumentStatusTV.text = getString(R.string.submitted)
            b.onboardKycIdentityActivityIdDocumentStatusTV.setTextColor(
                ContextCompat.getColor(
                    this, R.color.accentPositive
                )
            )

            //Store id document details as tags
            when (idDocument) {
                getString(R.string.national_id) -> {
                    b.onboardKycIdentityActivityIdDocNationalIdRB.isChecked = true
                    b.onboardKycIdentityActivityIdDocNationalIdRB.setTag(
                        R.string.key_document_front_url, idDocFront
                    )
                    b.onboardKycIdentityActivityIdDocNationalIdRB.setTag(
                        R.string.key_document_back_url, idDocBack
                    )
                }

                getString(R.string.passport) -> {
                    b.onboardKycIdentityActivityIdDocPassportRB.isChecked = true
                    b.onboardKycIdentityActivityIdDocPassportRB.setTag(
                        R.string.key_document_front_url, idDocFront
                    )
                    //show additional fields
                    if (kycScope == Constants.KYC_NON_MALAWI) {
                        //For non malawi passport , 2 fields are required
                        b.onboardKycIdentityActivityIdDocPassportRB.setTag(
                            R.string.key_document_back_url, idDocBack
                        )

                        natureOfPermit = data.nature_of_permit ?: ""
                        numberOfReference = data.ref_no ?: ""
                        b.onboardKycIdentityActivityIdDocPassportStatusContainer.visibility = View.VISIBLE
                        b.onboardKycIdentityActivityIdDocPassportNatureOfPermitTV.visibility = View.VISIBLE
                        b.onboardKycIdentityActivityIdDocPassportNumberOfReferencesTV.visibility = View.VISIBLE
                        checkAndMarkSubmittedForPassport()
                    }
                }

                getString(R.string.refugee_id) -> {
                    b.onboardKycIdentityActivityIdDocRefugeeIdRB.isChecked = true
                    b.onboardKycIdentityActivityIdDocRefugeeIdRB.setTag(
                        R.string.key_document_front_url, idDocFront
                    )
                }

                getString(R.string.asylum_id) -> {
                    b.onboardKycIdentityActivityIdDocAsylumIdRB.isChecked = true
                    b.onboardKycIdentityActivityIdDocAsylumIdRB.setTag(
                        R.string.key_document_front_url, idDocFront
                    )
                }

                getString(R.string.driver_s_licence) -> {
                    b.onboardKycIdentityActivityIdDocDriverLicenseRB.isChecked = true
                    b.onboardKycIdentityActivityIdDocDriverLicenseRB.setTag(
                        R.string.key_document_front_url, idDocFront
                    )
                    b.onboardKycIdentityActivityIdDocDriverLicenseRB.setTag(
                        R.string.key_document_back_url, idDocBack
                    )
                }

                getString(R.string.traffic_register_card) -> {
                    b.onboardKycIdentityActivityIdDocTrafficRegisterCardRB.isChecked = true
                    b.onboardKycIdentityActivityIdDocTrafficRegisterCardRB.setTag(
                        R.string.key_document_front_url, idDocFront
                    )
                    b.onboardKycIdentityActivityIdDocTrafficRegisterCardRB.setTag(
                        R.string.key_document_back_url, idDocBack
                    )
                }

                getString(R.string.birth_certificate) -> {
                    b.onboardKycIdentityActivityIdDocBirthCertificateRB.isChecked = true
                    b.onboardKycIdentityActivityIdDocBirthCertificateRB.setTag(
                        R.string.key_document_front_url, idDocFront
                    )
                }

                getString(R.string.student_id) -> {
                    b.onboardKycIdentityActivityIdDocStudentIdRB.isChecked = true
                    b.onboardKycIdentityActivityIdDocStudentIdRB.setTag(
                        R.string.key_document_front_url, idDocFront
                    )
                }

                getString(R.string.employee_id) -> {
                    b.onboardKycIdentityActivityIdDocEmployeeIdRB.isChecked = true
                    b.onboardKycIdentityActivityIdDocEmployeeIdRB.setTag(
                        R.string.key_document_front_url, idDocFront
                    )
                }
            }

            //Change the status to submitted
            b.onboardKycIdentityActivitySelfieStatusTV.text = getString(R.string.submitted)
            b.onboardKycIdentityActivitySelfieStatusTV.setTextColor(
                ContextCompat.getColor(
                    this, R.color.accentPositive
                )
            )

            //Store selfie details
            b.onboardKycIdentityActivitySelfieTV.setTag(
                R.string.key_selfie_url, selfie
            )


            //Change the status to submitted
            b.onboardKycIdentityActivityVerificationDocumentStatusTV.text =
                getString(R.string.submitted)
            b.onboardKycIdentityActivityVerificationDocumentStatusTV.setTextColor(
                ContextCompat.getColor(
                    this, R.color.accentPositive
                )
            )

            //Store verification document details as tags
            when (verificationDoc) {
                getString(R.string.driver_s_licence) -> {
                    b.onboardKycIdentityActivityVerDocDriverLicenseRB.isChecked = true
                    b.onboardKycIdentityActivityVerDocDriverLicenseRB.setTag(
                        R.string.key_document_front_url, verificationDocFront
                    )
                    b.onboardKycIdentityActivityVerDocDriverLicenseRB.setTag(
                        R.string.key_document_back_url, verificationDocBack
                    )
                }

                getString(R.string.national_id) -> {
                    b.onboardKycIdentityActivityVerDocNationalIdRB.isChecked = true
                    b.onboardKycIdentityActivityVerDocNationalIdRB.setTag(
                        R.string.key_document_front_url, verificationDocFront
                    )
                    b.onboardKycIdentityActivityVerDocNationalIdRB.setTag(
                        R.string.key_document_back_url, verificationDocBack
                    )
                }

                getString(R.string.traffic_register_card) -> {
                    b.onboardKycIdentityActivityVerDocTrafficRegisterCardRB.isChecked = true
                    b.onboardKycIdentityActivityVerDocTrafficRegisterCardRB.setTag(
                        R.string.key_document_front_url, verificationDocFront
                    )
                    b.onboardKycIdentityActivityVerDocTrafficRegisterCardRB.setTag(
                        R.string.key_document_back_url, verificationDocBack
                    )
                }

                getString(R.string.birth_certificate) -> {
                    b.onboardKycIdentityActivityVerDocBirthCertificateRB.isChecked = true
                    b.onboardKycIdentityActivityVerDocBirthCertificateRB.setTag(
                        R.string.key_document_front_url, verificationDocFront
                    )
                }

                getString(R.string.employer_letter) -> {
                    b.onboardKycIdentityActivityVerDocEmployerLetterRB.isChecked = true
                    b.onboardKycIdentityActivityVerDocEmployerLetterRB.setTag(
                        R.string.key_document_front_url, verificationDocFront
                    )
                }

                getString(R.string.institution_letter) -> {
                    b.onboardKycIdentityActivityVerDocInstitutionLetterRB.isChecked = true
                    b.onboardKycIdentityActivityVerDocInstitutionLetterRB.setTag(
                        R.string.key_document_front_url, verificationDocFront
                    )
                }

                getString(R.string.religious_institution_district_commissioner_letter) -> {
                    b.onboardKycIdentityActivityVerDocReligiousInstitutionLetterRB.isChecked = true
                    b.onboardKycIdentityActivityVerDocReligiousInstitutionLetterRB.setTag(
                        R.string.key_document_front_url, verificationDocFront
                    )
                }
            }
        }

        b.onboardKycIdentityActivitySV.scrollTo(0, 0)
        b.onboardKycIdentityActivityLoaderLottie.visibility = View.GONE
        b.onboardKycIdentityActivityContentBox.visibility = View.VISIBLE
    }

    private fun clearAllFields() {
        /**Clear ID doc fields*/
        b.onboardKycIdentityActivityIdDocNationalIdRB.isChecked = false
        b.onboardKycIdentityActivityIdDocPassportRB.isChecked = false
        b.onboardKycIdentityActivityIdDocRefugeeIdRB.isChecked = false
        b.onboardKycIdentityActivityIdDocAsylumIdRB.isChecked = false
        b.onboardKycIdentityActivityIdDocDriverLicenseRB.isChecked = false
        b.onboardKycIdentityActivityIdDocTrafficRegisterCardRB.isChecked = false
        b.onboardKycIdentityActivityIdDocBirthCertificateRB.isChecked = false
        b.onboardKycIdentityActivityIdDocStudentIdRB.isChecked = false
        b.onboardKycIdentityActivityIdDocEmployeeIdRB.isChecked = false

        b.onboardKycIdentityActivityIdDocNationalIdRB.setTag(R.string.key_document_front_url, null)
        b.onboardKycIdentityActivityIdDocNationalIdRB.setTag(R.string.key_document_back_url, null)

        b.onboardKycIdentityActivityIdDocPassportRB.setTag(R.string.key_document_front_url, null)
        b.onboardKycIdentityActivityIdDocPassportRB.setTag(R.string.key_document_back_url, null)

        b.onboardKycIdentityActivityIdDocRefugeeIdRB.setTag(R.string.key_document_front_url, null)

        b.onboardKycIdentityActivityIdDocAsylumIdRB.setTag(R.string.key_document_front_url, null)

        b.onboardKycIdentityActivityIdDocDriverLicenseRB.setTag(
            R.string.key_document_front_url, null
        )
        b.onboardKycIdentityActivityIdDocDriverLicenseRB.setTag(
            R.string.key_document_back_url, null
        )

        b.onboardKycIdentityActivityIdDocTrafficRegisterCardRB.setTag(
            R.string.key_document_front_url, null
        )
        b.onboardKycIdentityActivityIdDocTrafficRegisterCardRB.setTag(
            R.string.key_document_back_url, null
        )

        b.onboardKycIdentityActivityIdDocBirthCertificateRB.setTag(
            R.string.key_document_front_url, null
        )

        b.onboardKycIdentityActivityIdDocStudentIdRB.setTag(R.string.key_document_front_url, null)

        b.onboardKycIdentityActivityIdDocEmployeeIdRB.setTag(R.string.key_document_front_url, null)

        //Clear fields related to passport
        natureOfPermit = ""
        numberOfReference = ""

        //Change the status of passport to Pending
        b.onboardKycIdentityActivityIdDocPassportStatusTV.text = getString(R.string.pending)
        b.onboardKycIdentityActivityIdDocPassportStatusTV.setTextColor(
            ContextCompat.getColor(
                this, R.color.accentWarning
            )
        )

        //Change the status to Pending
        b.onboardKycIdentityActivityIdDocumentStatusTV.text = getString(R.string.pending)
        b.onboardKycIdentityActivityIdDocumentStatusTV.setTextColor(
            ContextCompat.getColor(
                this, R.color.accentWarning
            )
        )

        /**Clear Selfie field*/
        b.onboardKycIdentityActivitySelfieTV.setTag(R.string.key_selfie_url, null)
        //Change the status to Pending
        b.onboardKycIdentityActivitySelfieStatusTV.text = getString(R.string.pending)
        b.onboardKycIdentityActivitySelfieStatusTV.setTextColor(
            ContextCompat.getColor(
                this, R.color.accentWarning
            )
        )

        /**Clear Verification doc fields*/
        b.onboardKycIdentityActivityVerDocBirthCertificateRB.isChecked = false
        b.onboardKycIdentityActivityVerDocDriverLicenseRB.isChecked = false
        b.onboardKycIdentityActivityVerDocNationalIdRB.isChecked = false
        b.onboardKycIdentityActivityVerDocEmployerLetterRB.isChecked = false
        b.onboardKycIdentityActivityVerDocInstitutionLetterRB.isChecked = false
        b.onboardKycIdentityActivityVerDocTrafficRegisterCardRB.isChecked = false
        b.onboardKycIdentityActivityVerDocReligiousInstitutionLetterRB.isChecked = false

        b.onboardKycIdentityActivityVerDocBirthCertificateRB.setTag(
            R.string.key_document_front_url, null
        )

        b.onboardKycIdentityActivityVerDocDriverLicenseRB.setTag(
            R.string.key_document_front_url, null
        )
        b.onboardKycIdentityActivityVerDocDriverLicenseRB.setTag(
            R.string.key_document_back_url, null
        )

        b.onboardKycIdentityActivityVerDocNationalIdRB.setTag(
            R.string.key_document_front_url, null
        )
        b.onboardKycIdentityActivityVerDocNationalIdRB.setTag(R.string.key_document_back_url, null)

        b.onboardKycIdentityActivityVerDocEmployerLetterRB.setTag(
            R.string.key_document_front_url, null
        )

        b.onboardKycIdentityActivityVerDocInstitutionLetterRB.setTag(
            R.string.key_document_front_url, null
        )

        b.onboardKycIdentityActivityVerDocTrafficRegisterCardRB.setTag(
            R.string.key_document_front_url, null
        )

        b.onboardKycIdentityActivityVerDocTrafficRegisterCardRB.setTag(
            R.string.key_document_back_url, null
        )

        b.onboardKycIdentityActivityVerDocReligiousInstitutionLetterRB.setTag(
            R.string.key_document_back_url, null
        )

        //Change the status to Pending
        b.onboardKycIdentityActivityVerificationDocumentStatusTV.text = getString(R.string.pending)
        b.onboardKycIdentityActivityVerificationDocumentStatusTV.setTextColor(
            ContextCompat.getColor(
                this, R.color.accentWarning
            )
        )
    }

    private fun checkPermissionsAndProceed() {
        cameraPermissionCheckLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA
            )
        )
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

    private fun checkAndMarkSubmittedForPassport() {
        //Mark as submitted
        if (natureOfPermit.isNotEmpty() && numberOfReference.isNotEmpty()) {
            b.onboardKycIdentityActivityIdDocPassportStatusTV.text = getString(R.string.submitted)
            b.onboardKycIdentityActivityIdDocPassportStatusTV.setTextColor(
                ContextCompat.getColor(
                    this, R.color.accentPositive
                )
            )

            //Make id doc whole as submitted
            b.onboardKycIdentityActivityIdDocumentStatusTV.text = getString(R.string.submitted)
            b.onboardKycIdentityActivityIdDocumentStatusTV.setTextColor(
                ContextCompat.getColor(
                    this, R.color.accentPositive
                )
            )
        } else {
            b.onboardKycIdentityActivityIdDocPassportStatusTV.text = getString(R.string.pending)
            b.onboardKycIdentityActivityIdDocPassportStatusTV.setTextColor(
                ContextCompat.getColor(
                    this, R.color.accentWarning
                )
            )

            //Make id doc whole as submitted
            b.onboardKycIdentityActivityIdDocumentStatusTV.text = getString(R.string.pending)
            b.onboardKycIdentityActivityIdDocumentStatusTV.setTextColor(
                ContextCompat.getColor(
                    this, R.color.accentWarning
                )
            )
        }
    }

    override fun onNatureOfPermitSelected(natureOfPermit: String) {
        this.natureOfPermit = natureOfPermit
        checkAndMarkSubmittedForPassport()
    }

    override fun onNumberOfReferencesTyped(numberOfReference: String) {
        this.numberOfReference = numberOfReference
        checkAndMarkSubmittedForPassport()
    }
}