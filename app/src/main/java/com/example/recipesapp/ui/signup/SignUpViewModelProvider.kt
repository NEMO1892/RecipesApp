package com.example.recipesapp.ui.signup

import com.example.recipesapp.repository.SharedPreferencesRepository
import com.example.recipesapp.ui.base.BaseViewModelFactory
import javax.inject.Inject

class SignUpViewModelProvider @Inject constructor(
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : BaseViewModelFactory<SignUpViewModel>(SignUpViewModel::class.java) {

    override fun createViewModel(): SignUpViewModel {
        return SignUpViewModel(sharedPreferencesRepository)
    }
}