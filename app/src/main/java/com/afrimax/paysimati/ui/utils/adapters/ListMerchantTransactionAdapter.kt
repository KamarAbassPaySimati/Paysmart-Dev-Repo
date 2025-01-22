package com.afrimax.paysimati.ui.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paysimati.BuildConfig
import com.afrimax.paysimati.common.presentation.utils.PaymaartIdFormatter
import com.afrimax.paysimati.data.model.MerchantList
import com.afrimax.paysimati.databinding.PayMerchantAdapterBinding
import com.afrimax.paysimati.util.getInitials
import com.bumptech.glide.Glide

class ListMerchantTransactionAdapter(
    private val merchantTransactions: List<MerchantList>
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
        val merchantdetails = merchantTransactions[position]
        with(holder) {
            binding.payMerchantName.visibility = View.GONE
            binding.payMerchantLocation.visibility = View.GONE
            binding.payMerchantId.visibility = View.GONE
            binding.payMerchantShortNameTV.visibility = View.GONE
            binding.payMerchantIV.visibility = View.GONE

            binding.payMerchantLocation.apply {
                visibility = View.VISIBLE
                text = merchantdetails.streetName
            }
            binding.payMerchantName.apply {
                visibility = View.VISIBLE
                text = merchantdetails.MerchantName
            }
            binding.payMerchantId.apply {
                visibility = View.VISIBLE
                text = PaymaartIdFormatter.formatId(merchantdetails.paymaartId)
            }
            binding.payMerchantShortNameTV.apply {
                visibility = View.VISIBLE
                text = getInitials(merchantdetails.MerchantName)
            }
            if (merchantdetails.profile_pic.isNullOrEmpty()) {
                binding.payMerchantIV.visibility = View.GONE
            } else {
                val imageUrl = BuildConfig.CDN_BASE_URL + merchantdetails.profile_pic
                binding.payMerchantIV.also {
                    it.visibility = View.VISIBLE
                    Glide
                        .with(holder.itemView.context)
                        .load(imageUrl)
                        .centerCrop()
                        .into(it)
                }
            }

            binding.root.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(merchantdetails)
                }
            }
        }
    }

    override fun getItemCount(): Int = merchantTransactions.size

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(transaction: MerchantList)
    }
}
