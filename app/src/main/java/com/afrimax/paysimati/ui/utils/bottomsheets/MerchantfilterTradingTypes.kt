package com.afrimax.paysimati.ui.utils.bottomsheets

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.afrimax.paysimati.R
import com.afrimax.paysimati.databinding.MerchantTypesFilterBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Named

class MerchantFilterTradingTypesSheet : BottomSheetDialogFragment() {
    private lateinit var binding: MerchantTypesFilterBottomSheetBinding
    private lateinit var sheetCallback: MerchantFilterCallback
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private val tradingTypes: ArrayList<TradingType> by lazy { provideTradingTypes() }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheet = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view = View.inflate(context, R.layout.merchant_types_filter_bottom_sheet, null)
        bottomSheet.setContentView(view)

        bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        return bottomSheet
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = MerchantTypesFilterBottomSheetBinding.inflate(inflater, container, false)
        setupLayout()
        setupListeners()
        return binding.root
    }


    private fun setupLayout() {
        populateTradingTypes()
    }

    private fun setupListeners() {
        binding.merchantFilterApplyButton.setOnClickListener {
            val selectedType = getSelectedTradingType()
            // Show toast with the selected trading type
            sheetCallback.onTradingTypeSelected(selectedType)
            dismiss()
        }

        binding.merchantFilterClearAllButton.setOnClickListener {
            tradingTypes.clear()
            sheetCallback.clearTradingTypeFilters()
            dismiss()
        }
    }

    private fun populateTradingTypes() {
        tradingTypes.forEachIndexed { index, type ->
            val radioButton = RadioButton(requireContext()).apply {
                id = index
                text = type.name
                textSize = 16f
                setPadding(16, 16, 16, 16) // Same as your XML padding
                setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.primaryFontColor
                    )
                ) // Set text color
                background = ContextCompat.getDrawable(
                    context,
                    R.drawable.bg_transaction_radio_button
                ) // Set background from drawable
                buttonTintList = ContextCompat.getColorStateList(
                    context,
                    R.color.bg_toggles_selector
                ) // Set button tint for the radio button
                // Set the gravity to align the text to the start and center vertically
                gravity = android.view.Gravity.START or android.view.Gravity.CENTER_VERTICAL

                val params = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 0, 0, 0) // 16dp start margin
                }
                layoutParams = params
            }
            // if(type.name == "All") radioButton.performClick()
            binding.tradingTypes.addView(radioButton)
        }
        binding.tradingTypes.check(0)
    }


    private fun getSelectedTradingType(): String {
        val selectedId = binding.tradingTypes.checkedRadioButtonId
        val selectedButton = binding.tradingTypes.findViewById<RadioButton>(selectedId)
        return selectedButton?.text?.toString() ?: ""
    }

    private fun provideTradingTypes(): ArrayList<TradingType> {
        return ArrayList<TradingType>().apply {
            add(TradingType(name = "All"))
            add(TradingType(name = "Hotels & Resorts"))
            add(TradingType(name = "Restaurants & Cafes"))
            add(TradingType(name = "Catering Services"))
            add(TradingType(name = "Food Trucks & Street Food Vendors"))
            add(TradingType(name = "Bed & Breakfast Guest Houses"))
            add(TradingType(name = "Event Catering & Management"))
            add(TradingType(name = "Fast Food Chains"))
            add(TradingType(name = "Specialty Food Stores"))

            // Agriculture, Forestry & Fishing
            add(TradingType(name = "Crop Farming"))
            add(TradingType(name = "Livestock Farming"))
            add(TradingType(name = "Timber Harvesting"))
            add(TradingType(name = "Aquaculture"))
            add(TradingType(name = "Forestry Management"))
            add(TradingType(name = "Fishing & Seafood Processing"))
            add(TradingType(name = "Organic Farming"))
            add(TradingType(name = "Agricultural Equipment Sales & Services"))

            // Commerce | Wholesale & Retail Trade
            add(TradingType(name = "Supermarkets & Grocery Stores"))
            add(TradingType(name = "Clothing Retailers"))
            add(TradingType(name = "Electronics Retailers"))
            add(TradingType(name = "Online Retailers"))
            add(TradingType(name = "Wholesale Distributors"))
            add(TradingType(name = "Specialty Retail Stores"))
            add(TradingType(name = "Department Stores"))
            add(TradingType(name = "E-commerce Platforms"))

            // Construction
            add(TradingType(name = "Residential Construction"))
            add(TradingType(name = "Commercial Construction"))
            add(TradingType(name = "Renovation Services"))
            add(TradingType(name = "Civil Engineering"))
            add(TradingType(name = "Architectural Services"))
            add(TradingType(name = "Building Materials Suppliers"))
            add(TradingType(name = "Interior Design Services"))
            add(TradingType(name = "Landscaping & Outdoor Construction"))

            // Education Services
            add(TradingType(name = "Schools & Universities"))
            add(TradingType(name = "Online Education Platforms"))
            add(TradingType(name = "Tutoring Services"))
            add(TradingType(name = "Educational Supplies Stores"))
            add(TradingType(name = "Language Schools"))
            add(TradingType(name = "Vocational Training Centers"))
            add(TradingType(name = "Educational Technology Providers"))
            add(TradingType(name = "Educational Consultancies"))

            // Financial & Insurance Activities
            add(TradingType(name = "Banks & Credit Unions"))
            add(TradingType(name = "Insurance Companies"))
            add(TradingType(name = "Investment & Financial Advisory Firms"))
            add(TradingType(name = "Payment (mobile-money) Services Providers"))
            add(TradingType(name = "Mortgage Lenders"))
            add(TradingType(name = "Accounting Services"))
            add(TradingType(name = "Tax Preparation Services"))
            add(TradingType(name = "Wealth Management Services"))

            // Healthcare Services
            add(TradingType(name = "Hospitals & Clinics"))
            add(TradingType(name = "Pharmacies"))
            add(TradingType(name = "Medical Laboratories"))
            add(TradingType(name = "Dental Clinics"))
            add(TradingType(name = "Mental Health Services"))
            add(TradingType(name = "Home Healthcare Services"))
            add(TradingType(name = "Medical Equipment Suppliers"))
            add(TradingType(name = "Alternative Medicine Providers"))

            // Information & Communication
            add(TradingType(name = "Software Development"))
            add(TradingType(name = "Telecommunications Services"))
            add(TradingType(name = "Internet Service Providers"))
            add(TradingType(name = "Media & Broadcasting"))
            add(TradingType(name = "Data Analytics Companies"))
            add(TradingType(name = "IT Consulting Services"))
            add(TradingType(name = "Social Media Platforms"))
            add(TradingType(name = "Cybersecurity Firms"))

            // Manufacturing
            add(TradingType(name = "Automotive Manufacturing"))
            add(TradingType(name = "Electronics Manufacturing"))
            add(TradingType(name = "Textile & Apparel Production"))
            add(TradingType(name = "Food Processing Plants"))
            add(TradingType(name = "Pharmaceutical Manufacturing"))
            add(TradingType(name = "Furniture Production"))
            add(TradingType(name = "Aerospace Manufacturing"))
            add(TradingType(name = "Chemical Production"))

            // Mining & Quarrying
            add(TradingType(name = "Coal Mining"))
            add(TradingType(name = "Metal Ore Mining"))
            add(TradingType(name = "Stone Quarrying"))
            add(TradingType(name = "Oil & Gas Extraction"))
            add(TradingType(name = "Mineral Exploration"))
            add(TradingType(name = "Diamond Mining"))
            add(TradingType(name = "Sand & Gravel Extraction"))
            add(TradingType(name = "Salt Mining"))

            // Public Administration & Defence
            add(TradingType(name = "Government Agencies"))
            add(TradingType(name = "Military Services"))
            add(TradingType(name = "Law Enforcement Agencies"))
            add(TradingType(name = "Public Utilities"))
            add(TradingType(name = "Emergency Services"))
            add(TradingType(name = "Border Control Services"))
            add(TradingType(name = "Public Works Departments"))
            add(TradingType(name = "Regulatory Bodies"))

            // Professional Services
            add(TradingType(name = "Legal Services"))
            add(TradingType(name = "Consulting Firms"))
            add(TradingType(name = "Marketing Agencies"))
            add(TradingType(name = "Engineering Services"))
            add(TradingType(name = "Architecture Firms"))
            add(TradingType(name = "Human Resources Consultancies"))
            add(TradingType(name = "Event Planning Services"))
            add(TradingType(name = "Translation Services"))

            // Real Estate Activities
            add(TradingType(name = "Real Estate Agencies"))
            add(TradingType(name = "Property Management Companies"))
            add(TradingType(name = "Real Estate Developers"))
            add(TradingType(name = "Rental Agencies"))
            add(TradingType(name = "Home Staging Services"))
            add(TradingType(name = "Real Estate Investment Firms"))
            add(TradingType(name = "Land Surveyors"))
            add(TradingType(name = "Title Insurance Companies"))

            // Transport & Storage Services
            add(TradingType(name = "Freight Transportation"))
            add(TradingType(name = "Logistics Companies"))
            add(TradingType(name = "Shipping & Maritime Services"))
            add(TradingType(name = "Warehousing & Distribution"))
            add(TradingType(name = "Moving & Storage Services"))
            add(TradingType(name = "Courier & Delivery Services"))
            add(TradingType(name = "Public Transportation"))
            add(TradingType(name = "Vehicle Rental Services"))

            // Utilities | Electricity, Gas & Water
            add(TradingType(name = "Electricity Providers"))
            add(TradingType(name = "Gas Distribution Companies"))
            add(TradingType(name = "Water Treatment Plants"))
            add(TradingType(name = "Renewable Energy Providers"))
            add(TradingType(name = "Utility Infrastructure Maintenance"))
            add(TradingType(name = "Waste Management Services"))
            add(TradingType(name = "Sewage Treatment Facilities"))
            add(TradingType(name = "Irrigation Systems Providers"))

            // Entertainment & Leisure Services
            add(TradingType(name = "Sports Clubs & Stadiums"))
            add(TradingType(name = "Event Planning Services"))
            add(TradingType(name = "Indoor Playgrounds"))
            add(TradingType(name = "Gaming & Casinos"))
            add(TradingType(name = "Night Clubs & Bars"))
            add(TradingType(name = "Spas & Wellness Centres"))
            add(TradingType(name = "Gym & Fitness Centres"))
            add(TradingType(name = "Salons & Barber Shops"))
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        sheetCallback = context as MerchantFilterCallback
    }

    companion object {
        const val TAG = "MerchantFilterTradingTypesSheet"
    }
}


data class TradingType(val name: String)

interface MerchantFilterCallback {
    fun onTradingTypeSelected(type: String)
    fun clearTradingTypeFilters()
}
