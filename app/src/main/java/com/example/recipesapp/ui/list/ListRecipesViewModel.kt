package com.example.recipesapp.ui.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipesapp.model.Recipe
import com.example.recipesapp.model.RecipeLoadingState
import com.example.recipesapp.repository.NetworkRepository
import com.example.recipesapp.repository.SharedPreferencesRepository
import kotlinx.coroutines.*

class ListRecipesViewModel(
    private val repository: NetworkRepository,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : ViewModel() {

    val listRecipes = MutableLiveData<ArrayList<Recipe>>()

    val recipeLoadingStateLiveData = MutableLiveData<RecipeLoadingState>()

    private var debouncePeriod: Long = 800

    private var searchJob: Job? = null

    fun onSearchQuery(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(debouncePeriod)
            if (query.length > 2) {
                fetchRecipeByQuery(query)
            }
        }
    }

    fun fetchRecipeByQuery(query: String, diet: String? = null, heath: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                recipeLoadingStateLiveData.postValue(RecipeLoadingState.LOADING)
                val response = repository.searchRecipes(query = query, diet = diet, health = heath)
                if (response.isSuccessful) {
                    response.body()?._links?.next?.href
                    listRecipes.postValue(response.body()?.hits?.map {
                        it.recipe
                    } as ArrayList<Recipe>)
                    if (listRecipes.value?.isNotEmpty() == true) {
                        sharedPreferencesRepository.addTypeOfDish(query)
                    }
                } else {
                    response.errorBody()
                }
                recipeLoadingStateLiveData.postValue(RecipeLoadingState.LOADED)
            } catch (e: Exception) {
                recipeLoadingStateLiveData.postValue(RecipeLoadingState.INVALID_API_KEY)
            }
        }
    }

    fun getRandomTypeOfDish() = sharedPreferencesRepository.getRandomTypeOfDish()
}

