package com.afrimax.paymaart.ui.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paymaart.BuildConfig
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.model.IndividualTransactionHistory
import com.afrimax.paymaart.data.model.PayPersonTransactions
import com.afrimax.paymaart.databinding.HomeRecyclerviewAdapterViewBinding
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter.Companion.CASHIN
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter.Companion.CASHOUT
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter.Companion.PAY_PERSON
import com.afrimax.paymaart.util.getDrawableExt
import com.afrimax.paymaart.util.getInitials
import com.bumptech.glide.Glide

class HomeScreenPayPersonAdapter(
    private val transactionList: List<PayPersonTransactions>,
): RecyclerView.Adapter<HomeScreenPayPersonAdapter.HomeScreenIconViewHolder>() {
    inner class HomeScreenIconViewHolder(val binding: HomeRecyclerviewAdapterViewBinding) : RecyclerView.ViewHolder(binding.root)
    private var onClickListener: OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeScreenIconViewHolder {
        val binding = HomeRecyclerviewAdapterViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HomeScreenIconViewHolder(binding)
    }

    override fun getItemCount() = if (transactionList.size > 4) 4 else transactionList.size

    override fun onBindViewHolder(holder: HomeScreenIconViewHolder, position: Int) {
        with(holder) {
            binding.iconImage.visibility = View.GONE
            binding.iconName.visibility = View.GONE
            binding.iconNameInitials.visibility = View.GONE
            binding.iconImage.setImageDrawable(holder.itemView.context.getDrawableExt(R.drawable.ico_afrimax))
            // Set transaction-specific views
            if (transactionList[position].profilePic.isNullOrEmpty()) {
                binding.iconNameInitials.apply {
                    visibility = View.VISIBLE
                    text = getInitials(transactionList[position].name)
                }
            } else {
                val imageUrl = BuildConfig.CDN_BASE_URL + transactionList[position].profilePic
                binding.iconImage.also {
                    it.visibility = View.VISIBLE
                    Glide
                        .with(holder.itemView.context)
                        .load(imageUrl)
                        .centerCrop()
                        .into(it)
                }
            }

            binding.iconName.apply {
                visibility = View.VISIBLE
                text = getFirstName(transactionList[position].name)
            }

            holder.itemView.setOnClickListener {
                onClickListener?.onClick(transactionList[position])
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(transaction: PayPersonTransactions)
    }

    private fun getFirstName(fullName: String?): String {
        val mUserName = fullName?.split(" ") ?: emptyList()
        return if (mUserName.size > 1) mUserName[0] else ""
    }
}