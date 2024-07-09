package com.afrimax.paymaart.ui.kyc

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.OpenableColumns
import android.view.View
import android.view.WindowManager
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.afrimax.paymaart.BuildConfig
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.ActivityKycCaptureUploadBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.util.Constants
import com.afrimax.paymaart.util.showLogE
import com.airbnb.lottie.LottieAnimationView
import com.amplifyframework.kotlin.core.Amplify
import com.amplifyframework.storage.StorageException
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class KycCaptureUploadActivity : BaseActivity() {
    private lateinit var b: ActivityKycCaptureUploadBinding
    private lateinit var kycScope: String

    private lateinit var cameraPermissionCheckLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var capturedImageResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var fileResultLauncher: ActivityResultLauncher<Array<String>>
    private var currentCaptureSide = ""
    private var currentDocSide = ""
    private var identityType = ""
    private lateinit var callbackIntent: Intent
    private var frontUploadType: UploadType = UploadType.PHOTO
    private var backUploadType: UploadType = UploadType.PHOTO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityKycCaptureUploadBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.kycCaptureUploadActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true

        identityType = intent.getStringExtra(Constants.KYC_IDENTITY_TYPE) ?: ""
        initViews()
        setUpLayout(identityType)
        setUpListeners()
    }

    private fun setUpLayout(identityType: String) {
        when (identityType) {
            //Id Documents
            Constants.KYC_IDENTITY_ID_NATIONAL_ID, Constants.KYC_IDENTITY_VER_NATIONAL_ID -> {
                b.kycCaptureUploadActivityHeaderTV.text = getString(R.string.national_id)
                b.kycCaptureUploadActivitySubTextTV.text = getString(R.string.national_id_subtext)
            }

            Constants.KYC_IDENTITY_ID_PASSPORT -> {
                b.kycCaptureUploadActivityHeaderTV.text = getString(R.string.passport)
                if (kycScope == Constants.KYC_NON_MALAWI) {
                    b.kycCaptureUploadActivitySubTextTV.text =
                        getString(R.string.passport_subtext_non_malawi)

                    b.kycCaptureUploadActivityFrontTV.text = getString(R.string.data_page)
                    b.kycCaptureUploadActivityBackTV.text = getString(R.string.visa_page)
                } else {
                    b.kycCaptureUploadActivitySubTextTV.text = getString(R.string.passport_subtext)

                    //Hide Back Section
                    b.kycCaptureUploadActivityBackTV.visibility = View.GONE
                    b.kycCaptureUploadActivityMediaBackContainer.visibility = View.GONE

                    //Rename front text to 'File name'
                    b.kycCaptureUploadActivityFrontTV.text = getString(R.string.data_page)
                }
            }

            Constants.KYC_IDENTITY_ID_REFUGEE_ID -> {
                b.kycCaptureUploadActivityHeaderTV.text = getString(R.string.refugee_id)
                b.kycCaptureUploadActivitySubTextTV.text = getString(R.string.refugee_id_subtext)

                //Hide Back Section
                b.kycCaptureUploadActivityBackTV.visibility = View.GONE
                b.kycCaptureUploadActivityMediaBackContainer.visibility = View.GONE

                //Rename front text to 'File name'
                b.kycCaptureUploadActivityFrontTV.text = getString(R.string.file_name)
            }


            Constants.KYC_IDENTITY_ID_ASYLUM_ID -> {
                b.kycCaptureUploadActivityHeaderTV.text = getString(R.string.asylum_id)
                b.kycCaptureUploadActivitySubTextTV.text = getString(R.string.asylum_id_subtext)

                //Hide Back Section
                b.kycCaptureUploadActivityBackTV.visibility = View.GONE
                b.kycCaptureUploadActivityMediaBackContainer.visibility = View.GONE

                //Rename front text to 'File name'
                b.kycCaptureUploadActivityFrontTV.text = getString(R.string.file_name)
            }

            Constants.KYC_IDENTITY_ID_DRIVER_LICENSE, Constants.KYC_IDENTITY_VER_DRIVER_LICENSE -> {
                b.kycCaptureUploadActivityHeaderTV.text = getString(R.string.driver_s_licence)
                b.kycCaptureUploadActivitySubTextTV.text =
                    getString(R.string.driver_license_subtext)
            }

            Constants.KYC_IDENTITY_ID_TRAFFIC_CARD, Constants.KYC_IDENTITY_VER_TRAFFIC_CARD -> {
                b.kycCaptureUploadActivityHeaderTV.text = getString(R.string.traffic_register)
                b.kycCaptureUploadActivitySubTextTV.text =
                    getString(R.string.traffic_register_subtext)
            }

            Constants.KYC_IDENTITY_ID_BIRTH_CERTIFICATE, Constants.KYC_IDENTITY_VER_BIRTH_CERTIFICATE -> {
                b.kycCaptureUploadActivityHeaderTV.text = getString(R.string.birth_certificate)
                b.kycCaptureUploadActivitySubTextTV.text =
                    getString(R.string.birth_certificate_subtext)

                //Hide Back Section
                b.kycCaptureUploadActivityBackTV.visibility = View.GONE
                b.kycCaptureUploadActivityMediaBackContainer.visibility = View.GONE

                //Rename front text to 'File name'
                b.kycCaptureUploadActivityFrontTV.text = getString(R.string.file_name)
            }

            Constants.KYC_IDENTITY_ID_STUDENT_ID -> {
                b.kycCaptureUploadActivityHeaderTV.text = getString(R.string.student_id)
                b.kycCaptureUploadActivitySubTextTV.text = getString(R.string.student_id_subtext)

                //Hide Back Section
                b.kycCaptureUploadActivityBackTV.visibility = View.GONE
                b.kycCaptureUploadActivityMediaBackContainer.visibility = View.GONE

                //Rename front text to 'File name'
                b.kycCaptureUploadActivityFrontTV.text = getString(R.string.file_name)
            }

            Constants.KYC_IDENTITY_ID_EMPLOYEE_ID -> {
                b.kycCaptureUploadActivityHeaderTV.text = getString(R.string.employee_id)
                b.kycCaptureUploadActivitySubTextTV.text = getString(R.string.employee_id_subtext)

                //Hide Back Section
                b.kycCaptureUploadActivityBackTV.visibility = View.GONE
                b.kycCaptureUploadActivityMediaBackContainer.visibility = View.GONE

                //Rename front text to 'File name'
                b.kycCaptureUploadActivityFrontTV.text = getString(R.string.file_name)
            }

            Constants.KYC_IDENTITY_VER_EMPLOYER_LETTER -> {
                b.kycCaptureUploadActivityHeaderTV.text = getString(R.string.employer_letter)
                b.kycCaptureUploadActivitySubTextTV.text =
                    getString(R.string.employer_letter_subtext)

                //Hide Back Section
                b.kycCaptureUploadActivityBackTV.visibility = View.GONE
                b.kycCaptureUploadActivityMediaBackContainer.visibility = View.GONE

                //Rename front text to 'File name'
                b.kycCaptureUploadActivityFrontTV.text = getString(R.string.file_name)
            }


            Constants.KYC_IDENTITY_VER_INSTITUTION_LETTER -> {
                b.kycCaptureUploadActivityHeaderTV.text = getString(R.string.institution_letter)
                b.kycCaptureUploadActivitySubTextTV.text =
                    getString(R.string.institution_letter_subtext)

                //Hide Back Section
                b.kycCaptureUploadActivityBackTV.visibility = View.GONE
                b.kycCaptureUploadActivityMediaBackContainer.visibility = View.GONE

                //Rename front text to 'File name'
                b.kycCaptureUploadActivityFrontTV.text = getString(R.string.file_name)
            }

            Constants.KYC_IDENTITY_VER_RELIGIOUS_INSTITUTION_LETTER -> {
                b.kycCaptureUploadActivityHeaderTV.text =
                    getString(R.string.religious_institution_district_commissioner_letter)
                b.kycCaptureUploadActivitySubTextTV.text =
                    getString(R.string.religious_institution_letter_subtext)

                //Hide Back Section
                b.kycCaptureUploadActivityBackTV.visibility = View.GONE
                b.kycCaptureUploadActivityMediaBackContainer.visibility = View.GONE

                //Rename front text to 'File name'
                b.kycCaptureUploadActivityFrontTV.text = getString(R.string.file_name)
            }
        }

        val frontDocUrl = intent.getStringExtra(Constants.KYC_DOCUMENT_FRONT_URL)
        val backDocUrl = intent.getStringExtra(Constants.KYC_DOCUMENT_BACK_URL)

        if (frontDocUrl != null) {
            //Add the same files to the callbackIntent to return the data to previous activity
            callbackIntent.putExtra(Constants.KYC_DOCUMENT_FRONT_URL, frontDocUrl)
            //The user already uploaded files, show those files
            if (frontDocUrl.endsWith(".pdf")) {
                frontUploadType = UploadType.FILE
                loadPdf(getString(R.string.front), frontDocUrl, true)
            } else
                loadImage(getString(R.string.front), frontDocUrl, true)

            if (backDocUrl != null) {
                callbackIntent.putExtra(Constants.KYC_DOCUMENT_BACK_URL, backDocUrl)
                if (backDocUrl.endsWith(".pdf")){
                    backUploadType = UploadType.FILE
                    loadPdf(getString(R.string.back), backDocUrl, true)
                }
                else loadImage(getString(R.string.back), backDocUrl, true)
            }
        }

    }

    private fun initViews() {
        kycScope = intent.getStringExtra(Constants.KYC_SCOPE) ?: ""

        callbackIntent = Intent()
        callbackIntent.putExtra(Constants.KYC_IDENTITY_TYPE, identityType)

        capturedImageResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // There are no request codes
                    val data = result.data
                    populateCapturedImages(data)
                }
            }

        //Declare Activity Result Contract for permissions
        cameraPermissionCheckLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { multiplePermissionsMap ->
                if (!multiplePermissionsMap.containsValue(false)) {
                    //If all permissions are granted then open Capture Activity to shoot photo

                    val docHeader = if (currentCaptureSide == getString(R.string.front)) {
                        b.kycCaptureUploadActivityFrontTV.text.toString()
                    } else b.kycCaptureUploadActivityBackTV.text.toString()
                    val intent = Intent(this, KycCaptureActivity::class.java)
                    intent.putExtra(Constants.KYC_SCOPE, kycScope)
                    intent.putExtra(Constants.KYC_DOC_HEADER, docHeader)
                    intent.putExtra(Constants.KYC_CAPTURED_IMAGE_SIDE, currentCaptureSide)
                    intent.putExtra(Constants.KYC_IDENTITY_TYPE, identityType)
                    capturedImageResultLauncher.launch(intent)
                } else {
                    showToast("Please allow camera permissions to continue")
                }
            }

        fileResultLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                lifecycleScope.launch {
                    populateSelectedFile(uri)
                }
            }
    }

    private suspend fun populateSelectedFile(uri: Uri?) {
        if (uri != null) {
            val fileType = getMimeType(uri)
            val stream = contentResolver.openInputStream(uri)

            if (fileType.isNotEmpty() && stream != null) {
                withContext(Dispatchers.IO) {
                    if (stream.available() < (10 * 1024 * 1024)) {
                        when (fileType) {
                            Constants.KYC_FORMAT_JPG, Constants.KYC_FORMAT_JPEG, Constants.KYC_FORMAT_PNG -> {
                                runOnUiThread { loadImage(currentDocSide, uri, false) }
                            }

                            Constants.KYC_FORMAT_PDF -> {
                                runOnUiThread { loadPdf(currentDocSide, uri, false) }
                            }
                        }
                    } else {
                        runOnUiThread { showToast("Can't upload file with size greater than 10MB") }
                    }
                    stream.close()
                }
            }
        }
    }

    private fun populateCapturedImages(data: Intent?) {
        if (data != null) {
            val imageSide = data.getStringExtra(Constants.KYC_CAPTURED_IMAGE_SIDE) ?: ""
            val imageUri = data.parcelable<Uri>(Constants.KYC_CAPTURED_IMAGE_URI)
            if (imageSide.isNotEmpty() && imageUri != null) {
                loadImage(imageSide, imageUri, false)
            }
        }
    }

    private fun loadImage(imageSide: String, imageUri: Any, isUploaded: Boolean) {
        when (imageSide) {
            getString(R.string.front) -> {
                //Show image preview
                b.kycCaptureUploadActivityCaptureUploadFrontContainer.visibility = View.GONE
                b.kycCaptureUploadActivityImagePreviewFrontContainer.visibility = View.VISIBLE
                b.kycCaptureUploadActivityFilePreviewFrontContainer.visibility = View.GONE

                //Already uploaded image - So append cdn base url to fetch from s3
                if (isUploaded) Glide.with(this).load(BuildConfig.CDN_BASE_URL + imageUri)
                    .placeholder(R.color.neutralGreyRipple)
                    .into(b.kycCaptureUploadActivityFrontImageIV)
                //Else load it directly from Uri
                else Glide.with(this).load(imageUri).placeholder(R.color.neutralGreyRipple)
                    .into(b.kycCaptureUploadActivityFrontImageIV)
                b.kycCaptureUploadActivityFrontImageIV.setTag(
                    R.string.key_image_path, imageUri
                )
                b.kycCaptureUploadActivityFrontImageIV.setTag(
                    R.string.key_is_uploaded, isUploaded
                )
            }

            getString(R.string.back) -> {
                //Show image preview
                b.kycCaptureUploadActivityCaptureUploadBackContainer.visibility = View.GONE
                b.kycCaptureUploadActivityImagePreviewBackContainer.visibility = View.VISIBLE
                b.kycCaptureUploadActivityFilePreviewBackContainer.visibility = View.GONE

                //Already uploaded image - So append cdn base url to fetch from s3
                if (isUploaded) Glide.with(this).load(BuildConfig.CDN_BASE_URL + imageUri)
                    .placeholder(R.color.neutralGreyRipple)
                    .into(b.kycCaptureUploadActivityBackImageIV)
                //Else load it directly from Uri
                else Glide.with(this).load(imageUri).placeholder(R.color.neutralGreyRipple)
                    .into(b.kycCaptureUploadActivityBackImageIV)
                b.kycCaptureUploadActivityBackImageIV.setTag(
                    R.string.key_image_path, imageUri
                )
                b.kycCaptureUploadActivityBackImageIV.setTag(
                    R.string.key_is_uploaded, isUploaded
                )
            }
        }

        checkAndShowSubmitButton()
    }

    private fun loadPdf(docSide: String, uri: Any, isUploaded: Boolean) {
        when (docSide) {
            getString(R.string.front) -> {
                //Show file preview
                b.kycCaptureUploadActivityCaptureUploadFrontContainer.visibility = View.GONE
                b.kycCaptureUploadActivityImagePreviewFrontContainer.visibility = View.GONE
                b.kycCaptureUploadActivityFilePreviewFrontContainer.visibility = View.VISIBLE

                //Load file
                val fileName = if (isUploaded) getFileNameFromUrl(uri)
                else getFileNameFromUri(this, uri)
                b.kycCaptureUploadActivityFrontFileNameTV.text = fileName
                b.kycCaptureUploadActivityFrontFileNameTV.setTag(
                    R.string.key_file_path, uri
                )
                b.kycCaptureUploadActivityFrontFileNameTV.setTag(
                    R.string.key_is_uploaded, isUploaded
                )

            }

            getString(R.string.back) -> {
                //Show file preview
                b.kycCaptureUploadActivityCaptureUploadBackContainer.visibility = View.GONE
                b.kycCaptureUploadActivityImagePreviewBackContainer.visibility = View.GONE
                b.kycCaptureUploadActivityFilePreviewBackContainer.visibility = View.VISIBLE

                //Load file
                val fileName = if (isUploaded) getFileNameFromUrl(uri)
                else getFileNameFromUri(this, uri)
                b.kycCaptureUploadActivityBackFileNameTV.text = fileName
                b.kycCaptureUploadActivityBackFileNameTV.setTag(
                    R.string.key_file_path, uri
                )
                b.kycCaptureUploadActivityBackFileNameTV.setTag(
                    R.string.key_is_uploaded, isUploaded
                )
            }
        }

        checkAndShowSubmitButton()
    }

    private fun checkAndShowSubmitButton() {
        val frontImageTag = b.kycCaptureUploadActivityFrontImageIV.getTag(R.string.key_image_path)
        val backImageTag = b.kycCaptureUploadActivityBackImageIV.getTag(R.string.key_image_path)
        val frontFileTag = b.kycCaptureUploadActivityFrontFileNameTV.getTag(R.string.key_file_path)
        val backFileTag = b.kycCaptureUploadActivityBackFileNameTV.getTag(R.string.key_file_path)

        when (identityType) {
            //Have front and back sides
            Constants.KYC_IDENTITY_ID_NATIONAL_ID, Constants.KYC_IDENTITY_VER_NATIONAL_ID, Constants.KYC_IDENTITY_ID_DRIVER_LICENSE, Constants.KYC_IDENTITY_ID_TRAFFIC_CARD, Constants.KYC_IDENTITY_VER_DRIVER_LICENSE, Constants.KYC_IDENTITY_VER_TRAFFIC_CARD -> {
                if ((frontImageTag != null && backImageTag != null) || (frontImageTag != null && backFileTag != null) || (frontFileTag != null && backImageTag != null) || (frontFileTag != null && backFileTag != null)) {
                    b.kycCaptureUploadActivitySubmitButton.visibility = View.VISIBLE
                } else {
                    b.kycCaptureUploadActivitySubmitButton.visibility = View.GONE
                }
            }
            //Have only front
            Constants.KYC_IDENTITY_ID_REFUGEE_ID, Constants.KYC_IDENTITY_ID_ASYLUM_ID, Constants.KYC_IDENTITY_ID_BIRTH_CERTIFICATE, Constants.KYC_IDENTITY_ID_STUDENT_ID, Constants.KYC_IDENTITY_ID_EMPLOYEE_ID, Constants.KYC_IDENTITY_VER_RELIGIOUS_INSTITUTION_LETTER, Constants.KYC_IDENTITY_VER_BIRTH_CERTIFICATE, Constants.KYC_IDENTITY_VER_EMPLOYER_LETTER, Constants.KYC_IDENTITY_VER_INSTITUTION_LETTER -> {
                if (frontImageTag != null || frontFileTag != null) {
                    b.kycCaptureUploadActivitySubmitButton.visibility = View.VISIBLE
                } else {
                    b.kycCaptureUploadActivitySubmitButton.visibility = View.GONE
                }
            }

            Constants.KYC_IDENTITY_ID_PASSPORT -> {
                if (kycScope == Constants.KYC_NON_MALAWI) {
                    // non malawi kyc have 2 docs
                    if ((frontImageTag != null && backImageTag != null) || (frontImageTag != null && backFileTag != null) || (frontFileTag != null && backImageTag != null) || (frontFileTag != null && backFileTag != null)) {
                        b.kycCaptureUploadActivitySubmitButton.visibility = View.VISIBLE
                    } else {
                        b.kycCaptureUploadActivitySubmitButton.visibility = View.GONE
                    }
                } else {
                    if (frontImageTag != null || frontFileTag != null) {
                        b.kycCaptureUploadActivitySubmitButton.visibility = View.VISIBLE
                    } else {
                        b.kycCaptureUploadActivitySubmitButton.visibility = View.GONE
                    }
                }
            }
        }
    }


    private fun setUpListeners() {

        b.kycCaptureUploadActivityBackButtonIV.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.kycCaptureUploadActivityInfoButtonIV.setOnClickListener {
            val i = Intent(this, KycRegistrationGuideActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            startActivity(i, options)
        }

        b.kycCaptureUploadActivityFrontCaptureButton.setOnClickListener {
            currentCaptureSide = getString(R.string.front)
            checkPermissionsAndProceed()
            frontUploadType = UploadType.PHOTO
        }

        b.kycCaptureUploadActivityBackCaptureButton.setOnClickListener {
            currentCaptureSide = getString(R.string.back)
            checkPermissionsAndProceed()
            backUploadType = UploadType.PHOTO
        }

        b.kycCaptureUploadActivityFrontRemoveButton.setOnClickListener {
            removeFrontImage()
        }

        b.kycCaptureUploadActivityBackRemoveButton.setOnClickListener {
            removeBackImage()
        }

        b.kycCaptureUploadActivityFrontReUploadButton.setOnClickListener {
            removeFrontImage()
            currentCaptureSide = getString(R.string.front)
            if (frontUploadType == UploadType.PHOTO)
                checkPermissionsAndProceed()
            else
                launchFilePicker()
        }

        b.kycCaptureUploadActivityBackReUploadButton.setOnClickListener {
            removeBackImage()
            currentCaptureSide = getString(R.string.back)
            if (backUploadType == UploadType.PHOTO)
                checkPermissionsAndProceed()
            else
                launchFilePicker()
        }

        b.kycCaptureUploadActivityFrontUploadButton.setOnClickListener {
            currentDocSide = getString(R.string.front)
            launchFilePicker()
            frontUploadType = UploadType.FILE
        }
        b.kycCaptureUploadActivityBackUploadButton.setOnClickListener {
            currentDocSide = getString(R.string.back)
            launchFilePicker()
            backUploadType = UploadType.FILE
        }

        b.kycCaptureUploadActivityFileFrontDeleteButton.setOnClickListener {
            removeFrontFile()
        }

        b.kycCaptureUploadActivityFileBackDeleteButton.setOnClickListener {
            removeBackFile()
        }

        b.kycCaptureUploadActivityFileFrontReUploadButton.setOnClickListener {
            removeFrontFile()
            currentDocSide = getString(R.string.front)
            launchFilePicker()
        }

        b.kycCaptureUploadActivityFileBackReUploadButton.setOnClickListener {
            removeBackFile()
            currentDocSide = getString(R.string.back)
            launchFilePicker()
        }

        b.kycCaptureUploadActivityFrontImageIV.setOnClickListener {
            onClickFrontImage()
        }

        b.kycCaptureUploadActivityBackImageIV.setOnClickListener {
            onClickBackImage()
        }

        b.kycCaptureUploadActivityFilePreviewFrontContainer.setOnClickListener {
            onClickFrontFile()
        }

        b.kycCaptureUploadActivityFilePreviewBackContainer.setOnClickListener {
            onClickBackFile()
        }

        b.kycCaptureUploadActivitySubmitButton.setOnClickListener {
            onClickSubmit()
        }

    }

    private fun onClickBackFile() {
        val backFileTag = b.kycCaptureUploadActivityBackFileNameTV.getTag(R.string.key_file_path)
        val backFileIsUploaded =
            b.kycCaptureUploadActivityBackFileNameTV.getTag(R.string.key_is_uploaded)
        if (backFileTag != null && backFileIsUploaded != null && backFileIsUploaded is Boolean) {
            val intent = Intent(this, KycFullScreenPreviewActivity::class.java)
            intent.putExtra(Constants.KYC_MEDIA_TYPE, Constants.KYC_MEDIA_PDF_TYPE)
            intent.putExtra(Constants.KYC_MEDIA_IS_UPLOADED, backFileIsUploaded)
            if (backFileIsUploaded) intent.putExtra(
                Constants.KYC_SELECTED_FILE_URI, BuildConfig.CDN_BASE_URL + backFileTag as String
            ) else intent.putExtra(Constants.KYC_SELECTED_FILE_URI, backFileTag as Parcelable)
            startActivity(intent)
        }
    }

    private fun onClickFrontFile() {
        val frontFileTag = b.kycCaptureUploadActivityFrontFileNameTV.getTag(R.string.key_file_path)
        val frontFileIsUploaded =
            b.kycCaptureUploadActivityFrontFileNameTV.getTag(R.string.key_is_uploaded)
        if (frontFileTag != null && frontFileIsUploaded != null && frontFileIsUploaded is Boolean) {
            val intent = Intent(this, KycFullScreenPreviewActivity::class.java)
            intent.putExtra(Constants.KYC_MEDIA_TYPE, Constants.KYC_MEDIA_PDF_TYPE)
            intent.putExtra(Constants.KYC_MEDIA_IS_UPLOADED, frontFileIsUploaded)
            if (frontFileIsUploaded) intent.putExtra(
                Constants.KYC_SELECTED_FILE_URI, BuildConfig.CDN_BASE_URL + frontFileTag as String
            )
            else intent.putExtra(Constants.KYC_SELECTED_FILE_URI, frontFileTag as Parcelable)
            startActivity(intent)
        }
    }

    private fun onClickBackImage() {
        val backImageTag = b.kycCaptureUploadActivityBackImageIV.getTag(R.string.key_image_path)
        val backImageIsUploaded =
            b.kycCaptureUploadActivityBackImageIV.getTag(R.string.key_is_uploaded)
        if (backImageTag != null && backImageIsUploaded != null && backImageIsUploaded is Boolean) {
            val intent = Intent(this, KycFullScreenPreviewActivity::class.java)
            intent.putExtra(Constants.KYC_MEDIA_TYPE, Constants.KYC_MEDIA_IMAGE_TYPE)
            intent.putExtra(Constants.KYC_MEDIA_IS_UPLOADED, backImageIsUploaded)
            if (backImageIsUploaded) intent.putExtra(
                Constants.KYC_CAPTURED_IMAGE_URI, backImageTag as String
            ) else intent.putExtra(Constants.KYC_CAPTURED_IMAGE_URI, backImageTag as Parcelable)
            startActivity(intent)
        }
    }

    private fun onClickFrontImage() {
        val frontImageTag = b.kycCaptureUploadActivityFrontImageIV.getTag(R.string.key_image_path)
        val frontImageIsUploaded =
            b.kycCaptureUploadActivityFrontImageIV.getTag(R.string.key_is_uploaded)
        if (frontImageTag != null && frontImageIsUploaded != null && frontImageIsUploaded is Boolean) {
            val intent = Intent(this, KycFullScreenPreviewActivity::class.java)
            intent.putExtra(Constants.KYC_MEDIA_TYPE, Constants.KYC_MEDIA_IMAGE_TYPE)
            intent.putExtra(Constants.KYC_MEDIA_IS_UPLOADED, frontImageIsUploaded)
            if (frontImageIsUploaded) intent.putExtra(
                Constants.KYC_CAPTURED_IMAGE_URI, frontImageTag as String
            ) else intent.putExtra(
                Constants.KYC_CAPTURED_IMAGE_URI, frontImageTag as Parcelable
            )
            startActivity(intent)
        }
    }

    private fun onClickSubmit() {
        val frontImageTag = b.kycCaptureUploadActivityFrontImageIV.getTag(R.string.key_image_path)
        val backImageTag = b.kycCaptureUploadActivityBackImageIV.getTag(R.string.key_image_path)
        val frontFileTag = b.kycCaptureUploadActivityFrontFileNameTV.getTag(R.string.key_file_path)
        val backFileTag = b.kycCaptureUploadActivityBackFileNameTV.getTag(R.string.key_file_path)

        var frontUri: Uri? = null
        if (frontImageTag != null && frontImageTag is Uri) frontUri =
            frontImageTag else if (frontFileTag != null && frontFileTag is Uri) frontUri =
            frontFileTag
        var backUri: Uri? = null
        if (backImageTag != null && backImageTag is Uri) backUri =
            backImageTag else if (backFileTag != null && backFileTag is Uri) backUri = backFileTag

        uploadAndFinish(frontUri, backUri)
    }

    private fun uploadAndFinish(frontUri: Uri?, backUri: Uri?) {
        showButtonLoader(
            b.kycCaptureUploadActivitySubmitButton,
            b.kycCaptureUploadActivitySubmitButtonLoaderLottie
        )
        var isValid = true

        lifecycleScope.launch {
            if (frontUri != null) {
                val frontDocKey = amplifyUpload(frontUri)
                if (frontDocKey.isNotEmpty()) callbackIntent.putExtra(
                    Constants.KYC_DOCUMENT_FRONT_URL, frontDocKey
                ) else isValid = false
            }
            if (backUri != null) {
                val backDocKey = amplifyUpload(backUri)
                if (backDocKey.isNotEmpty()) callbackIntent.putExtra(
                    Constants.KYC_DOCUMENT_BACK_URL, backDocKey
                ) else isValid = false
            }

            if (isValid) {
                setResult(RESULT_OK, callbackIntent)
                finish()
            }

            hideButtonLoader(
                b.kycCaptureUploadActivitySubmitButton,
                b.kycCaptureUploadActivitySubmitButtonLoaderLottie,
                getString(R.string.submit)
            )
        }
    }


    @OptIn(FlowPreview::class)
    private suspend fun amplifyUpload(uri: Uri): String {
        val paymaartId = retrievePaymaartId()
        val stream = contentResolver.openInputStream(uri)

        if (stream != null && paymaartId != null) {
            val objectKey = "kyc_data/$paymaartId/${UUID.randomUUID()}/${
                getFileNameFromUri(
                    this, uri
                )
            }"

            val upload = Amplify.Storage.uploadInputStream(objectKey, stream)
            try {
                val result = upload.result()
                "Result".showLogE(result.key)
                return result.key
            } catch (error: StorageException) {
                //
                "Result".showLogE(error)
            }
        }
        return ""
    }


    private fun removeFrontImage() {
        b.kycCaptureUploadActivityFrontImageIV.setImageDrawable(null)
        b.kycCaptureUploadActivityFrontImageIV.setTag(
            R.string.key_image_path, null
        )
        b.kycCaptureUploadActivityFrontImageIV.setTag(
            R.string.key_is_uploaded, null
        )

        //Hide the image preview view
        b.kycCaptureUploadActivityImagePreviewFrontContainer.visibility = View.GONE
        b.kycCaptureUploadActivityCaptureUploadFrontContainer.visibility = View.VISIBLE
        b.kycCaptureUploadActivityFilePreviewFrontContainer.visibility = View.GONE

        //Hide the button in bottom bar
        b.kycCaptureUploadActivitySubmitButton.visibility = View.GONE

    }

    private fun removeBackImage() {
        b.kycCaptureUploadActivityBackImageIV.setImageDrawable(null)
        b.kycCaptureUploadActivityBackImageIV.setTag(
            R.string.key_image_path, null
        )
        b.kycCaptureUploadActivityBackImageIV.setTag(
            R.string.key_is_uploaded, null
        )

        //Hide the image preview view
        b.kycCaptureUploadActivityImagePreviewBackContainer.visibility = View.GONE
        b.kycCaptureUploadActivityCaptureUploadBackContainer.visibility = View.VISIBLE
        b.kycCaptureUploadActivityFilePreviewBackContainer.visibility = View.GONE

        //Hide the button in bottom bar
        checkAndShowSubmitButton()
    }

    private fun removeFrontFile() {
        b.kycCaptureUploadActivityFrontFileNameTV.setTag(
            R.string.key_image_path, null
        )
        b.kycCaptureUploadActivityFrontFileNameTV.setTag(
            R.string.key_is_uploaded, null
        )


        //Hide the file preview view
        b.kycCaptureUploadActivityImagePreviewFrontContainer.visibility = View.GONE
        b.kycCaptureUploadActivityCaptureUploadFrontContainer.visibility = View.VISIBLE
        b.kycCaptureUploadActivityFilePreviewFrontContainer.visibility = View.GONE

        //Hide the button in bottom bar
        checkAndShowSubmitButton()

    }

    private fun removeBackFile() {
        b.kycCaptureUploadActivityBackFileNameTV.setTag(
            R.string.key_image_path, null
        )
        b.kycCaptureUploadActivityBackFileNameTV.setTag(
            R.string.key_is_uploaded, null
        )

        //Hide the file preview view
        b.kycCaptureUploadActivityImagePreviewBackContainer.visibility = View.GONE
        b.kycCaptureUploadActivityCaptureUploadBackContainer.visibility = View.VISIBLE
        b.kycCaptureUploadActivityFilePreviewBackContainer.visibility = View.GONE

        //Hide the button in bottom bar
        b.kycCaptureUploadActivitySubmitButton.visibility = View.GONE

    }

    private fun checkPermissionsAndProceed() {
        cameraPermissionCheckLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA
            )
        )
    }

    private fun launchFilePicker() {
        fileResultLauncher.launch(
            arrayOf(
                "image/jpeg", "image/jpg", "image/png", "application/pdf"
            )
        )
    }

    private fun getMimeType(uri: Uri): String {
        val cR: ContentResolver = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri)) ?: "jpg"
    }

    @SuppressLint("Range")
    private fun getFileNameFromUri(context: Context, uri: Any): String {
        if (uri is Uri) {
            val fileName: String
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.moveToFirst()
            fileName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                ?: "PMCMR_${System.currentTimeMillis()}.jpg"
            cursor?.close()
            return fileName
        }
        return ""
    }

    private fun getFileNameFromUrl(url: Any): String {
        if (url is String) {
            return url.split("/").last()
        }
        return ""
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

enum class UploadType{
    PHOTO,
    FILE
}