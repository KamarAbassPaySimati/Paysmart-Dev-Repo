package com.afrimax.paysimati.ui.paymerchant

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paysimati.R
import com.afrimax.paysimati.common.presentation.utils.DP
import com.afrimax.paysimati.common.presentation.utils.itemDecoration
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.MerchantListLocation
import com.afrimax.paysimati.data.model.SearchMerchantByLocation
import com.afrimax.paysimati.databinding.ActivityListMerchantByLocationBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.utils.adapters.ListMerchantByLocationAdapter
import com.afrimax.paysimati.ui.utils.bottomsheets.MerchantFilterCallback
import com.afrimax.paysimati.ui.utils.bottomsheets.MerchantFilterTradingTypesSheet
import com.afrimax.paysimati.util.showLogE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListMerchantByLocationActivity : BaseActivity(), MerchantFilterCallback {
    private lateinit var binding:ActivityListMerchantByLocationBinding
    private val mMerchantList = mutableListOf<MerchantListLocation>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var searchText: String = ""
    private var tradeType:String=""
    private var typeJob: Job? = null
    private var searchByPaymaartCredentials: Boolean = true
    private var isPaginating: Boolean = false
    private var paginationEnd: Boolean = false
    private var page: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityListMerchantByLocationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.listMerchantbyLocation)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true
        setupView()
        setupListeners()
    }
    override fun onTradingTypeSelected(type: String) {
        tradeType=type
        Toast.makeText(this, type, Toast.LENGTH_SHORT).show()
        searchForMerchantByLocation()
    }

    override fun clearTradingTypeFilters() {
        tradeType = ""

        // Clear the search text (if needed)
        searchText = ""

        // Reset the list of merchants to show all or a default list
        mMerchantList.clear() // Clear the current merchant list

        // Optionally, notify the adapter that the data has changed
        binding.listMerchantByLocationRV.adapter?.notifyDataSetChanged()

        // Optionally, update the UI to reflect the cleared filters (e.g., show all merchants)
        showToast("Filters cleared!") // Optional: Show a toast message
    }


    private fun setupView() {

        val payMerchantListAdapter = ListMerchantByLocationAdapter(mMerchantList)
        binding.listMerchantByLocationRV.apply {
            layoutManager = LinearLayoutManager(
                this@ListMerchantByLocationActivity, LinearLayoutManager.VERTICAL, false
            )
            adapter = payMerchantListAdapter
            itemDecoration(0.DP) { outRect, position, margin ->
                if (position == 0) outRect.top = margin

            }
        }
        binding.listMerchantByLocationRV.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE && !isPaginating && !paginationEnd) {
                    isPaginating = true
                    if (searchText.isNotEmpty()) {
                        //paymaartMerchantPagination()
                    } else {
                        //getRecentMerchantTransactionsPagination()
                    }
                }
            }
        })
        binding.listMerchantFilterBySearch.setOnClickListener {
            val merchantFilterSheet = MerchantFilterTradingTypesSheet()
            merchantFilterSheet.show(supportFragmentManager, MerchantFilterTradingTypesSheet.TAG)
        }

        binding.listMerchantByLocationSearchET.hint=getString(R.string.search_by_location)

        binding.listMerchantByLocationcancelIV.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }
    private fun setupListeners() {
        binding.listMerchantByLocationSearchET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed before the text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.isNullOrEmpty()){
                    binding.searchicon.visibility= View.VISIBLE
                    binding.listMerchantByLocationSearchClearIV.visibility= View.GONE

                }else{
                    binding.searchicon.visibility= View.GONE
                    binding.listMerchantByLocationSearchClearIV.visibility= View.VISIBLE
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
                        binding.listMerchantByLocationSearchClearIV.setOnClickListener{
                            text.clear()
                        }
                        searchText = text.toString()
                        if(searchByPaymaartCredentials){
                            if(searchText.isNotEmpty() && searchText.length>4){
                                searchForMerchantByLocation()
                            }
                            else{
                                mMerchantList.clear()
                                // binding.listMerchantTransactionRV.adapter?.notifyDataSetChanged()
                                if(searchText.isNotEmpty()){
                                    //    showEmptyScreen(true)
                                }
                                else{
                                    //  showEmptyScreen(false)
                                }
                            }
                        }
                    }
                }


            }

        })
    }

    private fun searchForMerchantByLocation() {
        coroutineScope.launch {
          //  showLoader()
            val idtoken = fetchIdToken()
            try{
                val searchMerchant = ApiClient.apiService.searchMerchantByLocation(idtoken,searchText,tradeType)
                searchMerchant.enqueue(object : Callback<SearchMerchantByLocation> {
                    override fun onResponse(
                        call: Call<SearchMerchantByLocation>,
                        response: Response<SearchMerchantByLocation>
                    ) {
                      //  hideLoader()
                        if(response.isSuccessful){
                            if(response.code()==204){
                                showEmptyScreen(false)
                            }
                            else{
                                val data= response.body()
                                if(data!=null){
                                    mMerchantList.clear()
                                    mMerchantList.addAll(data.MerchantListLocation)
//                                    paginationEnd = mMerchantList.size >= data.totalCount
//                                    if (!paginationEnd) {
//                                        page++
//                                    }
                                    if (mMerchantList.isEmpty()) {
                                        binding.listMerchantByLocationRV.adapter?.notifyDataSetChanged()
                                        // showEmptyScreen(false)
                                    } else {
                                        binding.listMerchantByLocationRV.adapter?.notifyDataSetChanged()
                                    }

                                }

                            }
                            // Toast.makeText(this@ListMerchantTransactionActivity, data?.message, Toast.LENGTH_LONG).show()
                        }else{
                            //  hideLoader()
                            //   showEmptyScreen(false)
                        }
                    }

                    override fun onFailure(call: Call<SearchMerchantByLocation>, t: Throwable) {
                        hideLoader()
                        showEmptyScreen(false)
                    }
                })
            }

            catch (e:Exception){
//hideLoader()
                if(searchText.isEmpty()){
                    //     showEmptyScreen(true)
                }else{
                    //  showEmptyScreen(false)
                }
                showToast(getString(R.string.default_error_toast))
            }
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
        if (searchText.isNotEmpty()) binding.listMerchantByLocationRV.visibility =
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


