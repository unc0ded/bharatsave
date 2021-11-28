package com.bharatsave.goldapp.view.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionManager
import androidx.viewpager2.widget.ViewPager2
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.FragmentBankSelectionBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BankSelectionBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBankSelectionBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel by viewModels<HomeViewModel>()

    private val args by navArgs<BankSelectionBottomSheetFragmentArgs>()

    private lateinit var bottomSheet: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeViewModel.getUserBankList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBankSelectionBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false
        setupObservers()
        setupViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        homeViewModel.banksData.observe(viewLifecycleOwner) {
            if (it != null && it.isNotEmpty()) {
                val adapter = BankListAdapter(it.asReversed())
                binding.pagerBankList.adapter = adapter
                TabLayoutMediator(
                    binding.indicatorBanks,
                    binding.pagerBankList
                ) { _, _ -> }.attach()

                TransitionManager.beginDelayedTransition(bottomSheet)
                binding.pagerBankList.isVisible = true
                if (it.size > 1) binding.indicatorBanks.isVisible = true
                binding.ivSelectedIcon.isVisible = true

                binding.pagerBankList.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageScrollStateChanged(state: Int) {
                        binding.ivSelectedIcon.isVisible = state == ViewPager2.SCROLL_STATE_IDLE
                    }
                })
            } else {
                TransitionManager.beginDelayedTransition(bottomSheet)
                binding.pagerBankList.isVisible = false
                binding.indicatorBanks.isVisible = false
            }
        }

        homeViewModel.bankCreateStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.contains("success", true)) {
                    Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show()
                    binding.cardAddBankDetails.isVisible = false
                    homeViewModel.getUserBankList()
                } else {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        homeViewModel.sellGoldStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.contains("success", true)) {
                    Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupViews() {
        bottomSheet = dialog!!.findViewById(R.id.design_bottom_sheet)

        binding.btnAddBank.setOnClickListener {
            TransitionManager.beginDelayedTransition(bottomSheet)
            binding.cardAddBankDetails.isVisible = !binding.cardAddBankDetails.isVisible
        }

        (binding.etAccountNumber.editText as TextInputEditText).doOnTextChanged { text, _, _, _ ->
            binding.etAccountNumber.error = if (text.toString().isEmpty()) "Required" else ""
        }
        (binding.etIfscCode.editText as TextInputEditText).doOnTextChanged { text, _, _, _ ->
            binding.etIfscCode.error = if (text.toString().isEmpty()) "Required" else ""
        }
        (binding.etAccountName.editText as TextInputEditText).doOnTextChanged { text, _, _, _ ->
            binding.etAccountName.error = if (text.toString().isEmpty()) "Required" else ""
        }

        binding.btnCreateAccount.setOnClickListener {
            if (!binding.etAccountNumber.error.isNullOrBlank() || !binding.etIfscCode.error.isNullOrBlank() || !binding.etAccountName.error.isNullOrBlank()) {
                Toast.makeText(context, "Some fields missing!", Toast.LENGTH_SHORT).show()
            } else {
                when {
                    (binding.etAccountNumber.editText as TextInputEditText).text.toString()
                        .isBlank() -> binding.etAccountNumber.error = "Required"
                    (binding.etIfscCode.editText as TextInputEditText).text.toString()
                        .isBlank() -> binding.etIfscCode.error = "Required"
                    (binding.etAccountName.editText as TextInputEditText).text.toString()
                        .isBlank() -> binding.etAccountName.error = "Required"
                    else -> {
                        val accountNumber =
                            (binding.etAccountNumber.editText as TextInputEditText).text.toString()
                        val ifscCode =
                            (binding.etIfscCode.editText as TextInputEditText).text.toString()
                        val accountName =
                            (binding.etAccountName.editText as TextInputEditText).text.toString()
                        homeViewModel.createUserBank(
                            hashMapOf(
                                "accountNumber" to accountNumber,
                                "ifscCode" to ifscCode,
                                "accountName" to accountName
                            )
                        )
                    }
                }
            }
        }

        binding.btnConfirmBank.setOnClickListener {
            if (binding.pagerBankList.isVisible && (binding.pagerBankList.adapter?.itemCount
                    ?: 0) != 0
            ) {
                val parameters = args.sellParameters
                homeViewModel.sellGold(parameters.apply {
                    put(
                        "userBankId",
                        (binding.pagerBankList.adapter as BankListAdapter).getItem(binding.pagerBankList.currentItem).id
                    )
                })
            } else {
                Toast.makeText(context, "Please add a bank account", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}