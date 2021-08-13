package com.bharatsave.goldapp.view.main.learn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.FragmentLearnBinding
import com.bharatsave.goldapp.util.setCustomTypefaceSpanString

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
        binding.tvTitleLearn.setCustomTypefaceSpanString(
            "nudge",
            R.font.eina01_light,
            "Learn",
            R.font.eina01_semi_bold
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}