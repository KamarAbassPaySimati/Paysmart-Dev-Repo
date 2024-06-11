package com.afrimax.paymaart.ui.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.ActivityWebViewBinding
import com.afrimax.paymaart.util.Constants

class WebViewActivity : AppCompatActivity() {
    private lateinit var b: ActivityWebViewBinding
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityWebViewBinding.inflate(layoutInflater)
        setAnimation()
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.webViewActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true

        val type = intent.getStringExtra(Constants.TYPE) ?: Constants.PRIVACY_POLICY_TYPE
        var url = ""
        if (type == Constants.PRIVACY_POLICY_TYPE) {
            b.webViewActivityTitleTV.text = ContextCompat.getString(this, R.string.privacy_policy)
            url = Constants.PRIVACY_POLICY_URL
        } else if (type == Constants.TERMS_AND_CONDITIONS_TYPE) {
            b.webViewActivityTitleTV.text =
                ContextCompat.getString(this, R.string.terms_and_conditions_wv)
            url = Constants.TERMS_AND_CONDITIONS_URL
        }

        b.webViewActivityCloseButton.setOnClickListener {
            finishAfterTransition()
        }

        b.webViewActivityWV.apply {
            loadUrl(url)
            settings.javaScriptEnabled = true
        }
        object : WebViewClient() {
            //For API < 24
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null && url.contains("mailto:") && view != null) {
                    view.context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    )
                }
                return true
            }

            //For API > 24
            override fun shouldOverrideUrlLoading(
                view: WebView?, request: WebResourceRequest?
            ): Boolean {
                if (request != null && request.url.toString().contains("mailto:") && view != null) {
                    view.context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW, request.url
                        )
                    )
                }
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                b.webViewActivityLoaderLottie.visibility = View.GONE
            }
        }.also { b.webViewActivityWV.webViewClient = it }
    }

    private fun setAnimation() {
        val slide = Slide()
        slide.slideEdge = Gravity.BOTTOM
        slide.setDuration(300)
        slide.setInterpolator(AccelerateInterpolator())

        window.enterTransition = slide
        window.returnTransition = slide
    }
}