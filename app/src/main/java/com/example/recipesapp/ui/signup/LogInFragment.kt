package com.example.recipesapp.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.recipesapp.R
import com.example.recipesapp.databinding.FragmentLogInBinding
import com.example.recipesapp.ui.navigation.BottomNavigationFragment
import com.google.firebase.auth.FirebaseAuth

class LogInFragment : Fragment() {

    private var binding: FragmentLogInBinding? = null

    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.run {

            emailLogInTextField.editText?.setOnFocusChangeListener { _, focused ->
                if (focused) {
                    emailLogInTextField.error = null
                }
            }
            passwordLogInTextField.editText?.setOnFocusChangeListener { _, focused ->
                if (focused) {
                    passwordLogInTextField.error = null
                }
            }
            logInButton.setOnClickListener {
                logIn(
                    emailLogInTextField.editText?.text.toString().trim(),
                    passwordLogInTextField.editText?.text.toString().trim()
                )
            }
            moveToSignUpTextView.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, SignUpFragment())
                    .addToBackStack("")
                    .commit()
            }
        }
    }


    private fun logIn(email: String, password: String) {
        if (email.isEmpty()) {
            showEmptyEmailError()
        } else if (password.isEmpty()) {
            showEmptyPasswordError()
        } else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (auth.currentUser?.isEmailVerified == true) {
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.container, BottomNavigationFragment())
                                .commit()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "You should verify your account!",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    } else {
                        showError(it.exception?.localizedMessage ?: "")
                    }
                }
        }
    }

    private fun showEmptyEmailError() {
        binding?.run {
            emailLogInTextField.error = "Email must contain at least 1 character!"
        }
    }

    private fun showEmptyPasswordError() {
        binding?.run {
            passwordLogInTextField.error = "Password must contain at least 1 character"
        }
    }

    private fun showError(error: String) {
        binding?.run {
            emailLogInTextField.error = error
            passwordLogInTextField.error = error
        }
    }
}