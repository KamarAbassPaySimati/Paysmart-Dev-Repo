package com.afrimax.paysimati.ui.kyc

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Rational
import android.view.Surface
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.afrimax.paysimati.R
import com.afrimax.paysimati.databinding.ActivityKycCaptureBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.util.Constants
import com.bumptech.glide.Glide
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class KycCaptureActivity : BaseActivity() {
    private lateinit var b: ActivityKycCaptureBinding
    private lateinit var kycScope: String

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private var imageCapture: ImageCapture? = null
    private lateinit var imgCaptureExecutor: ExecutorService

    private lateinit var imageSide: String
    private var imageUri: Uri? = null

    private var identityType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityKycCaptureBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.kycCaptureActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true

        imageSide = intent.getStringExtra(Constants.KYC_CAPTURED_IMAGE_SIDE) ?: ""
        val docHeader = intent.getStringExtra(Constants.KYC_DOC_HEADER) ?: ""
        b.kycCaptureActivityDocSideTV.text = docHeader

        kycScope = intent.getStringExtra(Constants.KYC_SCOPE) ?: ""
        identityType = intent.getStringExtra(Constants.KYC_IDENTITY_TYPE) ?: ""
        setUpLayout(identityType)
        initViews()
        setUpListeners()
        showCamera()
    }

    private fun setUpLayout(identityType: String) {
        when (identityType) {
            Constants.KYC_IDENTITY_ID_NATIONAL_ID, Constants.KYC_IDENTITY_VER_NATIONAL_ID -> {
                b.kycCaptureActivityHeaderTV.text = getString(R.string.national_id)
                b.kycCaptureActivitySubTextTV.text = getString(R.string.national_id_subtext)
            }

            Constants.KYC_IDENTITY_ID_PASSPORT -> {
                b.kycCaptureActivityHeaderTV.text = getString(R.string.passport)
                if (kycScope == Constants.KYC_NON_MALAWI) b.kycCaptureActivitySubTextTV.text =
                    getString(R.string.passport_subtext_non_malawi)
                else b.kycCaptureActivitySubTextTV.text = getString(R.string.passport_subtext)
            }

            Constants.KYC_IDENTITY_ID_REFUGEE_ID -> {
                b.kycCaptureActivityHeaderTV.text = getString(R.string.refugee_id)
                b.kycCaptureActivitySubTextTV.text = getString(R.string.refugee_id_subtext)
            }

            Constants.KYC_IDENTITY_ID_ASYLUM_ID -> {
                b.kycCaptureActivityHeaderTV.text = getString(R.string.asylum_id)
                b.kycCaptureActivitySubTextTV.text = getString(R.string.asylum_id_subtext)
            }

            Constants.KYC_IDENTITY_ID_EMPLOYEE_ID -> {
                b.kycCaptureActivityHeaderTV.text = getString(R.string.employee_id)
                b.kycCaptureActivitySubTextTV.text = getString(R.string.employee_id_subtext)
            }

            Constants.KYC_IDENTITY_ID_STUDENT_ID -> {
                b.kycCaptureActivityHeaderTV.text = getString(R.string.student_id)
                b.kycCaptureActivitySubTextTV.text = getString(R.string.student_id_subtext)
            }

            Constants.KYC_IDENTITY_ID_DRIVER_LICENSE, Constants.KYC_IDENTITY_VER_DRIVER_LICENSE -> {
                b.kycCaptureActivityHeaderTV.text = getString(R.string.driver_s_licence)
                b.kycCaptureActivitySubTextTV.text = getString(R.string.driver_license_subtext)
            }

            Constants.KYC_IDENTITY_ID_TRAFFIC_CARD, Constants.KYC_IDENTITY_VER_TRAFFIC_CARD -> {
                b.kycCaptureActivityHeaderTV.text = getString(R.string.traffic_register)
                b.kycCaptureActivitySubTextTV.text = getString(R.string.traffic_register_subtext)
            }

            Constants.KYC_IDENTITY_ID_BIRTH_CERTIFICATE, Constants.KYC_IDENTITY_VER_BIRTH_CERTIFICATE -> {
                b.kycCaptureActivityHeaderTV.text = getString(R.string.birth_certificate)
                b.kycCaptureActivitySubTextTV.text = getString(R.string.birth_certificate_subtext)
            }

            Constants.KYC_IDENTITY_VER_EMPLOYER_LETTER -> {
                b.kycCaptureActivityHeaderTV.text = getString(R.string.employer_letter)
                b.kycCaptureActivitySubTextTV.text = getString(R.string.employer_letter_subtext)
            }

            Constants.KYC_IDENTITY_VER_INSTITUTION_LETTER -> {
                b.kycCaptureActivityHeaderTV.text = getString(R.string.institution_letter)
                b.kycCaptureActivitySubTextTV.text = getString(R.string.institution_letter_subtext)
            }

            Constants.KYC_IDENTITY_VER_RELIGIOUS_INSTITUTION_LETTER -> {
                b.kycCaptureActivityHeaderTV.text =
                    getString(R.string.religious_institution_district_commissioner_letter)
                b.kycCaptureActivitySubTextTV.text =
                    getString(R.string.religious_institution_letter_subtext)
            }
        }
    }

    private fun initViews() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        imgCaptureExecutor = Executors.newSingleThreadExecutor()
    }

    private fun setUpListeners() {

        b.kycCaptureActivityBackButtonIV.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.kycCaptureActivityInfoButtonIV.setOnClickListener {
            val i = Intent(this, KycRegistrationGuideActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            startActivity(i, options)
        }

        b.kycCaptureActivityCaptureButton.setOnClickListener {
            //Disable the button instantly so the user won't click multiple multiple time resulting in throwing an error
            b.kycCaptureActivityCaptureButton.isEnabled = false
            takePhoto()
        }

        b.kycCaptureActivityReCaptureButton.setOnClickListener {
            showCamera()
        }

        b.kycCaptureActivityLooksGoodButton.setOnClickListener {
            looksGoodAndProceed()
        }
    }

    private fun showCamera() {
        startCamera()

        //Show camera view
        b.kycCaptureActivityPreviewViewContainer.visibility = View.VISIBLE
        b.kycCaptureActivityImagePreviewContainer.visibility = View.GONE

        //Show appropriate button
        b.kycCaptureActivityCaptureButton.visibility = View.VISIBLE
        b.kycCaptureActivityRecaptureContainer.visibility = View.GONE

    }

    private fun startCamera() {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(b.kycCaptureActivityPreviewView.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder().build()

        //Specify the image width and height in the aspect ratio 18:11
        val viewPort = ViewPort.Builder(Rational(1800, 1100), Surface.ROTATION_0).build()
        val useCaseGroup = UseCaseGroup.Builder().addUseCase(preview).addUseCase(imageCapture!!)
            .setViewPort(viewPort).build()

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, useCaseGroup
                )
            } catch (e: Exception) {
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        imageCapture?.let {
            val fileName = "PMCMR_${System.currentTimeMillis()}.jpg"
            val file = File(externalMediaDirs[0], fileName)


            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

            it.takePicture(outputFileOptions,
                imgCaptureExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        imageUri = file.toUri()
                        runOnUiThread {
                            closeCamera()
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        runOnUiThread {
                            showToast(getString(R.string.default_error_toast))
                            closeCamera()
                        }
                    }
                })
        }
    }

    private fun closeCamera() {
        cameraProviderFuture.get().unbindAll()

        //Hide camera view
        b.kycCaptureActivityPreviewViewContainer.visibility = View.GONE
        b.kycCaptureActivityImagePreviewContainer.visibility = View.VISIBLE

        //Set captured image
        Glide.with(this).load(imageUri).into(b.kycCaptureActivityImageIV)

        //Show appropriate button
        b.kycCaptureActivityRecaptureContainer.visibility = View.VISIBLE
        b.kycCaptureActivityCaptureButton.visibility = View.GONE

        //Re enable the button
        b.kycCaptureActivityCaptureButton.isEnabled = true

    }

    private fun looksGoodAndProceed() {
        if (imageUri != null && imageSide.isNotEmpty()) {
            val intent = Intent()
            intent.putExtra(Constants.KYC_CAPTURED_IMAGE_SIDE, imageSide)
            intent.putExtra(Constants.KYC_CAPTURED_IMAGE_URI, imageUri as Parcelable)
            setResult(RESULT_OK, intent)
        }
        finish()
    }
}