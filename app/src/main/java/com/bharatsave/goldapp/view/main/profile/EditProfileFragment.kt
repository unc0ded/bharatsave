package com.bharatsave.goldapp.view.main.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bharatsave.goldapp.databinding.FragmentEditProfileBinding
import com.bharatsave.goldapp.model.User
import com.bharatsave.goldapp.util.textChanges
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private val TAG = "EditProfileFragment"

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ProfileViewModel>()

    private var name: String? = null
    private var email: String? = null
    private var pincode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadUserData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.userData.observe(viewLifecycleOwner) {
            if (it != null) {
                name = it.name
                (binding.etFullName.editText as TextInputEditText).setText(it.name)
                if (it.email.isNotBlank()) {
                    email = it.email
                    (binding.etEmail.editText as TextInputEditText).setText(it.email)
                } else email = ""
                pincode = it.pinCode
                (binding.etPincode.editText as TextInputEditText).setText(it.pinCode)
                binding.etFullName.isEnabled = true
                binding.etEmail.isEnabled = true
                binding.etPincode.isEnabled = true
            }
        }

        viewModel.updateStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.contains("success", true)) {
                    Toast.makeText(context, "Details updated!", Toast.LENGTH_SHORT).show()
                    viewModel.loadUserData()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        (binding.etFullName.editText as TextInputEditText).textChanges().debounce(400)
            .onEach {
                try {
                    binding.etFullName.error = if (it.isNullOrBlank()) "Cannot be empty" else ""
                    binding.btnSave.isEnabled =
                        it.toString()
                            .trim() != name || (binding.etEmail.editText as TextInputEditText).text.toString()
                            .trim() != email || (binding.etPincode.editText as TextInputEditText).text.toString()
                            .trim() != pincode
                } catch (exception: Exception) {
                    Log.e(
                        TAG,
                        "#onViewCreated.fullNameEditTextTextChangedListener: ${exception.message}"
                    )
                }
            }.launchIn(lifecycleScope)

        (binding.etEmail.editText as TextInputEditText).textChanges().debounce(400)
            .onEach {
                try {
                    binding.etEmail.error = if (it.isNullOrBlank()) "Cannot be empty" else ""
                    binding.btnSave.isEnabled =
                        it.toString()
                            .trim() != email || (binding.etFullName.editText as TextInputEditText).text.toString()
                            .trim() != name || (binding.etPincode.editText as TextInputEditText).text.toString()
                            .trim() != pincode
                } catch (exception: Exception) {
                    Log.e(
                        TAG,
                        "#onViewCreated.emailEditTextTextChangedListener: ${exception.message}"
                    )
                }
            }.launchIn(lifecycleScope)

        (binding.etPincode.editText as TextInputEditText).textChanges().debounce(400)
            .onEach {
                try {
                    binding.etPincode.error = if (it.isNullOrBlank()) "Cannot be empty" else ""
                    binding.btnSave.isEnabled =
                        it.toString()
                            .trim() != pincode || (binding.etFullName.editText as TextInputEditText).text.toString()
                            .trim() != name || (binding.etEmail.editText as TextInputEditText).text.toString()
                            .trim() != email
                } catch (exception: Exception) {
                    Log.e(
                        TAG,
                        "#onViewCreated.pincodeEditTextTextChangedListener: ${exception.message}"
                    )
                }
            }.launchIn(lifecycleScope)

        binding.btnSave.setOnClickListener {
            if (!binding.etFullName.error.isNullOrBlank() ||
                !binding.etEmail.error.isNullOrBlank() ||
                !binding.etPincode.error.isNullOrBlank()
            )
                Toast.makeText(context, "Field(s) cannot be empty!", Toast.LENGTH_SHORT).show()
            else {
                viewModel.saveUserDetails(
                    User(
                        name = (binding.etFullName.editText as TextInputEditText).text.toString()
                            .trim(),
                        email = (binding.etEmail.editText as TextInputEditText).text.toString()
                            .trim(),
                        pinCode = (binding.etPincode.editText as TextInputEditText).text.toString()
                            .trim()
                    )
                )
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}