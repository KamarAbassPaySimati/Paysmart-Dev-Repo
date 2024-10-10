package com.afrimax.paysimati.ui.paytoaffrimax

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paysimati.R
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.AfrimaxPlan
import com.afrimax.paysimati.data.model.GetAfrimaxPlansResponse
import com.afrimax.paysimati.data.model.PayAfrimaxResponse
import com.afrimax.paysimati.databinding.ActivityPayAfrimaxBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.membership.MembershipPlanModel
import com.afrimax.paysimati.ui.payment.PaymentSuccessfulActivity
import com.afrimax.paysimati.ui.utils.adapters.ChoosePlanAdapter
import com.afrimax.paysimati.ui.utils.adapters.decoration.RecyclerViewDecoration
import com.afrimax.paysimati.ui.utils.bottomsheets.SendPaymentBottomSheet
import com.afrimax.paysimati.ui.utils.bottomsheets.TotalReceiptSheet
import com.afrimax.paysimati.ui.utils.interfaces.SendPaymentInterface
import com.afrimax.paysimati.util.Constants
import com.afrimax.paysimati.util.getInitials
import com.afrimax.paysimati.util.showLogE
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PayAfrimaxActivity : BaseActivity(), SendPaymentInterface {
    private lateinit var b: ActivityPayAfrimaxBinding
    private lateinit var afrimaxId: String
    private lateinit var afrimaxName: String
    private lateinit var plansList: ArrayList<AfrimaxPlan>
    private lateinit var userName: String
    private var pageValue: Int? = 1
    private var isPaginating = false //Variable to check already pagination API is called
    private var selectedPlan: AfrimaxPlan? = null
    private var customerName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPayAfrimaxBinding.inflate(layoutInflater)
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(b.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true

        initViews()
        setUpLayout()
        setUpRecyclerView()
        setUpListeners()
    }

    private fun initViews() {
        plansList = ArrayList()
        userName = intent.getStringExtra(Constants.CUSTOMER_NAME) ?: ""
        afrimaxId = intent.getStringExtra(Constants.AFRIMAX_ID) ?: ""
        afrimaxName = intent.getStringExtra(Constants.AFRIMAX_NAME) ?: ""
        customerName = intent.getStringExtra(Constants.CUSTOMER_NAME) ?: ""
    }

    private fun setUpLayout() {
        b.payAfrimaxActivityShortNameTV.text = getInitials(afrimaxName)
        b.payAfrimaxActivityNameTV.text = afrimaxName
        b.payAfrimaxActivityAfrimaxIdTV.text = afrimaxId
    }

    private fun setUpRecyclerView() {
        val rvAdapter = ChoosePlanAdapter(this, plansList).apply {
            setOnClickListener(object : ChoosePlanAdapter.OnClickListener {

                @SuppressLint("NotifyDataSetChanged")
                override fun onClick(position: Int, plan: AfrimaxPlan, view: View) {
                    selectedPlan = plan

                    /**Check whether there is already a selected item from user.
                     * if it there then fetch its index and make it unselected */
                    val currentSelectedPlanIndex = plansList.indexOfFirst { it.isSelected == true }
                    if (currentSelectedPlanIndex != -1) plansList.find { it.isSelected == true }?.isSelected =
                        false

                    /**Make the current item as selected*/
                    plansList.find { it.id == plan.id }?.isSelected = true

                    /**Notify the adapter based on the items changed*/
                    if (currentSelectedPlanIndex != -1) b.payAfrimaxActivityRV.adapter?.notifyItemChanged(
                        currentSelectedPlanIndex
                    )
                    b.payAfrimaxActivityRV.adapter?.notifyItemChanged(position)

                    //Hide any warnings
                    b.payAfrimaxActivityPaymentErrorBox.visibility = View.GONE
                }

            })
        }

        b.payAfrimaxActivityRV.apply {
            layoutManager = LinearLayoutManager(this@PayAfrimaxActivity)
            isNestedScrollingEnabled = true
            adapter = rvAdapter
            addItemDecoration(RecyclerViewDecoration(toPx(16)))

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE && !isPaginating) {
                        isPaginating = true
                        paginatePlansApi()
                    }
                }
            })
        }

    }

    private fun setUpListeners() {

        b.payAfrimaxActivityBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        b.payAfrimaxActivityEnterAmountTV.setOnClickListener {
            onClickEnterAmountTab()
        }

        b.payAfrimaxActivityChoosePlanTV.setOnClickListener {
            onClickChoosePlanTab()
        }

        b.payAfrimaxActivityAmountBox.setOnClickListener {
            b.payAfrimaxActivityAmountET.requestFocus()
            showKeyboard(this)
        }

        b.payAfrimaxActivitySendPaymentButton.setOnClickListener {
            onClickSendPayment()
        }

        configureAmountTextChangeListener()
    }

    private fun configureAmountTextChangeListener() {
        b.payAfrimaxActivityAmountET.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                //
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //before value will be 1 if the user is clicking backspace
                if (before != 1) {
                    val currentAmount = s.toString()
                    val numList = s.toString().split(".")

                    var finalString = s.toString()

                    //Remove listener
                    b.payAfrimaxActivityAmountET.removeTextChangedListener(this)

                    //User already entered a decimal point
                    if (numList.size > 1) finalString =
                        "${numList[0].take(7)}.${numList[1].take(2)}"
                    else if (currentAmount.length > 7) finalString = currentAmount.take(7)

                    finalString = finalString.replaceFirst("^0+".toRegex(), "")
                    b.payAfrimaxActivityAmountET.setText(finalString)
                    b.payAfrimaxActivityAmountET.setSelection(finalString.length)

                    if (finalString.isEmpty()) b.payAfrimaxActivityAmountET.setGravity(Gravity.START)
                    else b.payAfrimaxActivityAmountET.setGravity(Gravity.CENTER)

                    //re register listener
                    b.payAfrimaxActivityAmountET.addTextChangedListener(this)
                } else if (s.toString().isEmpty()) {
                    b.payAfrimaxActivityAmountET.setGravity(Gravity.START)
                }

                //Hide any error warning
                b.payAfrimaxActivityPaymentErrorBox.visibility = View.GONE
            }
        })
    }

    private fun onClickSendPayment() {
        if (b.payAfrimaxActivityEnterAmountContentContainer.isVisible) validateAmount()
        else validatePlan()
    }

    private fun onClickEnterAmountTab() {
        b.payAfrimaxActivityPaymentErrorBox.visibility = View.GONE
        hideKeyboard(this@PayAfrimaxActivity)

        b.payAfrimaxActivityEnterAmountTV.background =
            ContextCompat.getDrawable(this, R.drawable.bg_pay_afrimax_tab)
        b.payAfrimaxActivityEnterAmountTV.setTextColor(
            ContextCompat.getColor(
                this@PayAfrimaxActivity, R.color.primaryColor
            )
        )

        b.payAfrimaxActivityChoosePlanTV.background = ContextCompat.getDrawable(this, R.color.white)
        b.payAfrimaxActivityChoosePlanTV.setTextColor(
            ContextCompat.getColor(
                this@PayAfrimaxActivity, R.color.neutralGrey
            )
        )

        b.payAfrimaxActivityEnterAmountContentContainer.visibility = View.VISIBLE
        b.payAfrimaxActivityRecyclerViewContainer.visibility = View.GONE
    }

    private fun onClickChoosePlanTab() {
        b.payAfrimaxActivityPaymentErrorBox.visibility = View.GONE
        hideKeyboard(this@PayAfrimaxActivity)

        /**Call this API only once when the user clicks choose plan*/
        if (pageValue == 1) getPlansApi()

        b.payAfrimaxActivityChoosePlanTV.background =
            ContextCompat.getDrawable(this, R.drawable.bg_pay_afrimax_tab)
        b.payAfrimaxActivityChoosePlanTV.setTextColor(
            ContextCompat.getColor(
                this@PayAfrimaxActivity, R.color.primaryColor
            )
        )

        b.payAfrimaxActivityEnterAmountTV.background =
            ContextCompat.getDrawable(this, R.color.white)
        b.payAfrimaxActivityEnterAmountTV.setTextColor(
            ContextCompat.getColor(
                this@PayAfrimaxActivity, R.color.neutralGrey
            )
        )

        b.payAfrimaxActivityEnterAmountContentContainer.visibility = View.GONE
        b.payAfrimaxActivityRecyclerViewContainer.visibility = View.VISIBLE
    }

    private fun validateAmount() {
        var isValid = true
        val numList = amount.split(".")
        val mainDigits = numList[0]
        val decimalDigits = if (numList.size > 1) numList[1] else ""

        when {
            amount.isEmpty() -> {
                isValid = false
                b.payAfrimaxActivityPaymentErrorBox.visibility = View.VISIBLE
                b.payAfrimaxActivityPaymentErrorTV.text = getString(R.string.please_enter_amount)
            }

            amount.toDouble() < 1.0 -> {
                isValid = false
                b.payAfrimaxActivityPaymentErrorBox.visibility = View.VISIBLE
                b.payAfrimaxActivityPaymentErrorTV.text =
                    getString(R.string.minimum_amount_is_1_mwk)
            }

            amount.toDouble() > 576000.00 -> {
                isValid = false
                b.payAfrimaxActivityPaymentErrorBox.visibility = View.VISIBLE
                b.payAfrimaxActivityPaymentErrorTV.text = getString(R.string.invalid_amount)
            }
//            mainDigits.length > 7 || decimalDigits.length > 2 -> {
//                isValid = false
//                b.payAfrimaxActivityPaymentErrorBox.visibility = View.VISIBLE
//                b.payAfrimaxActivityPaymentErrorTV.text = getString(R.string.invalid_amount)
//            }

        }

        if (isValid) {
            //Valid amount
            hideKeyboard(this@PayAfrimaxActivity)
            val payAfrimaxModel = PayAfrimaxModel(
                amount = amount,
                enteredAmount = amount,
                txnFee = "0",
                vat = "0",
                afrimaxId = afrimaxId,
                afrimaxName = afrimaxName,
                customerName = customerName,
                customerId = customerId.uppercase()
            )
            val totalReceiptSheet = TotalReceiptSheet(payAfrimaxModel)
            totalReceiptSheet.show(supportFragmentManager, TotalReceiptSheet.TAG)
        }
    }

    private val customerId: String
        get() = retrievePaymaartId()

    private fun validatePlan() {
        if (selectedPlan != null) {
            //Valid amount
            hideKeyboard(this@PayAfrimaxActivity)
            val payAfrimaxModel = PayAfrimaxModel(
                amount = selectedPlan!!.price,
                txnFee = "0",
                vat = "0",
                afrimaxId = afrimaxId,
                afrimaxName = afrimaxName,
                customerName = customerName,
                customerId = customerId.uppercase(),
                planName = selectedPlan!!.serviceName[0],
                enteredAmount = selectedPlan!!.price
            )
            val totalReceiptSheet = TotalReceiptSheet(payAfrimaxModel)
            totalReceiptSheet.show(supportFragmentManager, TotalReceiptSheet.TAG)
        } else {
            b.payAfrimaxActivityPaymentErrorBox.visibility = View.VISIBLE
            b.payAfrimaxActivityPaymentErrorTV.text = getString(R.string.please_choose_a_plan)
        }
    }

    private fun getPlansApi() {
        b.payAfrimaxActivityLoaderLottie.visibility = View.VISIBLE
        b.payAfrimaxActivityRV.visibility = View.INVISIBLE

        lifecycleScope.launch {
            val idToken = fetchIdToken()
            //We keep page number always 1
            val fetchUsersCall = ApiClient.apiService.getAfrimaxPlans(idToken, 1)

            fetchUsersCall.enqueue(object : Callback<GetAfrimaxPlansResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<GetAfrimaxPlansResponse>, response: Response<GetAfrimaxPlansResponse>
                ) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        pageValue = if (body.data.size == 10) 2 else null
                        populateSearchResults(body.data)
                    } else {
                        showToast(getString(R.string.default_error_toast))
                    }

                    isPaginating = false
                }

                override fun onFailure(call: Call<GetAfrimaxPlansResponse>, t: Throwable) {
                    //Hide global loader lottie
                    showToast(getString(R.string.default_error_toast))
                    b.payAfrimaxActivityLoaderLottie.visibility = View.GONE
                    b.payAfrimaxActivityRV.visibility = View.VISIBLE

                    isPaginating = false
                }

            })
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun populateSearchResults(data: ArrayList<AfrimaxPlan>) {

        plansList.clear()
        plansList.addAll(data)

        /** Check if the response contains 10 plans. If it does, increment pageValue.
         *  Otherwise, set it to null to prevent further API calls as there are no more plans available.*/
        if (data.size == 10) {
            plansList.add(
                AfrimaxPlan(
                    id = 0,
                    title = "",
                    serviceName = ArrayList(),
                    price = "",
                    billingDaysCount = 0,
                    speedDownload = 0,
                    speedUpload = 0,
                    updatedAt = "",
                    viewType = "loader"
                )
            )
        }
        runOnUiThread {
            b.payAfrimaxActivityRV.adapter?.notifyDataSetChanged()
            b.payAfrimaxActivityRV.scrollToPosition(0)

            //Hide global loader lottie
            b.payAfrimaxActivityLoaderLottie.visibility = View.GONE
            b.payAfrimaxActivityRV.visibility = View.VISIBLE

        }
    }

    private fun paginatePlansApi() {
        val currentPage = pageValue
        if (currentPage != null) {
            lifecycleScope.launch {
                val idToken = fetchIdToken()
                val fetchUsersCall = ApiClient.apiService.getAfrimaxPlans(idToken, currentPage)

                fetchUsersCall.enqueue(object : Callback<GetAfrimaxPlansResponse> {
                    override fun onResponse(
                        call: Call<GetAfrimaxPlansResponse>,
                        response: Response<GetAfrimaxPlansResponse>
                    ) {
                        val body = response.body()
                        if (body != null && response.isSuccessful) {
                            //Remove pagination loader and append data
                            val initialCount = plansList.size
                            plansList.removeAt(plansList.size - 1)
                            plansList.addAll(body.data)

                            /** Check if the response contains 10 plans. If it does, increment pageValue.
                             *  Otherwise, set it to null to prevent further API calls as there are no more plans available.*/
                            if (body.data.size == 10) {
                                pageValue = currentPage + 1
                                plansList.add(
                                    AfrimaxPlan(
                                        id = 0,
                                        title = "",
                                        serviceName = ArrayList(),
                                        price = "",
                                        billingDaysCount = 0,
                                        speedDownload = 0,
                                        speedUpload = 0,
                                        updatedAt = "",
                                        viewType = "loader"
                                    )
                                )
                            } else {
                                pageValue = null
                            }

                            b.payAfrimaxActivityRV.adapter?.notifyItemRangeInserted(
                                initialCount - 1, plansList.size - initialCount
                            )
                        } else {
                            showToast(getString(R.string.default_error_toast))
                        }
                        isPaginating = false
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    override fun onFailure(call: Call<GetAfrimaxPlansResponse>, t: Throwable) {
                        showToast(getString(R.string.default_error_toast))
                        isPaginating = false
                    }

                })

            }
        }
    }

    val amount: String
        get() = b.payAfrimaxActivityAmountET.text.toString()


    override fun onPaymentSuccess(successData: Any?) {
        val newPlan = selectedPlan?.let { it.serviceName[0] } ?: ""
        val intent = Intent(this, PaymentSuccessfulActivity::class.java)
        val sceneTransitions = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
        if (successData != null) {
            when (successData) {
                is PayAfrimaxResponse -> intent.putExtra(Constants.SUCCESS_PAYMENT_DATA, successData.copy(plan = newPlan) as Parcelable)
                else -> intent.putExtra(Constants.SUCCESS_PAYMENT_DATA, successData as Parcelable)
            }
        }
        startActivity(intent, sceneTransitions)
        finishAfterTransition()
    }

    override fun onPaymentFailure(message: String) {
        b.payAfrimaxActivityPaymentErrorBox.visibility = View.VISIBLE
        b.payAfrimaxActivityPaymentErrorTV.text = message
    }

    override fun onSubmitClicked(membershipPlanModel: MembershipPlanModel) {}


}