package com.afrimax.paysimati.ui.paymerchant

import android.content.Intent
import android.os.Bundle

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paysimati.BuildConfig
import com.afrimax.paysimati.R
import com.afrimax.paysimati.common.presentation.utils.PaymaartIdFormatter
import com.afrimax.paysimati.common.presentation.utils.PhoneNumberFormatter
import com.afrimax.paysimati.common.presentation.utils.parseTillNumber
import com.afrimax.paysimati.data.ApiClient
import com.afrimax.paysimati.data.model.MerchantProfileResponse
import com.afrimax.paysimati.databinding.ActivityMerchantProfileBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.kyc.KycFullScreenPreviewActivity
import com.afrimax.paysimati.ui.utils.adapters.ImageGridAdapter
import com.afrimax.paysimati.ui.utils.bottomsheets.TillNumberBottomSheet
import com.afrimax.paysimati.util.Constants
import com.afrimax.paysimati.util.getInitials
import com.bumptech.glide.Glide
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

        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

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

        val tradingImages = data.data.tradingImages
        val imageList = tradingImages.map { imagePath ->
            BuildConfig.CDN_BASE_URL + imagePath
        }
        setupRecyclerView(imageList)

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

        val PayMaartId =PaymaartIdFormatter.formatId(payMaartId)
        binding.viewMerchantActivityNameTV.text=MerchantName

        binding.viewMerchantLocationTv.text = locationList.joinToString(", ")
        binding.viewMerchantActivityPaymaartIdTV.text=PayMaartId
        binding.viewMerchantPhoneNumberTV.text = data.data.countryCode + " " + Phonenumber

       if(data.data.tradingName==null){
           binding.viewMerchantTradingnameTV.text= "-"
       }else{
           binding.viewMerchantTradingnameTV.text = data.data.tradingName
       }
        binding.viewMerchantTradingTypesTV.text = formattedTradingTypes
        binding.viewMerchantActivityBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        showTillNumbersBottomSheet(tillNumbers)

    }
    private fun setupRecyclerView(imageList: List<String>) {
        val recyclerView: RecyclerView = findViewById(R.id.viewMerchantBusinessTypesRV)
        val layoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = layoutManager

        val imageAdapter = ImageGridAdapter(imageList)
        recyclerView.adapter = imageAdapter

        imageAdapter.setOnClickListener(object : ImageGridAdapter.OnClickListener {
            override fun onClick(imageUrl: String) {
                val intent = Intent(this@MerchantProfile, KycFullScreenPreviewActivity::class.java)
                intent.putExtra(Constants.KYC_MEDIA_IS_UPLOADED, true)
                intent.putExtra(Constants.KYC_MEDIA_TYPE, Constants.KYC_MEDIA_IMAGE_TYPE)
                intent.putExtra(Constants.KYC_CAPTURED_IMAGE_URI, imageUrl)
                startActivity(intent)
            }
        })
    }

    private fun showTillNumbersBottomSheet(tillNumbers: List<String>) {
        if (tillNumbers.isNotEmpty()) {
            val formattedTills = tillNumbers.map { it.parseTillNumber() }
            val displayedTills = if (formattedTills.size > 4) {
                formattedTills.take(4).joinToString(", ") + ", ${formattedTills[4].take(3)}..."
            } else {
                formattedTills.joinToString(", ")
            }

            binding.viewMerchantTilno.text = displayedTills

            if (formattedTills.size > 4) {
                binding.viewAllTillNumbers.visibility = View.VISIBLE
                binding.viewAllTillNumbers.setOnClickListener {
                    val bottomSheetFragment = TillNumberBottomSheet.newInstance(formattedTills)
                    bottomSheetFragment.show(supportFragmentManager, TillNumberBottomSheet.TAG)
                }
            }
        }
    }



    private fun showLoader() {
        binding.listMerchantTransactionLoaderLottie.visibility = View.VISIBLE
        binding.loaderOverlay.visibility =View.VISIBLE

    }
    private fun hideLoader() {
        binding.listMerchantTransactionLoaderLottie.visibility = View.GONE
        binding.loaderOverlay.visibility =View.GONE

    }


}