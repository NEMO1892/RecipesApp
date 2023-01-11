package com.example.recipesapp.ui.list

import com.example.recipesapp.repository.NetworkRepository
import com.example.recipesapp.repository.SharedPreferencesRepository
import com.example.recipesapp.ui.base.BaseViewModelFactory
import javax.inject.Inject

class ListRecipesViewModelProvider @Inject constructor(
    private val repository: NetworkRepository,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) :
    BaseViewModelFactory<ListRecipesViewModel>(ListRecipesViewModel::class.java) {
    override fun createViewModel(): ListRecipesViewModel {
        return ListRecipesViewModel(repository, sharedPreferencesRepository)
    }
}