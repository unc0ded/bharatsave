package com.dev.`in`.drogon.view.register

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dev.`in`.drogon.R
import kotlinx.android.synthetic.main.fragment_phone_number.*


class PhoneNumberFragment : Fragment() {

    private var registrationActivity: RegistrationActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_number, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registrationActivity = context as RegistrationActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_continue.setOnClickListener {
            val phoneNumber = et_mobile_number.text.trim().toString()
            if (phoneNumber.length != 10) {
                Toast.makeText(registrationActivity, "Invalid mobile number", Toast.LENGTH_SHORT).show()
            } else {
                registrationActivity?.showOtpBottomSheet(phoneNumber)
            }
        }
    }


}