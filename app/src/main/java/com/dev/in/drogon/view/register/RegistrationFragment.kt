package com.dev.`in`.drogon.view.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dev.`in`.drogon.AuthNavigationDirections
import com.dev.`in`.drogon.databinding.FragmentRegistrationBinding
import com.dev.`in`.drogon.model.User
import com.dev.`in`.drogon.util.StringUtil
import com.dev.`in`.drogon.util.actionGo
import com.dev.`in`.drogon.view.register.viewmodel.RegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding: FragmentRegistrationBinding
        get() = _binding!!

    private val args by navArgs<RegistrationFragmentArgs>()
    private val viewModel by viewModels<RegistrationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.response.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                findNavController().navigate(AuthNavigationDirections.actionOnboarding())
                viewModel.saveTokens(response.authToken, response.refreshToken)
                activity?.finish()
            }
        }

        viewModel.message.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

        binding.etEmail.editText?.actionGo { binding.btnRegister.callOnClick() }

        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullName.editText?.text?.trim().toString()
            val email = binding.etEmail.editText?.text?.trim().toString()
            if (validDetailsProvided(fullName, email, args.phone)) {
                viewModel.signUp(User(fullName, email, args.phone))
            } else {
                Toast.makeText(context, "Please enter valid details", Toast.LENGTH_SHORT).show()
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}