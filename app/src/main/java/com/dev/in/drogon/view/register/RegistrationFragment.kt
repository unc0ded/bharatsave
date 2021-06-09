package com.dev.`in`.drogon.view.register

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dev.`in`.drogon.R
import com.dev.`in`.drogon.model.User
import com.dev.`in`.drogon.util.StringUtil
import kotlinx.android.synthetic.main.fragment_registration.*


class RegistrationFragment : Fragment() {

    private var registrationActivity: RegistrationActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registrationActivity = context as RegistrationActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_register.setOnClickListener {
            val fullName = et_full_name.text.trim().toString()
            val email = et_email.text.trim().toString()
            val phoneNumber = et_phone_number.text.trim().toString()
            if (validDetailsProvided(fullName, email, phoneNumber)) {
                registrationActivity?.verifyPhoneNumberForUser(User(fullName, email, phoneNumber))
            } else {
                Toast.makeText(
                    registrationActivity,
                    "Please enter valid details",
                    Toast.LENGTH_SHORT
                ).show()
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

}