package com.afrimax.paymaart.ui.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paymaart.data.model.MembershipPlan
import com.afrimax.paymaart.databinding.HomeRecyclerviewAdapterViewBinding
import com.afrimax.paymaart.databinding.MembershipPlansAdapterViewBinding

class MembershipPlanAdapter(
    private val membershipPlans: List<MembershipPlan>
): RecyclerView.Adapter<MembershipPlanAdapter.MembershipPlanViewHolder>() {
    inner class MembershipPlanViewHolder(val binding: MembershipPlansAdapterViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembershipPlanViewHolder {
        val binding = MembershipPlansAdapterViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MembershipPlanViewHolder(binding)
    }

    override fun getItemCount() = membershipPlans.size

    override fun onBindViewHolder(holder: MembershipPlanViewHolder, position: Int) {
        with(holder) {
            binding.membershipPlanNameTV.text = membershipPlans[position].serviceBeneficiary ?: ""
            binding.membershipPlanSubTitleTV.text = membershipPlans[position].subtitle ?: ""
            binding.membershipPlanPrimeTV.text = membershipPlans[position].prime ?: ""
            binding.membershipPlanPrimeXTV.text = membershipPlans[position].primeX ?: ""
            binding.membershipPlanGoTV.text = membershipPlans[position].go ?: ""
        }
    }
}