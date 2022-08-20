package com.example.recipesapp.ui.chosen_recipe

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipesapp.model.Hits
import com.example.recipesapp.model.Recipe
import com.example.recipesapp.repository.NetworkRepository
import com.example.recipesapp.repository.SharedPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChosenRecipeViewModel(
    private val repository: NetworkRepository,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : ViewModel() {

    val oneRecipe = MutableLiveData<Hits>()

    fun addRecipe(recipe: Recipe) = sharedPreferencesRepository.addRecipe(recipe)

    fun deleteRecipe(recipe: Recipe) = sharedPreferencesRepository.deleteRecipe(recipe)

    fun isRecipeAdded(recipe: Recipe): Boolean = sharedPreferencesRepository.isRecipeAdded(recipe)

    fun getOneCharacter(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getOneRecipe(id = id)
            if (response.isSuccessful) {
                oneRecipe.postValue(response.body())
            } else {
                response.errorBody()
            }
        }
    }
}