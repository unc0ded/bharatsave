package com.dev.`in`.drogon.view.register

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dev.`in`.drogon.R
import com.dev.`in`.drogon.model.User
import kotlinx.android.synthetic.main.fragment_sign_in.*

/**
 * A simple [Fragment] subclass.
 */
class SignInFragment : Fragment() {

    private lateinit var registrationActivity: RegistrationActivity

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

        btn_sign_in.setOnClickListener {
            val phoneNumber = et_phone_number.text.toString().trim()

            if (phoneNumber.length == 10) {
                registrationActivity.verifyPhoneNumberForUser(User("", "", phoneNumber))
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