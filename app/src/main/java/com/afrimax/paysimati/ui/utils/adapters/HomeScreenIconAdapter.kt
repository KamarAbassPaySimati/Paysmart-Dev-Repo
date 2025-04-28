package com.afrimax.paysimati.ui.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paysimati.BuildConfig
import com.afrimax.paysimati.R
import com.afrimax.paysimati.data.model.IndividualTransactionHistory
import com.afrimax.paysimati.databinding.HomeRecyclerviewAdapterViewBinding
import com.afrimax.paysimati.ui.utils.adapters.TransactionHistoryAdapter.Companion.CASHIN
import com.afrimax.paysimati.ui.utils.adapters.TransactionHistoryAdapter.Companion.CASHOUT
import com.afrimax.paysimati.ui.utils.adapters.TransactionHistoryAdapter.Companion.PAY_MERCHANT
import com.afrimax.paysimati.ui.utils.adapters.TransactionHistoryAdapter.Companion.PAY_PERSON
import com.afrimax.paysimati.util.getDrawableExt
import com.afrimax.paysimati.util.getInitials
import com.bumptech.glide.Glide

class HomeScreenIconAdapter(
    private val transactionList: List<IndividualTransactionHistory>,
    private val userPaymaartId: String
): RecyclerView.Adapter<HomeScreenIconAdapter.HomeScreenIconViewHolder>() {
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
            when (transactionList[position].transactionType) {
                CASH_IN, CASHIN -> {
                    binding.iconName.apply {
                        visibility = View.VISIBLE
                        text = holder.itemView.context.getString(R.string.cash_in)
                    }
                    binding.iconNameInitials.apply {
                        visibility = View.VISIBLE
                        text = getInitials(transactionList[position].senderName)
                    }
                }
                CASH_OUT, CASHOUT, CASH_OUT_REQUEST, CASH_OUT_FAILED -> {
                    binding.iconName.apply {
                        visibility = View.VISIBLE
                        text = holder.itemView.context.getString(R.string.cash_out)
                    }
                    binding.iconNameInitials.apply {
                        visibility = View.VISIBLE
                        text = getInitials(transactionList[position].receiverName)
                    }
                }
                PAY_IN -> {
                    binding.iconName.apply {
                        visibility = View.VISIBLE
                        text = holder.itemView.context.getString(R.string.pay_in)
                    }
                    binding.iconNameInitials.apply {
                        visibility = View.VISIBLE
                        text = getInitials(transactionList[position].enteredByName)
                    }
                }
                REFUND, PAY_UNREGISTERED_REFUND -> {
                    binding.iconName.apply {
                        visibility = View.VISIBLE
                        text = holder.itemView.context.getString(R.string.refund)
                    }
                    binding.iconNameInitials.apply {
                        visibility = View.VISIBLE
                        text = getInitials(context.getString(R.string.refund))
                    }
                }
                INTEREST -> {
                    binding.iconName.apply {
                        visibility = View.VISIBLE
                        text =holder.itemView.context.getString(R.string.interest)
                    }
                    binding.iconImage.also {
                        it.visibility = View.VISIBLE
                        Glide
                            .with(holder.itemView.context)
                            .load(R.drawable.ico_paymaart_icon)
                            .fitCenter()
                            .into(it)
                    }
                }
                G2P_PAY_IN -> {
                    binding.iconName.apply {
                        visibility = View.VISIBLE
                        text = holder.itemView.context.getString(R.string.g2p_pay_in)
                    }
                    binding.iconNameInitials.apply {
                        visibility = View.VISIBLE
                        text = getInitials(transactionList[position].senderName)
                    }
                }


                G2P_REFUND ->{
                    binding.iconName.apply {
                        visibility = View.VISIBLE
                        text = holder.itemView.context.getString(R.string.g2p_rev_out)
                    }
                    binding.iconNameInitials.apply {
                        visibility = View.VISIBLE
                        text = getInitials(context.getString(R.string.g2p_rev_out))
                    }

                }
                PAYMAART -> {
                    binding.iconName.apply {
                        visibility = View.VISIBLE
                        text =holder.itemView.context.getString(R.string.paysimati)
                    }
                    binding.iconImage.apply {
                        visibility = View.VISIBLE
                        setImageDrawable(holder.itemView.context.getDrawableExt(R.drawable.ico_paymaart_icon))
                    }
                }
                AFRIMAX -> {
                    binding.iconName.apply {
                        visibility = View.VISIBLE
                        text = context.getString(R.string.afrimax)
                    }
                    binding.iconImage.apply {
                        visibility = View.VISIBLE
                        setImageDrawable(context.getDrawableExt(R.drawable.ico_afrimax))
                    }
                }
                PAY_PERSON -> {
                    if (userPaymaartId == transactionList[position].senderId)
                        setPersonImageDrawable(holder, transactionList[position].receiverProfilePic, transactionList[position].receiverName)
                    else
                        setPersonImageDrawable(holder, transactionList[position].senderProfilePic, transactionList[position].senderName)
                }
                PAY_MERCHANT -> {
                    if (userPaymaartId == transactionList[position].senderId)
                        setPersonImageDrawable(holder, transactionList[position].receiverProfilePic, transactionList[position].receiverName)
                    else
                        setPersonImageDrawable(holder, transactionList[position].senderProfilePic, transactionList[position].senderName)
                }
                PAY_UNREGISTERED -> {
                    binding.iconName.apply {
                        visibility = View.VISIBLE
                        text = getFirstName(transactionList[position].receiverName)
                    }
                    binding.iconNameInitials.apply {
                        visibility = View.VISIBLE
                        text = getInitials(transactionList[position].receiverName)
                    }
                }else -> {}
            }
        }
        holder.itemView.setOnClickListener {
            onClickListener?.onClick(transactionList[position])
        }
    }

    private fun setPersonImageDrawable(holder: HomeScreenIconViewHolder, userImage: String?, userName: String?){
        if (userImage.isNullOrEmpty()){
            holder.binding.iconImage.visibility = View.GONE
            holder.binding.iconNameInitials.visibility = View.VISIBLE
            holder.binding.iconNameInitials.text = getInitials(userName)
            holder.binding.iconName.visibility = View.VISIBLE
            holder.binding.iconName.text = getInitials(userName)
        }else {
            holder.binding.iconNameInitials.visibility = View.GONE
            val imageUrl = BuildConfig.CDN_BASE_URL + userImage
            holder.binding.iconImage.also {
                it.visibility = View.VISIBLE
                Glide
                    .with(holder.itemView.context)
                    .load(imageUrl)
                    .centerCrop()
                    .into(it)
            }
            holder.binding.iconName.apply {
                visibility = View.VISIBLE
                text = getFirstName(userName)
            }
        }

    }

    private fun getFirstName(name: String?): String {
        if (name.isNullOrEmpty()) return ""
        return name.split(" ").firstOrNull() ?: ""
    }
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(transaction: IndividualTransactionHistory)
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
        const val G2P_REFUND = "reversed_g2p_payment"
        const val CASH_OUT_REQUEST = "cashout_request"
        const val CASH_OUT_FAILED = "cashout_failed"
        const val PAY_UNREGISTERED = "pay_unregister"
        const val PAY_UNREGISTERED_REFUND = "pay_unregister_refund"
    }
}