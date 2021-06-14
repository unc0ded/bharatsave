package com.dev.`in`.drogon.view.register

import android.content.Intent
import android.os.Bundle
import com.dev.`in`.drogon.R
import com.dev.`in`.drogon.data.repository.PreferenceRepository
import com.dev.`in`.drogon.model.User
import com.dev.`in`.drogon.view.base.BaseActivity
import com.dev.`in`.drogon.view.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationActivity : BaseActivity() {

    private lateinit var mFirebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        setupFirebaseAuth()
        /*
            1: Check if user is already signed in
            2: If user is signed in, fetch the details corresponding to that phone number
                from our back-end and re-direct to home screen.
         */
        mFirebaseAuth.currentUser?.let {
            showHomeScreen(it.phoneNumber)
        } ?: showIntroductionFragment()
    }

    private fun setupFirebaseAuth() {
        mFirebaseAuth = Firebase.auth
        mFirebaseAuth.useAppLanguage()
    }

    private fun showIntroductionFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, IntroductionFragment())
            .commitAllowingStateLoss()
    }

    fun showRegistrationFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, RegistrationFragment())
            .commitAllowingStateLoss()
    }

    fun verifyPhoneNumberForUser(user: User, origin: Int) {
        val otpFragment = OtpFragment.newInstance(user, origin)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, otpFragment)
            .commitAllowingStateLoss()
    }

    fun showHomeScreen(phoneNumber: String?) {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    fun showSignInFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, SignInFragment())
            .commitAllowingStateLoss()
    }
}