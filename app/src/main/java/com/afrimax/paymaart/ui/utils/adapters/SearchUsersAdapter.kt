package com.afrimax.paymaart.ui.utils.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.afrimax.paymaart.data.model.IndividualSearchUserData
import com.afrimax.paymaart.databinding.CardPagerLoaderBinding
import com.afrimax.paymaart.databinding.SearchCustomersAdapterViewBinding
import com.afrimax.paymaart.ui.cashout.CashOutSearchActivity

class SearchUsersAdapter(
    private val context: Context, private val userList: ArrayList<IndividualSearchUserData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    class SearchUsersViewHolder(b: SearchCustomersAdapterViewBinding) : RecyclerView.ViewHolder(b.root) {
        val cardUserSearchResult = b.cardUserSearchResult
        val cardUserSearchShortNameTV = b.cardUserSearchShortNameTV
        val cardUserSearchNameTV = b.cardUserSearchNameTV
        val cardUserSearchResultPaymaartIdTV = b.cardUserSearchResultPaymaartIdTV
        val cardUserSearchResultPhoneTV = b.cardUserSearchResultPhoneTV
    }

    class PagerLoaderViewHolder(b: CardPagerLoaderBinding) : RecyclerView.ViewHolder(b.root) {
        val cardPagerLoader = b.cardPagerLoader
        val cardPagerLoaderLottie = b.cardPagerLoaderLottie
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            PAGER_LOADER -> PagerLoaderViewHolder(
                CardPagerLoaderBinding.inflate(
                    LayoutInflater.from(context), parent, false
                )
            )

            else -> SearchUsersViewHolder(
                SearchCustomersAdapterViewBinding.inflate(
                    LayoutInflater.from(context), parent, false
                )
            )
        }

    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SearchUsersViewHolder) {
            val userData = userList[position]

            holder.cardUserSearchNameTV.text = userData.name
            holder.cardUserSearchResultPaymaartIdTV.text = userData.paymaartId

           when (context) {
               is CashOutSearchActivity -> {holder.cardUserSearchResultPhoneTV.visibility = View.GONE}
               else -> {
                   val formattedNumber = StringBuilder(userData.phoneNumber)
                   formattedNumber.insert(2, ' ')
                   formattedNumber.insert(6, ' ')
                   val phone = "${userData.countryCode} $formattedNumber"
                   holder.cardUserSearchResultPhoneTV.text = phone
               }
           }

            val nameList = userData.name.uppercase().split(" ")
            val shortName = "${nameList[0][0]}${nameList[1][0]}${nameList[2][0]}"
            holder.cardUserSearchShortNameTV.text = shortName

            holder.cardUserSearchResult.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(userData)
                }
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        val data = userList[position]
        return when (data.viewType) {
            "loader" -> PAGER_LOADER
            else -> USER_DATA
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(userData: IndividualSearchUserData)
    }

    companion object {
        const val USER_DATA = 1
        const val PAGER_LOADER = 2
    }
}