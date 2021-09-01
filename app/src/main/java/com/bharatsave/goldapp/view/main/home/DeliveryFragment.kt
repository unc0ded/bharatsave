package com.bharatsave.goldapp.view.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.FragmentDeliveryBinding
import com.bharatsave.goldapp.util.increaseHitArea
import com.bharatsave.goldapp.util.setCustomTypefaceSpanString
import com.bharatsave.goldapp.view.main.MainViewModel
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class DeliveryFragment : Fragment() {

    private var _binding: FragmentDeliveryBinding? = null
    private val binding get() = _binding!!

    private val normalDecimalFormat by lazy {
        DecimalFormat("#,##,##0.00")
    }

    private val viewModel by activityViewModels<MainViewModel>()

    private lateinit var deliveryOptions: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        viewModel.balanceData.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.tvInvestment.text =
                    "₹${normalDecimalFormat.format(it.amountInvested.toFloat())}"
                binding.tvGoldWorth.text =
                    "${normalDecimalFormat.format(it.goldBalance.toFloat())}gm"
                viewModel.goldRateData.value?.first?.run {
                    binding.tvPortfolioValue.text =
                        "₹${normalDecimalFormat.format(it.goldBalance.toFloat() * this.totalSellPrice.toFloat())}"
                }
                deliveryOptions = resources.getStringArray(R.array.delivery_list).asList().run {
                    when {
                        it.goldBalance.toFloat() < 0.1f -> take(0)
                        it.goldBalance.toFloat() < 0.5f -> take(1)
                        it.goldBalance.toFloat() < 1f -> take(2)
                        it.goldBalance.toFloat() < 2f -> take(3)
                        it.goldBalance.toFloat() < 5f -> take(4)
                        it.goldBalance.toFloat() < 8f -> take(5)
                        it.goldBalance.toFloat() < 10f -> take(6)
                        it.goldBalance.toFloat() < 20f -> take(7)
                        it.goldBalance.toFloat() < 50f -> take(8)
                        else -> this
                    }
                }
                (binding.etGoldDeliveryOption.editText as MaterialAutoCompleteTextView).setAdapter(
                    ArrayAdapter(requireContext(), R.layout.dropdown_menu_item, deliveryOptions)
                )
            }
        }
    }

    private fun setupViews() {
        binding.tvDeliverGoldTitle.setCustomTypefaceSpanString(
            "deliver",
            R.font.eina01_regular,
            " ",
            "gold",
            R.font.eina01_semi_bold
        )
        binding.btnBack.run {
            increaseHitArea(40f)
            setOnClickListener { findNavController().popBackStack() }
        }

        (binding.etGoldDeliveryOption.editText as MaterialAutoCompleteTextView).setOnItemClickListener { _, _, position, _ ->
            binding.tvMakingChargesPrice.text = when (position) {
                0 -> "₹200"
                1 -> "₹300"
                2 -> "₹350"
                3 -> "₹400"
                4 -> "₹500"
                5 -> "₹650"
                6 -> "₹800"
                7 -> "₹1100"
                8 -> "₹2100"
                else -> "N/A"
            }
            binding.cardMakingCharges.visibility = View.VISIBLE
        }
    }
}