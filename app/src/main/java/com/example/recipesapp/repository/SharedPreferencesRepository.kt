package com.example.recipesapp.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.recipesapp.model.Recipe
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

const val DEFAULT_PREFERENCE = "default_preference"

const val LIST_RECIPES = "list_recipes"

const val PROFILE_NAME = "profile_name"

const val LIST_TYPES_OF_DISHES = "list_types_of_dishes"

@Singleton
class SharedPreferencesRepository @Inject constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(DEFAULT_PREFERENCE, Context.MODE_PRIVATE)

    fun saveProfileName(profileName: String) {
        sharedPreferences.edit {
            putString(PROFILE_NAME, profileName)
        }
    }

    fun getProfileName(): String? {
        return sharedPreferences.getString(PROFILE_NAME, "")
    }

    private fun saveListTypesOfDishes() {
        val list: ArrayList<String> = arrayListOf(
            "chicken", "biscuits", "cakes", "pasta", "pizza", "coffee", "meat", "fruits",
            "vegetables", "grains", "fish", "soup", "cheese"
        )
        val serializeList = Gson().toJson(list)
        sharedPreferences.edit {
            putString(LIST_TYPES_OF_DISHES, serializeList)
        }
    }

    private fun getListTypesOfDishes(): MutableSet<String> {
        val listType = object : TypeToken<MutableSet<String>>() {}.type
        var stringTypeOfDish = sharedPreferences.getString(LIST_TYPES_OF_DISHES, "")
        if (stringTypeOfDish.isNullOrBlank()) {
            saveListTypesOfDishes()
            stringTypeOfDish = sharedPreferences.getString(LIST_TYPES_OF_DISHES, "")
            return Gson().fromJson(stringTypeOfDish, listType)
        }
        return Gson().fromJson(stringTypeOfDish, listType)
    }

    fun getRandomTypeOfDish(): String {
        return getListTypesOfDishes().random()
    }

    fun addTypeOfDish(type: String) {
        val existList = getListTypesOfDishes()
        existList.add(type)
        val serializeList = Gson().toJson(existList)
        sharedPreferences.edit {
            putString(LIST_TYPES_OF_DISHES, serializeList)
        }
    }

    fun getAllRecipes(): ArrayList<Recipe> {
        val listType = object : TypeToken<ArrayList<Recipe>>() {}.type
        val stringRecipe = sharedPreferences.getString(LIST_RECIPES, "")
        if (stringRecipe.isNullOrBlank()) {
            return arrayListOf()
        }
        return Gson().fromJson(stringRecipe, listType)
    }

    fun addRecipe(recipe: Recipe) {
        val existList = getAllRecipes()
        existList.add(recipe)
        val serializeList = Gson().toJson(existList)
        sharedPreferences.edit {
            putString(LIST_RECIPES, serializeList)
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        val existList = getAllRecipes()
        var removingElement: Recipe? = null
        existList.forEach {
            if (it.label == recipe.label) {
                removingElement = it
            }
        }
        if (removingElement != null) {
            existList.remove(removingElement)
        }
        val serializeList = Gson().toJson(existList)
        sharedPreferences.edit {
            putString(LIST_RECIPES, serializeList)
        }
    }

    fun isRecipeAdded(recipe: Recipe): Boolean {
        val existList = getAllRecipes()
        existList.forEach {
            if (it.label == recipe.label) {
                return true
            }
        }
        return false
    }
}