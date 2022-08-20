package com.example.recipesapp.ui.profile_modal_bottom_sheet

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.recipesapp.R
import com.example.recipesapp.databinding.LayoutBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ProfileModalBottomSheet(
    private val takePhotoClick: () -> Unit,
    private val choosePhotoClick: () -> Unit
) : BottomSheetDialogFragment() {

    private var binding: LayoutBottomSheetBinding? = null

    private val auth = FirebaseAuth.getInstance()

    private val storage = FirebaseStorage.getInstance()

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
            takePhotoFromStorage()
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

    private fun takePhotoFromStorage() {
        val storageReference = storage.getReference("images/" + auth.uid)
        val localFile = File.createTempFile("tempFile", ".jpg")
        storageReference.getFile(localFile)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                binding?.profileImageView?.setImageBitmap(bitmap)
            }
            .addOnFailureListener {
                binding?.profileImageView?.setImageResource(R.drawable.ic_default_profile_photo)
            }
    }
}