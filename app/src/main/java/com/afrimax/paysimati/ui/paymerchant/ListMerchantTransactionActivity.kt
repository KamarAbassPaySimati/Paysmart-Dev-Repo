package com.afrimax.paysimati.ui.paymerchant

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
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.PayMerchantResponse
import com.afrimax.paysimati.data.model.PayPerson
import com.afrimax.paysimati.data.model.PayPersonRequestBody
import com.afrimax.paysimati.data.model.PayPersonResponse
import com.afrimax.paysimati.databinding.ActivityListMerchantTransactionBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.payperson.Contacts
import com.afrimax.paysimati.ui.payperson.ListPersonTransactionActivity
import com.afrimax.paysimati.ui.payperson.PayPersonActivity
import com.afrimax.paysimati.ui.payperson.PersonTransactionActivity
import com.afrimax.paysimati.ui.utils.adapters.ListMerchantTransactionAdapter
import com.afrimax.paysimati.ui.utils.adapters.PayPersonListAdapter
import com.afrimax.paysimati.util.Constants
import com.afrimax.paysimati.util.showLogE
import com.squareup.okhttp.Call
import com.squareup.okhttp.Callback
import com.squareup.okhttp.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * This activity is used to display a list of transactions for a specific merchant.
 * It uses the same layout as the ListPersonTransactionActivity.
 */
class ListMerchantTransactionActivity : BaseActivity() {
    private lateinit var binding: ActivityListMerchantTransactionBinding
    private val mContactsList = mutableListOf<PayPerson>()
    private var searchByPaymaartCredentials: Boolean = true
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var phoneNumberList: MutableList<Contacts> = mutableListOf()
    private var searchText: String = ""
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
    }

    private fun setupView() {
        val payMerchantListAdapter = ListMerchantTransactionAdapter(mContactsList)
        payMerchantListAdapter.setOnClickListener(object :
            ListMerchantTransactionAdapter.OnClickListener {
            override fun onClick(transaction: PayPerson) {
                val intent = Intent(
                    this@ListMerchantTransactionActivity, PersonTransactionActivity::class.java
                )
                intent.putExtra(Constants.PAYMAART_ID, transaction.paymaartId)
                intent.putExtra(Constants.CUSTOMER_NAME, transaction.fullName)
                intent.putExtra(Constants.PROFILE_PICTURE, transaction.profilePicture)
                intent.putExtra(Constants.PHONE_NUMBER, transaction.phoneNumber)
                intent.putExtra(Constants.COUNTRY_CODE, transaction.countryCode)
                startActivity(intent)
            }
        })

        binding.listMerchantTransactionRV.apply {
            layoutManager = LinearLayoutManager(
                this@ListMerchantTransactionActivity, LinearLayoutManager.VERTICAL, false
            )
            adapter = payMerchantListAdapter
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
        //here search by location
//        binding.listMerchantLocation.setOnClickListener{
//            startActivity(Intent(this@ListMerchantTransactionActivity))
//        }

//        if (searchText.isNotEmpty()) {
//            showEmptyScreen(true)
//            mContactsList.clear()  //location
//            binding.listMerchantTransactionRV.adapter?.notifyDataSetChanged()
//        }
//        searchByPaymaartCredentials = !searchByPaymaartCredentials
//        binding.listMerchantTransactionSearchET.text.clear()
//

        binding.listMerchantTransactionBackButton.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun setupListeners(){
        binding.listMerchantTransactionSearchET.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                TODO("Not yet implemented")
            }

            override fun afterTextChanged(editable: Editable) {
              page =1
                coroutineScope.launch {
                    delay(500)
                    editable?.let {
                        text->
                        //loation clear if needed
                        searchText = text.toString()
                        if(searchByPaymaartCredentials){
                            if(searchText.isNotEmpty() && searchText.length>5){
                               searchForMerchant()
                            }
                            else{
                                binding.listMerchantTransactionRV.adapter?.notifyDataSetChanged()
//                                if(searchText.isNotEmpty()){
//                                    showEmptyScreen(true)
//                                }
//                                else {
//                                    showEmptyScreen(false)
//
//                                }

                            }
                        }
                        else{
                            if(searchText.isEmpty()){
                                ListMerchantTransaction()
                            }
                            else{
                                binding.listMerchantTransactionRV.adapter?.notifyDataSetChanged()
//                                if(searchText.isEmpty()){
//                                    showEmptyScreen(true)
//                                }
//                                else{
//                                    showEmptyScreen(false)
//                                }
                            }

                        }
                    }

                }
            }

        })
    }

    private fun ListMerchantTransaction() {
        coroutineScope.launch {
            showLoader()
            val idtoken = fetchIdToken()
            try{
                val recentTransactionHandler = ApiClient.apiService.getMerchantTransactionList(
                    header = idtoken
                )

//                recentTransactionHandler.enqueue(object :Callback<PayMerchantResponse>{
//                    override fun onResponse(
//                        p0: retrofit2.Call<PayPersonResponse>,
//                        p1: retrofit2.Response<PayPersonResponse>
//                    ) {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun onFailure(p0: retrofit2.Call<PayPersonResponse>, p1: Throwable) {
//                        TODO("Not yet implemented")
//                    }
//
//                })

            }
            catch (e:Exception){
            }
        }

    }

    private fun searchForMerchant() {
        TODO("Not yet implemented")

    }


    private fun getRecentMerchantTransactionsPagination() {

//        coroutineScope.launch {
//            val idtoken = fetchIdToken()
//            try {
//                val response = ApiClient.apiService.getMerchantTransactionList(header = idtoken)
//
//                if (response.isSuccessful && response.body() != null) {
//                    val data = response.body()
//                    if (data != null) {
//                        val previousListSize = mContactsList.size
//                        mContactsList.addAll(data.payMerchantList)
//                        paginationEnd = mContactsList.size >= data.totalCount
//                        if (!paginationEnd) {
//                            page++
//                        }
//                        binding.listMerchantTransactionRV.adapter?.notifyItemRangeInserted(
//                            previousListSize, mContactsList.size
//                        )
//                    }
//                    isPaginating = false
//                } else {
//                    showEmptyScreen(false)
//                }
//            } catch (e: Exception) {
//            }
//        }
   }

    private fun paymaartMerchantPagination() {
        coroutineScope.launch {
            val idtoken = fetchIdToken()
            try{
                val response =
                    if (searchByPaymaartCredentials) ApiClient.apiService.searchUsersByPaymaartCredentials(
                        header = idtoken, search = searchText, page = page
                    )
                    else ApiClient.apiService.searchUsersByPhoneCredentials(
                        header = idtoken, body = PayPersonRequestBody(phoneNumberList)
                    )
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()
                    if (data != null) {
                        val previousListSize = mContactsList.size

                        mContactsList.addAll(data.payPersonList)

                        paginationEnd = mContactsList.size >= data.totalCount
                        if (!paginationEnd) {
                            page++
                        }

                        binding.listMerchantTransactionRV.adapter?.notifyItemRangeInserted(
                            previousListSize, mContactsList.size
                        )
                    }
                    isPaginating = false
                } else {
                  showEmptyScreen(false)
                }
            } catch (e: Exception) {
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
        binding.listMerchantTransactionNoDataFoundTitleTV .text =
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