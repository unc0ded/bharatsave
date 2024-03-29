package com.bharatsave.goldapp.view.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.navigation.fragment.findNavController
import com.bharatsave.goldapp.custom_widget.CheckablePlanCard
import com.bharatsave.goldapp.databinding.FragmentAddPlanBinding
import com.bharatsave.goldapp.util.increaseHitArea
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
        binding.btnBack.run {
            increaseHitArea(20f)
            setOnClickListener { findNavController().popBackStack() }
        }

        binding.gridPlanCards.addView(
            CheckablePlanCard(requireContext()).apply {
                useGridLayout()
                setPlanName("Daily Savings")
                setIconText("tue", "wed", "thurs")
                setPlanDescription("Save something on a daily basis. Move closer to your goals")
                setMarginsDp(start = 40, end = 5, top = 10, bottom = 10)
            }
        )

        binding.gridPlanCards.addView(
            CheckablePlanCard(requireContext()).apply {
                useGridLayout()
                setPlanName("Weekly Savings")
                setIconText("week", "week", "week")
                setPlanDescription("Grow your savings with investments, week by week")
                setMarginsDp(start = 5, end = 40, top = 10, bottom = 10)
            }
        )

        binding.gridPlanCards.addView(
            CheckablePlanCard(requireContext()).apply {
                useGridLayout()
                setPlanName("Monthly Savings")
                setIconText("feb", "april", "may")
                setPlanDescription("Invest every month whenever you get paid, with auto-pay")
                setMarginsDp(start = 40, end = 5, top = 10, bottom = 10)
            }
        )

        binding.btnSetupAutopay.setOnClickListener {
            val checkedItem =
                binding.gridPlanCards.children.filter { (it as CheckablePlanCard).isChecked }
                    .elementAt(0) as CheckablePlanCard
            findNavController().navigate(AddPlanFragmentDirections.actionPlanDetails(checkedItem.getPlanName()))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}