package com.afrimax.paymaart.ui.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paymaart.databinding.HomeRecyclerviewAdapterViewBinding

class HomeScreenIconAdapter(
    private val someList: List<String>
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

    override fun getItemCount() = someList.size

    override fun onBindViewHolder(holder: HomeScreenIconViewHolder, position: Int) {
        with(holder) {
            binding.iconImage.visibility = View.GONE
            binding.iconNameInitials.text = getInitials(someList[position])
            binding.iconName.text = someList[position]
        }
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
}