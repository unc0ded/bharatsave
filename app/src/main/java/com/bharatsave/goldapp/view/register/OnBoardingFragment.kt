package com.bharatsave.goldapp.view.register

import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.os.HandlerCompat
import androidx.navigation.fragment.findNavController
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.FragmentOnboardingBinding
import com.bharatsave.goldapp.model.OnBoardingItem
import com.google.android.material.tabs.TabLayoutMediator

class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    private lateinit var autoSwipeRunnable: Runnable
    private val mHandler = HandlerCompat.createAsync(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        autoSwipeRunnable = Runnable {
            binding.pagerOnboard.apply {
                setCurrentItem((currentItem + 1) % adapter!!.itemCount, true)
            }
            mHandler.postDelayed(autoSwipeRunnable, 3000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.pagerOnboard.apply {
            adapter = OnBoardingAdapter(
                listOf(
                    OnBoardingItem(
                        "Save. Invest. Forget",
                        "Financial investment simplified through",
                        "Gold",
                        R.drawable.mascot_3
                    ),
                    OnBoardingItem(
                        "Fulfill your goals with Nudge",
                        "Plan your goals and invest with",
                        "nudgePlans",
                        R.drawable.mascot_1
                    ),
                    OnBoardingItem(
                        "Learn with nudgeLearn",
                        "Learn all about finance easily with",
                        "nudgeLearn",
                        R.drawable.mascot_2
                    )
                ), context
            )
            TabLayoutMediator(
                binding.indicatorOnboard, this
            ) { _, _ -> }.attach()

            getChildAt(0).setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    if (HandlerCompat.hasCallbacks(mHandler, autoSwipeRunnable)) {
                        mHandler.removeCallbacks(autoSwipeRunnable)
                    }
                    return@setOnTouchListener true
                }
                v.performClick()
                return@setOnTouchListener false
            }
        }

        binding.btnContinue.setOnClickListener {
            findNavController().navigate(OnBoardingFragmentDirections.actionAuth())
        }
    }

    override fun onResume() {
        super.onResume()
        mHandler.postDelayed(autoSwipeRunnable, 3000)
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacks(autoSwipeRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}