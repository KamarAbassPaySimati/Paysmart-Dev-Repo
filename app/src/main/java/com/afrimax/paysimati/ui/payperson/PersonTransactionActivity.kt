package com.afrimax.paysimati.ui.payperson

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.afrimax.paysimati.BuildConfig
import com.afrimax.paysimati.R
import com.afrimax.paysimati.common.presentation.utils.PhoneNumberFormatter
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.IndividualSearchUserData
import com.afrimax.paysimati.data.model.PersonTransactions
import com.afrimax.paysimati.data.model.Transaction
import com.afrimax.paysimati.databinding.ActivityPersonTransactionBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.utils.adapters.PaymentListAdapter
import com.afrimax.paysimati.util.Constants
import com.afrimax.paysimati.util.getInitials
import com.afrimax.paysimati.util.showLogE
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PersonTransactionActivity : BaseActivity() {
    private lateinit var binding: ActivityPersonTransactionBinding
    private var transactionList: MutableList<Transaction> = mutableListOf()
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private var paymaartID: String = ""
    private var userName: String = ""
    private var profilePicture: String = ""
    private var phoneNumber: String = ""
    private var countryCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonTransactionBinding.inflate(layoutInflater)
        paymaartID = intent.getStringExtra(Constants.PAYMAART_ID) ?: ""
        userName = intent.getStringExtra(Constants.CUSTOMER_NAME) ?: ""
        profilePicture = intent.getStringExtra(Constants.PROFILE_PICTURE) ?: ""
        phoneNumber = intent.getStringExtra(Constants.PHONE_NUMBER) ?: ""
        countryCode = intent.getStringExtra(Constants.COUNTRY_CODE) ?: ""

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.paymentListActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false
        windowInsetsController.isAppearanceLightNavigationBars = false
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        setUpLayout()
        setupRecyclerView()
        getPersonTransactions()
        setUpListeners()
    }

    private fun setUpListeners() {
        binding.paymentListSubmitButton.setOnClickListener {

            val i =
                if (transactionList.isNotEmpty() || (paymaartID.isNotEmpty() && phoneNumber.isNotEmpty())) Intent(
                    this@PersonTransactionActivity, PayPersonActivity::class.java
                ) else Intent(this@PersonTransactionActivity, UnregisteredPayActivity::class.java)

            val userData = IndividualSearchUserData(
                paymaartId = paymaartID,
                phoneNumber = phoneNumber,
                viewType = "",
                countryCode = countryCode,
                name = userName,
                membership = "",
                profilePicture = profilePicture
            )
            i.putExtra(Constants.USER_DATA, userData)
            startActivity(i)
        }
    }

    private fun setupRecyclerView() {
        binding.paymentListToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = false
        layoutManager.reverseLayout = true
        binding.paymentListRecyclerView.layoutManager = layoutManager
        val paymentListAdapter = PaymentListAdapter(this, transactionList)
        binding.paymentListRecyclerView.adapter = paymentListAdapter

    }

    private fun setUpLayout() {
        binding.paymentListReceiverName.text = userName

     /*   val paymaartIdOrPhone = paymaartID.ifEmpty {
            if (phoneNumber.startsWith("+")) PhoneNumberFormatter.formatWholeNumber(phoneNumber) else PhoneNumberFormatter.formatWholeNumber(
                "+265$phoneNumber"
            )
        }*/

        val paymaartIdOrPhone = if(paymaartID.startsWith("CMR")) paymaartID else PhoneNumberFormatter.formatWholeNumber(paymaartID)
        binding.paymentListReceiverPaymaartId.text = paymaartIdOrPhone

        if (profilePicture.isNotEmpty()) {
            binding.paymentListIconNameInitials.visibility = View.GONE
            binding.paymentListIconImage.visibility = View.VISIBLE
            Glide.with(this).load(BuildConfig.CDN_BASE_URL + profilePicture).centerCrop()
                .into(binding.paymentListIconImage)
        } else {
            binding.paymentListIconImage.visibility = View.GONE
            binding.paymentListIconNameInitials.apply {
                visibility = View.VISIBLE
                text = getInitials(userName)
            }
        }
    }

    private fun getPersonTransactions() {
        showLoader()
        scope.launch {
            val idToken = fetchIdToken()

            val paymaartIdOrPhone = paymaartID.ifEmpty {
                if (phoneNumber.startsWith("+")) phoneNumber.replace(
                    " ", ""
                ) else "+265$phoneNumber".replace(" ", "")
            }

            val personTransactionHandler = ApiClient.apiService.viewPersonTransactionHistory(
                header = idToken, paymaartId = paymaartIdOrPhone
            )

            personTransactionHandler.enqueue(object : Callback<PersonTransactions> {
                override fun onResponse(
                    call: Call<PersonTransactions>, response: Response<PersonTransactions>
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        body.fullName?.let { userName = it }
                        setUpLayout()

                        val transactions = body.transactions
                        if (transactions.isEmpty()) {
                            showEmptyScreen()
                        } else {
                            hideLoader()
                            transactionList.clear()
                            transactionList.addAll(processMessage(transactions))
                            binding.paymentListRecyclerView.adapter?.notifyDataSetChanged()
                        }
                    }
                }

                override fun onFailure(call: Call<PersonTransactions>, throwable: Throwable) {
                    showEmptyScreen()
                    showToast(getString(R.string.default_error_toast))
                }

            })
        }
    }

    private fun showLoader() {
        binding.paymentListNoDataFoundContainer.visibility = View.GONE
        binding.paymentListRecyclerView.visibility = View.GONE
        binding.paymentListSubmitButtonContainer.visibility = View.GONE
        binding.listPersonTransactionLoaderLottie.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        binding.paymentListNoDataFoundContainer.visibility = View.GONE
        binding.listPersonTransactionLoaderLottie.visibility = View.GONE
        binding.paymentListSubmitButtonContainer.visibility = View.VISIBLE
        binding.paymentListRecyclerView.visibility = View.VISIBLE
    }

    private fun showEmptyScreen() {
        binding.paymentListNoDataFoundContainer.visibility = View.VISIBLE
        binding.paymentListRecyclerView.visibility = View.GONE
        binding.paymentListSubmitButtonContainer.visibility = View.VISIBLE
        binding.listPersonTransactionLoaderLottie.visibility = View.GONE
    }

    private fun processMessage(transactions: List<Transaction>): List<Transaction> {
        if (transactions.isEmpty()) return transactions
        val groupedMessages = mutableListOf<Transaction>()
        var uniqueDate: String = getFormattedDate(transactions[0].createdAt)
        transactions.forEachIndexed { index, transaction ->
            val currentMessageDate = getFormattedDate(transaction.createdAt)
            "CurrentMessage".showLogE(currentMessageDate)
            if (currentMessageDate != uniqueDate) {
                uniqueDate = currentMessageDate
                groupedMessages.add(transactions[index - 1].copy(showDate = true))
            }
            groupedMessages.add(transaction.copy(showDate = false))
        }
        if (!groupedMessages.last().showDate) {
            groupedMessages.add(groupedMessages.last().copy(showDate = true))
        }
        "GroupedMessage".showLogE(groupedMessages)
        return groupedMessages
    }

    private fun getFormattedDate(timestamp: Double?): String {
        if (timestamp == null) return ""
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp.toLong() * 1000))
    }
}

