package com.afrimax.paysimati.ui.utils.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paysimati.BuildConfig
import com.afrimax.paysimati.R
import com.afrimax.paysimati.data.model.RefundRequest
import com.afrimax.paysimati.databinding.LoaderAdapterViewBinding
import com.afrimax.paysimati.databinding.RefundRequestAdapterViewBinding
import com.afrimax.paysimati.util.RecyclerViewType
import com.afrimax.paysimati.util.formatEpochTimeThree
import com.afrimax.paysimati.util.getFormattedAmount
import com.afrimax.paysimati.util.getInitials
import com.bumptech.glide.Glide
import kotlin.math.abs

class RefundRequestAdapter(val context: Context, val list: List<RefundRequest>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class RefundRequestViewHolder(val binding: RefundRequestAdapterViewBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(refundRequest: RefundRequest){
            binding.refundAdapterName.text = refundRequest.receiverName
            binding.refundAdapterId.text = refundRequest.receiverId
            binding.refundAdapterDate.text = formatEpochTimeThree(refundRequest.createdAt)
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
                            .load(BuildConfig.CDN_BASE_URL + refundRequest.profilePic)
                            .into(binding.iconImage)
                    }
                }
            }
            when(refundRequest.status){
                PENDING -> {
                    binding.refundRequestAmount.text = getFormattedAmount(abs(refundRequest.amount.toDouble()))
                    binding.refundAdapterStatus.apply {
                        text = ContextCompat.getString(context, R.string.pending)
                        setTextColor(ContextCompat.getColor(context, R.color.pendingCardTextColor))
                        background = ContextCompat.getDrawable(context, R.drawable.pending_bg)
                    }
                }
                REJECTED -> {
                    binding.refundRequestAmount.text = getFormattedAmount(refundRequest.amount)
                    binding.refundAdapterStatus.apply {
                        text = ContextCompat.getString(context, R.string.rejected)
                        setTextColor(ContextCompat.getColor(context, R.color.errorRed))
                        background = ContextCompat.getDrawable(context, R.drawable.error_bg)
                    }
                }
                else -> {
                    binding.refundRequestAmount.text = getFormattedAmount(refundRequest.amount)
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