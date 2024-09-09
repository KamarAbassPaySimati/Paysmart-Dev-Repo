package com.afrimax.paymaart.ui.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paymaart.BuildConfig
import com.afrimax.paymaart.common.presentation.utils.PhoneNumberFormatter
import com.afrimax.paymaart.data.model.PayPerson
import com.afrimax.paymaart.databinding.PayPersonAdapterViewBinding
import com.afrimax.paymaart.util.getInitials
import com.bumptech.glide.Glide

class PayPersonListAdapter(private val contacts: List<PayPerson>) :
    RecyclerView.Adapter<PayPersonListAdapter.ContactsViewHolder>() {
    private var onClickListener: OnClickListener? = null

    inner class ContactsViewHolder(val binding: PayPersonAdapterViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val view = PayPersonAdapterViewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ContactsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val contact = contacts[position]
        with(holder) {
            binding.payPersonName.visibility = View.GONE
            binding.payPersonUserPhoneNumber.visibility = View.GONE
            binding.payPersonUserId.visibility = View.GONE
            binding.payPersonShortNameTV.visibility = View.GONE
            binding.payPersonIV.visibility = View.GONE

            binding.payPersonName.apply {
                visibility = View.VISIBLE
                text = contacts[position].fullName
            }
            if (!contact.phoneNumber.isNullOrEmpty() && !contact.paymaartId.isNullOrEmpty()) {
                //registered customer
                binding.payPersonUserPhoneNumber.visibility = View.VISIBLE
                binding.payPersonUserPhoneNumber.text =
                    contact.let { PhoneNumberFormatter.formatWholeNumber(it.countryCode + it.phoneNumber) }

                binding.payPersonUserId.visibility = View.VISIBLE
                binding.payPersonUserId.text = contact.paymaartId
            } else if (contact.phoneNumber.isNullOrEmpty() && !contact.paymaartId.isNullOrEmpty()) {
                //Unregistered customer
                binding.payPersonUserPhoneNumber.visibility = View.VISIBLE
                binding.payPersonUserPhoneNumber.text =
                    PhoneNumberFormatter.formatWholeNumber(contact.paymaartId)
            } else if (contact.paymaartId.isNullOrEmpty() && !contact.phoneNumber.isNullOrEmpty()) {
                //Unregistered customer
                binding.payPersonUserPhoneNumber.visibility = View.VISIBLE
                binding.payPersonUserPhoneNumber.text =
                    PhoneNumberFormatter.formatWholeNumber(contact.phoneNumber)
            }

            if (contacts[position].profilePicture.isNullOrEmpty()) {
                binding.payPersonShortNameTV.apply {
                    visibility = View.VISIBLE
                    text = getInitials(contacts[position].fullName)
                }
            } else {
                binding.payPersonIV.also {
                    it.visibility = View.VISIBLE
                    Glide.with(it)
                        .load(BuildConfig.CDN_BASE_URL + contacts[position].profilePicture)
                        .centerCrop().into(it)
                }
            }
            binding.root.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(contacts[position])
                }
            }
        }
    }

    override fun getItemCount(): Int = contacts.size

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(transaction: PayPerson)
    }

    private fun formattedPhoneNumber(phoneNumber: String?): String {
        if (phoneNumber == null) return ""
        return try {
            "${phoneNumber.substring(0, 2)} ${
                phoneNumber.substring(
                    2, 6
                )
            } ${phoneNumber.substring(6)}"
        } catch (e: StringIndexOutOfBoundsException) {
            phoneNumber
        }
    }
}