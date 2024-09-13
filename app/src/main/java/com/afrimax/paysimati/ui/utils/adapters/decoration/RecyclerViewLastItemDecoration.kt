package com.afrimax.paysimati.ui.utils.adapters.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewLastItemDecoration(private val margin: Int): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        // Add padding to the bottom of the last item
        if (position == itemCount - 1) {
            outRect.bottom = margin
        }
    }
}