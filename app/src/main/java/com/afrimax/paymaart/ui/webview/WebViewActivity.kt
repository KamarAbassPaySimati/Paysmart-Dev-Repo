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
import com.afrimax.paymaart.util.showLogE

class WebViewActivity : AppCompatActivity() {
    private lateinit var b: ActivityWebViewBinding
    private var animate: Boolean = false
    private var type: String = ""
    private var toolBarType: ToolBarType = ToolBarType.WHITE
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.webViewActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        var url = ""
        animate = intent.getBooleanExtra(Constants.ANIMATE, false)
        val toolBar = intent.getStringExtra(Constants.TOOLBAR_TYPE)
        toolBarType = toolBar?.let { ToolBarType.valueOf(it) } ?: ToolBarType.WHITE
        type = intent.getStringExtra(Constants.TYPE) ?: Constants.PRIVACY_POLICY_TYPE
        if (animate) {
            setAnimation()
        }

        when (toolBarType) {
            ToolBarType.PRIMARY -> {
                b.webViewActivityToolbar.visibility = View.VISIBLE
                b.webViewActivityToolbarTwo.visibility = View.GONE
                b.webViewActivityToolbarThree.visibility = View.GONE
                wic.isAppearanceLightStatusBars = false
                wic.isAppearanceLightNavigationBars = false
                window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)
                window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
            }
            ToolBarType.WHITE -> {
                b.webViewActivityToolbarTwo.visibility = View.VISIBLE
                b.webViewActivityToolbar.visibility = View.GONE
                b.webViewActivityToolbarThree.visibility = View.GONE
            }
            ToolBarType.WHITE_START -> {
                b.webViewActivityToolbarThree.visibility = View.VISIBLE
                b.webViewActivityToolbar.visibility = View.GONE
                b.webViewActivityToolbarTwo.visibility = View.GONE
            }
        }
        when (type) {
            Constants.PRIVACY_POLICY_TYPE -> {
                b.webViewActivityTitleTV.text = ContextCompat.getString(this, R.string.privacy_policy)
                b.webViewActivityToolbar.title = ContextCompat.getString(this, R.string.privacy_policy)
                b.webViewActivityToolbarThree.title = ContextCompat.getString(this, R.string.privacy_policy)
                url = Constants.PRIVACY_POLICY_URL
            }
            Constants.TERMS_AND_CONDITIONS_TYPE -> {
                b.webViewActivityTitleTV.text = ContextCompat.getString(this, R.string.terms_and_conditions_wv)
                b.webViewActivityToolbar.title = ContextCompat.getString(this, R.string.terms_of_service)
                b.webViewActivityToolbarThree.title = ContextCompat.getString(this, R.string.terms_of_service)
                url = Constants.TERMS_AND_CONDITIONS_URL
            }
            Constants.ABOUT_US_TYPE -> {
                b.webViewActivityToolbarThree.title = ContextCompat.getString(this, R.string.about_us)
                url = Constants.ABOUT_US_URL
            }
            Constants.HELP_CENTER_TYPE -> {
                b.webViewActivityToolbarThree.title = getString(R.string.empty_string)
                url = Constants.HELP_CENTER_URL
            }
            Constants.FAQS_TYPE -> {
                b.webViewActivityToolbarThree.title = getString(R.string.faqs)
                url = Constants.FAQs_URL
            }

        }

        b.webViewActivityCloseButton.setOnClickListener {
            finishAfterTransition()
        }

        b.webViewActivityToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.webViewActivityToolbarThree.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
        //This is a slide up animation.
        //Used only in Registration Activity.
        val slide = Slide()
        slide.slideEdge = Gravity.BOTTOM
        slide.setDuration(300)
        slide.setInterpolator(AccelerateInterpolator())

        window.enterTransition = slide
        window.returnTransition = slide
    }
}

enum class ToolBarType{
    PRIMARY,
    WHITE,
    WHITE_START
}