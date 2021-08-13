package com.bharatsave.goldapp.view.register

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.bharatsave.goldapp.OnboardingNavigationDirections
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.ActivityRegistrationBinding
import com.bharatsave.goldapp.view.base.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationActivity : BaseActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var mFirebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        setupFirebaseAuth()
        /*
            1: Check if user is already signed in
            2: If user is signed in, fetch the details corresponding to that phone number
                from our back-end and re-direct to home screen.
         */
        mFirebaseAuth.currentUser?.let {
            navController.navigate(OnboardingNavigationDirections.actionMainActivity())
            finish()
        }
    }

    private fun setupFirebaseAuth() {
        mFirebaseAuth = Firebase.auth
        mFirebaseAuth.useAppLanguage()
    }
}