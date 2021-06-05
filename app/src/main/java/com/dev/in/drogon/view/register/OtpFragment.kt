package com.dev.`in`.drogon.view.register

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dev.`in`.drogon.R
import com.dev.`in`.drogon.model.User
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding3.widget.textChanges
import kotlinx.android.synthetic.main.fragment_otp.*
import java.util.concurrent.TimeUnit


private const val EXTRA_USER = "extra_user"

class OtpFragment : Fragment() {

    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    private var registrationActivity: RegistrationActivity? = null
    private var user: User? = null

    companion object {
        @JvmStatic
        fun newInstance(user: User) =
            OtpFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_USER, user)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getParcelable(EXTRA_USER)
        }

        setupFirebaseAuth()

        user?.let {
            sendOtp(it.phoneNumber)
        } ?: otpVerificationFailed(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_otp, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registrationActivity = context as RegistrationActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOtpEditTexts()

        btn_verify.setOnClickListener {
            verifyOtp("${otp_digit_1.text}${otp_digit_2.text}${otp_digit_3.text}" +
                    "${otp_digit_4.text}${otp_digit_5.text}${otp_digit_6.text}")
        }
    }

    @SuppressLint("CheckResult")
    private fun setupOtpEditTexts() {
        otp_digit_1.textChanges().filter { it.length == 1 }.subscribe { otp_digit_2.requestFocus() }
        otp_digit_2.textChanges().filter { it.length == 1 }.subscribe { otp_digit_3.requestFocus() }
        otp_digit_3.textChanges().filter { it.length == 1 }.subscribe { otp_digit_4.requestFocus() }
        otp_digit_4.textChanges().filter { it.length == 1 }.subscribe { otp_digit_5.requestFocus() }
        otp_digit_5.textChanges().filter { it.length == 1 }.subscribe { otp_digit_6.requestFocus() }

        otp_digit_1.filters = arrayOf(InputFilter.LengthFilter(1))
        otp_digit_2.filters = arrayOf(InputFilter.LengthFilter(1))
        otp_digit_3.filters = arrayOf(InputFilter.LengthFilter(1))
        otp_digit_4.filters = arrayOf(InputFilter.LengthFilter(1))
        otp_digit_5.filters = arrayOf(InputFilter.LengthFilter(1))
        otp_digit_6.filters = arrayOf(InputFilter.LengthFilter(1))
    }

    private fun setupFirebaseAuth() {
        mFirebaseAuth = Firebase.auth
        mFirebaseAuth.useAppLanguage()

        mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                val otp = credential.smsCode
                if (otp != null) {
                    // verifyOtp(otp)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(
                    registrationActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onCodeSent(
                verificationId: String,
                resendToken: PhoneAuthProvider.ForceResendingToken
            ) {
                this@OtpFragment.verificationId = verificationId
                this@OtpFragment.resendToken = resendToken

                tv_otp_status.text = "Otp has been sent to ${user?.phoneNumber}"
            }

        }
    }

    private fun sendOtp(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(mFirebaseAuth)
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(60, TimeUnit.SECONDS)
            .setActivity(registrationActivity!!)
            .setCallbacks(mCallBack)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyOtp(otp: String) {
        if (verificationId != null && registrationActivity != null) {
            mFirebaseAuth.signInWithCredential(
                PhoneAuthProvider.getCredential(verificationId!!, otp)
            )
                .addOnCompleteListener(registrationActivity!!) { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        Toast.makeText(
                            registrationActivity,
                            "Sign In Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        registrationActivity?.showHomeScreen(user?.phoneNumber)

                    } else {
                        // Sign in failed, display a message and update the UI
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            otpVerificationFailed(true)
                        } else {
                            otpVerificationFailed(false)
                        }
                    }
                }
        } else {
            otpVerificationFailed(false)
        }
    }

    private fun otpVerificationFailed(invalidOtpEntered: Boolean) {
        if (invalidOtpEntered) {
            Toast.makeText(registrationActivity, "Invalid Otp", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(registrationActivity, "An error occurred", Toast.LENGTH_SHORT).show()
        }
    }

}