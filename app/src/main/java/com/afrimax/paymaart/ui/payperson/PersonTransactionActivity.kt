package com.afrimax.paymaart.ui.payperson

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.PersonTransactions
import com.afrimax.paymaart.data.model.Transaction
import com.afrimax.paymaart.databinding.ActivityPersonTransactionBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.utils.adapters.PaymentListAdapter
import com.afrimax.paymaart.util.Constants
import com.afrimax.paymaart.util.showLogE
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
    private var paymaartId: String = ""
    private var userName: String = ""
    private var profilePicture: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonTransactionBinding.inflate(layoutInflater)
        paymaartId = intent.getStringExtra(Constants.PAYMAART_ID) ?: ""
        userName = intent.getStringExtra(Constants.CUSTOMER_NAME) ?: ""
        profilePicture = intent.getStringExtra(Constants.PROFILE_PICTURE) ?: ""
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
        window.navigationBarColor = ContextCompat.getColor(this, R.color.primaryColor)
        setupView()
        getPersonTransactions()
    }

    private fun setupView(){
        binding.paymentListToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.paymentListReceiverName.text = userName
        binding.paymentListReceiverPaymaartId.text = paymaartId

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = false
        layoutManager.reverseLayout = true
        binding.paymentListRecyclerView.layoutManager = layoutManager
        val paymentListAdapter = PaymentListAdapter(this, transactionList)
        binding.paymentListRecyclerView.adapter = paymentListAdapter

    }

    private fun getPersonTransactions() {
        showLoader()
        scope.launch {
            val idToken = fetchIdToken()
            val personTransactionHandler = ApiClient.apiService.viewPersonTransactionHistory(
                header = idToken,
                paymaartId = paymaartId
            )

            personTransactionHandler.enqueue(object: Callback<PersonTransactions> {
                override fun onResponse(call: Call<PersonTransactions>, response: Response<PersonTransactions>) {
                    if (response.isSuccessful && response.body() != null) {
                        if ( response.body() != null ) {
                            val transactions = response.body()?.transactions
                            transactionList.clear()
                            transactionList.addAll(processMessage(transactions ?: emptyList()))
                            "Response".showLogE(transactionList)
                            binding.paymentListRecyclerView.adapter?.notifyDataSetChanged()
                        }
                    }else {
                        showEmptyScreen()
                    }
                    hideLoader()
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
        binding.paymentListSubmitButtonContainer.visibility = View.GONE
        binding.listPersonTransactionLoaderLottie.visibility = View.GONE
    }

    private fun processMessage(transactions: List<Transaction>): List<Transaction>{
        if (transactions.isEmpty()) return transactions
        val groupedMessages = mutableListOf<Transaction>()
        var uniqueDate: String = getFormattedDate(transactions[0].createdAt)
        transactions.forEachIndexed { index, transaction ->
            val currentMessageDate = getFormattedDate(transaction.createdAt)
            if (currentMessageDate != uniqueDate) {
                uniqueDate = currentMessageDate
                groupedMessages.add(transactions[index - 1].copy(showDate = true))
            }
            groupedMessages.add(transaction.copy(showDate = false))
        }
        "Response".showLogE(groupedMessages.last().showDate)
        if (!groupedMessages.last().showDate){
            groupedMessages.add(groupedMessages.last().copy(showDate = true))
            "ResponseAgain".showLogE(groupedMessages.last().copy(showDate = true))
        }
        return groupedMessages
    }

    private fun getFormattedDate(timestamp: Long?): String {
        if (timestamp == null) return ""
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }
}

