package com.afrimax.paymaart.ui.utils.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
    private val transactionList: MutableList<IndividualTransactionHistory>,
    private val userPaymaartId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class TransactionHistoryViewHolder(val b: TransactionListViewBinding) : RecyclerView.ViewHolder(b.root) {
        fun setView(transaction: IndividualTransactionHistory) {
            val isCurrentUserDebited = transaction.senderId == userPaymaartId || transaction.enteredBy == userPaymaartId
            val amountPrefix = if (isCurrentUserDebited) "-" else "+"
            b.cardTransactionAmountDateTimeTV.text = formatEpochTimeThree(transaction.createdAt)
            b.cardTransactionAmountTV.text = context.getString(
                R.string.formatted_list_amount,
                amountPrefix,
                getFormattedAmount(abs(transaction.transactionAmount.toDouble()))
            )

            // Set common views
            b.cardTransactionIV.visibility = View.GONE
            b.cardTransactionShortNameTV.visibility = View.VISIBLE

            // Set transaction-specific views
            when (transaction.transactionType) {
                CASH_IN, CASHIN -> setTransactionDetails(transaction.senderId, transaction.senderName, R.string.cash_in)
                CASH_OUT, CASHOUT, CASH_OUT_REQUEST, CASH_OUT_FAILED -> setTransactionDetails(transaction.receiverId, transaction.receiverName, R.string.cash_out)
                PAY_IN -> setTransactionDetails(transaction.enteredBy ?: "", transaction.enteredByName ?: "", R.string.pay_in)
                REFUND -> setTransactionDetails(transaction.senderId, transaction.receiverName, R.string.refund)
                INTEREST -> setTransactionDetails(transaction.senderId, transaction.receiverName, R.string.interest)
                G2P_PAY_IN -> setTransactionDetails(transaction.senderId, transaction.receiverName, R.string.g2p_pay_in)
                PAYMAART -> setImageTransaction(R.string.paymaart, R.drawable.ico_paymaart_icon)
                AFRIMAX -> setImageTransaction(R.string.afrimax, R.drawable.ico_afrimax)
                PAY_PERSON -> {
                    if (userPaymaartId == transaction.senderId)
                        setPaymaartTransactionType(transaction.receiverName, transaction.receiverId, transaction.receiverProfilePic)
                    else
                        setPaymaartTransactionType(transaction.senderName, transaction.senderId, transaction.senderProfilePic)
                }
            }
        }

        private fun setTransactionDetails(id: String, name: String, nameResId: Int) {
            b.cardTransactionNameTV.text = context.getString(nameResId)
            b.cardTransactionPaymaartIdTV.text = id
            b.cardTransactionShortNameTV.text = getInitials(name)
        }

        private fun setImageTransaction(nameResId: Int, imageResId: Int) {
            b.cardTransactionNameTV.text = context.getString(nameResId)
            b.cardTransactionPaymaartIdTV.visibility = View.GONE
            b.cardTransactionShortNameTV.visibility = View.GONE
            b.cardTransactionIV.visibility = View.VISIBLE
            Glide
                .with(context)
                .load(imageResId)
                .into(b.cardTransactionIV)
        }

        private fun setPaymaartTransactionType(name: String, userId: String, image: String?){
            b.cardTransactionNameTV.text = name
            b.cardTransactionPaymaartIdTV.text = userId
            if (image.isNullOrEmpty()) {
                b.cardTransactionIV.visibility = View.GONE
                b.cardTransactionShortNameTV.visibility = View.VISIBLE
                b.cardTransactionShortNameTV.text = getInitials(name)
            }else {
                b.cardTransactionShortNameTV.visibility = View.GONE
                b.cardTransactionIV.visibility = View.VISIBLE
                Glide
                    .with(context)
                    .load(BuildConfig.CDN_BASE_URL + image)
                    .centerCrop()
                    .into(b.cardTransactionIV)
            }


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
        const val CASHIN = "cashin"
        const val CASH_IN = "cash_in"
        const val CASHOUT = "cashout"
        const val CASH_OUT = "cash_out"
        const val AFRIMAX = "afrimax"
        const val PAY_IN = "pay_in"
        const val PAY_PERSON = "pay_person"
        const val REFUND = "refund"
        const val PAYMAART = "paymaart"
        const val INTEREST = "interest"
        const val G2P_PAY_IN = "g2p_pay_in"
        const val CASH_OUT_REQUEST = "cashout_request"
        const val CASH_OUT_FAILED = "cashout_failed"
    }
}