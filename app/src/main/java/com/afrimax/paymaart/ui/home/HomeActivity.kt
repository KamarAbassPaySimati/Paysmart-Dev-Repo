package com.afrimax.paymaart.ui.home
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.databinding.ActivityHomeBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.kyc.KycProgressActivity
import com.afrimax.paymaart.ui.utils.adapters.HomeScreenIconAdapter
import com.afrimax.paymaart.util.getStringExt
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class HomeActivity : BaseActivity() {
    private lateinit var b: ActivityHomeBinding
    private lateinit var homeScreenIconAdapter: HomeScreenIconAdapter
    private var rejectionReasons = ArrayList<String>()
    private var dest = 0
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

    }

    private fun initViews() {

//        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                if (b.homeActivity.isDrawerVisible(GravityCompat.END)) b.homeActivity.closeDrawer(
//                    GravityCompat.END
//                )
//                else finish()
//            }
//        })
        b.homeActivityProfileNameTV.text = "Godis Jacob MJOJO"
        b.homeActivityProfilePaymaartIdTV.text = getString(R.string.paymaart_id_formatted, "CMR12345678")
        b.homeActivityProfilePaymaartMemberSinceTV.text = getString(R.string.member_since_formatted, "2024")
    }

    private fun setUpListeners() {

//        b.homeActivityMenuIcon.setOnClickListener {
//            openDrawer()
//        }
//
//        b.homeActivityEyeButton.setOnClickListener {
//            if (checkKycStatus()) {
//                //
//            }
//        }
//
//        b.homeActivityPayAfrimaxButton.setOnClickListener {
//            if (checkKycStatus()) {
//                //
//            }
//        }
//
//        b.homeActivityPayMerchantButton.setOnClickListener {
//            if (checkKycStatus()) {
//                //
//            }
//        }
//
//        b.homeActivityPayPaymaartButton.setOnClickListener {
//            if (checkKycStatus()) {
//                //
//            }
//        }
//
//        b.homeActivityCashInButton.setOnClickListener {
//            if (checkKycStatus()) {
//                //
//            }
//        }
//
//        b.homeActivityCashOutButton.setOnClickListener {
//            if (checkKycStatus()) {
//                //
//            }
//        }
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
        b.homeActivityPersonsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        b.homeActivityTransactionsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        b.homeActivityMerchantsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
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

//        setUpSideDrawerListeners()
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
}