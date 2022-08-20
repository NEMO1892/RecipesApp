package com.example.recipesapp.repository

import com.example.recipesapp.network.EdamamApi
import javax.inject.Inject

class NetworkRepository @Inject constructor(private val api: EdamamApi) {

    suspend fun searchRecipes(query: String, diet: String? = null, health: String? = null) =
        api.searchRecipes(query = query, diet = diet, health = health)

    suspend fun getOneRecipe(id: String) = api.getOneRecipe(id = id)
}