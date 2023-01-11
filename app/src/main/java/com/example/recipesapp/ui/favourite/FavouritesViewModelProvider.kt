package com.example.recipesapp.ui.favourite

import com.example.recipesapp.repository.SharedPreferencesRepository
import com.example.recipesapp.ui.base.BaseViewModelFactory
import javax.inject.Inject

class FavouritesViewModelProvider @Inject constructor(
    private val repository: SharedPreferencesRepository
) : BaseViewModelFactory<FavouritesViewModel>(FavouritesViewModel::class.java) {

    override fun createViewModel(): FavouritesViewModel {
        return FavouritesViewModel(repository)
    }
}