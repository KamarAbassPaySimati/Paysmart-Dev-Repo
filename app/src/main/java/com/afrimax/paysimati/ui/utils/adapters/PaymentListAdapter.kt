package com.afrimax.paysimati.ui.utils.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paysimati.data.model.Transaction
import com.afrimax.paysimati.databinding.DatePaymentListAdapterBinding
import com.afrimax.paysimati.databinding.ReceivedPaymentListAdapterViewBinding
import com.afrimax.paysimati.databinding.SentPaymentListAdapterViewBinding
import com.afrimax.paysimati.util.formatEpochTime
import com.afrimax.paysimati.util.formatEpochTimeFour
import com.afrimax.paysimati.util.formatEpochTimeThree
import com.afrimax.paysimati.util.getFormattedAmount

class PaymentListAdapter(val context: Context, private val transactions: List<Transaction>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
        private const val VIEW_TYPE_DATE_HEADER = 3
    }

    override fun getItemViewType(position: Int): Int {
        if (transactions[position].showDate)
            return VIEW_TYPE_DATE_HEADER
        if(transactions[position].transactionType == "credit")
            return VIEW_TYPE_RECEIVED
        if (transactions[position].transactionType == "debit")
            return VIEW_TYPE_SENT
        //Default - the list item should be either of these three.
        return -1
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
            totalAmount.text = getFormattedAmount(transaction.totalAmount)
            date.text = formatEpochTime(transaction.createdAt)
            transactionId.text = "Txn ID: ${transaction.transactionId}"
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
            totalAmount.text = getFormattedAmount(transaction.totalAmount)
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