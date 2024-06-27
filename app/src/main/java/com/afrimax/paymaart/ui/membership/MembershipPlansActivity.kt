package com.afrimax.paymaart.ui.membership

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.model.MembershipPlan
import com.afrimax.paymaart.databinding.ActivityMembershipPlansBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.utils.adapters.MembershipPlanAdapter

class MembershipPlansActivity : BaseActivity() {
    private lateinit var binding: ActivityMembershipPlansBinding
    private lateinit var membershipPlanAdapter: MembershipPlanAdapter
    private var planList: List<MembershipPlan> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityMembershipPlansBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        membershipPlanAdapter = MembershipPlanAdapter(planList)
        binding.membershipPlansRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MembershipPlansActivity, LinearLayoutManager.VERTICAL, false)
            adapter = membershipPlanAdapter
        }
    }
}