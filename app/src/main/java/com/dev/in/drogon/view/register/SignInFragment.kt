package com.dev.`in`.drogon.view.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.dev.`in`.drogon.databinding.FragmentSignInBinding
import com.dev.`in`.drogon.util.actionGo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding: FragmentSignInBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etPhoneNumber.editText?.actionGo { binding.btnSignIn.callOnClick() }

        binding.btnSignIn.setOnClickListener {
            val phoneNumber = binding.etPhoneNumber.editText?.text.toString().trim()

            if (phoneNumber.length == 10) {
                findNavController().navigate(SignInFragmentDirections.actionVerifyOtp(phoneNumber))
            } else {
                Toast.makeText(
                    context,
                    "Please enter a 10-digit phone number",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}