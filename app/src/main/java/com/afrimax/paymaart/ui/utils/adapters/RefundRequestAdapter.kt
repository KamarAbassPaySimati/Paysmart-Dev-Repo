package com.afrimax.paymaart.ui.utils.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.model.RefundRequest
import com.afrimax.paymaart.databinding.LoaderAdapterViewBinding
import com.afrimax.paymaart.databinding.RefundRequestAdapterViewBinding
import com.afrimax.paymaart.util.RecyclerViewType
import com.afrimax.paymaart.util.formatEpochTimeTwo
import com.afrimax.paymaart.util.getFormattedAmount
import com.afrimax.paymaart.util.getInitials
import com.bumptech.glide.Glide

class RefundRequestAdapter(val context: Context, val list: List<RefundRequest>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class RefundRequestViewHolder(val binding: RefundRequestAdapterViewBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(refundRequest: RefundRequest){
            binding.refundAdapterName.text = refundRequest.receiverName
            binding.refundAdapterId.text = refundRequest.receiverId
            binding.refundRequestAmount.text = getFormattedAmount(refundRequest.amount)
            binding.refundAdapterDate.text = formatEpochTimeTwo(refundRequest.createdAt)
            binding.refundAdapterTransactionId.text = refundRequest.transactionId
            when(refundRequest.transactionType) {
                AFRIMAX -> {
                    binding.iconNameInitials.visibility = View.GONE
                    binding.iconImage.visibility = View.VISIBLE
                    Glide
                        .with(context)
                        .load(R.drawable.ico_afrimax)
                        .fitCenter()
                        .into(binding.iconImage)
                }
                PAYMAART -> {
                    binding.iconNameInitials.visibility = View.GONE
                    binding.iconImage.visibility = View.VISIBLE
                    Glide
                        .with(context)
                        .load(R.drawable.ico_paymaart_icon)
                        .fitCenter()
                        .into(binding.iconImage)
                }
                else -> {
                    if (refundRequest.profilePic.isNullOrEmpty()){
                        binding.iconImage.visibility = View.GONE
                        binding.iconNameInitials.apply {
                            visibility = View.VISIBLE
                            text = getInitials(refundRequest.receiverName)
                        }
                    }else{
                        binding.iconNameInitials.visibility = View.GONE
                        binding.iconImage.visibility = View.VISIBLE
                        Glide
                            .with(context)
                            .load(refundRequest.profilePic)
                            .into(binding.iconImage)
                    }
                }
            }
            when(refundRequest.status){
                PENDING -> {
                    binding.refundAdapterStatus.apply {
                        text = ContextCompat.getString(context, R.string.pending)
                        setTextColor(ContextCompat.getColor(context, R.color.pendingCardTextColor))
                        background = ContextCompat.getDrawable(context, R.drawable.pending_bg)
                    }
                }
                REJECTED -> {
                    binding.refundAdapterStatus.apply {
                        text = ContextCompat.getString(context, R.string.rejected)
                        setTextColor(ContextCompat.getColor(context, R.color.errorRed))
                        background = ContextCompat.getDrawable(context, R.drawable.error_bg)
                    }
                }
                else -> {
                    binding.refundAdapterStatus.apply {
                        text = ContextCompat.getString(context, R.string.refunded)
                        setTextColor(ContextCompat.getColor(context, R.color.refundedCardTextColor))
                        background = ContextCompat.getDrawable(context, R.drawable.refunded_bg)
                    }
                }
            }
        }
    }

    inner class LoaderIconViewHolder(val binding: LoaderAdapterViewBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        val viewType = list[position].viewType
        return when(viewType){
            RecyclerViewType.LOADER -> LOADER
            else -> 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LOADER -> {
                val view = LoaderAdapterViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LoaderIconViewHolder(view)
            }
            else -> {
                val view = RefundRequestAdapterViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                RefundRequestViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val refundRequest = list[position]
        when(holder) {
            is RefundRequestViewHolder -> {
                holder.bind(refundRequest)
            }
            is LoaderIconViewHolder -> {}
        }
    }



    companion object {
        const val PENDING = "pending"
        const val REJECTED = "rejected"
        const val AFRIMAX = "afrimax"
        const val PAYMAART = "paymaart"
        const val LOADER = 111
    }
}