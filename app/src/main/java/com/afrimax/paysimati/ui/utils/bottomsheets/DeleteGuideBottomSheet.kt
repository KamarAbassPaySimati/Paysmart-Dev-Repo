package com.afrimax.paysimati.ui.utils.bottomsheets

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afrimax.paysimati.R
import com.afrimax.paysimati.databinding.DeleteGuideBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DeleteGuideBottomSheet: BottomSheetDialogFragment() {
    private lateinit var b: DeleteGuideBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        b = DeleteGuideBottomSheetBinding.inflate(inflater, container, false)

        setUpListeners()
        return b.root
    }

    private fun setUpListeners() {

        b.deleteAccountGuideSheetCloseButton.setOnClickListener {
            dismiss()
        }

        val ss = SpannableString(getString(R.string.delete_guide1))
        val clickableSpanPrivacyPolicy: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                val intent = Intent(Intent.ACTION_VIEW)
                val data: Uri = Uri.parse("mailto:hello@paymaart.com")
                intent.setData(data)
                startActivity(intent)
                dismiss()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }

        ss.setSpan(clickableSpanPrivacyPolicy, 160, 178, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        b.deleteAccountGuideSheetGuide1TV.movementMethod = LinkMovementMethod.getInstance()
        b.deleteAccountGuideSheetGuide1TV.text = ss
    }

    override fun getTheme(): Int = R.style.Base_Theme_PaymaartAgent_Bottom_Sheet_Registration_Guide

    companion object {
        const val TAG = "DeleteAccountGuideSheet"
    }
}