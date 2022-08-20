package com.example.recipesapp.ui.list.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.recipesapp.databinding.ItemRecipeBinding
import com.example.recipesapp.model.Recipe
import com.example.recipesapp.util.loadUrlImage

class ListRecipesViewHolder(
    private val binding: ItemRecipeBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Recipe) {
        binding.run {
            labelTextView.text = item.label
            itemImageView.loadUrlImage(item.image)
            caloriesTextView.text = (item.calories.div(item.yield)).toInt().toString() + " kcal"
            countTextView.text = item.yield.toString()
        }
    }
}

