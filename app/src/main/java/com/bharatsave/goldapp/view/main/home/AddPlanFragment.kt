package com.bharatsave.goldapp.view.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.navigation.fragment.findNavController
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.custom_widget.CheckablePlanCard
import com.bharatsave.goldapp.databinding.FragmentAddPlanBinding
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPlanFragment : Fragment() {

    private var _binding: FragmentAddPlanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.gridPlanCards.addView(
            CheckablePlanCard(requireContext()).useGridLayout().setPlanName("Round Up")
                .setPlanDescription("Round up each time you buy something. Invest the changes.")
                .setMarginsDp(start = 40, end = 5, top = 10, bottom = 10)
        )

        binding.gridPlanCards.addView(
            CheckablePlanCard(requireContext()).useGridLayout().setPlanName("Daily Savings")
                .setPlanDescription("Save something on a daily basis. Move closer to your goals")
                .setMarginsDp(start = 5, end = 40, top = 10, bottom = 10)
        )

        binding.gridPlanCards.addView(
            CheckablePlanCard(requireContext()).useGridLayout().setPlanName("Monthly Savings")
                .setPlanDescription("Invest every month whenever you get paid with auto-pay")
                .setMarginsDp(start = 40, end = 5, top = 10, bottom = 10)
        )

        binding.btnContinue.setOnClickListener {
            val checkedItem =
                binding.gridPlanCards.children.filter { (it as CheckablePlanCard).isChecked }
                    .elementAt(0) as CheckablePlanCard
            if (checkedItem.findViewById<MaterialTextView>(R.id.tv_plan_name).text.contentEquals(
                    "round up",
                    true
                )
            ) {
                findNavController().navigate(AddPlanFragmentDirections.actionRoundUpDetails())
            } else {
                Toast.makeText(context, "Coming Soon!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}