package com.bharatsave.goldapp.view.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.FragmentNewAddressBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewAddressFragment : Fragment() {

    private var _binding: FragmentNewAddressBinding? = null
    private val binding: FragmentNewAddressBinding
        get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupObservers()
        setupViews()
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        viewModel.addressCreateStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.contains("success", true)) {
                    Toast.makeText(
                        context,
                        "Address added successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    (parentFragment as DeliveryAddressBottomSheetFragment).dismiss()
                } else {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun setupViews() {
        (binding.etAddress.editText as TextInputEditText).doOnTextChanged { text, _, _, _ ->
            binding.etAddress.error = if (text.toString().isEmpty()) "Required" else ""
        }
        (binding.etName.editText as TextInputEditText).doOnTextChanged { text, _, _, _ ->
            binding.etName.error = if (text.toString().isEmpty()) "Required" else ""
        }
        (binding.etPhone.editText as TextInputEditText).doOnTextChanged { text, _, _, _ ->
            binding.etPhone.error = if (text.toString().isEmpty()) "Required" else ""
        }
        (binding.etPincode.editText as TextInputEditText).doOnTextChanged { text, _, _, _ ->
            binding.etPincode.error = if (text.toString().isEmpty()) "Required" else ""
        }

        (parentFragment as DeliveryAddressBottomSheetFragment).view?.findViewById<MaterialButton>(R.id.btn_add_address)
            ?.setOnClickListener {
                if (!binding.etAddress.error.isNullOrBlank() || !binding.etName.error.isNullOrBlank() || !binding.etPhone.error.isNullOrBlank() || !binding.etPincode.error.isNullOrBlank()) {
                    Toast.makeText(context, "Some fields missing!", Toast.LENGTH_SHORT).show()
                } else {
                    when {
                        (binding.etName.editText as TextInputEditText).text.toString()
                            .isBlank() -> binding.etName.error = "Required"
                        (binding.etAddress.editText as TextInputEditText).text.toString()
                            .isBlank() -> binding.etAddress.error = "Required"
                        (binding.etPincode.editText as TextInputEditText).text.toString()
                            .isBlank() -> binding.etPincode.error = "Required"
                        (binding.etPincode.editText as TextInputEditText).text.toString().length != 6 -> binding.etPincode.error =
                            "6-digit pincode"
                        (binding.etPhone.editText as TextInputEditText).text.toString()
                            .isBlank() -> binding.etPhone.error = "Required"
                        (binding.etPhone.editText as TextInputEditText).text.toString().length != 10 -> binding.etPhone.error =
                            "10-digit mobile number"
                        else -> {
                            val name =
                                (binding.etName.editText as TextInputEditText).text.toString()
                            val phoneNumber =
                                (binding.etPhone.editText as TextInputEditText).text.toString()
                            val address =
                                (binding.etAddress.editText as TextInputEditText).text.toString()
                            val pinCode =
                                (binding.etPincode.editText as TextInputEditText).text.toString()
                            val label =
                                if ((binding.etLabel.editText as TextInputEditText).text.toString() == "null") "Other" else (binding.etLabel.editText as TextInputEditText).text.toString()
                            viewModel.createUserAddress(
                                hashMapOf(
                                    "name" to name,
                                    "mobileNumber" to phoneNumber,
                                    "address" to address,
                                    "pincode" to pinCode,
                                    "label" to label
                                )
                            )
                        }
                    }
                }
            }
    }
}