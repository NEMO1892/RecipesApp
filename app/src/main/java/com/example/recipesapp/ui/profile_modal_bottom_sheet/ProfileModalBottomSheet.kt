package com.example.recipesapp.ui.profile_modal_bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.recipesapp.databinding.LayoutBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProfileModalBottomSheet(
    private val takePhotoClick: () -> Unit,
    private val choosePhotoClick: () -> Unit
) : BottomSheetDialogFragment() {

    private var binding: LayoutBottomSheetBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutBottomSheetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.run {
            takePhotoTextView.setOnClickListener {
                takePhotoClick.invoke()
                dismiss()
            }
            chooseFromGalleryTextView.setOnClickListener {
                choosePhotoClick.invoke()
                dismiss()
            }
            cancelTextView.setOnClickListener {
                dismiss()
            }
        }
    }
}