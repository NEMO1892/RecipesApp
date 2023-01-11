package com.example.recipesapp.ui.navigation

import com.example.recipesapp.repository.SharedPreferencesRepository
import com.example.recipesapp.ui.base.BaseViewModelFactory
import javax.inject.Inject

class BottomNavigationViewModelProvider @Inject constructor(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : BaseViewModelFactory<BottomNavigationViewModel>(BottomNavigationViewModel::class.java) {

    override fun createViewModel(): BottomNavigationViewModel {
        return BottomNavigationViewModel(sharedPreferencesRepository)
    }
}