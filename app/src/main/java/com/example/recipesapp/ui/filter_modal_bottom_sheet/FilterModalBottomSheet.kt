package com.example.recipesapp.ui.filter_modal_bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.example.recipesapp.R
import com.example.recipesapp.databinding.LayoutFilterModalBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FilterModalBottomSheet(
    private val search: (query: String, diet: String?, health: String?) -> Unit
) : BottomSheetDialogFragment() {

    private val viewModel by activityViewModels<FilterModalBottomSheetViewModel>()

    private var binding: LayoutFilterModalBottomSheetBinding? = null

    private var health: String? = ""

    private var diet: String? = ""

    private val auth = FirebaseAuth.getInstance()

    private val database =
        Firebase.database("https://recipesapp-22212-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("Users")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutFilterModalBottomSheetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        readFirebaseData()
        binding?.run {
            viewModel.run {
                getDietState()
                getHealthState()
                dietState.observe(viewLifecycleOwner) {
                    val chip: Chip? = it?.let { it1 -> dietFilterChips.findViewById(it1) }
                    chip?.isChecked = true
                }
                healthState.observe(viewLifecycleOwner) {
                    val chip: Chip? = it?.let { it1 -> healthFilterChips.findViewById(it1) }
                    chip?.isChecked = true
                }
            }
            searchEditText.doAfterTextChanged {
                validateForm()
            }
            filterChips()
            searchButton.setOnClickListener {
                search.invoke(searchEditText.text.toString(), diet, health)
                dismiss()
            }
        }
    }

    private fun validateForm() {
        binding?.run {
            searchButton.isEnabled = searchEditText.text.length > 2
        }
    }

    private fun filterChips() {
        binding?.dietFilterChips?.setOnCheckedChangeListener { group, checkedId ->
            val chip: Chip? = group.findViewById(checkedId)
            if (chip?.isChecked == true) {
                diet = chip.text.toString()
                viewModel.saveDietState(checkedId)
            }
        }

        binding?.healthFilterChips?.setOnCheckedChangeListener { group, checkedId ->
            val chip: Chip? = group.findViewById(checkedId)
            if (chip?.isChecked == true) {
                health = chip.text.toString()
                viewModel.saveHealthState(checkedId)
            }
        }
    }

    private fun readFirebaseData() {
        auth.uid?.let {
            database.child(it).get().addOnSuccessListener { data ->
                val name = data.child("profileName").value as String?
                binding?.titleTextView?.text = "Hello, $name"
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
}