package com.dev.`in`.drogon.view.register

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import com.dev.`in`.drogon.AuthNavigationDirections
import com.dev.`in`.drogon.R
import com.dev.`in`.drogon.databinding.ActivityRegistrationBinding
import com.dev.`in`.drogon.view.base.BaseActivity
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
            navController.navigate(AuthNavigationDirections.actionHomeActivity())
            finish()

        }
    }

    private fun setupFirebaseAuth() {
        mFirebaseAuth = Firebase.auth
        mFirebaseAuth.useAppLanguage()
    }
}