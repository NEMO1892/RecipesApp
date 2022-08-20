package com.example.recipesapp.ui.profile

import androidx.lifecycle.ViewModel
import com.example.recipesapp.repository.SharedPreferencesRepository

class ProfileViewModel(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : ViewModel() {

    fun getProfileName(): String? = sharedPreferencesRepository.getProfileName()

    fun saveProfileName(profileName: String) {
        sharedPreferencesRepository.saveProfileName(profileName)
    }
}