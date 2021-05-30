package com.dev.`in`.drogon.view.register

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dev.`in`.drogon.R
import kotlinx.android.synthetic.main.fragment_account_creation.*


class AccountCreationFragment : Fragment() {

    private var registrationActivity: RegistrationActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_creation, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registrationActivity = context as RegistrationActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_submit.setOnClickListener {
            val userFullName = et_full_name.text.trim().toString()
            val userAddress = "${et_house_no.text.trim()} ${et_city.text.trim()} ${et_state.text.trim()}"
            registerUser()
        }
    }

    private fun registerUser() {
        /*
            TODO:
                1: Create register model
                2: Call back-end to register/store user on our back-end
                3: Cache user details here in app
         */
        registrationActivity?.showHomeScreen()
    }


}