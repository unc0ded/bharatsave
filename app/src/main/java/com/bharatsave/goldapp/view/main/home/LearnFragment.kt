package com.bharatsave.goldapp.view.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.FragmentLearnBinding
import com.bharatsave.goldapp.util.getThemeColorFromAttr
import com.bharatsave.goldapp.util.setCustomSpanString

class LearnFragment : Fragment() {

    private var _binding: FragmentLearnBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLearnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tvTitleLearn.setCustomSpanString(
            "save",
            R.font.eina01_regular,
            requireContext().getThemeColorFromAttr(R.attr.colorPrimary),
            "",
            "Learn",
            R.font.eina01_semi_bold,
            requireContext().getThemeColorFromAttr(R.attr.colorSecondaryVariant)
        )

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}