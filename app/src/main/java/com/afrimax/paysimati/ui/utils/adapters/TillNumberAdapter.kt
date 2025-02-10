package com.afrimax.paysimati.ui.utils.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paysimati.databinding.TillNumberGridBinding


class TillNumberAdapter(
    private val items: List<String>
) : RecyclerView.Adapter<TillNumberAdapter.GridViewHolder>() {

    inner class GridViewHolder(val binding: TillNumberGridBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = TillNumberGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val item = items[position]
        holder.binding.textView.text = item
    }

    override fun getItemCount(): Int = items.size
}
