package com.afrimax.paysimati.ui.reportMerchant

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.afrimax.paysimati.R
import com.afrimax.paysimati.databinding.ActivityReportMerchantBinding
import com.afrimax.paysimati.ui.BaseActivity

class ReportMerchantActivity :BaseActivity() {
    private lateinit var binding : ActivityReportMerchantBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.reportMerchantProfile)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }
}