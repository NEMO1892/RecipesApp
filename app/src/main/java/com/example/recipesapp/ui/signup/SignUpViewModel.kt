package com.example.recipesapp.ui.signup

import androidx.lifecycle.ViewModel
import com.example.recipesapp.repository.SharedPreferencesRepository

class SignUpViewModel(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : ViewModel() {

    fun saveProfileName(name: String) = sharedPreferencesRepository.saveProfileName(name)
}