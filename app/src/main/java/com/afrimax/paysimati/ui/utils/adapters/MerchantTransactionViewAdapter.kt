package com.afrimax.paysimati.ui.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paysimati.BuildConfig
import com.afrimax.paysimati.R
import com.afrimax.paysimati.data.model.MerchantTransaction
import com.afrimax.paysimati.databinding.HomeRecyclerviewAdapterViewBinding
import com.afrimax.paysimati.util.getInitials
import com.bumptech.glide.Glide

class MerchantTransactionViewAdapter(
    private val merchantTransactionList: List<MerchantTransaction>,
) : RecyclerView.Adapter<MerchantTransactionViewAdapter.MerchantTransactionViewHolder>() {

    inner class MerchantTransactionViewHolder(val binding: HomeRecyclerviewAdapterViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MerchantTransactionViewHolder {
        val binding = HomeRecyclerviewAdapterViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MerchantTransactionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        // Limit the number of items to 4 if the list size is greater than 4
        return if (merchantTransactionList.size > 4) 4 else merchantTransactionList.size
    }

    override fun onBindViewHolder(holder: MerchantTransactionViewHolder, position: Int) {
        val merchantTransaction = merchantTransactionList[position]

        with(holder.binding) {
            // Hide unnecessary views
            iconImage.visibility = View.GONE
            iconName.visibility = View.GONE
            iconNameInitials.visibility = View.GONE

            // Set merchant-specific data
            iconName.apply {
                visibility = View.VISIBLE
                text = merchantTransaction.tradingName ?: "Unknown Merchant"
            }

            iconNameInitials.apply {
                visibility = View.VISIBLE
                text = getInitials(merchantTransaction.tradingName ?: "UM")
            }

            // Load profile picture if available
            if (!merchantTransaction.profilePic.isNullOrEmpty()) {
                val imageUrl = BuildConfig.CDN_BASE_URL + merchantTransaction.profilePic
                iconImage.visibility = View.VISIBLE
                Glide.with(holder.itemView.context)
                    .load(imageUrl)
                    .centerCrop()
                    .into(iconImage)
            } else {
                iconImage.visibility = View.GONE
                iconNameInitials.visibility = View.VISIBLE
                iconNameInitials.text = com.afrimax.paysimati.util.getInitials(merchantTransaction.tradingName)
                iconName.visibility = View.VISIBLE
                iconName.text = com.afrimax.paysimati.util.getInitials(merchantTransaction.tradingName)
            }
        }

        // Set click listener
        holder.itemView.setOnClickListener {
            onClickListener?.onClick(merchantTransaction)
        }
    }

    private fun getInitials(name: String?): String {
        if (name.isNullOrEmpty()) return ""
        return name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(merchantTransaction: MerchantTransaction)
    }
}