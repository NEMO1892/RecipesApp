package com.example.recipesapp.ui.profile

import com.example.recipesapp.repository.SharedPreferencesRepository
import com.example.recipesapp.ui.base.BaseViewModelFactory
import javax.inject.Inject

class ProfileViewModelProvider @Inject constructor(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) :
    BaseViewModelFactory<ProfileViewModel>(ProfileViewModel::class.java) {

    override fun createViewModel(): ProfileViewModel {
        return ProfileViewModel(sharedPreferencesRepository)
    }
}