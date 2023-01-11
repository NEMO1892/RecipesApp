package com.example.recipesapp.ui.chosen_recipe

import com.example.recipesapp.repository.NetworkRepository
import com.example.recipesapp.repository.SharedPreferencesRepository
import com.example.recipesapp.ui.base.BaseViewModelFactory
import javax.inject.Inject

class ChosenRecipeViewModelProvider @Inject constructor(
    private val repository: NetworkRepository,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : BaseViewModelFactory<ChosenRecipeViewModel>(ChosenRecipeViewModel::class.java) {

    override fun createViewModel(): ChosenRecipeViewModel {
        return ChosenRecipeViewModel(repository, sharedPreferencesRepository)
    }
}