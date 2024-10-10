package com.afrimax.paysimati.ui.webview

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.afrimax.paysimati.R
import com.afrimax.paysimati.databinding.ActivityHelpCenterBinding
import com.afrimax.paysimati.util.Constants
import com.afrimax.paysimati.util.getStringExt

class HelpCenterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHelpCenterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHelpCenterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true
        window.statusBarColor = ContextCompat.getColor(this, R.color.offWhite)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.offWhite)
        initViews()
    }

    private fun initViews() {
        binding.helpCenterActivityToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.helpCenterActivityButton.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            intent.putExtra(Constants.TYPE, Constants.HELP_CENTER_TYPE)
            intent.putExtra(Constants.TOOLBAR_TYPE, ToolBarType.WHITE_START.name)
            startActivity(intent)
        }

        val text = this.getStringExt(R.string.help_center_description)
        val email = "hello@paymaart.com"

        val spannableString = SpannableString(text)
        val emailStartIndex = text.indexOf(email)
        val emailEndIndex = emailStartIndex + email.length

        // Set ClickableSpan for the email
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                val intent = Intent(Intent.ACTION_VIEW)
                val data: Uri = Uri.parse("mailto:hello@paymaart.com")
                intent.setData(data)
                startActivity(intent)
            }
            override fun updateDrawState(textPaint: TextPaint) {
                super.updateDrawState(textPaint)
                textPaint.isUnderlineText = false
            }
        }
        val color = this.getColor(R.color.accentInformation)
        val colorSpan = ForegroundColorSpan(color)
        spannableString.setSpan(clickableSpan, emailStartIndex, emailEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(colorSpan, emailStartIndex, emailEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.helpCenterActivityDescriptionTV.movementMethod = LinkMovementMethod.getInstance()
        binding.helpCenterActivityDescriptionTV.text = spannableString
    }
}