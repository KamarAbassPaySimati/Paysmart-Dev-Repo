package com.afrimax.paysimati.ui.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paysimati.BuildConfig
import com.afrimax.paysimati.common.presentation.utils.PaymaartIdFormatter
import com.afrimax.paysimati.common.presentation.utils.PhoneNumberFormatter
import com.afrimax.paysimati.data.model.PayPerson
import com.afrimax.paysimati.databinding.PayMerchantAdapterBinding
import com.afrimax.paysimati.databinding.PayPersonAdapterViewBinding
import com.afrimax.paysimati.util.getInitials
import com.bumptech.glide.Glide

class ListMerchantTransactionAdapter(
    private val merchantTransactions: List<PayPerson>
) : RecyclerView.Adapter<ListMerchantTransactionAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class ViewHolder(val binding: PayMerchantAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = PayMerchantAdapterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = merchantTransactions[position]
        with(holder) {
            binding.payMerchantName.visibility = View.GONE
            binding.payMerchantLocation.visibility = View.GONE
            binding.payMerchantId.visibility = View.GONE
            binding.payMerchantShortNameTV.visibility = View.GONE
            binding.payMerchantIV.visibility = View.GONE

            binding.payMerchantName.apply {
                visibility = View.VISIBLE
                text = merchantTransactions[position].fullName
            }
            if (!contact.phoneNumber.isNullOrEmpty() && !contact.paymaartId.isNullOrEmpty()) {
                //registered customer
                binding.payMerchantLocation.visibility = View.VISIBLE
                binding.payMerchantLocation.text =
                    contact.let { PhoneNumberFormatter.formatWholeNumber(it.countryCode + it.phoneNumber) }

                binding.payMerchantId.visibility = View.VISIBLE
                binding.payMerchantId.text = PaymaartIdFormatter.formatId(contact.paymaartId)
            }


            if (merchantTransactions[position].profilePicture.isNullOrEmpty()) {
                binding.payMerchantShortNameTV.apply {
                    visibility = View.VISIBLE
                    text = getInitials(merchantTransactions[position].fullName)
                }
            } else {
                binding.payMerchantIV.also {
                    it.visibility = View.VISIBLE
                    Glide.with(it)
                        .load(BuildConfig.CDN_BASE_URL + merchantTransactions[position].profilePicture)
                        .centerCrop().into(it)
                }
            }
            binding.root.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(merchantTransactions[position])
                }
            }
        }
    }

    override fun getItemCount(): Int = merchantTransactions.size

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(transaction: PayPerson)
    }

    private fun formattedPhoneNumber(phoneNumber: String?): String {
        if (phoneNumber == null) return ""
        return try {
            "${phoneNumber.substring(0, 2)} ${phoneNumber.substring(2, 6)} ${phoneNumber.substring(6)}"
        } catch (e: StringIndexOutOfBoundsException) {
            phoneNumber
        }
    }
}