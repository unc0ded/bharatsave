package com.dev.`in`.drogon.view.register

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.dev.`in`.drogon.R
import com.dev.`in`.drogon.util.Constants
import com.dev.`in`.drogon.view.register.viewmodel.RegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_sign_in.*

/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class SignInFragment : Fragment() {

    private lateinit var registrationActivity: RegistrationActivity
    private val viewModel by viewModels<RegistrationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registrationActivity = context as RegistrationActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.response.observe(viewLifecycleOwner) { response ->
            if (response.user != null) {
                registrationActivity.verifyPhoneNumberForUser(
                    response.user,
                    Constants.ORIGIN_SIGN_IN
                )
                viewModel.saveTokens(response.authToken, response.refreshToken)
            } else Toast.makeText(context, "Error logging in", Toast.LENGTH_SHORT).show()
        }

        viewModel.message.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

        btn_sign_in.setOnClickListener {
            val phoneNumber = et_phone_number.text.toString().trim()

            if (phoneNumber.length == 10) {
                viewModel.login(phoneNumber)
            } else {
                Toast.makeText(
                    registrationActivity,
                    "Please enter a 10-digit phone number",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}