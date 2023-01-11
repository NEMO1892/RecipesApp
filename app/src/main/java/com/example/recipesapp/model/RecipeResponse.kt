package com.example.recipesapp.model

data class RecipeResponse(
    val from: Int,
    val to: Int,
    val count: Int,
    val _links: Links,
    val hits: ArrayList<Hits>
)

data class Links(
    val next: Next
)

data class Next(
    val href: String,
    val title: String?
)

data class Hits(
    val recipe: Recipe
)

data class Recipe(
    val calories: Double,
    val cautions: List<String>?,
    val cuisineType: List<String?>,
    val dietLabels: List<String>?,
    val dishType: List<String>?,
    val healthLabels: List<String>?,
    val image: String,
    val ingredientLines: List<String>?,
    val ingredients: List<Ingredient>?,
    val label: String,
    val mealType: List<String>?,
    val shareAs: String?,
    val source: String?,
    val totalNutrients: TotalNutrients,
    val totalTime: Double?,
    val totalWeight: Double?,
    val uri: String,
    val url: String?,
    val yield: Int
)

data class TotalNutrients(
    val ENERC_KCAL: EnercKcal,
    val FAT: FAT,
    val CHOCDF: Carbs,
    val PROCNT: Protein,
)

data class EnercKcal(
    val label: String,
    val quantity: Double,
    val unit: String
)

data class FAT(
    val label: String,
    val quantity: Double,
    val unit: String
)

data class Carbs(
    val label: String,
    val quantity: Double,
    val unit: String
)

data class Protein(
    val label: String,
    val quantity: Double,
    val unit: String
)

data class Ingredient(
    val food: String,
    val foodCategory: String,
    val foodId: String,
    val image: String,
    val measure: String,
    val quantity: Double,
    val text: String,
    val weight: Double
)