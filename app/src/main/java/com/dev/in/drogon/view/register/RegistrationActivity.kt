package com.dev.`in`.drogon.view.register

import android.os.Bundle
import com.dev.`in`.drogon.R
import com.dev.`in`.drogon.view.base.BaseActivity

class RegistrationActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

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

}