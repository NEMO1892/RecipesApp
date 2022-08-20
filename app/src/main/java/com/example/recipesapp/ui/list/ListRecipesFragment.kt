package com.example.recipesapp.ui.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipesapp.R
import com.example.recipesapp.connectivity.ConnectivityLiveData
import com.example.recipesapp.databinding.FragmentListRecipesBinding
import com.example.recipesapp.di.MyApplication
import com.example.recipesapp.model.Recipe
import com.example.recipesapp.model.RecipeLoadingState
import com.example.recipesapp.ui.chosen_recipe.ChosenRecipeFragment
import com.example.recipesapp.ui.chosen_recipe.ID_RECIPE
import com.example.recipesapp.ui.filter_modal_bottom_sheet.FilterModalBottomSheet
import com.example.recipesapp.ui.list.adapter.ListRecipesAdapter
import com.google.android.material.chip.Chip
import javax.inject.Inject

class ListRecipesFragment : Fragment() {

    private var binding: FragmentListRecipesBinding? = null

    private lateinit var viewModel: ListRecipesViewModel

    @Inject
    lateinit var viewModelProvider: ListRecipesViewModelProvider

    @Inject
    lateinit var connectivityLiveData: ConnectivityLiveData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListRecipesBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MyApplication.appComponent.inject(this)
        viewModel =
            ViewModelProvider(this, viewModelProvider)[ListRecipesViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseObservers()
        binding?.run {
            viewModel.run {
                fetchRecipeByQuery(getRandomTypeOfDish())
                searchEditText.doAfterTextChanged {
                    onSearchQuery(it.toString())
                }
                filterImageButton.setOnClickListener {
                    val filterModalBottomSheet = FilterModalBottomSheet { query, diet, health ->
                        if (diet != "" && health != "") {
                            createChips(diet)
                            createChips(health)
                            fetchRecipeByQuery(query, diet, health)
                        } else if (diet != "") {
                            createChips(diet)
                            fetchRecipeByQuery(query, diet)
                        } else if (health != "") {
                            createChips(health)
                            fetchRecipeByQuery(query, health)
                        }
                    }
                    filterModalBottomSheet.show(
                        requireActivity().supportFragmentManager,
                        filterModalBottomSheet.tag
                    )
                }
            }
        }
    }

    private fun createChips(name: String?) {
        val chip = Chip(requireContext())
        chip.apply {
            text = name
            isClickable = true
            isCheckable = true
            chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_empty_heart)
            isChipIconVisible = false
            isCloseIconVisible = true
            binding?.run {
                entryChips.addView(chip as? View)
                chip.setOnCloseIconClickListener {
                    entryChips.removeView(chip as? View)
                }
            }
        }
    }

    private fun initialiseObservers() {
        binding?.run {
            viewModel.run {
                listRecipes.observe(viewLifecycleOwner) {
                    initAdapter(it)
                }
                recipeLoadingStateLiveData.observe(viewLifecycleOwner) {
                    onRecipeLoadingStateChanged(it)
                }
                connectivityLiveData.observe(viewLifecycleOwner) { isAvailable ->
                    when (isAvailable) {
                        true -> {
                            statusButton.visibility = View.GONE
                            searchEditText.visibility = View.VISIBLE
                            filterImageButton.visibility = View.VISIBLE
                            recyclerView.visibility = View.VISIBLE
                        }
                        false -> {
                            statusButton.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                            filterImageButton.visibility = View.GONE
                            searchEditText.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun onRecipeLoadingStateChanged(state: RecipeLoadingState) {
        binding?.run {
            when (state) {
                RecipeLoadingState.LOADING -> {
                    statusButton.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    loadingProgressBar.visibility = View.VISIBLE
                }
                RecipeLoadingState.LOADED -> {
                    statusButton.visibility = View.GONE
                    searchEditText.visibility = View.VISIBLE
                    filterImageButton.visibility = View.VISIBLE
                    recyclerView.visibility = View.VISIBLE
                    loadingProgressBar.visibility = View.GONE
                }
                RecipeLoadingState.ERROR -> {
                    statusButton.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    loadingProgressBar.visibility = View.GONE
                }
                else -> {
                    statusButton.visibility = View.VISIBLE
                    statusButton.text = "Invalid api key"
                    recyclerView.visibility = View.GONE
                    loadingProgressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun initAdapter(list: ArrayList<Recipe>) {
        binding?.run {
            if (recyclerView.adapter == null) {
                recyclerView.adapter = ListRecipesAdapter { id: String ->
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.container, ChosenRecipeFragment().apply {
                            arguments = bundleOf(ID_RECIPE to id)
                        })
                        .addToBackStack("")
                        .commit()
                }
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
            }
            (recyclerView.adapter as? ListRecipesAdapter)?.setList(list)
        }
    }
}