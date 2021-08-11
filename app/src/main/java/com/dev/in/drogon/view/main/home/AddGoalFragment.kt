package com.dev.`in`.drogon.view.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.dev.`in`.drogon.databinding.FragmentAddGoalBinding
import com.google.android.material.textfield.TextInputEditText

class AddGoalFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentAddGoalBinding? = null
    private val binding get() = _binding!!

    private var goalAmount: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.etGoalAmount.editText?.doOnTextChanged { text, _, _, _ ->
            goalAmount = if (text.toString().isNotBlank()) text.toString().toInt() else 0
        }

        binding.chip10.setOnClickListener(this)
        binding.chip50.setOnClickListener(this)
        binding.chip100.setOnClickListener(this)
        binding.chip500.setOnClickListener(this)
        binding.chip1000.setOnClickListener(this)
        binding.chip10000.setOnClickListener(this)

        binding.btnCreateGoal.setOnClickListener {
            if (binding.etGoalName.editText?.text.toString()
                    .isNotBlank() && binding.etGoalAmount.editText?.text.toString().isNotBlank()
            ) {
                // TODO save goal details
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        (binding.etGoalAmount.editText as TextInputEditText).apply {
            setText(
                (if ((binding.etGoalAmount.editText as TextInputEditText).text.toString()
                        .isBlank()
                ) v?.tag.toString().toInt() else binding.etGoalAmount.editText?.text.toString()
                    .toInt() + v?.tag.toString().toInt()).toString()
            )
            setSelection(if (this.text.toString().isNotBlank()) this.text.toString().length else 0)
        }
    }
}