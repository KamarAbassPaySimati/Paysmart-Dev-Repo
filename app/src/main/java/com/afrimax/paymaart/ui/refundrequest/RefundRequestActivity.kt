package com.afrimax.paymaart.ui.refundrequest

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.ActivityRefundRequestBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.utils.adapters.RefundRequestAdapter
import com.afrimax.paymaart.ui.utils.bottomsheets.SortParameterBottomSheet
import com.afrimax.paymaart.ui.utils.interfaces.RefundRequestSortFilterInterface
import com.afrimax.paymaart.util.showLogE

class RefundRequestActivity : BaseActivity(), RefundRequestSortFilterInterface {
    private lateinit var binding: ActivityRefundRequestBinding
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
    }

    private fun setupInitialView() {
        val refundRequestAdapter = RefundRequestAdapter(getSampleRefundData())
        binding.refundRequestRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RefundRequestActivity, LinearLayoutManager.VERTICAL, false)
            adapter = refundRequestAdapter
        }

        binding.refundRequestActivitySortButton.setOnClickListener {
            val sortParameterBottomSheet = SortParameterBottomSheet(sortParameter)
            sortParameterBottomSheet.show(supportFragmentManager, SortParameterBottomSheet.TAG)
        }
    }

    override fun onSortParameterSelected(type: Int) {
        "Response".showLogE(type)
    }

    override fun onFilterParameterSelected(type: String) {

    }
}

data class RefundModel(
    val profilePic: String?,
    val name: String,
    val amount: String,
    val paymaartId: String,
    val date: String,
    val status: String,
    val transactionId: String
)

fun getSampleRefundData(): List<RefundModel> {
    return listOf(
        RefundModel("https://media.vanityfair.com/photos/629e56c32347921cee05b4aa/4:3/w_1776,h_1332,c_limit/andrew-garfield-lgm.jpg", "Alice Johnson", "$120.50", "PM12345", "2024-07-20", "pending", "TXN10001"),
        RefundModel("", "Bob Smith", "$75.00", "PM12346", "2024-07-19", "pending", "TXN10002"),
        RefundModel("", "Carol White", "$200.00", "PM12347", "2024-07-18", "rejected", "TXN10003"),
        RefundModel("", "David Brown", "$50.25", "PM12348", "2024-07-17", "pending", "TXN10004"),
        RefundModel("", "Eva Green", "$99.99", "PM12349", "2024-07-16", "rejected", "TXN10005"),
        RefundModel("", "Frank Harris", "$150.75", "PM12350", "2024-07-15", "refunded", "TXN10006"),
        RefundModel("https://media.vanityfair.com/photos/629e56c32347921cee05b4aa/4:3/w_1776,h_1332,c_limit/andrew-garfield-lgm.jpg", "Grace Lee", "$60.00", "PM12351", "2024-07-14", "refunded", "TXN10007"),
        RefundModel("", "Henry Clark", "$250.30", "PM12352", "2024-07-13", "pending", "TXN10008"),
        RefundModel("", "Ivy Walker", "$80.45", "PM12353", "2024-07-12", "rejected", "TXN10009"),
        RefundModel("https://media.vanityfair.com/photos/629e56c32347921cee05b4aa/4:3/w_1776,h_1332,c_limit/andrew-garfield-lgm.jpg", "Jack Turner", "$35.00", "PM12354", "2024-07-11", "pending", "TXN10010")
    )
}

private val sortParameter: List<SortParameter>
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