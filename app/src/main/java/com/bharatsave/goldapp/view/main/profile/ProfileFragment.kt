package com.bharatsave.goldapp.view.main.profile

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
import com.bharatsave.goldapp.MainNavigationDirections
import com.bharatsave.goldapp.databinding.FragmentProfileBinding
import com.bharatsave.goldapp.util.generateRandomString
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

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
        binding.btnCopyReferral.generateRandomString(12)

        viewModel.userData.observe(viewLifecycleOwner) { user ->
            binding.tvName.text = user?.name
            binding.tvEmail.text = user?.email
        }

        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionEditProfile())
        }

        // TODO use a custom view for the dialog
        binding.btnLogOut.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton(
                    "Confirm"
                ) { dialog, _ ->
                    Firebase.auth.signOut()
                    viewModel.clearUserData()
                    dialog.dismiss()
                    findNavController().navigate(MainNavigationDirections.actionLogout())
                    activity?.finish()
                }
                .setNegativeButton(
                    "Cancel"
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(true)
                .show()
        }

        // TODO implement saved cards and accounts
        binding.btnLinked.setOnClickListener {
            Toast.makeText(context, "Coming soon!", Toast.LENGTH_SHORT).show()
        }

        binding.btnCopyReferral.setOnClickListener {
            val clip = ClipData.newPlainText("referral", binding.btnCopyReferral.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Referral code copied!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}