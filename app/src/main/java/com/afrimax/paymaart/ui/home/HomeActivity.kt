package com.afrimax.paymaart.ui.home

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.HomeScreenData
import com.afrimax.paymaart.data.model.HomeScreenResponse
import com.afrimax.paymaart.databinding.ActivityHomeBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.kyc.KycProgressActivity
import com.afrimax.paymaart.ui.utils.adapters.HomeScreenIconAdapter
import com.afrimax.paymaart.ui.utils.bottomsheets.LogoutConfirmationSheet
import com.afrimax.paymaart.util.showLogE
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.Calendar
import java.util.Date

class HomeActivity : BaseActivity() {
    private lateinit var b: ActivityHomeBinding
    private lateinit var homeScreenIconAdapter: HomeScreenIconAdapter
    private var rejectionReasons = ArrayList<String>()
    private var dest = 0
    private var isSettingsClicked: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        b = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homeActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false
        wic.isAppearanceLightNavigationBars = true
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.highlightedLight)

        initViews()
        setUpListeners()
        setDrawerListeners()
        getHomeScreenDataApi()
    }

    private fun initViews() {

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (b.homeActivity.isDrawerVisible(GravityCompat.END)) b.homeActivity.closeDrawer(
                    GravityCompat.END
                )
                else finish()
            }
        })
        b.homeActivityProfileNameTV.text = "Godis Jacob MJOJO"
        b.homeActivityProfilePaymaartIdTV.text = getString(R.string.paymaart_id_formatted, "CMR12345678")
        b.homeActivityProfilePaymaartMemberSinceTV.text = getString(R.string.member_since_formatted, "2024")
    }

    private fun setUpListeners() {

        b.homeActivityMenuIcon.setOnClickListener {
            b.homeActivity.openDrawer(GravityCompat.END)
        }

        b.homeActivityEyeButton.setOnClickListener {
        }

        b.homeActivityPayAfrimaxButton.setOnClickListener {
        }

        b.homeActivityPayMerchantButton.setOnClickListener {
        }

        b.homeActivityPayPaymaartButton.setOnClickListener {
        }

        b.homeActivityCashInButton.setOnClickListener {
        }

        b.homeActivityCashOutButton.setOnClickListener {
            startActivity(Intent(this, KycProgressActivity::class.java))
        }
//
        b.homeActivityTransactionsBox.setOnClickListener {
            toggleTransactions(
                b.homeActivityTransactionsHiddenContainer,
                b.homeActivityTransactionsTExpandButton
            )
        }
        b.homeActivityPersonsBox.setOnClickListener{
            toggleTransactions(
                b.homeActivityPersonsHiddenContainer,
                b.homeActivityPersonsTExpandButton
            )
        }
        b.homeActivityMerchantsBox.setOnClickListener {
            toggleTransactions(
                b.homeActivityMerchantsHiddenContainer,
                b.homeActivityMerchantsTExpandButton
            )
        }
        b.homeActivityPersonsRecyclerView.layoutManager = GridLayoutManager(this, 4)
        b.homeActivityTransactionsRecyclerView.layoutManager = GridLayoutManager(this, 4)
        b.homeActivityMerchantsRecyclerView.layoutManager = GridLayoutManager(this, 4)
        val nameList = listOf(
            "John Doe MJR",
            "Jane Smith ABC",
            "Michael Lee XYZ",
            "Sarah Wilson DEF",
        )
        homeScreenIconAdapter = HomeScreenIconAdapter(nameList)
        b.homeActivityPersonsRecyclerView.adapter = homeScreenIconAdapter
        b.homeActivityTransactionsRecyclerView.adapter = homeScreenIconAdapter
        b.homeActivityMerchantsRecyclerView.adapter = homeScreenIconAdapter
    }

    private fun setDrawerListeners() {
        b.homeActivityNavView.homeDrawerKycDetailsContainer.setOnClickListener {
            dest = 1
            //Close side drawer
            b.homeActivity.closeDrawer(GravityCompat.END)

        }

        b.homeActivityNavView.homeDrawerSettingsTV.setOnClickListener {
            isSettingsClicked = !isSettingsClicked
            toggleSettings()
        }

        b.homeActivityNavView.homeDrawerUpdatePasswordContainer.setOnClickListener {
            dest = DRAWER_UPDATE_PASSWORD
            b.homeActivity.closeDrawer(GravityCompat.END)

        }

        b.homeActivityNavView.homeDrawerDeleteAccountContainer.setOnClickListener {
            dest = DRAWER_DELETE_ACCOUNT
            b.homeActivity.closeDrawer(GravityCompat.END)
        }

        b.homeActivityNavView.homeDrawerLogOutContainer.setOnClickListener {
            dest = DRAWER_LOGOUT
            b.homeActivity.closeDrawer(GravityCompat.END)
            val logoutSheet = LogoutConfirmationSheet()
            logoutSheet.show(supportFragmentManager, LogoutConfirmationSheet.TAG)
        }
        setDrawerClosedListener()
    }

    private fun setDrawerClosedListener(){
        b.homeActivity.addDrawerListener(object: DrawerLayout.DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                isSettingsClicked = false
                toggleSettings()
            }

            override fun onDrawerStateChanged(newState: Int) {}

        })
    }

    override fun onStop() {
        super.onStop()
        //Close side drawer
        b.homeActivity.closeDrawer(GravityCompat.END)
    }


    private fun toggleTransactions(
        container: LinearLayout,
        button: ImageView,

    ) {
        val transition = AutoTransition()
        transition.duration = 100

        if (container.isVisible) {
            TransitionManager.beginDelayedTransition(
                b.homeActivityBaseCV, transition
            )
            container.visibility = View.GONE
            button.animate().rotation(0f).setDuration(100)
        } else {
            TransitionManager.beginDelayedTransition(
                b.homeActivityBaseCV, transition
            )
            container.visibility = View.VISIBLE
            button.animate().rotation(180f).setDuration(100)
        }
    }

    private fun toggleSettings() {
        if (isSettingsClicked) {
            b.homeActivityNavView.apply {
                homeDrawerSettingsDiv.visibility = View.VISIBLE
                homeDrawerSettingsHiddenContainer.visibility = View.VISIBLE
            }
        } else {

            b.homeActivityNavView.apply {
                homeDrawerSettingsDiv.visibility = View.GONE
                homeDrawerSettingsHiddenContainer.visibility = View.GONE
            }
        }
    }

    private fun getHomeScreenDataApi() {
        showLoader()
        lifecycleScope.launch {
            val idToken = fetchIdToken()
            val homeScreenDataCall = ApiClient.apiService.getHomeScreenData(idToken)

            homeScreenDataCall.enqueue(object : Callback<HomeScreenResponse> {
                override fun onResponse(
                    call: Call<HomeScreenResponse>, response: Response<HomeScreenResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        runOnUiThread {
                            populateHomeScreenData(body.homeScreenData)
                        }
                    } else {
                        runOnUiThread {
                            showToast(getString(R.string.default_error_toast))
                        }
                    }
                }

                override fun onFailure(call: Call<HomeScreenResponse>, t: Throwable) {
                    runOnUiThread {
                        showToast(getString(R.string.default_error_toast))
                    }
                }

            })
        }
    }

    private fun populateHomeScreenData(homeScreenData: HomeScreenData) {
        b.homeActivityProfileNameTV.text = homeScreenData.fullName
        b.homeActivityProfilePaymaartIdTV.text = getString(R.string.paymaart_id_formatted, homeScreenData.paymaartId)
        //Convert UnixTimeStamp
        val unixTimeMillis = homeScreenData.createdAt * 1000
        val year = "${Calendar.getInstance().apply { time = Date(unixTimeMillis) }[Calendar.YEAR]}"
        b.homeActivityProfilePaymaartMemberSinceTV.text = getString(R.string.member_since_formatted, year)
        //Populate kyc details to side drawer
        when (homeScreenData.membership){
            MembershipType.GO.type -> {
                membershipType(MembershipType.GO.typeName, R.color.goMemberStrokeColor, R.drawable.go_member_bg)
            }
            MembershipType.PRIME.type -> {
                membershipType(MembershipType.GO.typeName, R.color.primeMemberStrokeColor, R.drawable.prime_member_bg)
            }
            MembershipType.PRIMEX.type -> {
                membershipType(MembershipType.GO.typeName, R.color.primeXMemberStrokeColor, R.drawable.prime_x_member_bg)
            }
        }
        val balanceInt = homeScreenData.accountBalance.toInt()
        b.homeActivityProfileBalanceTV.text = formatNumber(balanceInt)
        val kycType = homeScreenData.kycType
        val citizen = homeScreenData.citizen
        val kycStatus = homeScreenData.kycStatus
        val completedStatus = homeScreenData.completed
        rejectionReasons = homeScreenData.rejectionReasons ?: ArrayList()
        hideLoader()
    }

    private fun membershipType(text: String, fontColor: Int, background: Int){
        b.homeActivityMembershipType.text = text
        b.homeActivityMembershipType.setTextColor(ContextCompat.getColor(this, fontColor))
        b.homeActivityMembershipType.background = ContextCompat.getDrawable(this, background)
    }


    private fun showLoader() {
        b.homeActivitySV.visibility = View.GONE
        b.registrationActivityLoaderLottie.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun hideLoader() {
        b.homeActivitySV.visibility = View.VISIBLE
        b.registrationActivityLoaderLottie.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun formatNumber(number: Int): String {
        val decimalFormat = DecimalFormat("#,###")

        return if (number == 0) {
            "0.00"
        } else {
            decimalFormat.format(number)
        }
    }

    companion object {
        const val DRAWER_UPDATE_PASSWORD = 9
        const val DRAWER_DELETE_ACCOUNT = 10
        const val DRAWER_LOGOUT = 11
    }
}

enum class MembershipType(val type: String, val typeName: String){
    GO("GO", "Go Member"),
    PRIME("PRIME", "Prime Member"),
    PRIMEX("PRIMEX", "PrimeX Member")
}