package com.afrimax.paymaart.ui.viewtransactions

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.IndividualTransactionHistory
import com.afrimax.paymaart.data.model.TransactionHistoryResponse
import com.afrimax.paymaart.databinding.ActivityTransactionHistoryListBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter.Companion.AFRIMAX
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter.Companion.CASHIN
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter.Companion.CASHOUT
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter.Companion.CASH_IN
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter.Companion.CASH_OUT
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter.Companion.CASH_OUT_FAILED
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter.Companion.CASH_OUT_REQUEST
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter.Companion.G2P_PAY_IN
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter.Companion.INTEREST
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter.Companion.PAYMAART
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter.Companion.PAY_IN
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter.Companion.PAY_PERSON
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter.Companion.PAY_UNREGISTERED
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter.Companion.REFUND
import com.afrimax.paymaart.ui.utils.adapters.decoration.RecyclerViewDecoration
import com.afrimax.paymaart.ui.utils.bottomsheets.TransactionHistorySheet
import com.afrimax.paymaart.ui.utils.interfaces.TransactionHistoryInterface
import com.afrimax.paymaart.util.Constants
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Timer
import java.util.TimerTask

class TransactionHistoryListActivity : BaseActivity(), TransactionHistoryInterface {
    private lateinit var b: ActivityTransactionHistoryListBinding
    private lateinit var transactionList: MutableList<IndividualTransactionHistory>
    private var pageValue: Int? = 1
    private var search: String? = null
    private var type: String? = null
    private var time: Int? = null
    private var timer: Timer? = null
    private var isPaginating = false //Variable to check already pagination API is called

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTransactionHistoryListBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(b.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false
        wic.isAppearanceLightNavigationBars = true
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)

        initViews()
        setUpRecyclerView()
        setUpListeners()
        searchTransactionsApi(null)
    }

    private fun initViews() {
        transactionList = mutableListOf()
    }

    private fun setUpRecyclerView() {
        b.transactionHistoryActivityRV.layoutManager = LinearLayoutManager(this)
        b.transactionHistoryActivityRV.isNestedScrollingEnabled = false
        val userPaymaartId: String = retrievePaymaartId()
        val adapter = TransactionHistoryAdapter(this, transactionList, userPaymaartId)
        adapter.setOnClickListener(object : TransactionHistoryAdapter.OnClickListener {
            override fun onClick(transaction: IndividualTransactionHistory) {
                val intent = Intent(this@TransactionHistoryListActivity, ViewSpecificTransactionActivity::class.java)
                intent.putExtra(Constants.TRANSACTION_ID, transaction.transactionId)
                startActivity(intent)
            }
        })
        b.transactionHistoryActivityRV.adapter = adapter
        b.transactionHistoryActivityRV.addItemDecoration(RecyclerViewDecoration(this.toPx(16)))

        b.transactionHistoryActivityRV.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE && !isPaginating) {
                    isPaginating = true
                    paginateTransactionsApi()
                }
            }
        })
    }

    private fun setUpListeners() {

        b.transactionHistoryActivityBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.transactionHistoryActivitySearchClearIV.setOnClickListener {
            b.transactionHistoryActivitySearchET.text.clear()
        }

        b.transactionHistoryActivityFilterIV.setOnClickListener {
            TransactionHistorySheet().apply {
                arguments = Bundle().apply {
                    if (time != null)
                        putInt(Constants.TRANSACTION_FILTER_TIME, time!!)
                    else
                        putInt(Constants.TRANSACTION_FILTER_TIME, 60)
                    putString(Constants.TRANSACTION_FILTER_TYPE, type)
                }
            }.show(supportFragmentManager, TransactionHistorySheet.TAG)
        }

        configureSearchTextChangeListener()
    }

    private fun configureSearchTextChangeListener() {

        b.transactionHistoryActivitySearchET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                runOnUiThread {
                    // user is typing: reset already started timer (if existing)
                    if (timer != null) {
                        timer!!.cancel()
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                //Change icons in search field
                if (b.transactionHistoryActivitySearchET.text.isEmpty()) {
                    b.transactionHistoryActivitySearchET.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.ico_search_neutral_grey, 0
                    )
                    b.transactionHistoryActivitySearchClearIV.visibility = View.GONE
                } else {
                    b.transactionHistoryActivitySearchET.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, 0, 0
                    )
                    b.transactionHistoryActivitySearchClearIV.visibility = View.VISIBLE
                }

                timer = Timer()
                timer!!.schedule(object : TimerTask() {
                    override fun run() {
                        if (b.transactionHistoryActivitySearchET.text.isEmpty()) searchTransactionsApi(
                            null
                        )
                        else if (b.transactionHistoryActivitySearchET.text.length >= 5) searchTransactionsApi(
                            b.transactionHistoryActivitySearchET.text.toString()
                        ) //Perform search
                    }
                }, 400)

            }
        })
    }


    private fun searchTransactionsApi(searchString: String?) {
        //Show global loader lottie
        runOnUiThread {
            b.transactionHistoryActivityLoaderLottie.visibility = View.VISIBLE
            b.transactionHistoryActivityContentBox.visibility = View.GONE
            b.transactionHistoryActivityNoDataFoundContainer.visibility = View.GONE
        }


        lifecycleScope.launch {
            val idToken = fetchIdToken()
            //We keep page number always 1
            val fetchUsersCall = ApiClient.apiService.getTransactionHistory(idToken, 1, searchString, type, time)

            fetchUsersCall.enqueue(object : Callback<TransactionHistoryResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TransactionHistoryResponse>,
                    response: Response<TransactionHistoryResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        populateSearchResults(body, searchString)
                    } else {
                        runOnUiThread { showToast(getString(R.string.default_error_toast)) }
                    }
                    isPaginating = false
                }

                override fun onFailure(call: Call<TransactionHistoryResponse>, t: Throwable) {
                    //Hide global loader lottie
                    runOnUiThread {
                        if (searchString.isNullOrEmpty()) {
                            //No previous cash out with agents
                            Glide.with(this@TransactionHistoryListActivity)
                                .load(R.drawable.ico_search_for_users)
                                .into(b.transactionHistoryActivityNoDataFoundIV)
                            b.transactionHistoryActivityNoDataFoundTitleTV.text =
                                getString(R.string.no_transactions_yet)
                            b.transactionHistoryActivityNoDataFoundSubtextTV.text =
                                getString(R.string.no_transactions_subtext)
                        } else {
                            //No users found for the search string
                            Glide.with(this@TransactionHistoryListActivity)
                                .load(R.drawable.ico_no_data_found)
                                .into(b.transactionHistoryActivityNoDataFoundIV)
                            b.transactionHistoryActivityNoDataFoundTitleTV.text =
                                getString(R.string.no_data_found)
                            b.transactionHistoryActivityNoDataFoundSubtextTV.text =
                                getString(R.string.no_data_found_subtext)
                        }

                        b.transactionHistoryActivityLoaderLottie.visibility = View.GONE
                        b.transactionHistoryActivityContentBox.visibility = View.GONE
                        b.transactionHistoryActivityNoDataFoundContainer.visibility = View.VISIBLE

                        showToast(getString(R.string.default_error_toast))
                    }

                    isPaginating = false
                }

            })
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun populateSearchResults(body: TransactionHistoryResponse, searchString: String?) {

        search = searchString
        pageValue = body.nextPage
        //Set title if not searching
        if (time == null || time == 60) b.transactionHistoryActivityRecentTransactionsTV.visibility =
            View.VISIBLE
        else b.transactionHistoryActivityRecentTransactionsTV.visibility = View.GONE


        transactionList.clear()
        val newList = body.transactionHistory.filter { it.transactionType in allowedTransactionTypes }
        transactionList.addAll(newList)
        if (body.nextPage != null) {
            //If there are users left to fetch , then also we add progress bar as the last item
            transactionList.add(
                IndividualTransactionHistory(viewType = "loader")
            )
        }
        runOnUiThread {
            b.transactionHistoryActivityRV.adapter?.notifyDataSetChanged()
            b.transactionHistoryActivityRV.scrollToPosition(0)

            //Hide global loader lottie
            b.transactionHistoryActivityLoaderLottie.visibility = View.GONE
            if (transactionList.isNotEmpty()) {
                b.transactionHistoryActivityContentBox.visibility = View.VISIBLE
                b.transactionHistoryActivityNoDataFoundContainer.visibility = View.GONE
            } else {
                if (searchString.isNullOrEmpty()) {
                    //No users onboarded by user
                    Glide.with(this@TransactionHistoryListActivity)
                        .load(R.drawable.ico_search_for_users)
                        .into(b.transactionHistoryActivityNoDataFoundIV)
                    b.transactionHistoryActivityNoDataFoundTitleTV.text =
                        getString(R.string.no_transactions_yet)
                    b.transactionHistoryActivityNoDataFoundSubtextTV.text =
                        getString(R.string.no_transactions_subtext)
                } else {
                    //No users found for the search string
                    Glide.with(this@TransactionHistoryListActivity).load(R.drawable.ico_no_data_found)
                        .into(b.transactionHistoryActivityNoDataFoundIV)
                    b.transactionHistoryActivityNoDataFoundTitleTV.text =
                        getString(R.string.no_data_found)
                    b.transactionHistoryActivityNoDataFoundSubtextTV.text =
                        getString(R.string.no_data_found_subtext)
                }
                b.transactionHistoryActivityNoDataFoundContainer.visibility = View.VISIBLE
                b.transactionHistoryActivityContentBox.visibility = View.GONE
            }
        }

    }

    private fun paginateTransactionsApi() {
        //pagination is happening
        val currentPage = pageValue
        if (currentPage != null) {
            lifecycleScope.launch {
                val idToken = fetchIdToken()
                val fetchUsersCall = ApiClient.apiService.getTransactionHistory(
                    idToken, pageValue, search, type, time
                )

                fetchUsersCall.enqueue(object : Callback<TransactionHistoryResponse> {
                    override fun onResponse(
                        call: Call<TransactionHistoryResponse>,
                        response: Response<TransactionHistoryResponse>
                    ) {
                        val body = response.body()
                        if (body != null && response.isSuccessful) {
                            populatePaginatedResults(body)
                        } else {
                            runOnUiThread {
                                showToast(getString(R.string.default_error_toast))
                            }
                        }
                        isPaginating = false
                    }

                    override fun onFailure(
                        call: Call<TransactionHistoryResponse>, t: Throwable
                    ) {
                        runOnUiThread {
                            showToast(getString(R.string.default_error_toast))
                        }
                        isPaginating = false
                    }

                })

            }
        }
    }

    private fun populatePaginatedResults(body: TransactionHistoryResponse) {
        val initialCount = transactionList.size
        pageValue = body.nextPage
        //Remove pagination loader and append data
        val newList = body.transactionHistory.filter { it.transactionType in allowedTransactionTypes }
        transactionList.removeAt(transactionList.size - 1)
        transactionList.addAll(newList)
        if (body.nextPage != null) {
            //If there are users left to fetch , then also we add progress bar as the last item
            transactionList.add(
                IndividualTransactionHistory(viewType = "loader")
            )
        }
        transactionList.filter { it.transactionType in allowedTransactionTypes }
        runOnUiThread {
            val updatedPosition = transactionList.size - initialCount
            if (updatedPosition == 0) b.transactionHistoryActivityRV.adapter?.notifyItemChanged(
                transactionList.size - 1
            )
            else b.transactionHistoryActivityRV.adapter?.notifyItemRangeInserted(
                initialCount - 1, transactionList.size - initialCount
            )
        }
    }

    private val allowedTransactionTypes = setOf(
        CASH_IN,
        CASHIN,
        CASH_OUT,
        CASHOUT,
        PAY_IN,
        REFUND,
        INTEREST,
        G2P_PAY_IN,
        CASH_OUT_REQUEST,
        CASH_OUT_FAILED,
        PAYMAART,
        AFRIMAX,
        PAY_PERSON,
        PAY_UNREGISTERED
    )

    override fun clearAllFilters() {
        time = null
        type = null
        searchTransactionsApi(search)
    }

    override fun onFiltersApplied(time: Int, transactionType: String) {
        this.time = time
        this.type = transactionType
        searchTransactionsApi(search)
    }
}