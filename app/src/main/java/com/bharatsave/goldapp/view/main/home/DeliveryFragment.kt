package com.bharatsave.goldapp.view.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.FragmentDeliveryBinding
import com.bharatsave.goldapp.model.GoldCoin
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

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val homeViewModel by viewModels<HomeViewModel>()

    private var productId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeViewModel.getGoldDeliveryOptions()
    }

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
        productId = savedInstanceState?.getString("productId", "") ?: ""

        setupViews()
        setupObservers()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("productId", productId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        mainViewModel.balanceData.observe(viewLifecycleOwner) {
            if (!it.isNullOrBlank()) {
                binding.tvGoldHeld.text =
                    "${normalDecimalFormat.format(it.toFloat())}gm"
                mainViewModel.goldRateData.value?.first?.run {
                    binding.tvPortfolioValue.text =
                        "₹${normalDecimalFormat.format(it.toFloat() * this.sellPrice.toFloat())}"
                }
            }
        }

        homeViewModel.productData.observe(viewLifecycleOwner) {
            if (it != null && it.isNotEmpty()) {
                (binding.etGoldDeliveryOption.editText as MaterialAutoCompleteTextView).setAdapter(
                    ArrayAdapter(requireContext(), R.layout.dropdown_menu_item, it)
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
            binding.tvMakingChargesPrice.text = try {
                (((binding.etGoldDeliveryOption.editText as MaterialAutoCompleteTextView).adapter as ArrayAdapter<*>).getItem(
                    position
                ) as GoldCoin).makingCharges
            } catch (e: ClassCastException) {
                "N/A"
            }
            productId =
                (((binding.etGoldDeliveryOption.editText as MaterialAutoCompleteTextView).adapter as ArrayAdapter<*>).getItem(
                    position
                ) as GoldCoin).sku
            binding.cardMakingCharges.isVisible = true
            binding.btnSelectAddress.isVisible = true
            binding.ivDeliveryBackground.isVisible = true
        }

        binding.btnSelectAddress.setOnClickListener {
            findNavController().navigate(
                DeliveryFragmentDirections.actionSelectDeliveryAddress(
                    purpose = AddressBottomSheetPurpose.SELECT_ADDRESS,
                    productId = productId
                )
            )
        }
    }
}