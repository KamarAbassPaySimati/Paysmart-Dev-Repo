package com.afrimax.paymaart.ui.membership

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.MembershipPlan
import com.afrimax.paymaart.data.model.MembershipPlansResponse
import com.afrimax.paymaart.databinding.ActivityMembershipPlansBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.utils.adapters.MembershipPlanAdapter
import com.afrimax.paymaart.util.showLogE
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MembershipPlansActivity : BaseActivity() {
    private lateinit var binding: ActivityMembershipPlansBinding
    private lateinit var membershipPlanAdapter: MembershipPlanAdapter
    private var planList: MutableList<MembershipPlan> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityMembershipPlansBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activityMembershipPlan)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true
        setUpView()
        setUpRecyclerView()
        populateMemberShipPlan()
    }

    private fun setUpView(){
        binding.membershipPlansBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setUpRecyclerView(){
        membershipPlanAdapter = MembershipPlanAdapter(planList)
        binding.membershipPlansRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MembershipPlansActivity, LinearLayoutManager.VERTICAL, false)
            adapter = membershipPlanAdapter
        }

        setMembershipBannerVisibility(false)
    }

    private fun populateMemberShipPlan(){
        showLoader()
        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val membershipPlansResponse = ApiClient.apiService.getMembershipDetails(idToken)
            membershipPlansResponse.enqueue(object: Callback<MembershipPlansResponse>{
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<MembershipPlansResponse>, response: Response<MembershipPlansResponse>, ) {
                    hideLoader()
                    val body = response.body()
                    if (response.isSuccessful && body != null){
                        planList.addAll(body.membershipPlans)
                        binding.membershipPlansRecyclerView.adapter?.notifyDataSetChanged()
                    } else {
                        runOnUiThread {
                            showToast(getString(R.string.default_error_toast))
                        }
                    }
                }

                override fun onFailure(call: Call<MembershipPlansResponse>, throwable: Throwable) {
                    hideLoader()
                    runOnUiThread {
                        showToast(getString(R.string.default_error_toast))
                    }
                }
            })
        }
    }

    private fun showLoader() {
        binding.membershipPlansRecyclerView.visibility = View.GONE
        binding.membershipPlansHeader.visibility = View.GONE
        binding.membershipPlansFooter.visibility = View.GONE
        binding.membershipPlanLoaderLottie.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun hideLoader() {
        binding.membershipPlansRecyclerView.visibility = View.VISIBLE
        binding.membershipPlansHeader.visibility = View.VISIBLE
        binding.membershipPlansFooter.visibility = View.VISIBLE
        binding.membershipPlanLoaderLottie.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}