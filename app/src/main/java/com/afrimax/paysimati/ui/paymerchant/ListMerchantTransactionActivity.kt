package com.afrimax.paysimati.ui.paymerchant

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paysimati.R
import com.afrimax.paysimati.common.presentation.utils.DP
import com.afrimax.paysimati.common.presentation.utils.itemDecoration
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.MerchantList
import com.afrimax.paysimati.data.model.PayMerchantResponse
import com.afrimax.paysimati.databinding.ActivityListMerchantTransactionBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.utils.adapters.ListMerchantTransactionAdapter
import com.afrimax.paysimati.util.showLogE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * This activity is used to display a list of transactions for a specific merchant.
 * It uses the same layout as the ListPersonTransactionActivity.
 */
class ListMerchantTransactionActivity : BaseActivity() {
    private lateinit var binding: ActivityListMerchantTransactionBinding
    private val mMerchantList = mutableListOf<MerchantList>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var searchText: String = ""
    private var typeJob: Job? = null
    private var searchByPaymaartCredentials: Boolean = true
    private var isPaginating: Boolean = false
    private var paginationEnd: Boolean = false
    private var page: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        binding = ActivityListMerchantTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Set padding on the root view to account for the system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.listMerchantTransactionActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Set the status bar and navigation bar colors to the primary color
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        setupView()
        setupListeners()
        listMerchantTransaction()
    }


    private fun setupView() {
        binding.listMerchantLocation.setOnClickListener{
            startActivity(Intent(this@ListMerchantTransactionActivity, ListMerchantByLocationActivity::class.java))
        }

        val payMerchantListAdapter = ListMerchantTransactionAdapter(mMerchantList)
        binding.listMerchantTransactionRV.apply {
            layoutManager = LinearLayoutManager(
                this@ListMerchantTransactionActivity, LinearLayoutManager.VERTICAL, false
            )
            adapter = payMerchantListAdapter
            itemDecoration(0.DP) { outRect, position, margin ->
                if (position == 0) outRect.top = margin

            }
        }
        binding.listMerchantTransactionRV.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE && !isPaginating && !paginationEnd) {
                    isPaginating = true
                    if (searchText.isNotEmpty()) {
                         paymaartMerchantPagination()
                    } else {
                        getRecentMerchantTransactionsPagination()
                    }
                }
            }
        })

        binding.listMerchantTransactionSearchET.hint=getString(R.string.search_by_paymaart_id_trading_name)

        binding.listMerchantTransactionBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }



    }
    private fun setupListeners() {
        binding.listMerchantTransactionSearchET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed before the text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.isNullOrEmpty()){
                    binding.searchicon.visibility=View.VISIBLE
                    binding.listMerchantTransactionSearchClearIV.visibility=View.GONE

                }else{
                    binding.searchicon.visibility=View.GONE
                    binding.listMerchantTransactionSearchClearIV.visibility=View.VISIBLE
                }
                // Handle real-time changes if required
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun afterTextChanged(editable: Editable?) {
                page = 1 // Reset the page to the first page
                typeJob?.cancel() // Cancel any ongoing job (e.g., a coroutine job)
                typeJob = coroutineScope.launch {
                    delay(500)
                    editable?.let {text->
                        binding.listMerchantTransactionSearchClearIV.setOnClickListener{
                            text.clear()
                        }
                        searchText = text.toString()
                        if(searchByPaymaartCredentials){
                            if(searchText.isNotEmpty() && searchText.length>4){
                                searchForMerchantTransactions()
                            }
                            else{
                                mMerchantList.clear()
                                binding.listMerchantTransactionRV.adapter?.notifyDataSetChanged()
                                if(searchText.isNotEmpty()){
                                    showEmptyScreen(true)
                                }
                                else{
                                    showEmptyScreen(false)
                                }
                            }
                        }
                    }
                }


            }
        })
    }

    private fun searchForMerchantTransactions() {
        coroutineScope.launch {
            showLoader()
            val idtoken = fetchIdToken()
            try{
                val searchMerchant =ApiClient.apiService.searchMerchantById(idtoken, search = searchText)
                searchMerchant.enqueue(object : Callback<PayMerchantResponse> {
                    override fun onResponse(
                        call: Call<PayMerchantResponse>,
                        response: Response<PayMerchantResponse>
                    ) {
                        hideLoader()
                        if(response.isSuccessful){
                            if(response.code()==204){
                                showEmptyScreen(false)
                            }
                            else{
                                val data= response.body()
                                if(data!=null){
                                    mMerchantList.clear()
                                    mMerchantList.addAll(data.payMerchantList)
                                    paginationEnd = mMerchantList.size >= data.totalCount
                                    if (!paginationEnd) {
                                        page++
                                    }
                                    if (mMerchantList.isEmpty()) {
                                        binding.listMerchantTransactionRV.adapter?.notifyDataSetChanged()
                                        showEmptyScreen(false)
                                    } else {
                                        binding.listMerchantTransactionRV.adapter?.notifyDataSetChanged()
                                    }

                                }

                            }
                            // Toast.makeText(this@ListMerchantTransactionActivity, data?.message, Toast.LENGTH_LONG).show()
                        }else{
                            hideLoader()
                            showEmptyScreen(false)
                        }
                    }

                    override fun onFailure(call: Call<PayMerchantResponse>, t: Throwable) {
                        hideLoader()
                        showEmptyScreen(false)
                    }
                })
            }

            catch (e:Exception){
                hideLoader()
                if(searchText.isEmpty()){
                    showEmptyScreen(true)
                }else{
                    showEmptyScreen(false)
                }
                showToast(getString(R.string.default_error_toast))
            }
        }
    }
    private fun listMerchantTransaction() {
        coroutineScope.launch {
            showLoader()
            val idtoken = fetchIdToken()
            val recentTransactionHandle =
                ApiClient.apiService.getMerchantTransactionList(idtoken)
            recentTransactionHandle.enqueue(object : Callback<PayMerchantResponse> {
                override fun onResponse(
                    call: Call<PayMerchantResponse>,
                    response: Response<PayMerchantResponse>
                ) {
                    hideLoader()
                    if (response.isSuccessful) {
                        if (response.code() == 204) {
                            showEmptyScreen(true)
                        } else {

                            val data = response.body()
                            val paymerchantList = response.body()?.payMerchantList
                            if (paymerchantList.isNullOrEmpty()) {
                                showEmptyScreen(true)

                            } else {
                                data?.let {
                                    mMerchantList.clear()
                                    mMerchantList.addAll(data.payMerchantList)

                                    paginationEnd = mMerchantList.size >= data.totalCount
                                    if (!paginationEnd) {
                                        page++
                                    }
                                    hideLoader()

                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<PayMerchantResponse>, throwable: Throwable) {
                    hideLoader()
                    showToast(getString(R.string.default_error_toast))
                }

            })

        }

    }
    private fun getRecentMerchantTransactionsPagination() {
        coroutineScope.launch {
            showLoader()
            val idtoken = fetchIdToken()
            val recentTransactionHandler =
                ApiClient.apiService.getMerchantTransactionList(idtoken, page)

            recentTransactionHandler.enqueue(object : Callback<PayMerchantResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<PayMerchantResponse>,
                    response: Response<PayMerchantResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()
                        if (data?.payMerchantList.isNullOrEmpty()) {
                            showEmptyScreen(true)

                        } else {
                            data?.let {
                                val previousListSize = mMerchantList.size
                                mMerchantList.addAll(data.payMerchantList)
                                paginationEnd = mMerchantList.size >= data.totalCount
                                if (!paginationEnd) {
                                    page++
                                }
                                binding.listMerchantTransactionRV.adapter?.notifyDataSetChanged()
                                if (!paginationEnd) {
                                    page++
                                }
                                binding.listMerchantTransactionRV.adapter?.notifyItemRangeInserted(
                                    previousListSize, mMerchantList.size
                                )
                            }
                        }
                        isPaginating = false
                    }
                    hideLoader()
                }

                override fun onFailure(p0: Call<PayMerchantResponse>, p1: Throwable) {
                    hideLoader()
                    showToast(getString(R.string.default_error_toast))
                }


            })


        }
    }
    private fun paymaartMerchantPagination() {
        coroutineScope.launch {
            showLoader()
            val idtoken = fetchIdToken()
            val recentTransactionHandler =
                ApiClient.apiService.searchMerchantById(idtoken, searchText,page)

            recentTransactionHandler.enqueue(object : Callback<PayMerchantResponse> {
                override fun onResponse(
                    call: Call<PayMerchantResponse>,
                    response: Response<PayMerchantResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()
                        if (data?.payMerchantList.isNullOrEmpty()) {
                            showEmptyScreen(true)

                        } else {
                            data?.let {
                                val previousListSize = mMerchantList.size
                                mMerchantList.addAll(data.payMerchantList)
                                paginationEnd = mMerchantList.size >= data.totalCount
                                if (!paginationEnd) {
                                    page++
                                }
                                binding.listMerchantTransactionRV.adapter?.notifyDataSetChanged()
                                if (!paginationEnd) {
                                    page++
                                }
                                binding.listMerchantTransactionRV.adapter?.notifyItemRangeInserted(
                                    previousListSize, mMerchantList.size
                                )
                            }
                        }
                        isPaginating = false
                    }
                    hideLoader()
                }

                override fun onFailure(p0: Call<PayMerchantResponse>, p1: Throwable) {
                    hideLoader()
                    showToast(getString(R.string.default_error_toast))
                }


            })


        }
    }
    private fun showLoader() {
        binding.listMerchantTransactionLoaderLottie.visibility = View.VISIBLE
        binding.listMerchantTransactionNoDataFoundContainer.visibility = View.GONE
        binding.listMerchantTransactionContentBox.visibility = View.GONE
    }
    private fun hideLoader() {
        binding.listMerchantTransactionLoaderLottie.visibility = View.GONE
        binding.listMerchantTransactionNoDataFoundContainer.visibility = View.GONE
        binding.listMerchantTransactionContentBox.visibility = View.VISIBLE
        if (searchText.isNotEmpty()) binding.listMerchantTransactionRecentTransactionsTV.visibility =
            View.GONE
    }
    private fun showEmptyScreen(condition: Boolean) {
        // true - no past transactions
        //false - no data found when searched,
        binding.listMerchantTransactionLoaderLottie.visibility = View.GONE
        binding.listMerchantTransactionContentBox.visibility = View.GONE
        binding.listMerchantTransactionNoDataFoundContainer.visibility = View.VISIBLE
        binding.listMerchantTransactionNoDataFoundIV.setImageResource(if (condition) R.drawable.ico_search_for_users else R.drawable.ico_no_data_found)
        binding.listMerchantTransactionNoDataFoundTitleTV.text =
            getString(if (condition) R.string.no_transactions_yet else R.string.no_data_found)
        binding.listMerchantTransactionNoDataFoundSubtextTV.text =
            getString(if (condition) R.string.no_transactions_subtext else R.string.no_data_found_subtext)


        try {
            binding.listMerchantTransactionLoaderLottie.visibility = View.GONE
            binding.listMerchantTransactionContentBox.visibility = View.GONE
            binding.listMerchantTransactionNoDataFoundContainer.visibility = View.VISIBLE
            binding.listMerchantTransactionNoDataFoundIV.setImageResource(if (condition) R.drawable.ico_search_for_users else R.drawable.ico_no_data_found)
            binding.listMerchantTransactionNoDataFoundTitleTV.text =
                getString(if (condition) R.string.no_transactions_yet else R.string.no_data_found)
            binding.listMerchantTransactionNoDataFoundSubtextTV.text =
                getString(if (condition) R.string.no_transactions_subtext else R.string.no_data_found_subtext)
        } catch (e: Exception) {
            "Response".showLogE(e.message ?: "")
        }
    }


}







