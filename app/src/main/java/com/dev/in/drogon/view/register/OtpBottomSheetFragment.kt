package com.dev.`in`.drogon.view.register

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dev.`in`.drogon.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_otp_bottom_sheet.*
import java.util.concurrent.TimeUnit


private const val EXTRA_PHONE_NUMBER = "extra_phone_number"

class OtpBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    private var registrationActivity: RegistrationActivity? = null
    private var phoneNumber: String? = null

    companion object {

        @JvmStatic
        fun newInstance(phoneNUmber: String) =
            OtpBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_PHONE_NUMBER, phoneNUmber)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            phoneNumber = it.getString(EXTRA_PHONE_NUMBER)
        }
        setupFirebaseAuth()
        phoneNumber?.let {
            sendOtp(it)
        } ?: otpVerificationFailed(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_otp_bottom_sheet, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registrationActivity = context as RegistrationActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enter_otp_wrapper.visibility = View.GONE
        progress_wrapper.visibility = View.VISIBLE

        btn_verify.setOnClickListener {
            verifyOtp(et_otp.text.trim().toString())
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
                    registrationActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onCodeSent(
                verificationId: String,
                resendToken: PhoneAuthProvider.ForceResendingToken
            ) {
                this@OtpBottomSheetFragment.verificationId = verificationId
                this@OtpBottomSheetFragment.resendToken = resendToken

                progress_wrapper.visibility = View.GONE
                enter_otp_wrapper.visibility = View.VISIBLE
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
                PhoneAuthProvider.getCredential(verificationId!!, otp))
                .addOnCompleteListener(registrationActivity!!) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    Toast.makeText(
                        registrationActivity,
                        "Sign In Successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    registrationActivity?.showAccountCreationFragment()
                    dismiss()

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