package com.bharatsave.goldapp.view.register

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bharatsave.goldapp.AuthNavigationDirections
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.FragmentOtpBinding
import com.bharatsave.goldapp.util.showSoftKeyboard
import com.bharatsave.goldapp.view.register.viewmodel.RegistrationViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class OtpFragment : Fragment() {

    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<OtpFragmentArgs>()

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
        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupObservers()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        verificationInProgress =
            savedInstanceState?.getBoolean(VERIFICATION_STATUS_FLAG, false) ?: false
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

    private fun setupViews() {
        binding.btnEditPhone.text = HtmlCompat.fromHtml(
            getString(R.string.edit_phone_text, "+91 ${args.phone}"),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        // TODO use a custom view for the dialog
        binding.btnEditPhone.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).setTitle("Edit Number")
                .setMessage(
                    HtmlCompat.fromHtml(
                        "Confirm that you want to edit the phone number<br><b>+91 ${args.phone}</b>",
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                )
                .setPositiveButton("Yes") { dialog, _ ->
                    // Manually cancel verification
                    mCallBack.onVerificationFailed(FirebaseException("User navigated away from screen"))
                    dialog.dismiss()
                    findNavController().popBackStack()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
        }

        binding.etOtp.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                when (text.length) {
                    6 -> binding.btnNext.isEnabled = true
                    else -> binding.btnNext.isEnabled = false
                }
            }
        }

        binding.btnNext.setOnClickListener {
            signInWithPhoneAuthCredential(
                PhoneAuthProvider.getCredential(
                    storedVerificationId!!,
                    binding.etOtp.text.toString()
                )
            )
        }

        binding.btnResendOtp.setOnClickListener {
            sendOtp(args.phone)
        }
    }

    private fun setupObservers() {
        viewModel.response.observe(viewLifecycleOwner) { response ->
            if (response?.user != null) {
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                viewModel.saveUser(response.user)
                findNavController().navigate(AuthNavigationDirections.actionMainActivity())
                viewModel.saveTokens(response.authToken, response.refreshToken)
                activity?.finish()
            }
        }
        viewModel.message.observe(viewLifecycleOwner) {
            if (it.contains("User not found", true)) {
                findNavController().navigate(OtpFragmentDirections.actionDetails(args.phone))
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

                binding.etOtp.isEnabled = true
                binding.etOtp.showSoftKeyboard()
                binding.tvTimer.visibility = View.VISIBLE
                binding.btnResendOtp.isEnabled = false
                timer.start()
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