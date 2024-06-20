package com.afrimax.paymaart.ui.utils.adapters

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paymaart.R
import com.afrimax.paymaart.databinding.KycInstituteAdapterViewBinding

class KycInstituteAdapter(
    private val context: Context, private val instituteList: ArrayList<String>
) : RecyclerView.Adapter<KycInstituteAdapter.KycInstituteViewHolder>() {

    private var onClickListener: OnClickListener? = null

    class KycInstituteViewHolder(b: KycInstituteAdapterViewBinding) : RecyclerView.ViewHolder(b.root) {
        val instituteTV = b.cardInstituteTV
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KycInstituteViewHolder {
        val b = KycInstituteAdapterViewBinding.inflate(
            LayoutInflater.from(context), parent, false
        )

        //If the option is 'Others' then we show ico_next_arrow at the end of the TextView
        if (viewType == 1) b.cardInstituteTV.setCompoundDrawablesWithIntrinsicBounds(
            0, 0, R.drawable.ico_next_arrow_black, 0
        )

        return KycInstituteViewHolder(b)
    }

    override fun getItemCount(): Int {
        return instituteList.size
    }

    override fun getItemViewType(position: Int): Int {
        val instituteName = instituteList[position]

        //If the option is 'Others' then return 1 otherwise 0
        if (instituteName.contains(context.getString(R.string.other), ignoreCase = true)) return 1
        return 0
    }

    override fun onBindViewHolder(holder: KycInstituteViewHolder, position: Int) {
        val instituteName = instituteList[position]

        holder.instituteTV.text = instituteName

        holder.instituteTV.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, instituteName, it)
            }
        }

    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, instituteName: String, view: View)
    }
}

class KycInstituteDecoration(private val margin: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.left = margin
        outRect.right = margin
    }
}