package com.example.recipesapp.ui.navigation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipesapp.model.Recipe
import com.example.recipesapp.repository.SharedPreferencesRepository

class BottomNavigationViewModel(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : ViewModel() {

    val listDisneyRecipes = MutableLiveData<ArrayList<Recipe>>()

    fun getAllRecipes() {
        listDisneyRecipes.value = sharedPreferencesRepository.getAllRecipes()
    }
}