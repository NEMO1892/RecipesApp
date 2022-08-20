package com.example.recipesapp.util

import android.text.Editable
import android.text.TextWatcher

class AfterTextChangedListener(
    val onChange: (s: String) -> Unit,
    val onChanged: (s: String) -> Unit
) : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        //do nothing
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        onChange(p0.toString())
    }

    override fun afterTextChanged(p0: Editable?) {
        onChanged(p0.toString())
    }
}