package com.afrimax.paysimati.ui.utils.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.afrimax.paysimati.databinding.TillNumberBottomSheetBinding
import com.afrimax.paysimati.ui.utils.adapters.TillNumberAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TillNumberBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: TillNumberBottomSheetBinding
    private var itemList: List<String> = emptyList()

    companion object {
        const val TAG = "TillNumberBottomSheet"

        fun newInstance(items: List<String>): TillNumberBottomSheet {
            val fragment = TillNumberBottomSheet()
            val args = Bundle()
            args.putStringArrayList("items", ArrayList(items))
            fragment.arguments = args
            return fragment
        }
    }
    private fun setUpListeners() {
        binding.sendPaymentClose.setOnClickListener{
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = TillNumberBottomSheetBinding.inflate(inflater, container, false)

        // Get the data passed to the bottom sheet
        itemList = arguments?.getStringArrayList("items") ?: emptyList()

        val gridAdapter = TillNumberAdapter(itemList)
        binding.gridRecyclerView.layoutManager = GridLayoutManager(context, 3) // 3 columns
        binding.gridRecyclerView.adapter = gridAdapter

        setUpListeners()

        return binding.root
    }
}