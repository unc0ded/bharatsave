package com.dev.`in`.drogon.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dev.`in`.drogon.MainNavigationDirections
import com.dev.`in`.drogon.databinding.FragmentHelpBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HelpBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentHelpBottomSheetBinding? = null
    private val binding: FragmentHelpBottomSheetBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logoutWrapper.setOnClickListener {
            val firebaseAuth = Firebase.auth
            firebaseAuth.signOut()
            findNavController().navigate(MainNavigationDirections.actionLogout())
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}