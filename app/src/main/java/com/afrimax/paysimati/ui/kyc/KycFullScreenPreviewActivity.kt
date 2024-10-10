package com.afrimax.paysimati.ui.kyc

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.BuildConfig
import com.afrimax.paysimati.R
import com.afrimax.paysimati.databinding.ActivityKycFullScreenPreviewBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.util.Constants
import com.afrimax.paysimati.util.showLogE
import com.bumptech.glide.Glide
import com.rajat.pdfviewer.HeaderData

class KycFullScreenPreviewActivity : BaseActivity() {
    private lateinit var b: ActivityKycFullScreenPreviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityKycFullScreenPreviewBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.kycFullScreenPreviewActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false
        wic.isAppearanceLightNavigationBars = false

        setUpLayout()
        setUpListeners()
    }

    private fun setUpLayout() {
        val mediaType = intent.getStringExtra(Constants.KYC_MEDIA_TYPE)
        when (mediaType) {
            Constants.KYC_MEDIA_IMAGE_TYPE -> {
                //Show image zoom layout
                b.kycFullScreenPreviewActivityZoomLayout.visibility = View.VISIBLE
                b.kycFullScreenPreviewActivityPdfView.visibility = View.GONE

                //Load image
                loadImage()
            }

            Constants.KYC_MEDIA_PDF_TYPE -> {
                //Show pdf layout
                b.kycFullScreenPreviewActivityPdfView.visibility = View.VISIBLE
                b.kycFullScreenPreviewActivityZoomLayout.visibility = View.GONE

                //Load pdf
                loadPdf()
            }
        }

        //In case of ViewSelfKycActivity we are passing file name from there
        val fileName = intent.getStringExtra(Constants.KYC_MEDIA_NAME) ?: ""
        if (fileName.isNotEmpty()) b.kycFullScreenPreviewActivityFileNameTV.text = fileName
    }

    private fun loadPdf() {
        val isUploaded = intent.getBooleanExtra(Constants.KYC_MEDIA_IS_UPLOADED, false)
        if (isUploaded) {
            val fileUrl = intent.getStringExtra(Constants.KYC_SELECTED_FILE_URI) ?: ""
            if (fileUrl.isNotEmpty()) {
                try {
                    b.kycFullScreenPreviewActivityPdfView.initWithUrl(
                        fileUrl, HeaderData(), lifecycleScope, lifecycle
                    )
                } catch (e: SecurityException) {
                    showToast("Unable to open password protected pdf file")
                    finish()
                }

                //Set filename
                b.kycFullScreenPreviewActivityFileNameTV.text = getFileNameFromUrl(fileUrl)
            }
        } else {
            val fileUri = intent.parcelable<Uri>(Constants.KYC_SELECTED_FILE_URI)
            if (fileUri != null) {
                try {
                    b.kycFullScreenPreviewActivityPdfView.initWithUri(fileUri)
                } catch (e: SecurityException) {
                    showToast("Unable to open password protected pdf file")
                    finish()
                }

                //Set filename
                b.kycFullScreenPreviewActivityFileNameTV.text = getFileNameFromUri(this, fileUri)
            }

        }
    }

    private fun loadImage() {
        val isUploaded = intent.getBooleanExtra(Constants.KYC_MEDIA_IS_UPLOADED, false)
        if (isUploaded) {
            val imageUrl: String = intent.getStringExtra(Constants.KYC_CAPTURED_IMAGE_URI) ?: ""
            Glide.with(this).load(BuildConfig.CDN_BASE_URL + imageUrl)
                .into(b.kycFullScreenPreviewActivityIV)
            //Set filename
            b.kycFullScreenPreviewActivityFileNameTV.text = getFileNameFromUrl(imageUrl)
        } else {
            val imageUri = intent.parcelable<Uri>(Constants.KYC_CAPTURED_IMAGE_URI)
            Glide.with(this).load(imageUri).into(b.kycFullScreenPreviewActivityIV)

            //Set filename
            if (imageUri != null) b.kycFullScreenPreviewActivityFileNameTV.text =
                getFileNameFromUri(this, imageUri)
        }
    }

    private fun setUpListeners() {
        b.kycFullScreenPreviewActivityCloseButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    @SuppressLint("Range")
    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        val fileName: String?
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        fileName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        cursor?.close()
        return fileName
    }

    private fun getFileNameFromUrl(url: Any): String {
        if (url is String) {
            return url.split("/").last()
        }
        return ""
    }
}