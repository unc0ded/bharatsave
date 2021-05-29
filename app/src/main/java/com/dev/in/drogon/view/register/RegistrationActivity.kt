package com.dev.`in`.drogon.view.register

import android.os.Bundle
import android.widget.Toast
import com.dev.`in`.drogon.R
import com.dev.`in`.drogon.view.base.BaseActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class RegistrationActivity : BaseActivity() {

    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        setupFirebaseAuth()
        if (mFirebaseAuth.currentUser == null) {
            showPhoneNumberFragment()
        } else {
            val userName = mFirebaseAuth.currentUser!!.displayName
            if (userName.isNullOrEmpty()) {
                showAccountCreationFragment()
            } else {
                // TODO: handle this
                Toast.makeText(this, "handle this", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupFirebaseAuth() {
        mFirebaseAuth = Firebase.auth
        mFirebaseAuth.useAppLanguage()

        mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                val otp = credential.smsCode
                if (otp != null) {
                    verifyOtp(otp)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(
                    this@RegistrationActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onCodeSent(
                verificationId: String,
                resendToken: PhoneAuthProvider.ForceResendingToken
            ) {
                this@RegistrationActivity.verificationId = verificationId
                this@RegistrationActivity.resendToken = resendToken

                showOtpBottomSheet()
            }

        }
    }

    public fun verifyOtp(otp: String) {
        verificationId?.let {
            mFirebaseAuth.signInWithCredential(
                PhoneAuthProvider.getCredential(verificationId!!, otp)
            ).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        Toast.makeText(
                            this,
                            "Sign In Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        showAccountCreationFragment();
                    } else {
                        // Sign in failed, display a message and update the UI
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            otpVerificationFailed(true)
                        } else {
                            otpVerificationFailed(false)
                        }
                    }
                }
        } ?: otpVerificationFailed(false)
    }

    private fun showAccountCreationFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, AccountCreationFragment())
            .commitAllowingStateLoss()
    }

    private fun otpVerificationFailed(invalidOtpEntered: Boolean) {
        if (invalidOtpEntered) {
            Toast.makeText(this, "Invalid Otp", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPhoneNumberFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, PhoneNumberFragment())
            .commitAllowingStateLoss()
    }


    fun showOtpBottomSheet() {
        val bottomSheetFragment = OtpBottomSheetFragment()
        bottomSheetFragment.show(supportFragmentManager, "otp_bottom_sheet")
    }

    fun sendOtp(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(mFirebaseAuth)
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(60, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallBack)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}