package com.afrimax.paysimati.ui.paymerchant

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paysimati.R
import com.afrimax.paysimati.common.presentation.utils.DP
import com.afrimax.paysimati.common.presentation.utils.VIEW_MODEL_STATE
import com.afrimax.paysimati.common.presentation.utils.itemDecoration
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.MerchantListLocation
import com.afrimax.paysimati.data.model.SearchMerchantByLocation
import com.afrimax.paysimati.data.model.chat.ChatState
import com.afrimax.paysimati.databinding.ActivityListMerchantByLocationBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.chatMerchant.ui.ChatMerchantActivity
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
    private var tradeType:String="All"
    private var tradeTypetext:String=""
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
        showEmptyScreen(true)
        setupListeners()
    }
    private fun setupView() {
        val payMerchantListAdapter = ListMerchantByLocationAdapter(mMerchantList)


        payMerchantListAdapter.setOnClickListener(object: ListMerchantByLocationAdapter.OnClickListener{
            override fun onClick(transaction: MerchantListLocation) {
                val intent = Intent(this@ListMerchantByLocationActivity, ChatMerchantActivity::class.java)
                intent.putExtra(
                    VIEW_MODEL_STATE, ChatState(
                    receiverName = transaction.merchantName!!,
                    receiverId = transaction.paymaartId!!,
                    receiverProfilePicture = transaction.profilePic,
                    receiverAddress = transaction.streetName!!,
                    tillnumber = transaction.tillNumber!!

                    )
                )
                startActivity(intent)

            }

        })
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
                     paymaartMerchantLocationPagination()
                    } else {

                    }
                }
            }
        })
        binding.listMerchantFilterBySearch.setOnClickListener {
            val merchantFilterSheet = MerchantFilterTradingTypesSheet()
            val bundle = Bundle().apply {
                putString("tradeType", tradeType)
            }
            merchantFilterSheet.arguments = bundle
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
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.isNullOrEmpty()){
                    binding.searchicon.visibility= View.VISIBLE
                    binding.listMerchantByLocationSearchClearIV.visibility= View.GONE

                }else{
                    binding.searchicon.visibility= View.GONE
                    binding.listMerchantByLocationSearchClearIV.visibility= View.VISIBLE
                }
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
                               binding.listMerchantByLocationRV.adapter?.notifyDataSetChanged()
                                if(searchText.isNotEmpty() && searchText.length>4){
                                       showEmptyScreen(false)
                                }
                                else{
                                     showEmptyScreen(true)
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
          showLoader()
            val idtoken = fetchIdToken()
            try{
                val searchMerchant = ApiClient.apiService.searchMerchantByLocation(idtoken,searchText,tradeTypetext)
                searchMerchant.enqueue(object : Callback<SearchMerchantByLocation> {
                    override fun onResponse(
                        call: Call<SearchMerchantByLocation>,
                        response: Response<SearchMerchantByLocation>
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
                                    mMerchantList.addAll(data.MerchantListLocation)
                                    paginationEnd = mMerchantList.size >= data.totalCount
                                    if (!paginationEnd) {
                                        page++
                                    }
                                    if (mMerchantList.isEmpty()) {
                                        binding.listMerchantByLocationRV.adapter?.notifyDataSetChanged()
                                        showEmptyScreen(false)
                                    } else {
                                        binding.listMerchantByLocationRV.adapter?.notifyDataSetChanged()
                                    }

                                }
                            }
                        }else{
                             hideLoader()
                              showEmptyScreen(false)
                        }
                    }
                    override fun onFailure(call: Call<SearchMerchantByLocation>, t: Throwable) {
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
    private fun paymaartMerchantLocationPagination() {
        coroutineScope.launch {
            showLoader()
            val idtoken = fetchIdToken()
            val searchMerchanthandler = ApiClient.apiService.searchMerchantByLocation(idtoken,searchText,tradeTypetext)

            searchMerchanthandler.enqueue(object : Callback<SearchMerchantByLocation> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<SearchMerchantByLocation>,
                    response: Response<SearchMerchantByLocation>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()
                        if (data?.MerchantListLocation.isNullOrEmpty()) {
                            showEmptyScreen(true)

                        } else {
                            data?.let {
                                val previousListSize = mMerchantList.size
                                mMerchantList.addAll(data.MerchantListLocation)
                                paginationEnd = mMerchantList.size >= data.totalCount
                                if (!paginationEnd) {
                                    page++
                                }
                                binding.listMerchantByLocationRV.adapter?.notifyDataSetChanged()
                                if (!paginationEnd) {
                                    page++
                                }
                                binding.listMerchantByLocationRV.adapter?.notifyItemRangeInserted(
                                    previousListSize, mMerchantList.size
                                )
                            }
                        }
                        isPaginating = false
                    }
                    hideLoader()
                }

                override fun onFailure(p0: Call<SearchMerchantByLocation>, p1: Throwable) {
                    hideLoader()
                    showToast(getString(R.string.default_error_toast))
                }


            })


        }
    }
    override fun onTradingTypeSelected(type: String) {
        tradeTypetext=type
        tradeType=type
        if(tradeTypetext == "All"){
            tradeTypetext=""
        }
        searchForMerchantByLocation()
    }
    override fun clearTradingTypeFilters(type: String) {
        tradeTypetext=""
        tradeType = type
        searchForMerchantByLocation()
    }
    private fun showLoader() {
        binding.listMerchantByLocationLoaderLottie.visibility = View.VISIBLE
        binding.listMerchantByLocationNoDataFoundContainer.visibility = View.GONE
        binding.listMerchantByLocationContentBox.visibility = View.GONE
    }
    private fun hideLoader() {
        binding.listMerchantByLocationLoaderLottie.visibility = View.GONE
        binding.listMerchantByLocationNoDataFoundContainer.visibility = View.GONE
        binding.listMerchantByLocationContentBox.visibility = View.VISIBLE
    }
    private fun showEmptyScreen(condition: Boolean) {
        // true - search for merchant
        //false - no data found when searched,
        binding.listMerchantByLocationLoaderLottie.visibility = View.GONE
        binding.listMerchantByLocationContentBox.visibility = View.GONE
        binding.listMerchantByLocationNoDataFoundContainer.visibility = View.VISIBLE
        binding.listMerchantByLocationNoDataFoundIV.setImageResource(if (condition) R.drawable.ico_search_for_merchant else R.drawable.ico_no_data_found)
        binding.listMerchantByLocationNoDataFoundTitleTV.text =
            getString(if (condition) R.string.search_for_merchant else R.string.no_data_found)
        binding.listMerchantByLocationNoDataFoundSubtextTV.text =
            getString(if (condition) R.string.search_for_merchant_subtext else R.string.no_data_found_subtext)

        try {
            binding.listMerchantByLocationLoaderLottie.visibility = View.GONE
            binding.listMerchantByLocationContentBox.visibility = View.GONE
            binding.listMerchantByLocationNoDataFoundContainer.visibility = View.VISIBLE
            binding.listMerchantByLocationNoDataFoundIV.setImageResource(if (condition) R.drawable.ico_search_for_merchant else R.drawable.ico_no_data_found)
            binding.listMerchantByLocationNoDataFoundTitleTV.text =
                getString(if (condition) R.string.search_for_merchant else R.string.no_data_found)
            binding.listMerchantByLocationNoDataFoundSubtextTV.text =
                getString(if (condition) R.string.search_for_merchant_subtext else R.string.no_data_found_subtext)
        } catch (e: Exception) {
            "Response".showLogE(e.message ?: "")
        }
    }

}


