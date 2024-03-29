package com.bharatsave.goldapp.view.main.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bharatsave.goldapp.MainNavigationDirections
import com.bharatsave.goldapp.databinding.FragmentProfileBinding
import com.bharatsave.goldapp.model.AlertData
import com.bharatsave.goldapp.util.DeviceUtils
import com.bharatsave.goldapp.util.clickWithThrottle
import com.bharatsave.goldapp.view.main.home.AddressBottomSheetPurpose
import com.bharatsave.goldapp.view.main.home.BottomSheetPurpose
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
        viewModel.userData.observe(viewLifecycleOwner) { user ->
            binding.tvName.text = user?.name
            binding.tvEmail.text = user?.email
            binding.btnCopyReferral.text = HtmlCompat.fromHtml(
                "Your referral code: <b>${user?.referralCode}</b>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
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

        binding.btnLinked.clickWithThrottle {
            findNavController().navigate(
                ProfileFragmentDirections.actionViewAccounts(
                    purpose = BottomSheetPurpose.VIEW_ACCOUNTS,
                    sellParameters = null
                )
            )
        }
        binding.btnViewAddress.clickWithThrottle {
            findNavController().navigate(
                ProfileFragmentDirections.actionViewAddress(
                    purpose = AddressBottomSheetPurpose.VIEW_ADDRESS,
                    productId = null
                )
            )
        }

        binding.btnCopyReferral.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionAlertReferralDetails(
                    AlertData(
                        titleText = "Why Refer?",
                        subTitleText = "Referring can help you earn chances to play games and win " +
                                "prizes!! Everytime you refer a user, and they join, both the new " +
                                "user and you earn a earn to play the games hosted by the " +
                                "BharatSave team and chances to win exciting prizes",
                        positiveText = "Understood, copy my code!",
                        positiveAction = {
                            val clip =
                                ClipData.newPlainText(
                                    "referral",
                                    binding.btnCopyReferral.text.split(": ")[1]
                                )
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Referral code copied!", Toast.LENGTH_SHORT)
                                .show()

                            DeviceUtils.vibrateDevice(
                                activity,
                                DeviceUtils.VibrationStrength.MEDIUM
                            )
                        },
                        negativeText = "Cancel"
                    )
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}