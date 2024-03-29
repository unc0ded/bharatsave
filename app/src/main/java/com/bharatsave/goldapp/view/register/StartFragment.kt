package com.bharatsave.goldapp.view.register

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bharatsave.goldapp.databinding.FragmentStartBinding

class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tvTc.movementMethod = LinkMovementMethod.getInstance()

        binding.btnStart.setOnClickListener {
            findNavController().navigate(StartFragmentDirections.actionLogin())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}