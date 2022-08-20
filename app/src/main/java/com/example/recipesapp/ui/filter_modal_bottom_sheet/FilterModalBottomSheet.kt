package com.example.recipesapp.ui.filter_modal_bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.example.recipesapp.databinding.LayoutFilterModalBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip

class FilterModalBottomSheet(
    private val search: (query: String, diet: String?, health: String?) -> Unit
) : BottomSheetDialogFragment() {

    private val viewModel by activityViewModels<FilterModalBottomSheetViewModel>()

    private var binding: LayoutFilterModalBottomSheetBinding? = null

    private var health: String? = ""

    private var diet: String? = ""

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

//        binding?.dietFilterChips?.setOnCheckedStateChangeListener { group, checkedIds ->
//
//        }
    }
}