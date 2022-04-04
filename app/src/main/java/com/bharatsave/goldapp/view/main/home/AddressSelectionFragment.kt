package com.bharatsave.goldapp.view.main.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.FragmentAddressSelectionBinding
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressSelectionFragment : Fragment() {

    private var _binding: FragmentAddressSelectionBinding? = null
    private val binding: FragmentAddressSelectionBinding
        get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>()

    val EXTRA_BS_PURPOSE = "extra_bs_purpose"
    private var purpose = AddressBottomSheetPurpose.SELECT_ADDRESS

    companion object {

        @JvmStatic
        fun newInstance(purpose: AddressBottomSheetPurpose) = AddressSelectionFragment().apply {
            arguments = Bundle().apply {
                putSerializable(EXTRA_BS_PURPOSE, purpose)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getSerializable(EXTRA_BS_PURPOSE)?.let {
            purpose = (it as AddressBottomSheetPurpose)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getAddressList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddressSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupObservers()
        setupViews()
    }

    // TODO find a better way to do this
    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        viewModel.addressData.observe(viewLifecycleOwner) {
            if (it != null && it.isNotEmpty()) {
                val adapter = AddressListAdapter(
                    it,
                    purpose != AddressBottomSheetPurpose.VIEW_ADDRESS
                )
                binding.rvAddress.adapter = adapter
                binding.rvAddress.isVisible = true
                if (purpose == AddressBottomSheetPurpose.SELECT_ADDRESS) {
                    (parentFragment as DeliveryAddressBottomSheetFragment).view?.findViewById<MaterialButton>(
                        R.id.btn_place_order
                    )?.isVisible = true
                }
            } else {
                binding.rvAddress.isVisible = false
                (parentFragment as DeliveryAddressBottomSheetFragment).view?.findViewById<MaterialButton>(
                    R.id.btn_place_order
                )?.isVisible = false
            }
        }

        viewModel.orderStatus.observe(viewLifecycleOwner) {
            if (it != null && !it.contains("success", true)) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.orderData.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(
                    context,
                    "${it.productName} ordered successfully!",
                    Toast.LENGTH_SHORT
                ).show()
                (parentFragment as DeliveryAddressBottomSheetFragment).dismiss()
            }
        }
    }

    private fun setupViews() {
        binding.rvAddress.layoutManager = LinearLayoutManager(context)

        if (purpose == AddressBottomSheetPurpose.VIEW_ADDRESS) {
            binding.tvTitle.text = getString(R.string.saved_addresses)
        } else {
            binding.tvTitle.text = getString(R.string.select_delivery_address)
        }

        (parentFragment as DeliveryAddressBottomSheetFragment).view?.run {
            findViewById<MaterialButton>(R.id.btn_add_address)?.setOnClickListener {
                findViewById<ViewPager2>(R.id.pager_address).currentItem = 1
            }
            findViewById<MaterialButton>(R.id.btn_place_order)?.setOnClickListener {
                if (binding.rvAddress.adapter!!.itemCount != 0 && (binding.rvAddress.adapter as AddressListAdapter).getCheckedAddressId()
                        .isNotEmpty()
                ) {
                    val productId =
                        (parentFragment as DeliveryAddressBottomSheetFragment).getProductId()
                    productId?.let {
                        viewModel.placeProductOrder(
                            hashMapOf(
                                "addressId" to (binding.rvAddress.adapter as AddressListAdapter).getCheckedAddressId(),
                                "productId" to it,
                                "quantity" to "1"
                            )
                        )
                    }
                }
            }
        }
    }
}