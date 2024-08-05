package com.afrimax.paymaart.ui.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paymaart.data.model.IndividualTransactionHistory
import com.afrimax.paymaart.databinding.HomeRecyclerviewAdapterViewBinding

class HomeScreenIconAdapter(
    private val transactionList: List<IndividualTransactionHistory>,
    private val userPaymaartId: String
): RecyclerView.Adapter<HomeScreenIconAdapter.HomeScreenIconViewHolder>() {
    inner class HomeScreenIconViewHolder(val binding: HomeRecyclerviewAdapterViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeScreenIconViewHolder {
        val binding = HomeRecyclerviewAdapterViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HomeScreenIconViewHolder(binding)
    }

    override fun getItemCount() = transactionList.size

    override fun onBindViewHolder(holder: HomeScreenIconViewHolder, position: Int) {
        with(holder) {
            val isCurrentUserDebited = transactionList[position].senderId == userPaymaartId || transactionList[position].enteredBy == userPaymaartId
            binding.iconImage.visibility = View.GONE
            binding.iconNameInitials.text = getInitials(transactionList[position].receiverName)
            binding.iconName.text = getFirstName(transactionList[position].receiverName)
            when (transactionList[position].transactionType) {
                CASH_IN -> {

                }
                CASH_OUT -> {

                }
                AFRIMAX -> {

                }
                PAY_IN -> {

                }
                PAY_OUT -> {

                }
                REFUND -> {

                }
                PAYMAART -> {

                }
                INTEREST -> {

                }
                G2P_PAY_IN -> {

                }
                CASH_OUT_REQUEST -> {

                }
                CASH_OUT_FAILED -> {

                }
            }
        }
    }

    private fun getFirstName(name: String?): String{
        if (name.isNullOrEmpty()) return ""
        return name.split(" ")[0]
    }

    private fun getInitials(name: String?): String {
        if (name.isNullOrEmpty()) return ""
        var initials = ""
        for (word in name.split(" ")) {
            if (word.isNotEmpty()) {
                initials += word.first().uppercase()
            }
        }
        return initials
    }
    companion object {
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