package com.bharatsave.goldapp.view.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bharatsave.goldapp.databinding.FragmentPlanSummaryBinding
import com.bharatsave.goldapp.util.increaseHitArea

class PlanSummaryFragment : Fragment() {

    private var _binding: FragmentPlanSummaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.run {
            increaseHitArea(20f)
            setOnClickListener { findNavController().popBackStack() }
        }
    }
}