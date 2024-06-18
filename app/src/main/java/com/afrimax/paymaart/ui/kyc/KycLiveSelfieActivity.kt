package com.afrimax.paymaart.ui.kyc

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Rational
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
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
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.afrimax.paymaart.BuildConfig
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.ActivityKycLiveSelfieBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.util.Constants
import com.afrimax.paymaart.util.showLogE
import com.airbnb.lottie.LottieAnimationView
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.StorageException
import com.bumptech.glide.Glide
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class KycLiveSelfieActivity : BaseActivity() {
    private lateinit var b: ActivityKycLiveSelfieBinding

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private var imageCapture: ImageCapture? = null
    private lateinit var imgCaptureExecutor: ExecutorService
    private var imageUri: Any? = null
    private lateinit var callbackIntent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityKycLiveSelfieBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.kycLiveSelfieActivity)) { v, insets ->
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
        callbackIntent = Intent()
        callbackIntent.putExtra(Constants.KYC_IDENTITY_TYPE, Constants.KYC_IDENTITY_LIVE_SELFIE)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        imgCaptureExecutor = Executors.newSingleThreadExecutor()
    }

    private fun setUpLayout() {
        val selfieUrl = intent.getStringExtra(Constants.KYC_SELFIE_URL)
        if (selfieUrl != null) {
            closeCamera(selfieUrl, true)
        } else {
            showCamera()
        }
    }

    private fun setUpListeners() {

        b.kycLiveSelfieActivityBackButtonIV.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.kycLiveSelfieActivityInfoButtonIV.setOnClickListener {
            val i = Intent(this, KycRegistrationGuideActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            startActivity(i, options)
        }

        b.kycLiveSelfieActivitySelfieMustBeContainer.setOnClickListener {
            toggleSelfieMustBeDetails()
        }
        b.kycLiveSelfieActivityYouMustBeContainer.setOnClickListener {
            toggleYouMustBeDetails()
        }

        b.kycLiveSelfieActivityCaptureButton.setOnClickListener {
            takePhoto()
        }

        b.kycLiveSelfieActivityReCaptureButton.setOnClickListener {
            showCamera()
        }

        b.kycLiveSelfieActivitySubmitButton.setOnClickListener {
            onClickSubmit()
        }
    }

    private fun showCamera() {
        startCamera()

        //Show camera view
        b.kycLiveSelfieActivityPreviewViewContainer.visibility = View.VISIBLE
        b.kycLiveSelfieActivityImagePreviewContainer.visibility = View.GONE

        //Show appropriate button
        b.kycLiveSelfieActivityCaptureButton.visibility = View.VISIBLE
        b.kycLiveSelfieActivityRecaptureContainer.visibility = View.GONE

    }

    private fun startCamera() {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(b.kycLiveSelfieActivityPreviewView.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder().build()

        //Specify the image width and height in the aspect ratio 1:1
        val viewPort = ViewPort.Builder(Rational(1080, 1080), Surface.ROTATION_0).build()
        val useCaseGroup = UseCaseGroup.Builder().addUseCase(preview).addUseCase(imageCapture!!)
            .setViewPort(viewPort).build()

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_FRONT_CAMERA, useCaseGroup
                )
            } catch (e: Exception) {
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        imageCapture?.let { imageCapture ->
            val fileName = "PMCMR_${System.currentTimeMillis()}.jpg"
            val file = File(externalMediaDirs[0], fileName)

            //Enable mirroring to the saved images
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file)
                .setMetadata(ImageCapture.Metadata().also {
                    it.isReversedHorizontal = true
                }).build()

            imageCapture.takePicture(
                outputFileOptions,
                imgCaptureExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        imageUri = file.toUri()
                        runOnUiThread {
                            closeCamera(file.toUri(), false)
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        runOnUiThread {
                            showToast(getString(R.string.default_error_toast))
                        }
                    }
                })
        }
    }

    private fun closeCamera(imageUri: Any, isUploaded: Boolean) {
        cameraProviderFuture.get().unbindAll()
        "Response".showLogE(imageUri)
        "Response2".showLogE(isUploaded)
        //Hide camera view
        b.kycLiveSelfieActivityPreviewViewContainer.visibility = View.GONE
        b.kycLiveSelfieActivityImagePreviewContainer.visibility = View.VISIBLE

        //Set captured image
        if (isUploaded) Glide.with(this).load(BuildConfig.CDN_BASE_URL + imageUri)
            .placeholder(R.color.neutralGreyRipple).into(b.kycLiveSelfieActivityImageIV)
        else Glide.with(this).load(imageUri).placeholder(R.color.neutralGreyRipple)
            .into(b.kycLiveSelfieActivityImageIV)
        b.kycLiveSelfieActivityImageIV.setTag(R.string.key_selfie_url, imageUri)

        //Show appropriate button
        b.kycLiveSelfieActivityRecaptureContainer.visibility = View.VISIBLE
        b.kycLiveSelfieActivityCaptureButton.visibility = View.GONE

    }

    private fun toggleSelfieMustBeDetails() {
        if (b.kycLiveSelfieActivitySelfieMustBeTextContainer.root.isVisible) {
            b.kycLiveSelfieActivitySelfieMustBeNextArrowIV.animate().rotation(0f).setDuration(200)
            b.kycLiveSelfieActivitySelfieMustBeTextContainer.root.visibility = View.GONE
        } else {
            b.kycLiveSelfieActivitySelfieMustBeNextArrowIV.animate().rotation(90f).setDuration(200)
            b.kycLiveSelfieActivitySelfieMustBeTextContainer.root.visibility = View.VISIBLE
        }
    }

    private fun toggleYouMustBeDetails() {
        if (b.kycLiveSelfieActivityYouMustBeTextContainer.root.isVisible) {
            b.kycLiveSelfieActivityYouMustBeNextArrowIV.animate().rotation(0f).setDuration(200)
            b.kycLiveSelfieActivityYouMustBeTextContainer.root.visibility = View.GONE
        } else {
            b.kycLiveSelfieActivityYouMustBeNextArrowIV.animate().rotation(90f).setDuration(200)
            b.kycLiveSelfieActivityYouMustBeTextContainer.root.visibility = View.VISIBLE
        }
    }

    private fun onClickSubmit() {
        val imageTag = b.kycLiveSelfieActivityImageIV.getTag(R.string.key_selfie_url)

        var uri: Uri? = null
        if (imageTag != null && imageTag is Uri) uri = imageTag
        amplifyGetPaymaartIdAndProceed(uri)
    }

    private fun amplifyGetPaymaartIdAndProceed(uri: Uri?) {
        showButtonLoader(
            b.kycLiveSelfieActivitySubmitButton, b.kycLiveSelfieActivitySubmitButtonLoaderLottie
        )
        var isValid = true
        Amplify.Auth.getCurrentUser({ authUser ->
            lifecycleScope.launch {

                if (uri != null) {
                    val selfieKey = amplifyUpload(uri, authUser.username)
                    if (selfieKey.isNotEmpty()) callbackIntent.putExtra(
                        Constants.KYC_SELFIE_URL, selfieKey
                    ) else isValid = false
                }

                if (isValid) {
                    setResult(RESULT_OK, callbackIntent)
                    finish()
                }

                hideButtonLoader(
                    b.kycLiveSelfieActivitySubmitButton,
                    b.kycLiveSelfieActivitySubmitButtonLoaderLottie,
                    getString(R.string.submit)
                )
            }
        }, {
            runOnUiThread { showToast(getString(R.string.default_error_toast)) }
            hideButtonLoader(
                b.kycLiveSelfieActivitySubmitButton,
                b.kycLiveSelfieActivitySubmitButtonLoaderLottie,
                getString(R.string.submit)
            )
        })
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private suspend fun amplifyUpload(uri: Uri, paymaartId: String): String {
        "Response".showLogE(paymaartId)
        val objectKey = "kyc_data/$paymaartId/${UUID.randomUUID()}/${
            getFileNameFromUri(
                this, uri
            )
        }"
        val stream = contentResolver.openInputStream(uri)

        if (stream != null) {
            val upload = com.amplifyframework.kotlin.core.Amplify.Storage.uploadInputStream(
                objectKey, stream
            )
            try {
                val result = upload.result()
                "Response".showLogE(result.key)
                return result.key
            } catch (error: StorageException) {
                //
            }
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

    @SuppressLint("Range")
    private fun getFileNameFromUri(context: Context, uri: Any): String {
        if (uri is Uri) {
            val fileName: String
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.moveToFirst()
            fileName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                ?: "PMAGT_${System.currentTimeMillis()}.jpg"
            cursor?.close()
            return fileName
        }
        return ""
    }
}