package com.example.recipesapp.ui.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipesapp.databinding.ItemRecipeBinding
import com.example.recipesapp.model.Recipe

class ListRecipesAdapter(
    private val onClick: (id: String) -> Unit
) : RecyclerView.Adapter<ListRecipesViewHolder>() {

    private var list: ArrayList<Recipe> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ListRecipesViewHolder(
            ItemRecipeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: ListRecipesViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            onClick.invoke(
                list[position].uri.takeLastWhile {
                    it != '_'
                }
            )
        }
    }

    override fun getItemCount(): Int = list.size

    fun setList(list: ArrayList<Recipe>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun addList(list: ArrayList<Recipe>) {
        this.list.addAll(list)
        notifyDataSetChanged()
    }
}