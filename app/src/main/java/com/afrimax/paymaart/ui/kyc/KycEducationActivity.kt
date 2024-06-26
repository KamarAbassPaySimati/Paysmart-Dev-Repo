package com.afrimax.paymaart.ui.kyc

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.ApiClient
import com.afrimax.paymaart.data.model.GetInstitutesResponse
import com.afrimax.paymaart.databinding.ActivityKycEducationBinding
import com.afrimax.paymaart.ui.BaseActivity
import com.afrimax.paymaart.ui.utils.adapters.KycInstituteAdapter
import com.afrimax.paymaart.ui.utils.adapters.KycInstituteDecoration
import com.afrimax.paymaart.ui.utils.bottomsheets.KycInstitutionOthersSheet
import com.afrimax.paymaart.ui.utils.interfaces.KycOccupationEducationInterface
import com.afrimax.paymaart.util.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KycEducationActivity : BaseActivity(), KycOccupationEducationInterface {
    private lateinit var b: ActivityKycEducationBinding
    private lateinit var instituteList: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityKycEducationBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setAnimation()
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.kycOccupationEducationActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true

        initViews()
        setUpListeners()
        retrieveInstitutesApi("")
    }

    private fun initViews() {
        instituteList = ArrayList()

        setUpRecyclerView()
    }

    private fun setUpListeners() {
        b.kycOccupationEducationActivityCloseButtonIV.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        configureEditTextFocusListener()
        configureEditTextChangeListener()
    }

    private fun setUpRecyclerView() {
        b.kycOccupationEducationActivityRV.layoutManager = LinearLayoutManager(this)
        b.kycOccupationEducationActivityRV.isNestedScrollingEnabled = false

        val adapter = KycInstituteAdapter(this, instituteList)
        adapter.setOnClickListener(object : KycInstituteAdapter.OnClickListener {
            override fun onClick(position: Int, instituteName: String, view: View) {

                if (instituteName.contains(getString(R.string.other), ignoreCase = true)) {
                    val institutionOthersSheet = KycInstitutionOthersSheet()
                    institutionOthersSheet.show(
                        supportFragmentManager, KycInstitutionOthersSheet.TAG
                    )
                } else {
                    val intent = Intent()
                    intent.putExtra(Constants.KYC_OCCUPATION_EDUCATION_TYPE, instituteName)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        })
        b.kycOccupationEducationActivityRV.adapter = adapter
        b.kycOccupationEducationActivityRV.addItemDecoration(KycInstituteDecoration(toPx(16)))

    }

    private fun configureEditTextFocusListener() {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        b.kycOccupationEducationActivityET.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) b.kycOccupationEducationActivityET.background = focusDrawable
                else b.kycOccupationEducationActivityET.background = notInFocusDrawable

            }
    }

    private fun configureEditTextChangeListener() {
        b.kycOccupationEducationActivityET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                retrieveInstitutesApi(b.kycOccupationEducationActivityET.text.toString())
            }
        })
    }

    private fun retrieveInstitutesApi(searchParam: String) {
        val instituteListCall = ApiClient.apiService.getInstitutes(searchParam)
        instituteListCall.enqueue(object : Callback<GetInstitutesResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<GetInstitutesResponse>, response: Response<GetInstitutesResponse>
            ) {
                val body = response.body()
                if (body != null && response.isSuccessful) {
                    instituteList.clear()
                    instituteList.addAll(body.institutionNames)
                    b.kycOccupationEducationActivityRV.adapter?.notifyDataSetChanged()

                    //Show views
                    b.kycOccupationEducationActivityRV.visibility = View.VISIBLE
                    b.kycOccupationEducationActivityLoaderLottie.visibility = View.GONE
                } else {
                    showToast(getString(R.string.default_error_toast))
                }
            }

            override fun onFailure(call: Call<GetInstitutesResponse>, t: Throwable) {
                showToast(getString(R.string.default_error_toast))
            }

        })
    }

    private fun setAnimation() {
        val slide = Slide()
        slide.slideEdge = Gravity.BOTTOM
        slide.setDuration(300)
        slide.setInterpolator(AccelerateInterpolator())

        window.enterTransition = slide
        window.returnTransition = slide
    }

    override fun onInstitutionTyped(instituteName: String) {
        val intent = Intent()
        intent.putExtra(Constants.KYC_OCCUPATION_EDUCATION_TYPE, instituteName)
        //This field is required to call the API
        intent.putExtra(Constants.KYC_OCCUPATION_EDUCATION_IS_CUSTOM, true)
        setResult(RESULT_OK, intent)
        finish()
    }
}