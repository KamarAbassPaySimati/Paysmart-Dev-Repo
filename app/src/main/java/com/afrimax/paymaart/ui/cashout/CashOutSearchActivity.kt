package com.afrimax.paymaart.ui.cashout

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
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
import com.afrimax.paymaart.data.model.IndividualSearchUserData
import com.afrimax.paymaart.data.model.SearchUsersDataResponse
import com.afrimax.paymaart.databinding.ActivityCashOutSearchBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.utils.adapters.SearchUsersAdapter
import com.afrimax.paymaart.ui.utils.adapters.decoration.RecyclerViewDecoration
import com.afrimax.paymaart.util.Constants
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Timer
import java.util.TimerTask

class CashOutSearchActivity : BaseActivity() {
    //CashOutSearchActivity === SelfCashOutSearchActivity(agent application)
    private lateinit var b: ActivityCashOutSearchBinding
    private var timer: Timer? = null
    private lateinit var userList: ArrayList<IndividualSearchUserData>
    private var search: String? = null
    private var pageValue: Int? = 1
    private var isPaginating = false //Variable to check already pagination API is called
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityCashOutSearchBinding.inflate(layoutInflater)
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
        searchUsersApi(null)
    }

    private fun initViews() {
        userList = ArrayList()
    }

    private fun setUpRecyclerView() {
        b.selfCashOutSearchActivityRV.layoutManager = LinearLayoutManager(this)
        b.selfCashOutSearchActivityRV.isNestedScrollingEnabled = false

        val adapter = SearchUsersAdapter(this, userList)
        adapter.setOnClickListener(object : SearchUsersAdapter.OnClickListener {
            override fun onClick(userData: IndividualSearchUserData) {
                startActivity(
                    Intent(
                    this@CashOutSearchActivity, CashOutActivity::class.java
                ).apply {
                    putExtra(Constants.USER_DATA, userData as Parcelable)
                })
            }
        })
        b.selfCashOutSearchActivityRV.adapter = adapter
        b.selfCashOutSearchActivityRV.addItemDecoration(RecyclerViewDecoration(this.toPx(16)))

        b.selfCashOutSearchActivityRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE && !isPaginating) {
                    isPaginating = true
                    paginateUsersApi()
                }
            }
        })

    }

    private fun setUpListeners() {

        b.selfCashOutSearchActivityBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.selfCashOutSearchActivitySearchClearIV.setOnClickListener {
            b.selfCashOutSearchActivitySearchET.text.clear()
        }

        configureSearchTextChangeListener()
    }

    private fun configureSearchTextChangeListener() {

        b.selfCashOutSearchActivitySearchET.addTextChangedListener(object : TextWatcher {
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
                if (b.selfCashOutSearchActivitySearchET.text.isEmpty()) {
                    b.selfCashOutSearchActivitySearchET.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.ico_search_neutral_grey, 0
                    )
                    b.selfCashOutSearchActivitySearchClearIV.visibility = View.GONE
                } else {
                    b.selfCashOutSearchActivitySearchET.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, 0, 0
                    )
                    b.selfCashOutSearchActivitySearchClearIV.visibility = View.VISIBLE
                }

                timer = Timer()
                timer!!.schedule(object : TimerTask() {
                    override fun run() {
                        if (b.selfCashOutSearchActivitySearchET.text.isEmpty()) searchUsersApi(null) //Show onboarded users//
                        else if (b.selfCashOutSearchActivitySearchET.text.length >= 5) searchUsersApi(
                            b.selfCashOutSearchActivitySearchET.text.toString()
                        ) //Perform search
                    }
                }, 400)

            }
        })
    }


    private fun searchUsersApi(searchString: String?) {
        //Show global loader lottie
        runOnUiThread {
            b.selfCashOutSearchActivityLoaderLottie.visibility = View.VISIBLE
            b.selfCashOutSearchActivityContentBox.visibility = View.GONE
            b.selfCashOutSearchActivityNoDataFoundContainer.visibility = View.GONE
        }


        lifecycleScope.launch {
            val idToken = fetchIdToken()
            //We keep page number always 1
            val fetchUsersCall =
                ApiClient
                    .apiService
                    .getAgentsForSelfCashOut(idToken, 1, searchString)

            fetchUsersCall.enqueue(object : Callback<SearchUsersDataResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<SearchUsersDataResponse>, response: Response<SearchUsersDataResponse>
                ) {

                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        populateSearchResults(body, searchString)
                    } else {
                        runOnUiThread { showToast(getString(R.string.default_error_toast)) }
                    }
                    isPaginating = false
                }

                override fun onFailure(call: Call<SearchUsersDataResponse>, t: Throwable) {
                    //Hide global loader lottie
                    runOnUiThread {
                        if (searchString.isNullOrEmpty()) {
                            //No previous cash out with agents
                            Glide.with(this@CashOutSearchActivity)
                                .load(R.drawable.ico_search_for_users)
                                .into(b.selfCashOutSearchActivityNoDataFoundIV)
                            b.selfCashOutSearchActivityNoDataFoundTitleTV.text =
                                getString(R.string.no_cash_out_yet)
                            b.selfCashOutSearchActivityNoDataFoundSubtextTV.text =
                                getString(R.string.no_cash_out_yet_subtext)
                        } else {
                            //No users found for the search string
                            Glide.with(this@CashOutSearchActivity)
                                .load(R.drawable.ico_no_data_found)
                                .into(b.selfCashOutSearchActivityNoDataFoundIV)
                            b.selfCashOutSearchActivityNoDataFoundTitleTV.text =
                                getString(R.string.no_data_found)
                            b.selfCashOutSearchActivityNoDataFoundSubtextTV.text =
                                getString(R.string.no_data_found_subtext)
                        }

                        b.selfCashOutSearchActivityLoaderLottie.visibility = View.GONE
                        b.selfCashOutSearchActivityContentBox.visibility = View.GONE
                        b.selfCashOutSearchActivityNoDataFoundContainer.visibility = View.VISIBLE

                        showToast(getString(R.string.default_error_toast))
                    }

                    isPaginating = false
                }

            })
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun populateSearchResults(body: SearchUsersDataResponse, searchString: String?) {

        search = searchString
        pageValue = body.nextPage
        //Set title if not searching
        if (searchString.isNullOrEmpty()) b.selfCashOutSearchActivityRecentCashOutTV.visibility =
            View.VISIBLE
        else b.selfCashOutSearchActivityRecentCashOutTV.visibility = View.GONE


        userList.clear()
        userList.addAll(body.data)
        if (body.nextPage != null) {
            //If there are users left to fetch , then also we add progress bar as the last item
            userList.add(
                IndividualSearchUserData(
                    paymaartId = "",
                    name = "",
                    countryCode = "",
                    phoneNumber = "",
                    viewType = "loader"
                )
            )
        }
        runOnUiThread {
            b.selfCashOutSearchActivityRV.adapter?.notifyDataSetChanged()
            b.selfCashOutSearchActivityRV.scrollToPosition(0)

            //Hide global loader lottie
            b.selfCashOutSearchActivityLoaderLottie.visibility = View.GONE
            if (userList.isNotEmpty()) {
                b.selfCashOutSearchActivityContentBox.visibility = View.VISIBLE
                b.selfCashOutSearchActivityNoDataFoundContainer.visibility = View.GONE
            } else {
                if (searchString.isNullOrEmpty()) {
                    //No users onboarded by user
                    Glide.with(this@CashOutSearchActivity).load(R.drawable.ico_search_for_users)
                        .into(b.selfCashOutSearchActivityNoDataFoundIV)
                    b.selfCashOutSearchActivityNoDataFoundTitleTV.text =
                        getString(R.string.no_cash_out_yet)
                    b.selfCashOutSearchActivityNoDataFoundSubtextTV.text =
                        getString(R.string.no_cash_out_yet_subtext)
                } else {
                    //No users found for the search string
                    Glide.with(this@CashOutSearchActivity).load(R.drawable.ico_no_data_found)
                        .into(b.selfCashOutSearchActivityNoDataFoundIV)
                    b.selfCashOutSearchActivityNoDataFoundTitleTV.text =
                        getString(R.string.no_data_found)
                    b.selfCashOutSearchActivityNoDataFoundSubtextTV.text =
                        getString(R.string.no_data_found_subtext)
                }
                b.selfCashOutSearchActivityNoDataFoundContainer.visibility = View.VISIBLE
                b.selfCashOutSearchActivityContentBox.visibility = View.GONE
            }
        }

    }

    private fun paginateUsersApi() {
        val currentPage = pageValue
        if (currentPage != null) {
            //pagination is happening

            lifecycleScope.launch {
                val idToken = fetchIdToken()
                val fetchUsersCall =
                    ApiClient
                    .apiService
                    .getAgentsForSelfCashOut(idToken, currentPage, search)

                fetchUsersCall.enqueue(object : Callback<SearchUsersDataResponse> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(
                        call: Call<SearchUsersDataResponse>,
                        response: Response<SearchUsersDataResponse>
                    ) {
                        val body = response.body()
                        if (body != null && response.isSuccessful) {
                            pageValue = body.nextPage
                            //Remove pagination loader and append data
                            userList.removeAt(userList.size - 1)
                            userList.addAll(body.data)
                            if (body.nextPage != null) {
                                //If there are users left to fetch , then also we add progress bar as the last item
                                userList.add(
                                    IndividualSearchUserData(
                                        paymaartId = "",
                                        name = "",
                                        countryCode = "",
                                        phoneNumber = "",
                                        viewType = "loader"
                                    )
                                )
                            }
                            runOnUiThread { b.selfCashOutSearchActivityRV.adapter?.notifyDataSetChanged() }
                        } else {
                            //Hide pagination loader and append data
                            userList.removeAt(userList.size - 1)

                            runOnUiThread {
                                showToast(getString(R.string.default_error_toast))
                                b.selfCashOutSearchActivityRV.adapter?.notifyDataSetChanged()
                            }

                        }
                        isPaginating = false
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    override fun onFailure(call: Call<SearchUsersDataResponse>, t: Throwable) {
                        //Hide pagination loader and append data
                        userList.removeAt(userList.size - 1)

                        runOnUiThread {
                            showToast(getString(R.string.default_error_toast))
                            b.selfCashOutSearchActivityRV.adapter?.notifyDataSetChanged()
                        }

                        isPaginating = false
                    }

                })

            }
        }
    }
}