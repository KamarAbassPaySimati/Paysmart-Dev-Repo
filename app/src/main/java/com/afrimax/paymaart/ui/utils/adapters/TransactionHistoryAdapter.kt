package com.afrimax.paymaart.ui.utils.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paymaart.BuildConfig
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.model.IndividualTransactionHistory
import com.afrimax.paymaart.databinding.CardPagerLoaderBinding
import com.afrimax.paymaart.databinding.TransactionListViewBinding
import com.afrimax.paymaart.util.formatEpochTimeThree
import com.afrimax.paymaart.util.formatEpochTimeTwo
import com.afrimax.paymaart.util.getFormattedAmount
import com.afrimax.paymaart.util.getInitials
import com.afrimax.paymaart.util.showLogE
import com.bumptech.glide.Glide
import java.util.Locale
import kotlin.math.abs

class TransactionHistoryAdapter(
    private val context: Context,
    private val transactionList: ArrayList<IndividualTransactionHistory>,
    private val userPaymaartId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class TransactionHistoryViewHolder(val b: TransactionListViewBinding) : RecyclerView.ViewHolder(b.root) {
        fun setView(transaction: IndividualTransactionHistory){
            var title = ""
            val isCurrentUserDebited = transaction.senderId == userPaymaartId || transaction.enteredBy == userPaymaartId
            when (transaction.transactionType) {
                CASH_IN -> {
                    title = context.getString(R.string.cash_in)
                    if (isCurrentUserDebited) {
                        b.cardTransactionPaymaartIdTV.apply {
                            visibility = View.VISIBLE
                            text = transaction.receiverId
                        }
                        if (transaction.receiverProfilePic.isNullOrEmpty()) {
                            b.cardTransactionIV.visibility = View.GONE
                            b.cardTransactionNameBox.visibility = View.VISIBLE
                            b.cardTransactionShortNameTV.text = getInitials(transaction.receiverName)
                        } else {
                            b.cardTransactionIV.visibility = View.VISIBLE
                            b.cardTransactionNameBox.visibility = View.INVISIBLE
                            Glide
                                .with(context)
                                .load(BuildConfig.CDN_BASE_URL + transaction.receiverProfilePic)
                                .centerCrop()
                                .into(b.cardTransactionIV)
                        }
                    } else {
                        b.cardTransactionPaymaartIdTV.visibility = View.GONE
                        if (transaction.senderProfilePic.isNullOrEmpty()) {
                            b.cardTransactionIV.visibility = View.GONE
                            b.cardTransactionNameBox.visibility = View.VISIBLE
                            b.cardTransactionShortNameTV.text = getInitials(transaction.senderName)
                        } else {
                            b.cardTransactionIV.visibility = View.VISIBLE
                            b.cardTransactionNameBox.visibility = View.INVISIBLE
                            Glide
                                .with(context)
                                .load(BuildConfig.CDN_BASE_URL + transaction.senderProfilePic)
                                .centerCrop()
                                .into(b.cardTransactionIV)
                        }
                    }
                }

                CASH_OUT -> {
                    b.cardTransactionIV.visibility = View.GONE
                    b.cardTransactionNameBox.visibility = View.VISIBLE
                    title = context.getString(R.string.cash_out)
                    if (isCurrentUserDebited) {
                        b.cardTransactionPaymaartIdTV.visibility = View.GONE
                        if (transaction.receiverProfilePic.isNullOrEmpty()) {
                            b.cardTransactionIV.visibility = View.GONE
                            b.cardTransactionNameBox.visibility = View.VISIBLE
                            b.cardTransactionShortNameTV.text = getInitials(transaction.receiverName)
                        } else {
                            b.cardTransactionIV.visibility = View.VISIBLE
                            b.cardTransactionNameBox.visibility = View.INVISIBLE
                            Glide
                                .with(context)
                                .load(BuildConfig.CDN_BASE_URL + transaction.receiverProfilePic)
                                .centerCrop()
                                .into(b.cardTransactionIV)
                        }
                    } else {
                        b.cardTransactionPaymaartIdTV.visibility = View.VISIBLE
                        b.cardTransactionPaymaartIdTV.text = transaction.senderId
                        if (transaction.receiverProfilePic.isNullOrEmpty()) {
                            b.cardTransactionIV.visibility = View.GONE
                            b.cardTransactionNameBox.visibility = View.VISIBLE

                            b.cardTransactionShortNameTV.text = getInitials(transaction.senderName)
                        } else {
                            b.cardTransactionIV.visibility = View.VISIBLE
                            b.cardTransactionNameBox.visibility = View.INVISIBLE
                            Glide
                                .with(context)
                                .load(BuildConfig.CDN_BASE_URL + transaction.senderProfilePic)
                                .centerCrop().into(b.cardTransactionIV)
                        }
                    }
                }

                AFRIMAX -> {
                    b.cardTransactionIV.visibility = View.VISIBLE
                    b.cardTransactionNameBox.visibility = View.GONE
                    title = context.getString(R.string.afrimax)
                    b.cardTransactionPaymaartIdTV.visibility = View.GONE
                    Glide
                        .with(context)
                        .load(R.drawable.ico_afrimax)
                        .into(b.cardTransactionIV)
                }

                PAY_IN -> {
                    b.cardTransactionIV.visibility = View.GONE
                    b.cardTransactionNameBox.visibility = View.VISIBLE
                    title = context.getString(R.string.pay_in)
                    b.cardTransactionPaymaartIdTV.visibility = View.GONE
                    b.cardTransactionShortNameTV.text = getInitials(transaction.senderName)
                }

                PAY_OUT -> {
                    b.cardTransactionIV.visibility = View.GONE
                    b.cardTransactionNameBox.visibility = View.VISIBLE
                    title = context.getString(R.string.pay_out)
                    b.cardTransactionPaymaartIdTV.visibility = View.GONE
                    b.cardTransactionShortNameTV.text = getInitials(transaction.senderName)
                }

                REFUND -> {
                    b.cardTransactionIV.visibility = View.GONE
                    b.cardTransactionNameBox.visibility = View.VISIBLE
                    title = context.getString(R.string.refund)
                    b.cardTransactionPaymaartIdTV.visibility = View.GONE
                    b.cardTransactionShortNameTV.text = title[0].toString()
                }

                PAYMAART -> {
                    b.cardTransactionIV.visibility = View.VISIBLE
                    b.cardTransactionNameBox.visibility = View.GONE
                    title = context.getString(R.string.paymaart)
                    b.cardTransactionPaymaartIdTV.visibility = View.GONE
                    Glide
                        .with(context)
                        .load(R.drawable.ico_paymaart_icon)
                        .into(b.cardTransactionIV)
                }
            }

            val amount = if (isCurrentUserDebited) "-" else "+"
            b.cardTransactionAmountTV.text = context.getString(R.string.formatted_list_amount, amount, getFormattedAmount(abs(transaction.transactionAmount.toDouble())))
            b.cardTransactionNameTV.text = title
            b.cardTransactionAmountDateTimeTV.text = formatEpochTimeThree(transaction.createdAt)
        }
        
    }

    class PagerLoaderViewHolder(b: CardPagerLoaderBinding) : RecyclerView.ViewHolder(b.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            PAGER_LOADER -> PagerLoaderViewHolder(
                CardPagerLoaderBinding.inflate(
                    LayoutInflater.from(context), parent, false
                )
            )

            else -> TransactionHistoryViewHolder(
                TransactionListViewBinding.inflate(
                    LayoutInflater.from(context), parent, false
                )
            )
        }

    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TransactionHistoryViewHolder) {
            holder.setView(transactionList[position])
            holder.b.cardTransaction.setOnClickListener {
                onClickListener?.onClick(transactionList[position])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val data = transactionList[position]
        return when (data.viewType) {
            "loader" -> PAGER_LOADER
            else -> USER_DATA
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(transaction: IndividualTransactionHistory)
    }

    companion object {
        const val USER_DATA = 1
        const val PAGER_LOADER = 2
        //transaction types
        const val CASH_IN = "cash_in"
        const val CASH_OUT = "cash_out"
        const val AFRIMAX = "afrimax"
        const val PAY_IN = "pay_in"
        const val PAY_OUT = "pay_out"
        const val REFUND = "refund"
        const val PAYMAART = "paymaart"
        const val INTEREST = "interest"
        const val G2P_PAY_IN = "g2p_pay_in"
        const val CASH_OUT_REQUEST = "cashout_request"
        const val CASH_OUT_FAILED = "cashout_failed"
    }
}