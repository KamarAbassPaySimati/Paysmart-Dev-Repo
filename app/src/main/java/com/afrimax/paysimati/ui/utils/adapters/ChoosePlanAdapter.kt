package com.afrimax.paysimati.ui.utils.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paysimati.R
import com.afrimax.paysimati.data.model.AfrimaxPlan
import com.afrimax.paysimati.databinding.CardChoosePlanBinding
import com.afrimax.paysimati.databinding.CardPagerLoaderBinding
import java.util.Locale

class ChoosePlanAdapter(
    private val context: Context, private val plansList: ArrayList<AfrimaxPlan>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    class ChoosePlanViewHolder(b: CardChoosePlanBinding) : RecyclerView.ViewHolder(b.root) {
        val cardChoosePlan = b.cardChoosePlan
        val cardChoosePlanNameTV = b.cardChoosePlanNameTV
        val cardChoosePlanPriceTV = b.cardChoosePlanPriceTV
        val cardChoosePlanDataTV = b.cardChoosePlanDataTV
        val cardChoosePlanValidityTV = b.cardChoosePlanValidityTV
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

            else -> ChoosePlanViewHolder(
                CardChoosePlanBinding.inflate(
                    LayoutInflater.from(context), parent, false
                )
            )
        }

    }

    override fun getItemCount(): Int {
        return plansList.size
    }

    override fun onBindViewHolder(h: RecyclerView.ViewHolder, position: Int) {
        if (h is ChoosePlanViewHolder) {
            val plan = plansList[position]

            val price = "${String.format(Locale.US, "%,.2f", plan.price.toDouble())} MWK"
            h.cardChoosePlanPriceTV.text = price

            h.cardChoosePlanNameTV.text = plan.serviceName[0]
            h.cardChoosePlanDataTV.text = plan.serviceName[1]
            h.cardChoosePlanValidityTV.text = plan.serviceName[2]

            //If selected then change background
            h.cardChoosePlan.background = if (plan.isSelected == true) ContextCompat.getDrawable(
                context, R.drawable.bg_choose_plan_selected
            )
            else ContextCompat.getDrawable(context, R.drawable.bg_choose_plan_unselected)

            h.cardChoosePlan.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, plan, it)
                }
            }
        }

    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun getItemViewType(position: Int): Int {
        val data = plansList[position]
        return when (data.viewType) {
            "loader" -> PAGER_LOADER
            else -> PLANS_DATA
        }
    }

    interface OnClickListener {
        fun onClick(position: Int, plan: AfrimaxPlan, view: View)
    }

    companion object {
        const val PLANS_DATA = 1
        const val PAGER_LOADER = 2
    }
}