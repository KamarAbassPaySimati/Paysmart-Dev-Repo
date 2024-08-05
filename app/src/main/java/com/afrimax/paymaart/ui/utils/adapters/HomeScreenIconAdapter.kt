package com.afrimax.paymaart.ui.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paymaart.BuildConfig
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.model.IndividualTransactionHistory
import com.afrimax.paymaart.databinding.HomeRecyclerviewAdapterViewBinding
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter.Companion
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter.Companion.CASHIN
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter.Companion.CASHOUT
import com.afrimax.paymaart.ui.utils.adapters.TransactionHistoryAdapter.Companion.PAY_PERSON
import com.afrimax.paymaart.util.formatEpochTimeThree
import com.afrimax.paymaart.util.getDrawableExt
import com.afrimax.paymaart.util.getFormattedAmount
import com.afrimax.paymaart.util.getInitials
import com.bumptech.glide.Glide
import kotlin.math.abs

class HomeScreenIconAdapter(
    private val transactionList: List<IndividualTransactionHistory>,
    private val userPaymaartId: String
): RecyclerView.Adapter<HomeScreenIconAdapter.HomeScreenIconViewHolder>() {
    inner class HomeScreenIconViewHolder(val binding: HomeRecyclerviewAdapterViewBinding) :
        RecyclerView.ViewHolder(binding.root)

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
            binding.iconImage.setImageDrawable(holder.itemView.context.getDrawableExt(R.drawable.ico_afrimax))
            // Set transaction-specific views
            when (transactionList[position].transactionType) {
                CASH_IN, CASHIN -> {
                    binding.iconImage.visibility = View.GONE
                    binding.iconName.text = holder.itemView.context.getString(R.string.cash_in)
                    binding.iconNameInitials.text = getInitials(transactionList[position].senderName)
                }
                CASH_OUT, CASHOUT, CASH_OUT_REQUEST, CASH_OUT_FAILED -> {
                    binding.iconImage.visibility = View.GONE
                    binding.iconName.text = holder.itemView.context.getString(R.string.cash_out)
                    binding.iconNameInitials.text = getInitials(transactionList[position].receiverName)
                }
                PAY_IN -> {
                    binding.iconImage.visibility = View.GONE
                    binding.iconName.text = holder.itemView.context.getString(R.string.pay_in)
                    binding.iconNameInitials.text = getInitials(transactionList[position].enteredByName)
                }
                REFUND -> {
                    binding.iconImage.visibility = View.GONE
                    binding.iconName.text = holder.itemView.context.getString(R.string.refund)
                    binding.iconNameInitials.text = getInitials(transactionList[position].senderName)
                }
                INTEREST -> {
                    binding.iconImage.visibility = View.GONE
                    binding.iconName.text = holder.itemView.context.getString(R.string.interest)
                    binding.iconNameInitials.visibility = View.GONE
                }
                G2P_PAY_IN -> {
                    binding.iconImage.visibility = View.GONE
                    binding.iconName.text = holder.itemView.context.getString(R.string.g2p_pay_in)
                    binding.iconNameInitials.text = getInitials(transactionList[position].senderName)
                }
                PAYMAART -> {
                    binding.iconNameInitials.visibility = View.GONE
                    binding.iconName.text =holder.itemView.context.getString(R.string.paymaart)
                    binding.iconImage.apply {
                        visibility = View.VISIBLE
                        setImageDrawable(holder.itemView.context.getDrawableExt(R.drawable.ico_paymaart_icon))
                    }
                }
                AFRIMAX -> {
                    binding.iconNameInitials.visibility = View.GONE
                    binding.iconName.text =holder.itemView.context.getString(R.string.afrimax)
                    binding.iconImage.also {
                        it.visibility = View.VISIBLE
                        Glide
                            .with(holder.itemView.context)
                            .load(R.drawable.ico_afrimax)
                            .fitCenter()
                            .into(it)
                    }
                }
                PAY_PERSON -> {
                    binding.iconNameInitials.visibility = View.GONE
                    if (userPaymaartId == transactionList[position].senderId)
                        setPersonImageDrawable(holder, transactionList[position].receiverProfilePic, transactionList[position].receiverName)
                    else
                        setPersonImageDrawable(holder, transactionList[position].senderProfilePic, transactionList[position].senderName)
                }else -> {}
            }
        }
    }

    private fun setPersonImageDrawable(holder: HomeScreenIconViewHolder, userImage: String?, userName: String?){
        Glide
            .with(holder.itemView.context)
            .load(BuildConfig.CDN_BASE_URL + userImage)
            .centerCrop()
            .into(holder.binding.iconImage)
        val mUserName = userName?.split(" ") ?: emptyList()
        holder.binding.iconName.text = if (mUserName.size > 1) mUserName[0] else ""
    }
    
    companion object {
        const val CASH_IN = "cash_in"
        const val CASH_OUT = "cash_out"
        const val AFRIMAX = "afrimax"
        const val PAY_IN = "pay_in"
        const val REFUND = "refund"
        const val PAYMAART = "paymaart"
        const val INTEREST = "interest"
        const val G2P_PAY_IN = "g2p_pay_in"
        const val CASH_OUT_REQUEST = "cashout_request"
        const val CASH_OUT_FAILED = "cashout_failed"
    }
}