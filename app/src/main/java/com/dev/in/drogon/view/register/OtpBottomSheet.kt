package com.dev.`in`.drogon.view.register

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dev.`in`.drogon.AuthNavigationDirections
import com.dev.`in`.drogon.R
import com.dev.`in`.drogon.databinding.FragmentOtpBottomSheetBinding
import com.dev.`in`.drogon.view.register.viewmodel.RegistrationViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class OtpBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentOtpBottomSheetBinding? = null
    private val binding: FragmentOtpBottomSheetBinding
        get() = _binding!!

    private val args by navArgs<OtpBottomSheetArgs>()

    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var verificationInProgress: Boolean = false

    private val viewModel by viewModels<RegistrationViewModel>()

    private lateinit var timer: CountDownTimer

    companion object {
        const val VERIFICATION_STATUS_FLAG = "verification_status"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTimer.text = "(${millisUntilFinished / 1000})"
            }

            override fun onFinish() {
                binding.btnResendOtp.isEnabled = true
                binding.tvTimer.visibility = View.GONE
            }
        }

        setupFirebaseAuth()

        sendOtp(args.phone)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtpBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.show()

        setupOtpEditTexts()
        setupObservers()

        binding.btnResendOtp.setOnClickListener {
            sendOtp(args.phone)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        verificationInProgress = savedInstanceState?.getBoolean(VERIFICATION_STATUS_FLAG, false)?: false
    }

    override fun onStart() {
        super.onStart()
        if (verificationInProgress) {
            sendOtp(args.phone)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(VERIFICATION_STATUS_FLAG, verificationInProgress)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        timer.cancel()
    }

    private fun setupOtpEditTexts() {
        binding.otpDigit1.textChanges().filter { it.length == 1 }
            .subscribe { binding.otpDigit2.requestFocus() }
        binding.otpDigit2.textChanges().filter { it.length == 1 }
            .subscribe { binding.otpDigit3.requestFocus() }
        binding.otpDigit3.textChanges().filter { it.length == 1 }
            .subscribe { binding.otpDigit4.requestFocus() }
        binding.otpDigit4.textChanges().filter { it.length == 1 }
            .subscribe { binding.otpDigit5.requestFocus() }
        binding.otpDigit5.textChanges().filter { it.length == 1 }
            .subscribe { binding.otpDigit6.requestFocus() }
        binding.otpDigit6.textChanges().filter { it.length == 1 }
            .subscribe {
                signInWithPhoneAuthCredential(
                    PhoneAuthProvider.getCredential(
                        storedVerificationId!!,
                        "${binding.otpDigit1.text}${binding.otpDigit2.text}${binding.otpDigit3.text}" +
                                "${binding.otpDigit4.text}${binding.otpDigit5.text}${binding.otpDigit6.text}"
                    )
                )
            }

        binding.otpDigit1.filters = arrayOf(InputFilter.LengthFilter(1))
        binding.otpDigit2.filters = arrayOf(InputFilter.LengthFilter(1))
        binding.otpDigit3.filters = arrayOf(InputFilter.LengthFilter(1))
        binding.otpDigit4.filters = arrayOf(InputFilter.LengthFilter(1))
        binding.otpDigit5.filters = arrayOf(InputFilter.LengthFilter(1))
        binding.otpDigit6.filters = arrayOf(InputFilter.LengthFilter(1))
    }

    private fun setupObservers() {
        viewModel.response.observe(viewLifecycleOwner) { response ->
            if (response?.user != null) {
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                findNavController().navigate(AuthNavigationDirections.actionOnboarding())
                viewModel.saveTokens(response.authToken, response.refreshToken)
                activity?.finish()
            }
        }
        viewModel.message.observe(viewLifecycleOwner) {
            if (it.contains("User not found", true)) {
                findNavController().navigate(OtpBottomSheetDirections.actionDetails(args.phone))
            } else {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupFirebaseAuth() {
        mFirebaseAuth = Firebase.auth
        mFirebaseAuth.useAppLanguage()

        mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                verificationInProgress = false
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                verificationInProgress = false
                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> Toast.makeText(
                        context,
                        "Invalid request: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    is FirebaseTooManyRequestsException -> Toast.makeText(
                        context,
                        "Too many requests, please try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                    else -> Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                resendToken = token

                binding.tvTimer.visibility = View.VISIBLE
                binding.btnResendOtp.isEnabled = false
                timer.start()
                binding.tvOtpStatus.text = "OTP sent to +91${args.phone}. Trying to auto-verify..."
                binding.progressBar.hide()
                binding.otpDigit1.isEnabled = true
                binding.otpDigit2.isEnabled = true
                binding.otpDigit3.isEnabled = true
                binding.otpDigit4.isEnabled = true
                binding.otpDigit5.isEnabled = true
                binding.otpDigit6.isEnabled = true
            }
        }
    }

    private fun sendOtp(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(mFirebaseAuth)
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(60, TimeUnit.SECONDS)
            .setActivity(activity as Activity)
            .setCallbacks(mCallBack)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        if (!verificationInProgress) verificationInProgress = true
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mFirebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    timer.cancel()
                    task.result?.user?.phoneNumber?.let {
                        val phone = it.substring(it.length - 10)
                        viewModel.login(phone)
                    } ?: Toast.makeText(context, "Firebase user error", Toast.LENGTH_SHORT).show()
                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(context, "Invalid OTP", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Error: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }
}