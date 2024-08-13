package com.afrimax.paymaart.ui.utils.adapters

import android.content.Context
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paymaart.R
import com.afrimax.paymaart.data.model.Transaction
import com.afrimax.paymaart.databinding.DatePaymentListAdapterBinding
import com.afrimax.paymaart.databinding.ReceivedPaymentListAdapterViewBinding
import com.afrimax.paymaart.databinding.SentPaymentListAdapterViewBinding
import com.afrimax.paymaart.util.formatEpochTime
import com.afrimax.paymaart.util.formatEpochTimeFour
import com.afrimax.paymaart.util.formatEpochTimeThree
import com.afrimax.paymaart.util.formatEpochTimeTwo
import com.afrimax.paymaart.util.showLogE

class PaymentListAdapter(val context: Context, private val transactions: List<Transaction>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
        private const val VIEW_TYPE_DATE_HEADER = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when{
            transactions[position].transactionType == "credit" -> VIEW_TYPE_RECEIVED
            transactions[position].showDate -> VIEW_TYPE_DATE_HEADER
            else -> VIEW_TYPE_SENT
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE_SENT -> {
                val view = SentPaymentListAdapterViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SentPaymentViewHolder(view)
            }

            VIEW_TYPE_DATE_HEADER -> {
                val view = DatePaymentListAdapterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                PaymentDateHeaderViewHolder(view)
            }
            else -> {
                val view = ReceivedPaymentListAdapterViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ReceivedPaymentViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val transactions = transactions[position]
        when (holder) {
            is SentPaymentViewHolder -> holder.bind(transactions)
            is ReceivedPaymentViewHolder -> holder.bind(transactions)
            is PaymentDateHeaderViewHolder -> holder.bind(transactions)
        }
    }

    inner class SentPaymentViewHolder(binding: SentPaymentListAdapterViewBinding): RecyclerView.ViewHolder(binding.root){
        private val totalAmount = binding.paymentListAdapterAmount
        private val transactionId = binding.paymentListAdapterTransactionId
        private val date = binding.paymentListAdapterPaymentDateTime
        private val divider = binding.paymentListAdapterDivider
        private val note = binding.paymentListAdapterNoteTV
        fun bind(transaction: Transaction){
            totalAmount.text = transaction.totalAmount
            date.text = formatEpochTime(transaction.createdAt)
            transactionId.text = transaction.transactionId
            note.text = transaction.note ?: ""
            if ( transaction.note.isNullOrEmpty() ){
                note.visibility = View.GONE
                divider.visibility = View.GONE
            }
        }
    }

    inner class ReceivedPaymentViewHolder(binding: ReceivedPaymentListAdapterViewBinding): RecyclerView.ViewHolder(binding.root){
        private val totalAmount = binding.paymentListAdapterAmount
        private val transactionId = binding.paymentListAdapterTransactionId
        private val date = binding.paymentListAdapterPaymentDateTime
        private val divider = binding.paymentListAdapterDivider
        private val note = binding.paymentListAdapterNoteTV
        fun bind(transaction: Transaction){
            totalAmount.text = transaction.totalAmount
            date.text = formatEpochTimeThree(transaction.createdAt)
            transactionId.text = "Txn ID: ${transaction.transactionId}"
            note.text = transaction.note ?: ""
            if ( transaction.note.isNullOrEmpty() ){
                note.visibility = View.GONE
                divider.visibility = View.GONE
            }
        }
    }

    inner class PaymentDateHeaderViewHolder(binding: DatePaymentListAdapterBinding): RecyclerView.ViewHolder(binding.root) {
        private val dateText = binding.datePaymentListDatePill
        fun bind(transaction: Transaction){
            dateText.text = formatEpochTimeFour(transaction.createdAt)
        }
    }

}