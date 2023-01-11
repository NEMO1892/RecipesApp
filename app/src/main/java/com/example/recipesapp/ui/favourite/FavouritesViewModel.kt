package com.example.recipesapp.ui.favourite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipesapp.model.Recipe
import com.example.recipesapp.repository.SharedPreferencesRepository

class FavouritesViewModel(private val repository: SharedPreferencesRepository) : ViewModel() {

    val listDisneyRecipes = MutableLiveData<ArrayList<Recipe>>()

    fun getAllRecipes() {
        listDisneyRecipes.value = repository.getAllRecipes()
    }
}