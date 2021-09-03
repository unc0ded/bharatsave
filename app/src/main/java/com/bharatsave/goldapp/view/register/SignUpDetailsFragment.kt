package com.bharatsave.goldapp.view.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bharatsave.goldapp.AuthNavigationDirections
import com.bharatsave.goldapp.databinding.FragmentSignUpDetailsBinding
import com.bharatsave.goldapp.model.User
import com.bharatsave.goldapp.util.StringUtil
import com.bharatsave.goldapp.util.actionGo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class SignUpDetailsFragment : Fragment() {

    private var _binding: FragmentSignUpDetailsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<SignUpDetailsFragmentArgs>()
    private val viewModel by viewModels<RegistrationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        // Sign out firebase user if sign up was not completed before exiting app
        runBlocking {
            val loginComplete = viewModel.checkTokens()
            if (!loginComplete) {
                Firebase.auth.signOut()
            }
        }
    }

    private fun setupViews() {
        binding.etFullName.editText?.doOnTextChanged { text, _, _, _ ->
            binding.btnRegister.isEnabled =
                !(text.toString().isBlank() || binding.etEmail.editText?.text.toString()
                    .isBlank() || binding.etPostalCode.editText?.text.toString().isBlank())
        }
        binding.etEmail.editText?.doOnTextChanged { text, _, _, _ ->
            binding.btnRegister.isEnabled =
                !(binding.etFullName.editText?.text.toString().isBlank() || text.toString()
                    .isBlank() || binding.etPostalCode.editText?.text.toString().isBlank())
        }
        binding.etPostalCode.editText?.doOnTextChanged { text, _, _, _ ->
            binding.btnRegister.isEnabled = !(binding.etFullName.editText?.text.toString()
                .isBlank() || binding.etEmail.editText?.text.toString().isBlank() || text.toString()
                .isBlank())
        }

        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullName.editText?.text?.trim().toString()
            val email = binding.etEmail.editText?.text?.trim().toString()
            val pinCode = binding.etPostalCode.editText?.text?.trim().toString()
            // TODO unused referral field
            val referral = binding.etReferral.editText?.text?.trim().toString()
            if (validDetailsProvided(fullName, email, args.phone)) {
                viewModel.signUp(
                    User(
                        name = fullName,
                        email = email,
                        phoneNumber = args.phone,
                        pinCode = pinCode
                    )
                )
            } else {
                Toast.makeText(context, "Please enter valid details", Toast.LENGTH_SHORT).show()
            }
        }

        binding.etEmail.editText?.actionGo { binding.btnRegister.callOnClick() }
    }

    private fun setupObservers() {
        viewModel.response.observe(viewLifecycleOwner) { response ->
            if (response?.user != null) {
                Toast.makeText(context, "User Created", Toast.LENGTH_SHORT).show()
                viewModel.saveUser(response.user)
                findNavController().navigate(AuthNavigationDirections.actionMainActivity())
                viewModel.saveAuthData(
                    response.authToken,
                    response.user.phoneNumber,
                    Firebase.auth.currentUser!!.uid
                )
                activity?.finish()
            }
        }

        viewModel.message.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun validDetailsProvided(
        fullName: String,
        email: String,
        phoneNumber: String
    ): Boolean {
        if (fullName.isEmpty() ||
            email.isEmpty() ||
            phoneNumber.length != 10 ||
            !StringUtil.isEmailValid(email)
        ) {
            return false
        }
        return true
    }
}