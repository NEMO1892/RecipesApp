package com.example.recipesapp.ui.favourite

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipesapp.databinding.FragmentFavouritesBinding
import com.example.recipesapp.di.MyApplication
import com.example.recipesapp.model.Recipe
import com.example.recipesapp.ui.list.adapter.ListRecipesAdapter
import com.example.recipesapp.ui.navigation.BottomNavigationFragmentDirections
import com.example.recipesapp.util.findTopNavController
import javax.inject.Inject

class FavouritesFragment : Fragment() {

    private var binding: FragmentFavouritesBinding? = null

    private lateinit var viewModel: FavouritesViewModel

    @Inject
    lateinit var viewModelProvider: FavouritesViewModelProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MyApplication.appComponent.inject(this)
        viewModel = ViewModelProvider(this, viewModelProvider)[FavouritesViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.run {
            viewModel.listDisneyRecipes.observe(viewLifecycleOwner) {
                setSharedPreferenceList(it)
            }
            viewModel.getAllRecipes()
        }
    }

    private fun setSharedPreferenceList(list: ArrayList<Recipe>) {
        binding?.run {
            if (recyclerView.adapter == null) {
                recyclerView.adapter = ListRecipesAdapter { id: String ->
                    val action =
                        BottomNavigationFragmentDirections.actionBottomNavigationFragmentToChosenRecipeFragment(
                            id
                        )
                    findTopNavController().navigate(action)
                }
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
            }
            (recyclerView.adapter as? ListRecipesAdapter)?.setList(list)
        }
    }
}