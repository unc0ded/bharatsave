package com.dev.`in`.drogon.view.register

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dev.`in`.drogon.R


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

    }


}