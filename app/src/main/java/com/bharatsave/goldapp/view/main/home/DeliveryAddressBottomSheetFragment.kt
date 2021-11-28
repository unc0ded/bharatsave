package com.bharatsave.goldapp.view.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.FragmentDeliveryAddressBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeliveryAddressBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDeliveryAddressBottomSheetBinding? = null
    private val binding: FragmentDeliveryAddressBottomSheetBinding
        get() = _binding!!

    private val args by navArgs<DeliveryAddressBottomSheetFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveryAddressBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupViews() {
        binding.pagerAddress.isUserInputEnabled = false
        binding.pagerAddress.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

//                if (childFragmentManager.fragments.size > position) {
//                    val fragment = childFragmentManager.fragments[position]
//                    Log.d("fragment", "$fragment")
////                    fragment.view?.let {
////                        binding.pagerAddress.updatePagerHeightForChild(it)
////                    }
//                }

                binding.btnAddAddress.run {
                    when (position) {
                        0 -> {
                            text = getString(R.string.new_address)
                            setIconResource(R.drawable.ic_add_black_24dp)
                            binding.btnPlaceOrder.isVisible = true
                        }
                        1 -> {
                            text = getString(R.string.save_address)
                            setIconResource(R.drawable.ic_check_circle_outline_black_24dp)
                            binding.btnPlaceOrder.isVisible = false
                        }
                    }
                }
            }
        })
        binding.pagerAddress.adapter = AddressPagerAdapter(this)
    }

    fun getProductId() = args.productId
}

// TODO work on this later
private fun ViewPager2.updatePagerHeightForChild(view: View) {
    view.post {
        val wMeasureSpec = View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
        val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(wMeasureSpec, hMeasureSpec)

        if (this.layoutParams.height != view.measuredHeight) {
            this.layoutParams = (this.layoutParams as ConstraintLayout.LayoutParams).also {
                it.height = view.measuredHeight
            }
        }
    }
}
