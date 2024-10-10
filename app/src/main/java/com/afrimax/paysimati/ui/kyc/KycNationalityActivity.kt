package com.afrimax.paysimati.ui.kyc

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
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.afrimax.paysimati.R
import com.afrimax.paysimati.databinding.ActivityKycNationalityBinding
import com.afrimax.paysimati.ui.BaseActivity
import com.afrimax.paysimati.ui.utils.adapters.KycNationAdapter
import com.afrimax.paysimati.ui.utils.adapters.KycNationDecoration
import com.afrimax.paysimati.util.Constants

class KycNationalityActivity : BaseActivity() {
    private lateinit var b: ActivityKycNationalityBinding
    private lateinit var nationsList: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityKycNationalityBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setAnimation()
        setContentView(b.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.kycNationalityActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val wic = WindowInsetsControllerCompat(window, window.decorView)
        wic.isAppearanceLightStatusBars = true
        wic.isAppearanceLightNavigationBars = true

        initViews()
        setUpListeners()
    }

    private fun initViews() {
        nationsList = getAllNations()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        b.kycNationalityActivityRV.layoutManager = LinearLayoutManager(this)
        b.kycNationalityActivityRV.isNestedScrollingEnabled = false

        val adapter = KycNationAdapter(this, nationsList)
        adapter.setOnClickListener(object : KycNationAdapter.OnClickListener {
            override fun onClick(position: Int, nation: String, view: View) {
                val intent = Intent()
                intent.putExtra(Constants.KYC_NATION, nation)
                setResult(RESULT_OK, intent)
                finish()
            }
        })
        b.kycNationalityActivityRV.adapter = adapter
        b.kycNationalityActivityRV.addItemDecoration(KycNationDecoration(toPx(16)))

    }

    private fun setUpListeners() {
        b.kycNationalityActivityCloseButtonIV.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        configureEditTextFocusListener()
        configureEditTextChangeListener()
    }

    private fun configureEditTextFocusListener() {
        val focusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_focused)
        val notInFocusDrawable = ContextCompat.getDrawable(this, R.drawable.bg_edit_text_unfocused)

        b.kycNationalityActivityET.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) b.kycNationalityActivityET.background = focusDrawable
                else b.kycNationalityActivityET.background = notInFocusDrawable

            }
    }

    private fun configureEditTextChangeListener() {
        b.kycNationalityActivityET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(p0: Editable?) {
                searchNations(b.kycNationalityActivityET.text.toString())
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun searchNations(queryString: String) {
        val match = getAllNations().filter { it.contains(queryString, ignoreCase = true) }
        nationsList.clear()
        nationsList.addAll(match)
        b.kycNationalityActivityRV.adapter?.notifyDataSetChanged()
    }

    private fun setAnimation() {
        val slide = Slide()
        slide.slideEdge = Gravity.BOTTOM
        slide.setDuration(300)
        slide.setInterpolator(AccelerateInterpolator())

        window.enterTransition = slide
        window.returnTransition = slide
    }

    private fun getAllNations(): ArrayList<String> {
        return arrayListOf(
            "Afghan",
            "Albanian",
            "Algerian",
            "American",
            "Andorran",
            "Angolan",
            "Antiguans",
            "Argentinean",
            "Armenian",
            "Australian",
            "Austrian",
            "Azerbaijani",
            "Bahamian",
            "Bahraini",
            "Bangladeshi",
            "Barbadian",
            "Barbudans",
            "Batswana",
            "Belarusian",
            "Belgian",
            "Belizean",
            "Beninese",
            "Bhutanese",
            "Bolivian",
            "Bosnian",
            "Brazilian",
            "British",
            "Bruneian",
            "Bulgarian",
            "Burkinabe",
            "Burmese",
            "Burundian",
            "Cambodian",
            "Cameroonian",
            "Canadian",
            "Cape Verdean",
            "Central African",
            "Chadian",
            "Chilean",
            "Chinese",
            "Colombian",
            "Comoran",
            "Congolese",
            "Costa Rican",
            "Croatian",
            "Cuban",
            "Cypriot",
            "Czech",
            "Danish",
            "Djibouti",
            "Dominican",
            "Dutch",
            "East Timorese",
            "Ecuadorean",
            "Egyptian",
            "Emirian",
            "Equatorial Guinean",
            "Eritrean",
            "Estonian",
            "Ethiopian",
            "Fijian",
            "Filipino",
            "Finnish",
            "French",
            "Gabonese",
            "Gambian",
            "Georgian",
            "German",
            "Ghanaian",
            "Greek",
            "Grenadian",
            "Guatemalan",
            "Guinea-Bissauan",
            "Guinean",
            "Guyanese",
            "Haitian",
            "Herzegovinian",
            "Honduran",
            "Hungarian",
            "I-Kiribati",
            "Icelander",
            "Indian",
            "Indonesian",
            "Iranian",
            "Iraqi",
            "Irish",
            "Israeli",
            "Italian",
            "Ivorian",
            "Jamaican",
            "Japanese",
            "Jordanian",
            "Kazakhstani",
            "Kenyan",
            "Kittian and Nevisian",
            "Kuwaiti",
            "Kyrgyz",
            "Laotian",
            "Latvian",
            "Lebanese",
            "Liberian",
            "Libyan",
            "Liechtensteiner",
            "Lithuanian",
            "Luxembourger",
            "Macedonian",
            "Malagasy",
            "Malaysian",
            "Maldivan",
            "Malian",
            "Maltese",
            "Marshallese",
            "Mauritanian",
            "Mauritian",
            "Mexican",
            "Micronesian",
            "Moldovan",
            "Monacan",
            "Mongolian",
            "Moroccan",
            "Mosotho",
            "Motswana",
            "Mozambican",
            "Namibian",
            "Nauruan",
            "Nepalese",
            "New Zealander",
            "Nicaraguan",
            "Nigerian",
            "Nigerien",
            "North Korean",
            "Northern Irish",
            "Norwegian",
            "Omani",
            "Pakistani",
            "Palauan",
            "Panamanian",
            "Papua New Guinean",
            "Paraguayan",
            "Peruvian",
            "Polish",
            "Portuguese",
            "Qatari",
            "Romanian",
            "Russian",
            "Rwandan",
            "Saint Lucian",
            "Salvadoran",
            "Samoan",
            "San Marinese",
            "Sao Tomean",
            "Saudi",
            "Scottish",
            "Senegalese",
            "Serbian",
            "Seychellois",
            "Sierra Leonean",
            "Singaporean",
            "Slovakian",
            "Slovenian",
            "Solomon Islander",
            "Somali",
            "South African",
            "South Korean",
            "Spanish",
            "Sri Lankan",
            "Sudanese",
            "Surinamer",
            "Swazi",
            "Swedish",
            "Swiss",
            "Syrian",
            "Taiwanese",
            "Tajik",
            "Tanzanian",
            "Thai",
            "Togolese",
            "Tongan",
            "Trinidadian or Tobagonian",
            "Tunisian",
            "Turkish",
            "Tuvaluan",
            "Ugandan",
            "Ukrainian",
            "Uruguayan",
            "Uzbekistani",
            "Venezuelan",
            "Vietnamese",
            "Welsh",
            "Yemenite",
            "Zambian",
            "Zimbabwean"
        )
    }
}