package com.afrimax.paysimati.ui.paymerchant

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.afrimax.paysimati.BuildConfig
import com.afrimax.paysimati.R
import com.afrimax.paysimati.common.presentation.utils.PhoneNumberFormatter
import com.afrimax.paysimati.common.presentation.utils.parseTillNumber
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.MerchantProfileResponse
import com.afrimax.paysimati.data.model.PayMerchantResponse
import com.afrimax.paysimati.databinding.ActivityMerchantProfileBinding
import com.afrimax.paysimati.databinding.ActivityPayMerchantBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.util.Constants
import com.afrimax.paysimati.util.getInitials
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MerchantProfile : BaseActivity() {
    private var payMaartId:String=""
    private var MerchantName:String=""
    private lateinit var binding:ActivityMerchantProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        payMaartId = intent.getStringExtra(Constants.PAYMAART_ID)?:""
        MerchantName = intent.getStringExtra(Constants.MERCHANT_NAME)?:""
        super.onCreate(savedInstanceState)
        binding = ActivityMerchantProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.viewMerchantProfile)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)and 0xB3FFFFFF.toInt()
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
        //showLoader()
        setupView()
    }

    private fun setupView() {
        lifecycleScope.launch {
            showLoader()
            val idToken = fetchIdToken()
            val merchantTransactionCall = ApiClient.apiService.getMerchantProfile(idToken,payMaartId)

            merchantTransactionCall.enqueue(object : Callback<MerchantProfileResponse> {
                override fun onResponse(
                    call: Call<MerchantProfileResponse>,
                    response: Response<MerchantProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()

                        if (data != null) {
                         updateui(data)
                        }

                    } else {
                        showToast(getString(R.string.default_error_toast))
                    }
                 hideLoader()
                }

                override fun onFailure(call: Call<MerchantProfileResponse>, throwable: Throwable) {
                    hideLoader()
                    showToast(getString(R.string.default_error_toast))
                }
            })
        }
    }

    private fun updateui(data: MerchantProfileResponse) {
        val locationList = listOf(
            data.data.tradingHouseName,
            data.data.tradingstreetName,
            data.data.tradingVillage,
            data.data.tradingDistrict
        ).filter { it != null && it.isNotEmpty() }


        val Phonenumber =PhoneNumberFormatter.format( data.data.countryCode,data.data.phoneNumber)

        val tradingTypes = data.data.tradingType
        val formattedTradingTypes = tradingTypes.joinToString(", ") { it.trim() }

        if(data.data.tradingImage==null){
            binding.viewMerchantActivityShortNameTV.text= getInitials(MerchantName)

        }else{
            binding.viewMerchantActivityShortNameTV.visibility=View.GONE
            val imageUrl = BuildConfig.CDN_BASE_URL + data.data.tradingImage[0] // First image

            Log.d("kk",imageUrl)
            binding.payMerchantIV.also {
                it.visibility = View.VISIBLE
                Glide
                    .with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .into(it)
            }
        }
        val tillNumbers = data.data.tillNumber


        binding.viewMerchantActivityNameTV.text=MerchantName

        binding.viewMerchantLocationTv.text = locationList.joinToString(", ")
        binding.viewMerchantActivityPaymaartIdTV.text= data.data.paymaartId
        binding.viewMerchantPhoneNumberTV.text =Phonenumber
        binding.viewMerchantTradingnameTV.text = data.data.tradingName
        binding.viewMerchantTradingTypesTV.text = formattedTradingTypes


        if (tillNumbers.isNotEmpty()) {
            // Show only the first 4 till numbers
            val displayedTills = tillNumbers.take(4).joinToString(", ")
            binding.viewMerchantTilno.text = displayedTills.parseTillNumber()

            // If more than 4, show the "View All" button
            if (tillNumbers.size > 4) {
                binding.viewAllTillNumbers.visibility = View.VISIBLE
            }


        }
    }



    private fun showLoader() {
        binding.listMerchantTransactionLoaderLottie.visibility = View.VISIBLE
      binding.listPersonTransactionNoDataFoundContainer.visibility = View.VISIBLE

    }
    private fun hideLoader() {
        binding.listMerchantTransactionLoaderLottie.visibility = View.GONE
    }


}