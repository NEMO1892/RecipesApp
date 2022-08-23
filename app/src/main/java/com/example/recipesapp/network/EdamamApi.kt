package com.example.recipesapp.network

import com.example.recipesapp.model.Hits
import com.example.recipesapp.model.RecipeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EdamamApi {


    @GET("/api/recipes/v2")
    suspend fun searchRecipes(
        @Query("q") query: String,
        @Query("app_key") appKey: String? = APP_KEY,
        @Query("type") type: String? = "public",
        @Query("app_id") appId: String? = APP_ID,
        @Query("diet") diet: String? = null,
        @Query("health") health: String? = null
    ): Response<RecipeResponse>

    @GET("/api/recipes/v2/{id}")
    suspend fun getOneRecipe(
        @Path("id") id: String? = "8275bb28647abcedef0baaf2dcf34f8b",
        @Query("type") type: String? = "public",
        @Query("app_key") appKey: String? = APP_KEY,
        @Query("app_id") appId: String? = APP_ID
    ): Response<Hits>
}