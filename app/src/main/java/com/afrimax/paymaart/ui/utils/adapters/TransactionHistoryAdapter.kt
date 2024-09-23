package com.afrimax.paymaart.ui.utils.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paymaart.BuildConfig
import com.afrimax.paymaart.R
import com.afrimax.paymaart.common.presentation.utils.PhoneNumberFormatter
import com.afrimax.paymaart.data.model.IndividualTransactionHistory
import com.afrimax.paymaart.databinding.CardPagerLoaderBinding
import com.afrimax.paymaart.databinding.TransactionListViewBinding
import com.afrimax.paymaart.util.formatEpochTimeThree
import com.afrimax.paymaart.util.getFormattedAmount
import com.afrimax.paymaart.util.getInitials
import com.bumptech.glide.Glide

class TransactionHistoryAdapter(
    private val context: Context,
    private val transactionList: MutableList<IndividualTransactionHistory>,
    private val userPaymaartId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class TransactionHistoryViewHolder(val b: TransactionListViewBinding) :
        RecyclerView.ViewHolder(b.root) {
        fun setView(transaction: IndividualTransactionHistory) {
            val isCurrentUserDebited =
                transaction.senderId == userPaymaartId || transaction.enteredBy == userPaymaartId

            val amountPrefix = if (isCurrentUserDebited) "-" else "+"
            b.cardTransactionAmountDateTimeTV.text = formatEpochTimeThree(transaction.createdAt)
            b.cardTransactionAmountTV.text = context.getString(
                R.string.formatted_list_amount,
                amountPrefix,
                getFormattedAmount(transaction.transactionAmount)
            )

            // Set common views
            b.cardTransactionNameTV.visibility = View.GONE
            b.cardTransactionIV.visibility = View.GONE
            b.cardTransactionShortNameTV.visibility = View.GONE

            // Set transaction-specific views
            when (transaction.transactionType) {
                CASH_IN, CASHIN -> setTransactionDetails(
                    transaction.senderId, transaction.senderName, R.string.cash_in
                )

                CASH_OUT, CASHOUT, CASH_OUT_REQUEST, CASH_OUT_FAILED -> setTransactionDetails(
                    transaction.receiverId, transaction.receiverName, R.string.cash_out
                )

                PAY_IN -> setTransactionDetails(
                    transaction.enteredBy ?: "", transaction.enteredByName ?: "", R.string.pay_in
                )

                REFUND, PAY_UNREGISTERED_REFUND ->{
                    setTransactionDetails(transaction.senderId, context.getString(R.string.refund), R.string.refund)
                    b.cardTransactionPaymaartIdTV.visibility = View.GONE
                }

                INTEREST -> {
                    setImageTransaction(R.string.interest, R.drawable.ico_paymaart_icon)
                }

                G2P_PAY_IN -> setTransactionDetails(
                    transaction.senderId, transaction.senderName, R.string.g2p_pay_in
                )

                PAYMAART -> setImageTransaction(R.string.paymaart, R.drawable.ico_paymaart_icon)
                AFRIMAX -> setImageTransaction(R.string.afrimax, R.drawable.ico_afrimax)
                PAY_PERSON -> {
                    if (userPaymaartId == transaction.senderId) setPaymaartTransactionType(
                        transaction.receiverName,
                        transaction.receiverId,
                        transaction.receiverProfilePic
                    )
                    else setPaymaartTransactionType(
                        transaction.senderName, transaction.senderId, transaction.senderProfilePic
                    )
                }

                PAY_UNREGISTERED -> {
                    //Using this because
                    setPaymaartTransactionType(
                        transaction.receiverName,
                        PhoneNumberFormatter.formatWholeNumber(transaction.receiverId),
                        transaction.receiverProfilePic
                    )
                }
            }
        }

        private fun setTransactionDetails(id: String?, name: String?, nameResId: Int) {
            b.cardTransactionNameTV.apply {
                visibility = View.VISIBLE
                text = context.getString(nameResId)
            }
            b.cardTransactionPaymaartIdTV.apply {
                visibility = View.VISIBLE
                text = id ?: "-"
            }
            b.cardTransactionShortNameTV.apply {
                visibility = View.VISIBLE
                text = getInitials(name)
            }
        }

        private fun setImageTransaction(nameResId: Int, imageResId: Int) {
            b.cardTransactionNameTV.apply {
                visibility = View.VISIBLE
                text = context.getString(nameResId)
            }
            b.cardTransactionPaymaartIdTV.visibility = View.GONE
            b.cardTransactionShortNameTV.visibility = View.GONE
            b.cardTransactionIV.also {
                it.visibility = View.VISIBLE
                Glide.with(context).load(imageResId).into(it)
            }
        }

        private fun setPaymaartTransactionType(name: String?, userId: String?, image: String?) {
            b.cardTransactionNameTV.apply {
                visibility = View.VISIBLE
                text = name ?: "-"
            }
            b.cardTransactionPaymaartIdTV.apply {
                visibility = View.VISIBLE
                text = userId ?: "-"
            }
            if (image.isNullOrEmpty()) {
                b.cardTransactionIV.visibility = View.GONE
                b.cardTransactionShortNameTV.apply {
                    visibility = View.VISIBLE
                    text = getInitials(name).ifEmpty { "-" }
                }
            } else {
                b.cardTransactionShortNameTV.visibility = View.GONE
                b.cardTransactionIV.also {
                    it.visibility = View.VISIBLE
                    Glide.with(context).load(BuildConfig.CDN_BASE_URL + image).centerCrop().into(it)
                }
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
        const val PAY_UNREGISTERED = "pay_unregister"
        const val PAY_UNREGISTERED_REFUND = "pay_unregister_refund"
    }
}