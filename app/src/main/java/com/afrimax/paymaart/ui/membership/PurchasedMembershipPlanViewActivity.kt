package com.afrimax.paymaart.ui.membership

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.ActivityPurchasedMembershipPlanViewBinding

class PurchasedMembershipPlanViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPurchasedMembershipPlanViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchasedMembershipPlanViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.purchasedMembershipPlanView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}