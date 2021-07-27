package com.dev.`in`.drogon.view.main.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dev.`in`.drogon.MainNavigationDirections
import com.dev.`in`.drogon.databinding.FragmentProfileBinding
import com.dev.`in`.drogon.util.generateRandomString
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding!!

    private val clipboard by lazy {
        context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // TODO get from backend
        binding.tvName.text = "Nudge Admin"
        binding.tvEmail.text = "admin@nudge.money"
        binding.btnCopyReferral.generateRandomString(12);

        // TODO add confirmation dialog
        binding.btnLogOut.setOnClickListener {
            val firebaseAuth = Firebase.auth
            firebaseAuth.signOut()
            viewModel.clearUserData()
            findNavController().navigate(MainNavigationDirections.actionLogout())
            activity?.finish()
        }

        binding.btnCopyReferral.setOnClickListener {
            val clip = ClipData.newPlainText("referral", binding.btnCopyReferral.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Referral code copied!", Toast.LENGTH_SHORT).show()
        }
    }
}