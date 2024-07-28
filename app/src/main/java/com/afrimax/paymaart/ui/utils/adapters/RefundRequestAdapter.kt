package com.afrimax.paymaart.ui.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.model.RefundRequest
import com.afrimax.paymaart.databinding.RefundRequestAdapterViewBinding
import com.afrimax.paymaart.util.getFormattedAmount
import com.afrimax.paymaart.util.getInitials
import com.bumptech.glide.Glide

class RefundRequestAdapter(val list: List<RefundRequest>): RecyclerView.Adapter<RefundRequestAdapter.RefundRequestViewHolder>() {

    inner class RefundRequestViewHolder(val binding: RefundRequestAdapterViewBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RefundRequestViewHolder {
        val view = RefundRequestAdapterViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RefundRequestViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RefundRequestViewHolder, position: Int) {
        with(holder){
            binding.refundAdapterName.text = list[position].receiverName
            binding.refundAdapterId.text = list[position].receiverId
            binding.refundRequestAmount.text = getFormattedAmount(list[position].amount)
            binding.refundAdapterDate.text = list[position].createdAt
            binding.refundAdapterTransactionId.text = list[position].transactionId
            when(list[position].transactionType) {
                AFRIMAX -> {
                    binding.iconNameInitials.visibility = View.GONE
                    binding.iconImage.visibility = View.VISIBLE
                    Glide
                        .with(holder.itemView.context)
                        .load(R.drawable.ico_afrimax)
                        .fitCenter()
                        .into(binding.iconImage)
                }
                PAYMAART -> {
                    binding.iconNameInitials.visibility = View.GONE
                    binding.iconImage.visibility = View.VISIBLE
                    Glide
                        .with(holder.itemView.context)
                        .load(R.drawable.ico_paymaart_icon)
                        .fitCenter()
                        .into(binding.iconImage)
                }
                else -> {
                    if (list[position].profilePic.isNullOrEmpty()){
                        binding.iconImage.visibility = View.GONE
                        binding.iconNameInitials.apply {
                            visibility = View.VISIBLE
                            text = getInitials(list[position].receiverName)
                        }
                    }else{
                        binding.iconNameInitials.visibility = View.GONE
                        binding.iconImage.visibility = View.VISIBLE
                        Glide
                            .with(holder.itemView.context)
                            .load(list[position].profilePic)
                            .into(binding.iconImage)
                    }
                }
            }
            when(list[position].status){
                PENDING -> {
                    binding.refundAdapterStatus.apply {
                        text = ContextCompat.getString(holder.itemView.context, R.string.pending)
                        setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.pendingCardTextColor))
                        background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.pending_bg)
                    }
                }
                REJECTED -> {
                    binding.refundAdapterStatus.apply {
                        text = ContextCompat.getString(holder.itemView.context, R.string.rejected)
                        setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.errorRed))
                        background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.error_bg)
                    }
                }
                else -> {
                    binding.refundAdapterStatus.apply {
                        text = ContextCompat.getString(holder.itemView.context, R.string.refunded)
                        setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.refundedCardTextColor))
                        background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.refunded_bg)
                    }
                }
            }
        }
    }

    companion object {
        const val PENDING = "pending"
        const val REJECTED = "rejected"
        const val AFRIMAX = "afrimax"
        const val PAYMAART = "paymaart"
    }
}