package com.example.recipesapp.ui.edit_text_bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.recipesapp.databinding.LayoutEditTextBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditTextModalBottomSheet(
    private val acceptChanges: (profileName: String) -> Unit
) : BottomSheetDialogFragment() {

    private var binding: LayoutEditTextBottomSheetBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutEditTextBottomSheetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.run {
            newProfileEditText.setOnFocusChangeListener { _, focused ->
                if (focused) {
                    newProfileEditText.error = null
                }
            }
            acceptButton.setOnClickListener {
                if (newProfileEditText.text.length > 1) {
                    acceptChanges.invoke(newProfileEditText.text.toString())
                    dismiss()
                } else {
                    newProfileEditText.error = "Profile name must contain at least 2 character!"
                }
            }
            cancelButton.setOnClickListener {
                dismiss()
            }
        }
    }
}