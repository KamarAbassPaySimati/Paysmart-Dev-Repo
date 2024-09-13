package com.afrimax.paysimati.ui.delete

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.afrimax.paysimati.databinding.ActivityDeleteAccountBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.utils.bottomsheets.DeleteAccountOthersSheet
import com.afrimax.paysimati.ui.utils.bottomsheets.DeleteConfirmationBottomSheet
import com.afrimax.paysimati.ui.utils.bottomsheets.DeleteGuideBottomSheet
import com.afrimax.paysimati.ui.utils.interfaces.DeleteAccountInterface
import com.afrimax.paysimati.util.Constants

class DeleteAccountActivity : BaseActivity(), DeleteAccountInterface {
    private lateinit var b: ActivityDeleteAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDeleteAccountBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(b.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true

        setUpListeners()
    }

    private fun setUpListeners() {

        b.deleteAccountActivityBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.deleteAccountActivityGuideButton.setOnClickListener {
            DeleteGuideBottomSheet().show(supportFragmentManager, DeleteGuideBottomSheet.TAG)
        }

        b.deleteAccountActivityReason1CB.setOnCheckedChangeListener { _, _ ->
            toggleDeleteAccountButton()
        }

        b.deleteAccountActivityReason2CB.setOnCheckedChangeListener { _, _ ->
            toggleDeleteAccountButton()
        }

        b.deleteAccountActivityReason3CB.setOnCheckedChangeListener { _, _ ->
            toggleDeleteAccountButton()
        }

        b.deleteAccountActivityReason4CB.setOnCheckedChangeListener { _, _ ->
            toggleDeleteAccountButton()
        }

        b.deleteAccountActivityReason5CB.setOnCheckedChangeListener { _, _ ->
            toggleDeleteAccountButton()
        }

        b.deleteAccountActivityReason6CB.setOnCheckedChangeListener { _, _ ->
            toggleDeleteAccountButton()
        }

        b.deleteAccountActivityOthersCL.setOnClickListener {
            toggleOthersCheckBox()
        }

        b.deleteAccountActivityDeleteAccountButton.setOnClickListener {
            val deleteConfirmSheet = DeleteConfirmationBottomSheet()
            val b = Bundle()
            b.putStringArrayList(Constants.DELETE_REASONS, getDeleteReasons())
            deleteConfirmSheet.arguments = b
            deleteConfirmSheet.show(supportFragmentManager, DeleteConfirmationBottomSheet.TAG)
        }
    }

    private fun toggleDeleteAccountButton() {
        b.deleteAccountActivityDeleteAccountButton.isEnabled = checkSelectedFields()
    }

    private fun checkSelectedFields(): Boolean {
        if (b.deleteAccountActivityReason1CB.isChecked) return true
        if (b.deleteAccountActivityReason2CB.isChecked) return true
        if (b.deleteAccountActivityReason3CB.isChecked) return true
        if (b.deleteAccountActivityReason4CB.isChecked) return true
        if (b.deleteAccountActivityReason5CB.isChecked) return true
        if (b.deleteAccountActivityReason6CB.isChecked) return true
        return false
    }

    override fun onDeleteReasonTyped(reason: String) {
        b.deleteAccountActivityReason6SubtextTV.visibility = View.VISIBLE
        b.deleteAccountActivityReason6SubtextTV.text = reason
        b.deleteAccountActivityReason6CB.isChecked = true
    }

    private fun toggleOthersCheckBox() {
        if (b.deleteAccountActivityReason6CB.isChecked) {
            b.deleteAccountActivityReason6SubtextTV.visibility = View.GONE
            b.deleteAccountActivityReason6CB.isChecked = false
        } else {
            DeleteAccountOthersSheet().show(supportFragmentManager, DeleteAccountOthersSheet.TAG)
        }
    }

    private fun getDeleteReasons(): ArrayList<String> {
        val reasonsList = ArrayList<String>()
        if (b.deleteAccountActivityReason1CB.isChecked) reasonsList.add(b.deleteAccountActivityReason1CB.text.toString())
        if (b.deleteAccountActivityReason2CB.isChecked) reasonsList.add(b.deleteAccountActivityReason2CB.text.toString())
        if (b.deleteAccountActivityReason3CB.isChecked) reasonsList.add(b.deleteAccountActivityReason3CB.text.toString())
        if (b.deleteAccountActivityReason4CB.isChecked) reasonsList.add(b.deleteAccountActivityReason4CB.text.toString())
        if (b.deleteAccountActivityReason5CB.isChecked) reasonsList.add(b.deleteAccountActivityReason5CB.text.toString())
        if (b.deleteAccountActivityReason6CB.isChecked) reasonsList.add(b.deleteAccountActivityReason6SubtextTV.text.toString())

        return reasonsList
    }
}