package com.dev.`in`.drogon.view.main.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.dev.`in`.drogon.R
import com.dev.`in`.drogon.databinding.FragmentHomeBinding
import com.dev.`in`.drogon.model.GoalDetails
import com.dev.`in`.drogon.util.setCustomTypefaceSpanString
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.tabs.TabLayoutMediator

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
        initBadge(binding.btnOptions, 0)
        setupViews()
    }

    private fun setupViews() {
        binding.tvPlans.setCustomTypefaceSpanString("nudge", "Plans")
        binding.tvLearn.setCustomTypefaceSpanString("nudge", "Learn")

        binding.btnAction.text = "manage goals"
        binding.pagerGoals.apply {

            // TODO use actual goals
            val goalsList = listOf(
                GoalDetails(goalName = "Child's Education", currentValue = 4654.78f),
                GoalDetails(goalName = "Daughter's Wedding", currentValue = 4654.78f),
                GoalDetails(goalName = "Maldives Vacation", currentValue = 4654.78f)
            )
            adapter = GoalsAdapter(goalsList)

            binding.tvDesc.text = goalsList[0].goalName
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    binding.tvDesc.text =
                        if (positionOffset > 0.5) goalsList[position + 1].goalName else goalsList[position].goalName
                }
            })
        }
        TabLayoutMediator(binding.pagerIndicator, binding.pagerGoals) { _, _ -> }.attach()

        binding.btnAction.setOnClickListener {
            // TODO add activity screen
        }

        binding.btnAddPlan.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionAddPlan())
        }

        binding.btnAddGoal.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionAddGoal())
        }
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