package com.example.recipesapp.ui.filter_modal_bottom_sheet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FilterModalBottomSheetViewModel : ViewModel() {

    val dietState = MutableLiveData<Int?>()

    val healthState = MutableLiveData<Int?>()

    fun getDietState(): Int? = healthState.value

    fun saveDietState(diet: Int?) {
        dietState.value = diet
    }

    fun getHealthState(): Int? = dietState.value

    fun saveHealthState(health: Int?) {
        healthState.value = health
    }
}