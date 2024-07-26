package com.afrimax.paymaart.ui.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.RefundRequestAdapterViewBinding
import com.afrimax.paymaart.ui.refundrequest.RefundModel
import com.afrimax.paymaart.util.getInitials
import com.bumptech.glide.Glide

class RefundRequestAdapter(val list: List<RefundModel>): RecyclerView.Adapter<RefundRequestAdapter.RefundRequestViewHolder>() {

    inner class RefundRequestViewHolder(val binding: RefundRequestAdapterViewBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RefundRequestViewHolder {
        val view = RefundRequestAdapterViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RefundRequestViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RefundRequestViewHolder, position: Int) {
        with(holder){
            binding.refundAdapterName.text = list[position].name
            binding.refundAdapterId.text = list[position].paymaartId
            binding.refundRequestAmount.text = list[position].amount
            binding.refundAdapterDate.text = list[position].date
            binding.refundAdapterTransactionId.text = list[position].transactionId
            if (list[position].profilePic.isNullOrEmpty()){
                binding.iconImage.visibility = View.GONE
                binding.iconNameInitials.apply {
                    visibility = View.VISIBLE
                    text = getInitials(list[position].name)
                }
            }else{
                binding.iconNameInitials.visibility = View.GONE
                binding.iconImage.visibility = View.VISIBLE
                Glide
                    .with(holder.itemView.context)
                    .load(list[position].profilePic)
                    .into(binding.iconImage)
            }
            when(list[position].status){
                "pending" -> {
                    binding.refundAdapterStatus.apply {
                        text = ContextCompat.getString(holder.itemView.context, R.string.pending)
                        setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.pendingCardTextColor))
                        background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.pending_bg)
                    }
                }
                "rejected" -> {
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

}