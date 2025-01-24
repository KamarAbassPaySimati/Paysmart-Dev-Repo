package com.afrimax.paysimati.ui.paymerchant

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.afrimax.paysimati.R
import com.afrimax.paysimati.databinding.ActivityListMerchantByLocationBinding
import com.afrimax.paysimati.databinding.ActivityListMerchantTransactionBinding
import com.afrimax.paysimati.ui.BaseActivity

class ListMerchantByLocationActivity : BaseActivity() {
    private lateinit var binding:ActivityListMerchantByLocationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityListMerchantByLocationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.listmerchantbylocation)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false
        wic.isAppearanceLightNavigationBars = true
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
    }
}