package com.dev.`in`.drogon.view.register

import android.content.Intent
import android.os.Bundle
import com.dev.`in`.drogon.R
import com.dev.`in`.drogon.view.base.BaseActivity
import com.dev.`in`.drogon.view.home.HomeActivity

class RegistrationActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        /*
            TODO:
                1: Check if user is already signed in
                2: If user is signed in, fetch the details corresponding to that phone number
                from our back-end and re-direct to home screen.
         */
        showPhoneNumberFragment()
    }

    fun showAccountCreationFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, AccountCreationFragment())
            .commitAllowingStateLoss()
    }

    private fun showPhoneNumberFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, PhoneNumberFragment())
            .commitAllowingStateLoss()
    }

    fun showOtpBottomSheet(phoneNumber: String) {
        val bottomSheetFragment = OtpBottomSheetFragment.newInstance(phoneNumber)
        bottomSheetFragment.show(supportFragmentManager, "otp_bottom_sheet")
    }

    fun showHomeScreen() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

}