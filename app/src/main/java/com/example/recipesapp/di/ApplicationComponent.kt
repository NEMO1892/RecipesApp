package com.example.recipesapp.di

import com.example.recipesapp.network.NetworkModule
import com.example.recipesapp.ui.profile_modal_bottom_sheet.ProfileModalBottomSheet
import com.example.recipesapp.ui.chosen_recipe.ChosenRecipeFragment
import com.example.recipesapp.ui.favourite.FavouritesFragment
import com.example.recipesapp.ui.signup.SignUpFragment
import com.example.recipesapp.ui.list.ListRecipesFragment
import com.example.recipesapp.ui.navigation.BottomNavigationFragment
import com.example.recipesapp.ui.profile.ProfileFragment
import dagger.Component
import javax.inject.Singleton

@Component(modules = [NetworkModule::class, AppModule::class])
@Singleton
interface ApplicationComponent {

    fun inject(fragment: ListRecipesFragment)

    fun inject(fragment: SignUpFragment)

    fun inject(fragment: ProfileFragment)

    fun inject(fragment: ProfileModalBottomSheet)

    fun inject(fragment: ChosenRecipeFragment)

    fun inject(fragment: FavouritesFragment)

    fun inject(fragment: BottomNavigationFragment)
}