package com.example.recipesapp.ui.navigation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.recipesapp.R
import com.example.recipesapp.databinding.FragmentBottomSheetBinding
import com.example.recipesapp.di.MyApplication
import com.example.recipesapp.ui.favourite.FavouritesFragment
import com.example.recipesapp.ui.list.ListRecipesFragment
import com.example.recipesapp.ui.profile.ProfileFragment
import javax.inject.Inject

class BottomNavigationFragment : Fragment() {

    private var binding: FragmentBottomSheetBinding? = null

    private lateinit var viewModel: BottomNavigationViewModel

    @Inject
    lateinit var viewModelProvider: BottomNavigationViewModelProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MyApplication.appComponent.inject(this)
        viewModel =
            ViewModelProvider(this, viewModelProvider)[BottomNavigationViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.run {
            viewModel.run {
                listDisneyRecipes.observe(viewLifecycleOwner) {
                    chipNavigationBar.showBadge(R.id.liked, it.size)
                }
                getAllRecipes()
            }

            chipNavigationBar.setItemSelected(R.id.search, true)
            parentFragmentManager.beginTransaction()
                .add(R.id.containerForBottomNavigationSheet, ListRecipesFragment())
//            .addToBackStack("")
                .commit()
            chipNavigationBar.setOnItemSelectedListener {
                when (it) {
                    R.id.search -> {
                        parentFragmentManager.beginTransaction()
                            .add(R.id.containerForBottomNavigationSheet, ListRecipesFragment())
//                        .addToBackStack("")
                            .commit()
                    }
                    R.id.liked -> {
                        parentFragmentManager.beginTransaction()
                            .add(R.id.containerForBottomNavigationSheet, FavouritesFragment())
//                        .addToBackStack("")
                            .commit()
                    }
                    R.id.profile -> {
                        parentFragmentManager.beginTransaction()
                            .add(R.id.containerForBottomNavigationSheet, ProfileFragment())
                            .commit()
                    }
                }
            }
        }
    }
}