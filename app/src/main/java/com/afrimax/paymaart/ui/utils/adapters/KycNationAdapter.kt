package com.afrimax.paymaart.ui.utils.adapters

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paymaart.databinding.KycNationalityAdapterViewBinding

class KycNationAdapter(
    private val context: Context, private val nationList: ArrayList<String>
) : RecyclerView.Adapter<KycNationAdapter.KycNationViewHolder>() {

    private var onClickListener: OnClickListener? = null

    class KycNationViewHolder(b: KycNationalityAdapterViewBinding) : RecyclerView.ViewHolder(b.root) {
        val nationTV = b.cardNationTV
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KycNationViewHolder {
        val b = KycNationalityAdapterViewBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return KycNationViewHolder(b)
    }

    override fun getItemCount(): Int {
        return nationList.size
    }

    override fun onBindViewHolder(holder: KycNationViewHolder, position: Int) {
        val nation = nationList[position]

        holder.nationTV.text = nation

        holder.nationTV.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, nation, it)
            }
        }

    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, nation: String, view: View)
    }
}

class KycNationDecoration(private val margin: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.left = margin
        outRect.right = margin
    }
}