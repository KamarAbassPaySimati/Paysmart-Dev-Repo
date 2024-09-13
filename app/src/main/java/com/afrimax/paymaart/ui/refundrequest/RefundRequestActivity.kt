package com.afrimax.paymaart.ui.refundrequest

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.RefundRequest
import com.afrimax.paymaart.data.model.RefundRequestResponse
import com.afrimax.paymaart.databinding.ActivityRefundRequestBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.utils.adapters.RefundRequestAdapter
import com.afrimax.paymaart.ui.utils.adapters.decoration.RecyclerViewLastItemDecoration
import com.afrimax.paymaart.ui.utils.bottomsheets.FilterParameterBottom
import com.afrimax.paymaart.ui.utils.bottomsheets.SortParameterBottomSheet
import com.afrimax.paymaart.ui.utils.interfaces.RefundRequestSortFilterInterface
import com.afrimax.paymaart.util.RecyclerViewType
import com.afrimax.paymaart.util.toDp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RefundRequestActivity : BaseActivity(), RefundRequestSortFilterInterface {
    private lateinit var binding: ActivityRefundRequestBinding
    private lateinit var refundList: MutableList<RefundRequest>
    private var isSortSelected = false
    private var isFilterSelected = false
    private var isPaginating = false
    private var page: Int = 1
    private var sortParameter: Int? = null
    private var selectedId: Int = 4
    private var filterParameters:String? = null
    private var selectedFilterParameters: List<FilterParameter> = emptyList()
    private var paginationEnd: Boolean = false
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRefundRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = false
        wic.isAppearanceLightNavigationBars = true
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)
        setupInitialView()
        getRefundRequests()
    }

    private fun setupInitialView() {
        refundList = mutableListOf()
        val bottomPadding = this.toDp(32)
        val refundRequestAdapter = RefundRequestAdapter(this, refundList)
        binding.refundRequestRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RefundRequestActivity, LinearLayoutManager.VERTICAL, false)
            adapter = refundRequestAdapter
            addItemDecoration(RecyclerViewLastItemDecoration(bottomPadding))
        }

        binding.refundRequestRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE && !isPaginating) {
                    isPaginating = true
                    if (!paginationEnd) { getRefundRequestsPaginatedResult() }
                }
            }
        })

        binding.refundRequestActivityBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.refundRequestActivitySortButton.setOnClickListener {
            val sortParameterBottomSheet = SortParameterBottomSheet(sortParameters, selectedId)
            sortParameterBottomSheet.show(supportFragmentManager, SortParameterBottomSheet.TAG)
        }

        binding.refundRequestActivityFilterButton.setOnClickListener {
            val filterParameterBottomSheet = FilterParameterBottom(selectedFilterParameters)
            filterParameterBottomSheet.show(supportFragmentManager, FilterParameterBottom.TAG)
        }
    }

    private fun getRefundRequests() {
        showLoader()
        scope.launch {
            val idToken = fetchIdToken()
            val requestRefundHandler = ApiClient.apiService.getRefundRequests(
                header = idToken,
                page = page,
                status = filterParameters,
                time = sortParameter,
            )

            requestRefundHandler.enqueue(object : Callback<RefundRequestResponse> {
                override fun onResponse(call: Call<RefundRequestResponse>, response: Response<RefundRequestResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val refundRequest = response.body()!!
                        paginationEnd = refundRequest.nextPage == null
                        if (refundRequest.refundRequest.isNotEmpty()) {
                            refundList.addAll(response.body()!!.refundRequest)
                            if (!paginationEnd) {
                                page++
                                refundList.add(RefundRequest(viewType = RecyclerViewType.LOADER))
                            }
                            //This api is called everytime when the whole screen is refreshed.
                            binding.refundRequestRecyclerView.adapter?.notifyItemInserted(0)
                            binding.refundRequestRecyclerView.visibility = View.VISIBLE
                            binding.refundRequestNoDataFoundContainer.visibility = View.GONE
                        }else{
                            binding.refundRequestRecyclerView.visibility = View.GONE
                            binding.refundRequestNoDataFoundContainer.visibility = View.VISIBLE
                            if(isSortSelected || isFilterSelected){
                                binding.refundRequestNoDataFoundTitle.text = getString(R.string.no_data_found)
                                binding.refundRequestNoDataFoundSubtext.text = if (isSortSelected) getString(R.string.no_data_found_subtext_refund) else getString(R.string.no_data_found_subtext_refund_two)
                            }
                        }
                    }
                    hideLoader()
                }
                override fun onFailure(call: Call<RefundRequestResponse>, throwable: Throwable) {
                    hideLoader()
                    showToast(getString(R.string.default_error_toast))
                }
            })
        }
    }

    private fun getRefundRequestsPaginatedResult() {
        scope.launch {
            val idToken = fetchIdToken()
            val requestRefundHandler = ApiClient.apiService.getRefundRequests(
                header = idToken,
                page = page,
                status = filterParameters,
                time = sortParameter,
            )

            requestRefundHandler.enqueue(object : Callback<RefundRequestResponse> {
                override fun onResponse(call: Call<RefundRequestResponse>, response: Response<RefundRequestResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val refundRequest = response.body()!!
                        paginationEnd = refundRequest.nextPage == null
                        if (refundRequest.refundRequest.isNotEmpty()) {
                            val initialListSize = refundList.size
                            //remove the loader added at the beginning
                            refundList.removeAt(refundList.size - 1)
                            //add the new response to the list
                            refundList.addAll(response.body()!!.refundRequest)
                            if (!paginationEnd) {
                                //Check whether there is page
                                page++
                                refundList.add(RefundRequest(viewType = RecyclerViewType.LOADER))
                            }
                            val updatePosition = refundList.size - initialListSize
                            if (updatePosition == 0) {
                                binding.refundRequestRecyclerView.adapter?.notifyItemChanged(refundList.size - 1)
                            }else {
                                binding.refundRequestRecyclerView.adapter?.notifyItemRangeChanged(initialListSize - 1, refundList.size - initialListSize)
                            }
                        }
                    }
                    isPaginating = false
                }
                override fun onFailure(call: Call<RefundRequestResponse>, throwable: Throwable) {
                    hideLoader()
                    showToast(getString(R.string.default_error_toast))
                    isPaginating = false
                }
            })
        }
    }

    private fun showLoader() {
        binding.refundRequestLoaderLottie.visibility = View.VISIBLE
        binding.refundRequestActivityContainer.visibility = View.GONE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun hideLoader() {
        binding.refundRequestLoaderLottie.visibility = View.GONE
        binding.refundRequestActivityContainer.visibility = View.VISIBLE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun onSortParameterSelected(type: Int) {
        isSortSelected = true
        sortParameter = sortParameters[type].id
        selectedId = type
        if (refundList.isNotEmpty()) binding.refundRequestRecyclerView.scrollToPosition(0)
        refundList.clear()
        binding.refundRequestRecyclerView.adapter?.notifyDataSetChanged()
        page = 1
        isPaginating = false
        getRefundRequests()
    }

    override fun onFilterParameterSelected(types: List<FilterParameter>) {
        isFilterSelected = true
        selectedFilterParameters = types
        filterParameters = types.filter { it.isSelected }.joinToString(separator = ",") { it.name }
        if (refundList.isNotEmpty()) binding.refundRequestRecyclerView.scrollToPosition(0)
        refundList.clear()
        binding.refundRequestRecyclerView.adapter?.notifyDataSetChanged()
        page = 1
        isPaginating = false
        getRefundRequests()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}

private val sortParameters: List<SortParameter>
    get() = listOf(
        SortParameter(0, "Today"),
        SortParameter(1, "Yesterday"),
        SortParameter(7, "Last 7 days"),
        SortParameter(30, "Last 30 days"),
        SortParameter(60, "Last 60 days")
    )

data class SortParameter(
    val id: Int,
    val name: String
)

data class FilterParameter(
    val isSelected: Boolean = false,
    val name: String
)