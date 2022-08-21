package com.example.recipesapp.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navOptions
import com.example.recipesapp.R
import com.example.recipesapp.databinding.FragmentProfileBinding
import com.example.recipesapp.di.MyApplication
import com.example.recipesapp.model.User
import com.example.recipesapp.ui.edit_text_bottom_sheet.EditTextModalBottomSheet
import com.example.recipesapp.ui.profile_modal_bottom_sheet.ProfileModalBottomSheet
import com.example.recipesapp.util.findTopNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding? = null

    private val auth = FirebaseAuth.getInstance()

    private lateinit var viewModel: ProfileViewModel

    @Inject
    lateinit var viewModelProvider: ProfileViewModelProvider

    private var photoUrl: Uri? = null

    private var photoBitMap: Bitmap? = null

    private val storage = FirebaseStorage.getInstance()

    private val database =
        Firebase.database("https://recipesapp-22212-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("Users")

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                photoUrl = result.data?.data
                binding?.profileImageView?.setImageURI(photoUrl)
            }
        }

    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                photoBitMap = result.data?.extras?.get("data") as Bitmap
                binding?.profileImageView?.setImageBitmap(photoBitMap)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MyApplication.appComponent.inject(this)
        viewModel = ViewModelProvider(this, viewModelProvider)[ProfileViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.run {
            readFirebaseData()
            takePhotoFromStorage()
            floatingChooseButton.setOnClickListener {
                val profileModalBottomSheet = ProfileModalBottomSheet(
                    {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        cameraActivityResultLauncher.launch(intent)
                    }, {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        activityResultLauncher.launch(intent)
                    })
                profileModalBottomSheet.show(
                    requireActivity().supportFragmentManager,
                    profileModalBottomSheet.tag
                )
            }
            saveButton.setOnClickListener {
                if (photoUrl != null) {
                    savePhotoToStorage(photoUrl!!)
                } else if (photoBitMap != null) {
                    getImageUri(photoBitMap!!)?.let { it1 -> savePhotoToStorage(it1) }
                }
            }
            editButton.setOnClickListener {
                val editTextModalBottomSheet = EditTextModalBottomSheet {
                    profileNameTextView.text = it
                    if (profileNameTextView.text.toString() != "" &&
                        profileNameTextView.text.toString() != viewModel.getProfileName()
                    ) {
                        viewModel.saveProfileName(profileNameTextView.text.toString())
                        saveFirebaseData()
                    }
                }
                editTextModalBottomSheet.show(
                    requireActivity().supportFragmentManager,
                    editTextModalBottomSheet.tag
                )
            }
            signOutButton.setOnClickListener {
                auth.signOut()
                findTopNavController().navigate(R.id.logInFragment, null, navOptions {
                    popUpTo(R.id.bottomNavigationFragment) {
                        inclusive = true
                    }
                })
            }
        }
    }

    private fun readFirebaseData() {
        auth.uid?.let {
            database.child(it).get().addOnSuccessListener { data ->
                val name = data.child("profileName").value as String?
                val email = data.child("email").value as String?
                binding?.profileNameTextView?.text = name
                binding?.emailTextView?.text = email
            }
                .addOnFailureListener {
                    Snackbar.make(
                        requireView(),
                        R.string.something_went_wrong_please_try_again_later,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Ok") {}
                        .show()
                }
        }
    }

    private fun saveFirebaseData() {
        auth.uid?.let {
            database.child(it).setValue(
                User(
                    email = binding?.emailTextView?.text.toString(),
                    profileName = binding?.profileNameTextView?.text.toString()
                )
            ).addOnSuccessListener {
                Snackbar.make(requireView(), R.string.new_profile_name_saved, Snackbar.LENGTH_LONG)
                    .setAction("Ok") {}
                    .show()
            }
                .addOnFailureListener {
                    Snackbar.make(
                        requireView(),
                        R.string.something_went_wrong_please_try_again_later,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Ok") {}
                        .show()
                }
        }
    }

    private fun savePhotoToStorage(uri: Uri) {
        val storageReference = storage.getReference("images/${auth.uid}")
        storageReference.putFile(uri)
            .addOnCompleteListener {
                Snackbar.make(requireView(), R.string.new_profile_name_saved, Snackbar.LENGTH_LONG)
                    .setAction("Ok") {}
                    .show()
            }
            .addOnFailureListener {
                Snackbar.make(requireView(), R.string.failure, Snackbar.LENGTH_LONG)
                    .setAction("Ok") {}
                    .show()
            }
    }

    private fun takePhotoFromStorage() {
        binding?.run {
            val storageReference = storage.getReference("images/" + auth.uid)
            val localFile = File.createTempFile("tempFile", ".jpg")
            storageReference.getFile(localFile)
                .addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                    profileImageView.setImageBitmap(bitmap)
                    loadingProgressBar.visibility = View.INVISIBLE
//                binding?.profileImageView?.loadBitMap(bitmap)
                }
                .addOnFailureListener {
                    profileImageView.setImageResource(R.drawable.ic_default_profile_photo)
                    loadingProgressBar.visibility = View.INVISIBLE
                }
        }
    }

    private fun getImageUri(inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(
                requireContext().contentResolver, inImage, "Title", null
            )
        return Uri.parse(path)
    }
}