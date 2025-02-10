package com.afrimax.paysimati.ui.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afrimax.paysimati.databinding.RecyclerItemImageBinding
import com.bumptech.glide.Glide

class ImageGridAdapter(
    private val imageList: List<String>
) : RecyclerView.Adapter<ImageGridAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class ViewHolder(val binding: RecyclerItemImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = RecyclerItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUrl = imageList[position]
        with(holder) {
            // Load the image into the ShapeableImageView using Glide
            if (imageUrl.isNotEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(imageUrl)
                    .centerCrop()
                    .into(binding.shapeableImageView)
            }

            // Set an OnClickListener on the image
            binding.root.setOnClickListener {
                onClickListener?.onClick(imageUrl)
            }
        }
    }

    override fun getItemCount(): Int = imageList.size

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(imageUrl: String)
    }
}
