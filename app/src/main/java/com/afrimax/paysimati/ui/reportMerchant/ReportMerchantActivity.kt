package com.afrimax.paysimati.ui.reportMerchant

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.BuildConfig
import com.afrimax.paysimati.R
import com.afrimax.paysimati.common.core.fileTypeFromUri
import com.afrimax.paysimati.common.data.utils.CONTENT_TYPE_PDF
import com.afrimax.paysimati.common.domain.enums.FileType
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.ReportMerchantRequest
import com.afrimax.paysimati.databinding.ActivityReportMerchantBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.kyc.KycCaptureActivity
import com.afrimax.paysimati.ui.kyc.UploadType
import com.afrimax.paysimati.ui.utils.bottomsheets.ReportCompleteMessage
import com.afrimax.paysimati.ui.utils.bottomsheets.ReportMerchantOtherReasons
import com.afrimax.paysimati.ui.utils.interfaces.ReportOtherReason
import com.afrimax.paysimati.util.Constants
import com.afrimax.paysimati.util.showLogE
import com.airbnb.lottie.LottieAnimationView
import com.amplifyframework.kotlin.core.Amplify
import com.amplifyframework.storage.StorageException
import com.amplifyframework.storage.options.StorageUploadInputStreamOptions
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ReportMerchantActivity :BaseActivity(),ReportOtherReason {
    private lateinit var binding : ActivityReportMerchantBinding
    private var paymaartId =""
    private lateinit var cameraPermissionCheckLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var capturedImageResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var fileResultLauncher: ActivityResultLauncher<Array<String>>
    private var currentCaptureSide = ""
    private var currentDocSide = ""
    private lateinit var callbackIntent: Intent
    private var identityType = "reportmerchant"
    private var Upload1: UploadType = UploadType.PHOTO
    private var Upload2: UploadType = UploadType.PHOTO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportMerchantBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        paymaartId = intent.getStringExtra(Constants.PAYMAART_ID)?:""
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reportMerchantProfile)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupview()
        initViews()
    }

    private fun initViews() {
        callbackIntent = Intent()
        capturedImageResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                    result->
                if(result.resultCode == RESULT_OK){
                    val data = result.data
                    populateCapturedImages(data)
                }
            }
        cameraPermissionCheckLauncher=
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){multiplepermission->
                if(!multiplepermission.containsValue(false)){
                    val intent = Intent(this, KycCaptureActivity::class.java)
                    intent.putExtra(Constants.KYC_CAPTURED_IMAGE_SIDE, currentCaptureSide)
                    intent.putExtra(Constants.KYC_IDENTITY_TYPE, identityType)
                    capturedImageResultLauncher.launch(intent)
                }
                else {
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

    private fun setupview() {
        setimage()
        binding.viewMerchantActivityBackButton.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        binding.checkOthers.setOnClickListener {
            ReportMerchantOtherReasons().show(supportFragmentManager,ReportMerchantOtherReasons.TAG)
        }
        binding.ReportMerchantsumbit.setOnClickListener {
            val selectedreason = mutableListOf<String>()
            if(binding.checkMerchantSecurity.isChecked){
                selectedreason.add(getString(R.string.report_merchant_q_1))
            }
            if(binding.checkMerchantDisputes.isChecked){
                selectedreason.add(getString(R.string.report_merchant_q_2))
            }

            if(binding.checkPrivacyConcerns.isChecked){
                selectedreason.add(getString(R.string.report_merchant_q_3))
            }
            if(binding.checkRegulatoryCompliance.isChecked){
                selectedreason.add(getString(R.string.report_merchant_q_4))
            }
            if(binding.checkOthers.isChecked){
                val otherReason = getOtherReason()
                if (!otherReason.isNullOrBlank()) {
                    selectedreason.add(otherReason)
                }
            }
            if (selectedreason.isEmpty()) {
                Toast.makeText(this, "Please select at least one reason", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val img1 = binding.reportmerchantUploadActivityFrontImage1IV.getTag(R.string.key_image_path)

            val img2 = binding.reportmerchantUploadActivityFrontImage2IV.getTag(R.string.key_file_path)

            val img1tag = binding.reportmerchantUploadActivity1FrontFileNameTV.getTag(R.string.key_file_path)

            val img2tag = binding.reportmerchantUploadActivity2FrontFileNameTV.getTag(R.string.key_file_path)


            var frontUri: Uri? = null
            if (img1 != null && img1 is Uri) frontUri =
                img1 else if (img1tag != null && img1tag is Uri) frontUri =
                img1tag
            var backUri: Uri? = null
            if (img2 != null && img2 is Uri) backUri =
                img2 else if (img2tag != null && img2tag is Uri) backUri = img2tag
            if (frontUri != null || backUri != null) {
                // If images are present, upload them to S3 first
                uploadImagesAndSubmitReport(frontUri, backUri,selectedreason)
            }
            else{
                submitReport(selectedreason)
            }



        }
    }



    private fun setimage() {
        binding.reportMerchantimgupload1.setOnClickListener {
            currentDocSide = getString(R.string.front)
            launchFilePicker()
            Upload1 = UploadType.FILE
        }
        binding.reportMerchantimgupload2.setOnClickListener {
            currentDocSide = getString(R.string.back)
            launchFilePicker()
            Upload2 = UploadType.FILE
        }
        binding.reportMerchantcaputre1.setOnClickListener{
            currentCaptureSide = getString(R.string.front)
            checkPermissionsAndProceed()
            Upload1 = UploadType.PHOTO
        }
        binding.reportMerchantcaputre2.setOnClickListener{
            currentCaptureSide = getString(R.string.back)
            checkPermissionsAndProceed()
            Upload1 = UploadType.PHOTO
        }
        binding.reportmerchantUploadActivityFileFrontDeleteButton.setOnClickListener {
            removeFrontFile()
        }
        binding.reportmerchantActivityFileFrontReUploadButton.setOnClickListener{
            removeFrontFile()
            currentDocSide = getString(R.string.front)
            launchFilePicker()
        }
        binding.reportmerchantUploadActivityFileFrontDeleteButton2.setOnClickListener {
            removesecondFile()
        }
        binding.reportmerchantActivityFileFrontReUploadButton2.setOnClickListener{
            removesecondFile()
            currentDocSide = getString(R.string.back)
            launchFilePicker()
        }
        binding.reportmerchantUploadActivity1FrontRemoveButton.setOnClickListener{
            removeFrontImage()
        }
        binding.reportmerchantUploadActivity2FrontRemoveButton.setOnClickListener {
            removeBackImage()
        }
        binding.reportmerchantUploadActivity1FrontReUploadButton.setOnClickListener {
            removeFrontImage()
            currentCaptureSide = getString(R.string.front)
            if (Upload1 == UploadType.PHOTO) checkPermissionsAndProceed()
            else launchFilePicker()
        }
        binding.reportmerchantUploadActivity2FrontReUploadButton.setOnClickListener {
            removeBackImage()
            currentCaptureSide = getString(R.string.front)
            if (Upload1 == UploadType.PHOTO) checkPermissionsAndProceed()
            else launchFilePicker()
        }
    }

    private fun submitReport(reasons: List<String>,imageUrls: List<String> = emptyList()) {
        showbuttonloader(
            binding.ReportMerchantsumbit,
            binding.payMerchantActivitySendPaymentButtonLoaderLottie
        )

        val request = ReportMerchantRequest(
            images = imageUrls,
            reasons = reasons,
            userId =  paymaartId
        )
        Log.d("kk","$request")
        lifecycleScope.launch {
            val id =fetchIdToken()
            val response = ApiClient.apiService.reportMerchant(id,request)
            if (response.isSuccessful) {
                hideButtonLoader(
                    binding.ReportMerchantsumbit,
                    binding.payMerchantActivitySendPaymentButtonLoaderLottie,
                    getString(R.string.Report_Merchant)
                )
                ReportCompleteMessage().show(supportFragmentManager,ReportCompleteMessage.TAG)
            } else {
                showToast(getString(R.string.default_error_toast))
            }
        }
    }

    private var otherReason: String? = null
    override fun onReportReasonTyped(reason: String) {
        otherReason = reason
        if (reason.isEmpty()) {
            binding.checkOthers.isChecked = false
        }
    }

    private fun getOtherReason(): String? {
        return otherReason
    }

    private fun showbuttonloader(actionButton: AppCompatButton, loaderLottie: LottieAnimationView) {
        actionButton.text = getString(R.string.empty_string)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        loaderLottie.visibility = VISIBLE
    }
    private fun hideButtonLoader(
        actionButton: AppCompatButton, loaderLottie: LottieAnimationView, buttonText: String
    ) {
        actionButton.text = buttonText
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        loaderLottie.visibility = GONE
    }


//Images selection


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
    private fun loadPdf(docSide: String, uri: Any, isUploaded: Boolean) {
        when (docSide) {
            getString(R.string.front) -> {
                //Show file preview
                binding.reportMerchantimg1.visibility = View.GONE
                binding.reportmerchantActivityImagePreview1FrontContainer.visibility = View.GONE
                binding.reportmerchantUploadActivityFilePreview1FrontContainer.visibility = View.VISIBLE

                //Load file
                val fileName = if (isUploaded) getFileNameFromUrl(uri)
                else getFileNameFromUri(this, uri)
                binding.reportmerchantUploadActivity1FrontFileNameTV.text = fileName
                binding.reportmerchantUploadActivity1FrontFileNameTV.setTag(
                    R.string.key_file_path, uri
                )
                binding.reportmerchantUploadActivity1FrontFileNameTV.setTag(
                    R.string.key_is_uploaded, isUploaded
                )

            }

            getString(R.string.back) -> {
                //Show file preview
                binding.reportMerchantimg2.visibility = View.GONE
                binding.reportmerchantActivityImagePreview2FrontContainer.visibility = View.GONE
                binding.reportmerchantUploadActivityFilePreview2FrontContainer.visibility = View.VISIBLE

                //Load file
                val fileName = if (isUploaded) getFileNameFromUrl(uri)
                else getFileNameFromUri(this, uri)
                binding.reportmerchantUploadActivity2FrontFileNameTV.text = fileName
                binding.reportmerchantUploadActivity2FrontFileNameTV.setTag(
                    R.string.key_file_path, uri
                )
                binding.reportmerchantUploadActivity2FrontFileNameTV.setTag(
                    R.string.key_is_uploaded, isUploaded
                )
            }
        }

    }
    private fun getFileNameFromUrl(url: Any): String {
        if (url is String) {
            return url.split("/").last()
        }
        return ""
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
    private fun loadImage(imageSide: String, imageUri: Any, isUploaded: Boolean) {
        when (imageSide) {
            getString(R.string.front) -> {
                //Show image preview
                binding.reportMerchantimg1.visibility = View.GONE
                binding.reportmerchantActivityImagePreview1FrontContainer.visibility = View.VISIBLE
                binding.reportmerchantUploadActivityFilePreview1FrontContainer.visibility = View.GONE

                //Already uploaded image - So append cdn base url to fetch from s3
                if (isUploaded) Glide.with(this).load(BuildConfig.CDN_BASE_URL + imageUri)
                    .placeholder(R.color.neutralGreyRipple)
                    .into(binding.reportmerchantUploadActivityFrontImage1IV)
                //Else load it directly from Uri
                else Glide.with(this).load(imageUri).placeholder(R.color.neutralGreyRipple)
                    .into(binding.reportmerchantUploadActivityFrontImage1IV)
                binding.reportmerchantUploadActivityFrontImage1IV.setTag(
                    R.string.key_image_path, imageUri
                )
                binding.reportmerchantUploadActivityFrontImage1IV.setTag(
                    R.string.key_is_uploaded, isUploaded
                )
            }

            getString(R.string.back) -> {
                //Show image preview
                binding.reportMerchantimg2.visibility = View.GONE
                binding.reportmerchantActivityImagePreview2FrontContainer.visibility = View.VISIBLE
                binding.reportmerchantUploadActivityFilePreview2FrontContainer.visibility = View.GONE

                //Already uploaded image - So append cdn base url to fetch from s3
                if (isUploaded) Glide.with(this).load(BuildConfig.CDN_BASE_URL + imageUri)
                    .placeholder(R.color.neutralGreyRipple)
                    .into(binding.reportmerchantUploadActivityFrontImage2IV)
                //Else load it directly from Uri
                else Glide.with(this).load(imageUri).placeholder(R.color.neutralGreyRipple)
                    .into(binding.reportmerchantUploadActivityFrontImage2IV)
                binding.reportmerchantUploadActivityFrontImage2IV.setTag(
                    R.string.key_image_path, imageUri
                )
                binding.reportmerchantUploadActivityFrontImage2IV.setTag(
                    R.string.key_is_uploaded, isUploaded
                )
            }
        }
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
    private fun checkPermissionsAndProceed() {
        cameraPermissionCheckLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA
            )
        )
    }
    private fun removeFrontFile() {
        binding.reportmerchantUploadActivity1FrontFileNameTV.setTag(
            R.string.key_image_path, null
        )
        binding.reportmerchantUploadActivity1FrontFileNameTV.setTag(
            R.string.key_is_uploaded, null
        )

        binding.reportmerchantActivityImagePreview1FrontContainer.visibility = View.GONE
        binding.reportMerchantimg1.visibility=View.VISIBLE
        binding.reportmerchantUploadActivityFilePreview1FrontContainer.visibility = View.GONE

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
    private fun removeFrontImage(){
        binding.reportmerchantUploadActivityFrontImage1IV.setImageDrawable(null)
        binding.reportmerchantUploadActivityFrontImage1IV.setTag(
            R.string.key_file_path,null
        )
        binding.reportmerchantUploadActivityFrontImage1IV.setTag(
            R.string.key_is_uploaded,null
        )
        binding.reportmerchantActivityImagePreview1FrontContainer.visibility = View.GONE
        binding.reportMerchantimg1.visibility=View.VISIBLE
        binding.reportmerchantUploadActivityFilePreview1FrontContainer.visibility = View.GONE


    }
    private fun removeBackImage(){
        binding.reportmerchantUploadActivityFrontImage2IV.setImageDrawable(null)
        binding.reportmerchantUploadActivityFrontImage2IV.setTag(
            R.string.key_image_path, null
        )
        binding.reportmerchantUploadActivityFrontImage2IV.setTag(
            R.string.key_is_uploaded, null
        )

        binding.reportmerchantActivityImagePreview2FrontContainer.visibility=View.GONE
        binding.reportMerchantimg2.visibility=View.VISIBLE
        binding.reportmerchantUploadActivityFilePreview2FrontContainer.visibility =View.GONE

    }
    private fun removesecondFile(){
        binding.reportmerchantUploadActivity2FrontFileNameTV.setTag(
            R.string.key_image_path, null
        )
        binding.reportmerchantUploadActivity2FrontFileNameTV.setTag(
            R.string.key_is_uploaded, null
        )
        binding.reportmerchantActivityImagePreview2FrontContainer.visibility=View.GONE
        binding.reportMerchantimg2.visibility=View.VISIBLE
        binding.reportmerchantUploadActivityFilePreview2FrontContainer.visibility =View.GONE

    }
    private fun uploadImagesAndSubmitReport(frontUri: Uri?, backUri: Uri?,reasons: List<String>) {
        showbuttonloader(
            binding.ReportMerchantsumbit,
            binding.payMerchantActivitySendPaymentButtonLoaderLottie
        )

        lifecycleScope.launch {
            val uploadedImageUrls = mutableListOf<String>()
            frontUri?.let { uri ->
                val frontDocKey = amplifyUpload(uri)
                if (frontDocKey.isNotEmpty()) {
                    uploadedImageUrls.add(frontDocKey)
                } else {
                    showToast("Failed to upload front image. Please try again.")
                    hideButtonLoader(
                        binding.ReportMerchantsumbit,
                        binding.payMerchantActivitySendPaymentButtonLoaderLottie,
                        getString(R.string.Report_Merchant)
                    )
                    return@launch
                }
            }

            // Upload back image if present
            backUri?.let { uri ->
                val backDocKey = amplifyUpload(uri)
                if (backDocKey.isNotEmpty()) {
                    uploadedImageUrls.add(backDocKey)
                } else {
                    showToast("Failed to upload back image. Please try again.")
                    hideButtonLoader(
                        binding.ReportMerchantsumbit,
                        binding.payMerchantActivitySendPaymentButtonLoaderLottie,
                        getString(R.string.Report_Merchant)
                    )
                    return@launch
                }
            }

            // Submit the report with the uploaded image URLs and selected reasons
            submitReport(reasons, uploadedImageUrls)


        }


    }

    private suspend fun amplifyUpload(uri: Uri): String {
        val stream = contentResolver.openInputStream(uri)

        if (stream != null) {
            val objectKey = "merchant_report/${UUID.randomUUID()}/${
                getFileNameFromUri(
                    this, uri
                )
            }"

            val fileType = this.fileTypeFromUri(uri)

            val contentType: String? = when (fileType) {
                FileType.IMAGE, null -> null
                FileType.PDF -> CONTENT_TYPE_PDF
            }

            val options = StorageUploadInputStreamOptions.builder().apply {
                contentType?.let { contentType(it) }
            }.build()

            val upload = Amplify.Storage.uploadInputStream(
                key = objectKey, local = stream, options = options
            )
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




}