package com.afrimax.paymaart.ui.utils.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paymaart.data.model.PayPerson
import com.afrimax.paymaart.databinding.PayPersonAdapterViewBinding
import com.afrimax.paymaart.ui.payperson.Contacts

class PayPersonListAdapter(private val contacts: List<PayPerson>) : RecyclerView.Adapter<PayPersonListAdapter.ContactsViewHolder>(){

    inner class ContactsViewHolder(val binding: PayPersonAdapterViewBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val view = PayPersonAdapterViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        with(holder){
            binding.payPersonName.text = contacts[position].fullName
            binding.payPersonUserPhoneNumber.text = contacts[position].phoneNumber
            binding.payPersonUserId.text = contacts[position].paymaartId
        }
    }

    override fun getItemCount(): Int = contacts.size

}