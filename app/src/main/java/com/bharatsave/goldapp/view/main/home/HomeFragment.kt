package com.bharatsave.goldapp.view.main.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.FragmentHomeBinding
import com.bharatsave.goldapp.util.getThemeColorFromAttr
import com.bharatsave.goldapp.util.setCustomSpanString
import com.bharatsave.goldapp.util.setCustomTypefaceSpanString
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Basic badge with no count
//        initBadge(binding.btnOptions, 0)
        setupViews()
    }

    private fun setupViews() {
        binding.tvPlans.setCustomTypefaceSpanString(
            "save",
            R.font.eina01_light,
            "",
            "Plans",
            R.font.eina01_semi_bold
        )
        binding.tvLearn.setCustomTypefaceSpanString(
            "know",
            R.font.eina01_light,
            " ",
            "Digital Gold",
            R.font.eina01_semi_bold
        )
        binding.tvTitle.setCustomSpanString(
            "Bharat",
            R.font.eina01_regular,
            requireContext().getThemeColorFromAttr(R.attr.colorPrimary),
            "",
            "Save",
            R.font.eina01_semi_bold,
            requireContext().getThemeColorFromAttr(R.attr.colorSecondaryVariant)
        )

        binding.btnPlanPeriodic.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionAddPlan())
        }

        binding.btnLearn.setOnClickListener { findNavController().navigate(HomeFragmentDirections.actionSaveLearn()) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun initBadge(view: View, notificationCount: Int) {
        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                BadgeDrawable.create(requireContext()).apply {
                    if (notificationCount == 0) {
                        clearNumber()
                    } else number = notificationCount
                    backgroundColor = ContextCompat.getColor(requireContext(), R.color.brand_gold)
                    verticalOffset = 10
                    horizontalOffset = 10
                    BadgeUtils.attachBadgeDrawable(this, binding.btnOptions)
                }
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
}