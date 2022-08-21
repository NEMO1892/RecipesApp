package com.example.recipesapp.ui.signup

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.recipesapp.R
import com.example.recipesapp.databinding.FragmentSignUpBinding
import com.example.recipesapp.di.MyApplication
import com.example.recipesapp.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class SignUpFragment : Fragment() {

    private var binding: FragmentSignUpBinding? = null

    private val auth = FirebaseAuth.getInstance()

    private lateinit var viewModel: SignUpViewModel

    @Inject
    lateinit var viewModelProvider: SignUpViewModelProvider

    private val database =
        Firebase.database("https://recipesapp-22212-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("Users")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MyApplication.appComponent.inject(this)
        viewModel =
            ViewModelProvider(this, viewModelProvider)[SignUpViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.run {
            profileNameTEditText.setOnFocusChangeListener { _, focused ->
                if (focused) {
                    profileNameTEditText.error = null
                    profileNameTextField.error = null
                }
            }
            emailSignUpTEditText.setOnFocusChangeListener { _, focused ->
                if (focused) {
                    emailSignUpTEditText.error = null
                    emailSignUpTextField.error = null
                }
            }
            passwordSignUpEditText.setOnFocusChangeListener { _, focused ->
                if (focused) {
                    passwordSignUpTextField.error = null
                    passwordSignUpTextField.error = null
                }
            }
            signUpButton.setOnClickListener {
                loadingProgressBar.visibility = View.VISIBLE
                signUp(
                    profileNameTextField.editText?.text.toString().trim(),
                    emailSignUpTextField.editText?.text.toString().trim(),
                    passwordSignUpTextField.editText?.text.toString().trim()
                )
            }
        }
    }

    private fun signUp(profileName: String, email: String, password: String) {
        if (profileName.isEmpty()) {
            showEmptyProfileError()
        } else if (password.isEmpty()) {
            showEmptyPasswordError()
        } else if (email.isEmpty()) {
            showEmptyEmailError()
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (isValidEmail(email)) {
                        emailVerification(profileName, email)
                    } else {
                        showError(it.exception?.localizedMessage ?: "")
                        binding?.loadingProgressBar?.visibility = View.INVISIBLE
                    }
                }
            viewModel.saveProfileName(profileName)
        }
    }

    private fun showError(error: String) {
        binding?.run {
            emailSignUpTextField.error = error
            passwordSignUpTextField.error = error
        }
    }

    private fun emailVerification(profileName: String, email: String) {
        val user = Firebase.auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Snackbar.make(
                        requireView(),
                        R.string.verification_email_has_been_sent,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("OK") {}
                        .show()
                    uploadToFirebase(profileName, email)
                } else {
                    Snackbar.make(
                        requireView(),
                        R.string.verification_email_not_sent,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("OK") {}
                        .show()
                    binding?.loadingProgressBar?.visibility = View.INVISIBLE
                }
            }
    }

    private fun uploadToFirebase(profileName: String, email: String) {
        val userDb = User(email = email, profileName = profileName)
        auth.uid?.let {
            database.child(it).setValue(userDb)
                .addOnSuccessListener {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.container, LogInFragment())
                        .commit()
                }
                .addOnFailureListener {
                    Snackbar.make(requireView(), R.string.not_uploaded, Snackbar.LENGTH_LONG)
                        .setAction("OK") {}
                        .show()
                    binding?.loadingProgressBar?.visibility = View.INVISIBLE
                }
        }
    }

    private fun showEmptyProfileError() {
        binding?.run {
            profileNameTextField.error = "Profile name must contain at least 2 characters!"
        }
    }

    private fun showEmptyEmailError() {
        binding?.run {
            emailSignUpTextField.error = "Invalid email!"
        }
    }

    private fun showEmptyPasswordError() {
        binding?.run {
            emailSignUpTextField.error = "Password must contain at least 6 characters"
        }
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }
}