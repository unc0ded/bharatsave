package com.dev.`in`.drogon.view.main.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.dev.`in`.drogon.R
import com.dev.`in`.drogon.databinding.FragmentHomeBinding
import com.dev.`in`.drogon.util.CustomTypefaceSpan
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Basic badge with no count
        initBadge(binding.btnOptions, 0)
        setupViews()
    }

    private fun setupViews() {
        binding.tvPlans.text = getCustomTypefaceSpanString("nudge", "Plans")
        binding.tvLearn.text = getCustomTypefaceSpanString("nudge", "Goals")
    }

    private fun getCustomTypefaceSpanString(
        firstWord: String,
        secondWord: String
    ): SpannableString {
        val spannable = SpannableString(firstWord + secondWord)
        spannable.setSpan(
            CustomTypefaceSpan(
                ResourcesCompat.getFont(
                    requireContext(),
                    R.font.eina01_light
                )!!
            ), 0, firstWord.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            CustomTypefaceSpan(
                ResourcesCompat.getFont(requireContext(), R.font.eina01_semi_bold)!!
            ),
            firstWord.length,
            firstWord.length + secondWord.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        return spannable
    }

    override fun onResume() {
        super.onResume()
        binding.btnOptions.isEnabled = true
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