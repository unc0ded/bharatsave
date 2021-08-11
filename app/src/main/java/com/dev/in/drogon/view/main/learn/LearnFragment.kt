package com.dev.`in`.drogon.view.main.learn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dev.`in`.drogon.databinding.FragmentLearnBinding
import com.dev.`in`.drogon.util.setCustomTypefaceSpanString

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
        binding.tvTitleLearn.setCustomTypefaceSpanString("nudge", "Learn")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}