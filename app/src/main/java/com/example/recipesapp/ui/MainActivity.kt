package com.example.recipesapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.recipesapp.R
import com.example.recipesapp.databinding.ActivityMainBinding
import com.example.recipesapp.ui.navigation.BottomNavigationFragment
import com.example.recipesapp.ui.signup.LogInFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
//        intent?.let {
//            handleDeepLink(it)
//        }
        if (auth.currentUser?.isEmailVerified != true) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, LogInFragment())
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, BottomNavigationFragment())
                .addToBackStack("")
                .commit()
        }
    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        intent?.let {
//            handleDeepLink(it)
//        }
//    }
//
//    private fun handleDeepLink(intent: Intent) {
//        intent.data?.path.let {
//            intent.data
//        }
//    }
}