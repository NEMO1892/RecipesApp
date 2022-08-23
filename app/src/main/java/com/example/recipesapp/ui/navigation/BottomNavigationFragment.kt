package com.example.recipesapp.ui.navigation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.animation.BounceInterpolator
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.recipesapp.R
import com.example.recipesapp.databinding.FragmentBottomSheetBinding
import com.example.recipesapp.di.MyApplication
import nl.joery.animatedbottombar.AnimatedBottomBar
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

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.run {
            viewModel.run {
                listDisneyRecipes.observe(viewLifecycleOwner) {
                    bottomBar.setBadgeAtTabId(
                        R.id.favourites_graph,
                        AnimatedBottomBar.Badge("${it.size}")
                    )
                }
                getAllRecipes()
            }
            val navHost =
                childFragmentManager.findFragmentById(R.id.bottom_nav_container) as NavHostFragment
            val navController = navHost.navController
            val menuBar = MenuBuilder(context)
            MenuInflater(context).inflate(R.menu.menu_bottom_navigation, menuBar)
            bottomBar.animationInterpolator = BounceInterpolator()
            bottomBar.setupWithNavController(menuBar, navController)
        }
    }

}