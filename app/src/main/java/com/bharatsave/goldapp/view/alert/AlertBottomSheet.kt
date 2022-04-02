package com.bharatsave.goldapp.view.alert

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bharatsave.goldapp.databinding.BottomSheetAlertBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AlertBottomSheet : BottomSheetDialogFragment() {

    private val TAG = "AlertBottomSheet"

    private var _binding: BottomSheetAlertBinding? = null
    private val binding: BottomSheetAlertBinding
        get() = _binding!!

    private val args by navArgs<AlertBottomSheetArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
    }

    private fun setupViews() {
        val alertData = args.alertData
        alertData?.let { alertData ->

            alertData.titleText?.let {
                binding.tvTitle.text = it
            } ?: run {
                binding.tvTitle.visibility = View.GONE
            }

            alertData.subTitleText?.let {
                binding.tvSubtitle.text = it
            } ?: run {
                binding.tvSubtitle.visibility = View.GONE
            }

            alertData.positiveText?.let {
                binding.btnPositive.text = it
                binding.btnPositive.setOnClickListener {
                    try {
                        alertData.positiveAction.invoke()
                    } catch (exception: Exception) {
                        Log.e(TAG, "#setupViews.positiveOnClick: ${exception.message}")
                    }
                }
            } ?: run {
                binding.btnPositive.visibility = View.GONE
            }

            alertData.negativeText?.let {
                binding.btnNegative.text = it
                binding.btnNegative.setOnClickListener {
                    try {
                        alertData.negativeAction.invoke()
                        dismiss()
                    } catch (exception: Exception) {
                        Log.e(TAG, "#setupViews.negativeOnClick: ${exception.message}")
                    }
                }
            } ?: run {
                binding.btnNegative.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}