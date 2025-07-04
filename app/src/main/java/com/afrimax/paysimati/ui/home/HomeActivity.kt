package com.afrimax.paysimati.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import com.afrimax.paysimati.R
import com.afrimax.paysimati.common.data.utils.safeApiCall2
import com.afrimax.paysimati.common.domain.utils.Result
import com.afrimax.paysimati.common.presentation.utils.PaymaartIdFormatter
import com.afrimax.paysimati.common.presentation.utils.VIEW_MODEL_STATE
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.HomeScreenData
import com.afrimax.paysimati.data.model.HomeScreenResponse
import com.afrimax.paysimati.data.model.IndividualTransactionHistory
import com.afrimax.paysimati.data.model.MerchantTransaction
import com.afrimax.paysimati.data.model.PayPersonTransactions
import com.afrimax.paysimati.data.model.ScanQrRequest
import com.afrimax.paysimati.data.model.WalletData
import com.afrimax.paysimati.databinding.ActivityHomeBinding
import com.afrimax.paysimati.main.ui.wallet_statement.wallet_statement.WalletStatementActivity
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.cashout.CashOutSearchActivity
import com.afrimax.paysimati.ui.chatMerchant.data.chat.ChatState
import com.afrimax.paysimati.ui.chatMerchant.ui.ChatMerchantActivity
import com.afrimax.paysimati.ui.delete.DeleteAccountActivity
import com.afrimax.paysimati.ui.membership.MembershipPlansActivity
import com.afrimax.paysimati.ui.password.UpdatePasswordPinActivity
import com.afrimax.paysimati.ui.paymerchant.ListMerchantTransactionActivity
import com.afrimax.paysimati.ui.paymerchant.PayMerchantActivity
import com.afrimax.paysimati.ui.payperson.ListPersonTransactionActivity
import com.afrimax.paysimati.ui.payperson.PersonTransactionActivity
import com.afrimax.paysimati.ui.paytoaffrimax.ValidateAfrimaxIdActivity
import com.afrimax.paysimati.ui.refundrequest.RefundRequestActivity
import com.afrimax.paysimati.ui.scanQr.CustomCaptureActivity
import com.afrimax.paysimati.ui.utils.adapters.HomeScreenIconAdapter
import com.afrimax.paysimati.ui.utils.adapters.HomeScreenPayPersonAdapter
import com.afrimax.paysimati.ui.utils.adapters.MerchantTransactionViewAdapter
import com.afrimax.paysimati.ui.utils.bottomsheets.CompleteKycSheet
import com.afrimax.paysimati.ui.utils.bottomsheets.LogoutConfirmationSheet
import com.afrimax.paysimati.ui.utils.bottomsheets.ViewKycPinSheet
import com.afrimax.paysimati.ui.utils.bottomsheets.ViewWalletPasswordSheet
import com.afrimax.paysimati.ui.utils.bottomsheets.ViewWalletPinSheet
import com.afrimax.paysimati.ui.utils.interfaces.HomeInterface
import com.afrimax.paysimati.ui.viewkyc.ViewKycDetailsActivity
import com.afrimax.paysimati.ui.viewtransactions.TransactionHistoryListActivity
import com.afrimax.paysimati.ui.viewtransactions.ViewSpecificTransactionActivity
import com.afrimax.paysimati.ui.webview.HelpCenterActivity
import com.afrimax.paysimati.ui.webview.ToolBarType
import com.afrimax.paysimati.ui.webview.WebViewActivity
import com.afrimax.paysimati.util.Constants
import com.afrimax.paysimati.util.Constants.MERCHANT_NAME
import com.afrimax.paysimati.util.Constants.PAYMAART_ID
import com.afrimax.paysimati.util.Constants.PROFILE_PICTURE
import com.afrimax.paysimati.util.Constants.STATUS_CODE
import com.afrimax.paysimati.util.Constants.STREET_NAME
import com.afrimax.paysimati.util.Constants.TILL_NUMBER
import com.afrimax.paysimati.util.getFormattedAmount
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.util.Date

class HomeActivity : BaseActivity(), HomeInterface {
    private lateinit var b: ActivityHomeBinding
    private var rejectionReasons = ArrayList<String>()
    private var dest = 0
    private var isSettingsClicked: Boolean = false
    private var publicProfile: Boolean = false
    private var profilePicUrl: String = ""
    private var mMembershipType: String = ""
    private var mKycStatus: String = ""
    private var customerName: String = ""
    private var allRecentTransactions: ArrayList<IndividualTransactionHistory> = ArrayList()
    private var allRecentPayPersonTransactions: ArrayList<PayPersonTransactions> = ArrayList()
    private var allRecentMerchants: ArrayList<MerchantTransaction> = ArrayList()
    private lateinit var notificationPermissionCheckLauncher: ActivityResultLauncher<String>
    private lateinit var checkUpdateLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var appUpdateManager: AppUpdateManager

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
        askNotificationPermission()
        checkForUpdate()
    }
//doubt
    private fun initViews() {
        appUpdateManager = AppUpdateManagerFactory.create(this)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
           //1
                if (b.homeActivity.isDrawerVisible(GravityCompat.END)) b.homeActivity.closeDrawer(
                    GravityCompat.END
                )
                else finish()
            }
        })

        notificationPermissionCheckLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

        checkUpdateLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
                if (result.resultCode != RESULT_OK) {
                    finish()
                }
            }
    }

    private fun setUpListeners() {

        b.homeActivityMenuIcon.setOnClickListener {
            b.homeActivity.openDrawer(GravityCompat.END)
        }

        b.homeActivityEyeButton.setOnClickListener {
            if (checkKycStatus()) {
                onClickEyeButton()
            }
        }

        b.homeActivityPayAfrimaxButton.setOnClickListener {
            if (checkKycStatus()) {
                val intent = Intent(this, ValidateAfrimaxIdActivity::class.java)
                intent.putExtra(Constants.CUSTOMER_NAME, customerName)
                startActivity(intent)
            }
        }

        b.homeActivityPayMerchantButton.setOnClickListener {
            if (checkKycStatus()) {
                startActivity(Intent(this,ListMerchantTransactionActivity::class.java))
            }
        }
        val scanner = registerForActivityResult<ScanOptions, ScanIntentResult>(
            ScanContract()
        ) { result ->
            if (result.contents != null) {
                val qrCodeContent = result.contents.removePrefix("paysimati://")
                lifecycleScope.launch {
                    val response = safeApiCall2 { ApiClient.apiService.scanqrPayment(
                        fetchIdToken(),
                        ScanQrRequest(
                            encryptedtill = qrCodeContent
                        )
                    )
                }

                    when(response){
                        is Result.Success -> {

                            val i = Intent(this@HomeActivity,PayMerchantActivity::class.java)
                            i.putExtra(PAYMAART_ID,response.data.paymerchant.paymaartId)
                            i.putExtra(MERCHANT_NAME,response.data.paymerchant.tradingName)
                            i.putExtra(STREET_NAME,response.data.paymerchant.tradingAddress)
                            i.putExtra(PROFILE_PICTURE,response.data.paymerchant.profilePic)
                            i.putExtra(TILL_NUMBER,response.data.paymerchant.tillNumber)
                            i.putExtra(STATUS_CODE,0)
                            startActivity(i)
                        }

                        is Result.Error -> handleError(response.error.errorMessage)
                    }
                }


            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }

        }

        b.homeActivityScanQrButton.setOnClickListener{
            if(checkKycStatus()){
                scanner.launch(
                    ScanOptions().setPrompt("").setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                        .setCaptureActivity(CustomCaptureActivity::class.java)
                )
            }
        }

        b.homeActivityPayPaymaartButton.setOnClickListener {
            if (checkKycStatus()) {
                val intent = Intent(this, MembershipPlansActivity::class.java)
                intent.putExtra(Constants.MEMBERSHIP_TYPE, mMembershipType)
                startActivity(intent)
            }
        }
        b.homeActivityPayPersonButton.setOnClickListener {
            if (checkKycStatus()) {
                startActivity(Intent(this, ListPersonTransactionActivity::class.java))
            }
        }



        b.homeActivityCashOutButton.setOnClickListener {
            if (checkKycStatus()) {
                startActivity(
                    Intent(this, CashOutSearchActivity::class.java)
                )
            }
        }
//
        b.homeActivityTransactionsBox.setOnClickListener {
            toggleTransactions(
                b.homeActivityTransactionsHiddenContainer, b.homeActivityTransactionsTExpandButton
            )
        }
        b.homeActivityPersonsBox.setOnClickListener {
            toggleTransactions(
                b.homeActivityPersonsHiddenContainer, b.homeActivityPersonsTExpandButton
            )
        }
        b.homeActivityMerchantsBox.setOnClickListener {
            toggleTransactions(
                b.homeActivityMerchantsHiddenContainer, b.homeActivityMerchantsTExpandButton
            )
        }
        b.homeActivityTransactionsSeeAllTV.setOnClickListener {
            startActivity(Intent(this, TransactionHistoryListActivity::class.java))
        }
        b.homeActivityPersonsSeeAllTV.setOnClickListener {
            startActivity(Intent(this, ListPersonTransactionActivity::class.java))
        }
        b.homeActivityMerchantsSeeAllTV.setOnClickListener {
            startActivity(Intent(this,ListMerchantTransactionActivity::class.java))
        }

        val userPaymaartId = retrievePaymaartId()
        val transactionHistoryListAdapter =
            HomeScreenIconAdapter(allRecentTransactions, userPaymaartId)
        val payPersonTransactionsAdapter =
            HomeScreenPayPersonAdapter(allRecentPayPersonTransactions)
        val merchantListAdapter =
            MerchantTransactionViewAdapter(allRecentMerchants)

        b.homeActivityPersonsRecyclerView.layoutManager = GridLayoutManager(this, 4)
        b.homeActivityTransactionsRecyclerView.layoutManager = GridLayoutManager(this, 4)
        b.homeActivityMerchantsRecyclerView.layoutManager = GridLayoutManager(this, 4)

        b.homeActivityPersonsRecyclerView.adapter = payPersonTransactionsAdapter
        b.homeActivityTransactionsRecyclerView.adapter = transactionHistoryListAdapter
        b.homeActivityMerchantsRecyclerView.adapter =merchantListAdapter

        transactionHistoryListAdapter.setOnClickListener(object :
            HomeScreenIconAdapter.OnClickListener {
            override fun onClick(transaction: IndividualTransactionHistory) {
                val intent = Intent(this@HomeActivity, ViewSpecificTransactionActivity::class.java)
                intent.putExtra(Constants.TRANSACTION_ID, transaction.transactionId)
                startActivity(intent)
            }
        })

        payPersonTransactionsAdapter.setOnClickListener(object :
            HomeScreenPayPersonAdapter.OnClickListener {
            override fun onClick(transaction: PayPersonTransactions) {
                println("mylog, ${transaction.paymaartId}")
                val intent = Intent(this@HomeActivity, PersonTransactionActivity::class.java)
                intent.putExtra(PAYMAART_ID, transaction.paymaartId)
                intent.putExtra(Constants.PHONE_NUMBER, transaction.phoneNumber)
                intent.putExtra(Constants.COUNTRY_CODE, transaction.countryCode)
                intent.putExtra(Constants.CUSTOMER_NAME, transaction.name)
                intent.putExtra(PROFILE_PICTURE, transaction.profilePic)
                startActivity(intent)
            }
        })
        merchantListAdapter.setOnClickListener(object : MerchantTransactionViewAdapter.OnClickListener {
            override fun onClick(merchantTransaction: MerchantTransaction) {
                val intent = Intent(this@HomeActivity, ChatMerchantActivity::class.java)
                intent.putExtra(VIEW_MODEL_STATE, ChatState(
                    receiverName = merchantTransaction.tradingName!!,
                    receiverId = merchantTransaction.userId!!,
                    receiverProfilePicture = merchantTransaction.profilePic,
                     tillnumber = merchantTransaction.tillNumber
                ))
                startActivity(intent)
            }
        })
    }

    fun handleError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }


    private fun onClickEyeButton() {
        val loginMode = retrieveLoginMode() ?: ""
        when (loginMode) {
            Constants.SELECTION_PIN -> {
                ViewWalletPinSheet().apply {
                    arguments = Bundle().apply {
                        putString(Constants.VIEW_WALLET_SCOPE, Constants.VIEW_WALLET_SIMPLE_SCOPE)
                    }
                    isCancelable = false
                }.show(supportFragmentManager, ViewKycPinSheet.TAG)
            }

            Constants.SELECTION_PASSWORD -> {
                ViewWalletPasswordSheet().apply {
                    arguments = Bundle().apply {
                        putString(Constants.VIEW_WALLET_SCOPE, Constants.VIEW_WALLET_SIMPLE_SCOPE)
                    }
                    isCancelable = false
                }.show(supportFragmentManager, ViewKycPinSheet.TAG)
            }
        }
    }

    private fun showBalance(data: WalletData?) {
        if (data != null) {
            b.homeActivityProfileBalanceTV.text = getFormattedAmount(data.accountBalance)
//                if (data.accountBalance == null) getString(R.string._0_00)
//                else formatNumber(data.accountBalance.toDouble())

            //Change icon
            b.homeActivityEyeButton.setBackgroundResource(R.drawable.ico_eye_slash_white)

            //Change click listener function
            b.homeActivityEyeButton.setOnClickListener {
                hideBalance()
            }
        }
    }

    private fun hideBalance() {
        b.homeActivityProfileBalanceTV.text = getString(R.string.balance_placeholder)

        //Change icon
        b.homeActivityEyeButton.setBackgroundResource(R.drawable.ico_eye_white)

        //Change click listener function
        b.homeActivityEyeButton.setOnClickListener {
            onClickEyeButton()
        }
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
        }

        b.homeActivityNavView.homeDrawerRefundRequestTV.setOnClickListener {
            dest = DRAWER_REFUND_REQUEST
            b.homeActivity.closeDrawer(GravityCompat.END)
        }

        b.homeActivityNavView.homeDrawerPrivacyPolicyTV.setOnClickListener {
            dest = DRAWER_PRIVACY_POLICY
            b.homeActivity.closeDrawer(GravityCompat.END)
        }

        b.homeActivityNavView.homeDrawerToSTV.setOnClickListener {
            dest = DRAWER_TERMS_AND_CONDITIONS
            b.homeActivity.closeDrawer(GravityCompat.END)
        }

        b.homeActivityNavView.homeDrawerAboutUsTV.setOnClickListener {
            dest = DRAWER_ABOUT_US
            b.homeActivity.closeDrawer(GravityCompat.END)
        }
        b.homeActivityNavView.homeDrawerHelpCenterTV.setOnClickListener {
            dest = HELP_CENTER
            b.homeActivity.closeDrawer(GravityCompat.END)
        }

        b.homeActivityNavView.homeDrawerWalletStatementTV.setOnClickListener {
            dest = WALLET_STATEMENT
            b.homeActivity.closeDrawer(GravityCompat.END)
        }

        b.homeActivityNavView.homeDrawerFaqTV.setOnClickListener {
            dest = FAQS
            b.homeActivity.closeDrawer(GravityCompat.END)
        }
        setDrawerClosedListener()
    }

    private fun setDrawerClosedListener() {
        b.homeActivity.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                when (dest) {
                    DRAWER_KYC_DETAILS -> {
                        val i = Intent(this@HomeActivity, ViewKycDetailsActivity::class.java)
                        i.putExtra(Constants.KYC_NAME, b.homeActivityProfileNameTV.text.toString())
                        i.putExtra(
                            Constants.KYC_PAYMAART_ID,
                            b.homeActivityProfilePaymaartIdTV.text.toString().replace(" ", "")
                        )
                        i.putExtra(
                            Constants.KYC_TYPE,
                            b.homeActivityNavView.homeDrawerKycTypeTV.text.toString()
                        )
                        i.putExtra(
                            Constants.KYC_STATUS,
                            b.homeActivityNavView.homeDrawerKycStatusTV.text.toString()
                        )
                        i.putExtra(Constants.PUBLIC_PROFILE, publicProfile)
                        i.putExtra(PROFILE_PICTURE, profilePicUrl)
                        if (b.homeActivityNavView.homeDrawerKycStatusTV.text.toString() == getString(
                                R.string.further_information_required
                            )
                        ) i.putExtra(Constants.KYC_REJECTION_REASONS, rejectionReasons)

                        startActivity(i)
                    }

                    DRAWER_UPDATE_PASSWORD -> {
                        startActivity(
                            Intent(
                                this@HomeActivity, UpdatePasswordPinActivity::class.java
                            )
                        )
                    }

                    DRAWER_DELETE_ACCOUNT -> {
                        startActivity(Intent(this@HomeActivity, DeleteAccountActivity::class.java))
                    }

                    DRAWER_LOGOUT -> {
                        val logoutSheet = LogoutConfirmationSheet()
                        logoutSheet.show(supportFragmentManager, LogoutConfirmationSheet.TAG)
                    }

                    DRAWER_REFUND_REQUEST -> {
                        startActivity(Intent(this@HomeActivity, RefundRequestActivity::class.java))
                    }

                    DRAWER_PRIVACY_POLICY -> {
                        val intent = Intent(this@HomeActivity, WebViewActivity::class.java)
                        intent.putExtra(Constants.TYPE, Constants.PRIVACY_POLICY_TYPE)
                        intent.putExtra(Constants.TOOLBAR_TYPE, ToolBarType.PRIMARY.name)
                        startActivity(intent)
                    }

                    DRAWER_TERMS_AND_CONDITIONS -> {
                        val intent = Intent(this@HomeActivity, WebViewActivity::class.java)
                        intent.putExtra(Constants.TYPE, Constants.TERMS_AND_CONDITIONS_TYPE)
                        intent.putExtra(Constants.TOOLBAR_TYPE, ToolBarType.PRIMARY.name)
                        startActivity(intent)
                    }

                    DRAWER_ABOUT_US -> {
                        val intent = Intent(this@HomeActivity, WebViewActivity::class.java)
                        intent.putExtra(Constants.TYPE, Constants.ABOUT_US_TYPE)
                        intent.putExtra(Constants.TOOLBAR_TYPE, ToolBarType.PRIMARY.name)
                        startActivity(intent)
                    }

                    HELP_CENTER -> {
                        startActivity(Intent(this@HomeActivity, HelpCenterActivity::class.java))
                    }

                    WALLET_STATEMENT -> {
                        startActivity(Intent(this@HomeActivity, WalletStatementActivity::class.java))
                    }

                    FAQS -> {
                        val intent = Intent(this@HomeActivity, WebViewActivity::class.java)
                        intent.putExtra(Constants.TYPE, Constants.FAQS_TYPE)
                        intent.putExtra(Constants.TOOLBAR_TYPE, ToolBarType.WHITE_START.name)
                        startActivity(intent)
                    }
                }
                dest = 0
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

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                notificationPermissionCheckLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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
                            populateRecyclerViews(body.transactionData)
                            populatePayPersonRecyclerView(body.payPersonData)
                            populatePayMerchantRecyclerViews(body.payMerchantData)
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

    @SuppressLint("StringFormatInvalid")
    private fun populateHomeScreenData(homeScreenData: HomeScreenData) {
        b.homeActivityProfileNameTV.text = homeScreenData.fullName
        b.homeActivityProfilePaymaartIdTV.text = getString(
            R.string.paysimati_id_formatted, PaymaartIdFormatter.formatId(homeScreenData.paymaartId)
        )
        //Convert UnixTimeStamp
        val unixTimeMillis = homeScreenData.createdAt * 1000
        val year = "${Calendar.getInstance().apply { time = Date(unixTimeMillis) }[Calendar.YEAR]}"
        b.homeActivityProfilePaymaartMemberSinceTV.text =
            getString(R.string.member_since_formatted, year)
        //Populate kyc details to side drawer
        when (homeScreenData.membership) {
            MembershipType.PRIME.type -> {
                membershipType(
                    MembershipType.PRIME.typeName,
                    R.color.primeMemberStrokeColor,
                    R.drawable.prime_member_bg
                )
            }

            MembershipType.PRIMEX.type -> {
                membershipType(
                    MembershipType.PRIMEX.typeName,
                    R.color.primeXMemberStrokeColor,
                    R.drawable.prime_x_member_bg
                )
            }

            else -> {
                membershipType(
                    MembershipType.GO.typeName, R.color.goMemberStrokeColor, R.drawable.go_member_bg
                )
            }
        }
        val kycType = homeScreenData.kycType
        val citizen = homeScreenData.citizen
        val kycStatus = homeScreenData.kycStatus
        val completedStatus = homeScreenData.completed
        mMembershipType = homeScreenData.membership
        mKycStatus = homeScreenData.kycStatus
        profilePicUrl = homeScreenData.profilePic
        publicProfile = homeScreenData.publicProfile
        rejectionReasons = homeScreenData.rejectionReasons ?: ArrayList()
        customerName = homeScreenData.fullName
        when {
            (kycStatus == null) -> {
                b.homeActivityNavView.homeDrawerKycStatusTV.text = getString(R.string.not_started)
                b.homeActivityNavView.homeDrawerKycStatusTV.setTextColor(
                    ContextCompat.getColor(
                        this, R.color.neutralGreyPrimaryText
                    )
                )
                b.homeActivityNavView.homeDrawerKycStatusTV.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_home_drawer_kyc_not_started)
            }

            (kycStatus == Constants.KYC_STATUS_IN_PROGRESS && !completedStatus) -> {
                b.homeActivityNavView.homeDrawerKycStatusTV.text = getString(R.string.in_progress)
                b.homeActivityNavView.homeDrawerKycStatusTV.setTextColor(
                    ContextCompat.getColor(
                        this, R.color.accentInformation
                    )
                )
                b.homeActivityNavView.homeDrawerKycStatusTV.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_home_drawer_kyc_in_progress)
            }

            kycStatus == Constants.KYC_STATUS_INFO_REQUIRED -> {
                b.homeActivityNavView.homeDrawerKycStatusTV.text =
                    getString(R.string.further_information_required)
                b.homeActivityNavView.homeDrawerKycStatusTV.setTextColor(
                    ContextCompat.getColor(
                        this, R.color.accentNegative
                    )
                )
                b.homeActivityNavView.homeDrawerKycStatusTV.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_home_drawer_kyc_rejected)
            }

            kycStatus == Constants.KYC_STATUS_COMPLETED -> {
                b.homeActivityNavView.homeDrawerKycStatusTV.text = getString(R.string.completed)
                b.homeActivityNavView.homeDrawerKycStatusTV.setTextColor(
                    ContextCompat.getColor(
                        this, R.color.accentPositive
                    )
                )
                b.homeActivityNavView.homeDrawerKycStatusTV.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_home_drawer_kyc_completed)
            }

            (kycStatus == Constants.KYC_STATUS_IN_PROGRESS && completedStatus) -> {
                b.homeActivityNavView.homeDrawerKycStatusTV.text = getString(R.string.in_review)
                b.homeActivityNavView.homeDrawerKycStatusTV.setTextColor(
                    ContextCompat.getColor(
                        this, R.color.accentWarning
                    )
                )
                b.homeActivityNavView.homeDrawerKycStatusTV.background =
                    ContextCompat.getDrawable(this, R.drawable.bg_home_drawer_kyc_in_review)
            }

        }

        when {
            (kycType == Constants.KYC_TYPE_MALAWI_FULL && citizen == Constants.KYC_CITIZEN_MALAWIAN) -> {
                b.homeActivityNavView.homeDrawerKycTypeTV.text =
                    getString(R.string.malawi_full_kyc_registration)
            }

            (kycType == Constants.KYC_TYPE_MALAWI_SIMPLIFIED && citizen == Constants.KYC_CITIZEN_MALAWIAN) -> {
                b.homeActivityNavView.homeDrawerKycTypeTV.text =
                    getString(R.string.malawi_simplified_kyc_registration)
            }

            //If citizen is null which means the user came to home screen after logging in
            //for first time
            citizen == null -> {
                b.homeActivityNavView.homeDrawerKycTypeTV.text =
                    getString(R.string.kyc_registration)
            }

            //Else non malawian user
            else -> {
                b.homeActivityNavView.homeDrawerKycTypeTV.text =
                    getString(R.string.non_malawi_full_kyc_registration)
            }
        }
        showMembershipBanner()
        hideLoader()
    }

    private fun populateRecyclerViews(transactionHistory: List<IndividualTransactionHistory>) {
        if (transactionHistory.isEmpty()) {
            b.homeActivityNoTransactionsTV.visibility = View.VISIBLE
            b.homeActivityTransactionsRecyclerView.visibility = View.GONE
            b.homeActivityTransactionsSeeAllTV.visibility = View.GONE
        } else {
            b.homeActivityNoTransactionsTV.visibility = View.GONE
            b.homeActivityTransactionsRecyclerView.visibility = View.VISIBLE
            if (transactionHistory.size > 4) b.homeActivityTransactionsSeeAllTV.visibility =
                View.VISIBLE
            allRecentTransactions.clear()
            allRecentTransactions.addAll(transactionHistory)
            b.homeActivityTransactionsRecyclerView.adapter?.notifyDataSetChanged()
        }
    }
    private fun populatePayMerchantRecyclerViews(transactionHistory: List<MerchantTransaction>) {
        Log.d("kk","${transactionHistory.size}")
        if (transactionHistory.isEmpty()) {
            b.homeActivityMerchantNoTransactionsTV.visibility = View.VISIBLE
            b.homeActivityMerchantsRecyclerView.visibility = View.GONE
            b.homeActivityMerchantsSeeAllTV.visibility = View.GONE
        } else {
            b.homeActivityMerchantNoTransactionsTV.visibility= View.GONE
            b.homeActivityMerchantsRecyclerView.visibility = View.VISIBLE
            if (transactionHistory.size > 4) b.homeActivityMerchantsSeeAllTV.visibility =
                View.VISIBLE
            allRecentMerchants.clear()
            allRecentMerchants.addAll(transactionHistory)
            b.homeActivityMerchantsRecyclerView.adapter?.notifyDataSetChanged()
        }
    }

    private fun populatePayPersonRecyclerView(payPersonTransactions: List<PayPersonTransactions>) {
        if (payPersonTransactions.isEmpty()) {
            b.homeActivityNoPersonTransactionsTV.visibility = View.VISIBLE
            b.homeActivityPersonsRecyclerView.visibility = View.GONE
            b.homeActivityPersonsSeeAllTV.visibility = View.GONE
        } else {
            b.homeActivityNoTransactionsTV.visibility = View.GONE
            b.homeActivityPersonsRecyclerView.visibility = View.VISIBLE
            if (payPersonTransactions.size > 4) b.homeActivityPersonsSeeAllTV.visibility =
                View.VISIBLE
            allRecentPayPersonTransactions.clear()
            allRecentPayPersonTransactions.addAll(payPersonTransactions)
            b.homeActivityPersonsRecyclerView.adapter?.notifyDataSetChanged()
        }
    }

    private fun showMembershipBanner() {
        val bannerVisibility = getBannerVisibility()
        if (mMembershipType == MembershipType.GO.type && mKycStatus == Constants.KYC_STATUS_COMPLETED && bannerVisibility) {
            val i = Intent(this, MembershipPlansActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            i.putExtra(Constants.DISPLAY_TYPE, Constants.HOME_SCREEN_BANNER)
            val options = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            //Kept some delay before the transition
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(i, options)
            }, 1000)


        }
    }

    private fun checkKycStatus(): Boolean {
        var status = false
        when {
            isKycCompleted() -> status = true
            isKycInReview() -> Toast.makeText(
                this,
                "You can access this feature once admin approves your KYC request",
                Toast.LENGTH_LONG
            ).show()

            else -> CompleteKycSheet().show(supportFragmentManager, CompleteKycSheet.TAG)
        }
        return status
    }

    private fun isKycCompleted(): Boolean {
        return b.homeActivityNavView.homeDrawerKycStatusTV.text == getString(R.string.completed)
    }

    private fun isKycInReview(): Boolean {
        return b.homeActivityNavView.homeDrawerKycStatusTV.text == getString(R.string.in_review)
    }

    private fun membershipType(text: String, fontColor: Int, background: Int) {
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

    private fun checkForUpdate() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                    AppUpdateType.IMMEDIATE
                )
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    checkUpdateLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getHomeScreenDataApi()

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                // If an in-app update is already running, resume the update.
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    checkUpdateLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }
        }
    }

    companion object {
        const val DRAWER_KYC_DETAILS = 1
        const val DRAWER_UPDATE_PASSWORD = 9
        const val DRAWER_DELETE_ACCOUNT = 10
        const val DRAWER_LOGOUT = 11
        const val DRAWER_REFUND_REQUEST = 12
        const val DRAWER_PRIVACY_POLICY = 13
        const val DRAWER_TERMS_AND_CONDITIONS = 14
        const val DRAWER_ABOUT_US = 15
        const val HELP_CENTER = 16
        const val FAQS = 17
        const val WALLET_STATEMENT = 18
    }

    override fun onClickViewBalance(viewWalletScope: String, data: WalletData?) {
        showBalance(data)
    }
}

enum class MembershipType(val type: String, val typeName: String, val displayName: String) {
    GO("GO", "Go Member", "Go"), PRIME("PRIME", "Prime Member", "Prime"), PRIMEX(
        "PRIMEX",
        "PrimeX Member",
        "PrimeX"
    )
}