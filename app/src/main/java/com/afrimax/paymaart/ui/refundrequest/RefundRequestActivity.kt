package com.afrimax.paymaart.ui.refundrequest

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.RefundRequest
import com.afrimax.paymaart.data.model.RefundRequestResponse
import com.afrimax.paymaart.databinding.ActivityRefundRequestBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.utils.adapters.RefundRequestAdapter
import com.afrimax.paymaart.ui.utils.bottomsheets.FilterParameterBottom
import com.afrimax.paymaart.ui.utils.bottomsheets.SortParameterBottomSheet
import com.afrimax.paymaart.ui.utils.interfaces.RefundRequestSortFilterInterface
import com.afrimax.paymaart.util.showLogE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
    private var selectedId: Int = -1
    private var filterParameters:String? = null
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
        val refundRequestAdapter = RefundRequestAdapter(refundList)
        binding.refundRequestRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RefundRequestActivity, LinearLayoutManager.VERTICAL, false)
            adapter = refundRequestAdapter
        }

        binding.refundRequestActivitySortButton.setOnClickListener {
            val sortParameterBottomSheet = SortParameterBottomSheet(sortParameters, selectedId)
            sortParameterBottomSheet.show(supportFragmentManager, SortParameterBottomSheet.TAG)
        }

        binding.refundRequestActivityFilterButton.setOnClickListener {
            val filterParameterBottomSheet = FilterParameterBottom()
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
                        if (refundRequest.refundRequest.isNotEmpty()) {
                            refundList.addAll(response.body()!!.refundRequest)
                            binding.refundRequestRecyclerView.adapter?.notifyItemChanged(0, refundList.size)
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
        if (refundList.isNotEmpty()) binding.refundRequestRecyclerView.smoothScrollToPosition(0)
        refundList.clear()
        getRefundRequests()
    }

    override fun onFilterParameterSelected(types: List<String>) {
        isFilterSelected = true
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
    val id: Int,
    val name: String
)