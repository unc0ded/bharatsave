package com.bharatsave.goldapp.view.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bharatsave.goldapp.databinding.FragmentPlanDetailsBinding
import com.bharatsave.goldapp.model.PlanDetail
import com.bharatsave.goldapp.util.increaseHitArea
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import dagger.hilt.android.AndroidEntryPoint
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@AndroidEntryPoint
class PlanDetailsFragment : Fragment() {

    private var _binding: FragmentPlanDetailsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<PlanDetailsFragmentArgs>()
    private val viewModel by viewModels<HomeViewModel>()

    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var subscriptionId: String

    private val SUBSCRIPTION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentDayMillis = ZonedDateTime.now().toInstant().toEpochMilli()

        val constraintBuilder = CalendarConstraints.Builder()
            .setStart(currentDayMillis)
            .setValidator(DateValidatorPointForward.from(currentDayMillis))

        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select start date")
            .setCalendarConstraints(constraintBuilder.build())
            .setSelection(
                Instant.ofEpochMilli(currentDayMillis).plus(1, ChronoUnit.DAYS).toEpochMilli()
            )
            .build()

        datePicker.addOnPositiveButtonClickListener {
            (binding.datePickerStartDate.editText as MaterialAutoCompleteTextView).setText(
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                ), false
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupObservers()
        setupViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SUBSCRIPTION_REQUEST_CODE && data != null) {
            Toast.makeText(
                context,
                data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"),
                Toast.LENGTH_SHORT
            ).show()
            viewModel.getSubscriptionStatus(subscriptionId)
        }
    }

    private fun setupObservers() {
        viewModel.subscriptionToken.observe(viewLifecycleOwner) {
            if (it != null) {
                subscriptionId = it.subscriptionId
                val paytmOrder = PaytmOrder(
                    it.orderId,
                    it.merchantId,
                    it.transactionToken,
                    "1.00",
                    "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=${it.orderId}"
                )
                val transactionManager =
                    TransactionManager(paytmOrder, object : PaytmPaymentTransactionCallback {
                        override fun onTransactionResponse(inResponse: Bundle?) {
                            Toast.makeText(
                                context,
                                "Payment Transaction response" + inResponse.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            viewModel.getSubscriptionStatus(it.subscriptionId)
                        }

                        override fun networkNotAvailable() {
                            Toast.makeText(
                                context,
                                "Network unavailable: Please try again",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onErrorProceed(p0: String?) {
                            Toast.makeText(context, "Error proceed: $p0", Toast.LENGTH_LONG).show()
                        }

                        override fun clientAuthenticationFailed(p0: String?) {
                            Toast.makeText(
                                context,
                                "Client authentication failed: $p0",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun someUIErrorOccurred(p0: String?) {
                            Toast.makeText(context, "UI error: $p0", Toast.LENGTH_LONG).show()
                        }

                        override fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?) {
                            Toast.makeText(
                                context,
                                "Error loading web page: $p1 $p2",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onBackPressedCancelTransaction() {
                            Toast.makeText(
                                context,
                                "Transaction Cancelled: User pressed back",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onTransactionCancel(p0: String?, p1: Bundle?) {
                            Toast.makeText(
                                context,
                                "Transaction Cancelled: $p0 " + p1.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
                transactionManager.setSubscriptioFlow(true)
                transactionManager.startTransaction(activity, SUBSCRIPTION_REQUEST_CODE)
            }
        }

        viewModel.subscriptionStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.status.contains("active", true)) {
                    Toast.makeText(
                        context,
                        "Subscription successfully created!",
                        Toast.LENGTH_SHORT
                    ).show()
                    // TODO Use WorkManager to setup periodic collects for daily frequency plan
                    viewModel.updateUserPlanDetails(
                        PlanDetail(
                            subscriptionId,
                            args.plan.toString(),
                            true,
                            it.userId
                        )
                    )
                    findNavController().navigate(PlanDetailsFragmentDirections.actionFinishPlan())
                } else {
                    when (it.status) {
                        "INIT" -> Toast.makeText(
                            context,
                            "Subscription Created, Pending Authorization",
                            Toast.LENGTH_SHORT
                        ).show()
                        "CLOSED" -> Toast.makeText(
                            context,
                            "Subscription closed due to incomplete authorization",
                            Toast.LENGTH_SHORT
                        ).show()
                        else -> Toast.makeText(context, it.status, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupViews() {
        binding.btnBack.run {
            increaseHitArea(20f)
            setOnClickListener { findNavController().popBackStack() }
        }

        binding.tvPlansTitle.text = args.plan.toString()

        binding.datePickerStartDate.setEndIconOnClickListener {
            datePicker.show(parentFragmentManager, "End Date")
        }
        (binding.datePickerStartDate.editText as MaterialAutoCompleteTextView).setOnClickListener {
            datePicker.show(parentFragmentManager, "Start Date")
        }

        (binding.menuDepositAmount.editText as TextInputEditText).doOnTextChanged { text, _, _, _ ->
            binding.menuDepositAmount.error =
                if (text.toString().isEmpty()) "Please enter amount" else ""
        }
        (binding.datePickerStartDate.editText as MaterialAutoCompleteTextView).doOnTextChanged { text, _, _, _ ->
            binding.datePickerStartDate.error =
                if (text.toString().isEmpty()) "Please select a date" else ""
        }

        binding.btnContinue.setOnClickListener {
            if ((binding.datePickerStartDate.editText as MaterialAutoCompleteTextView).text.toString()
                    .isNotBlank() && (binding.menuDepositAmount.editText as TextInputEditText).text.toString()
                    .isNotBlank()
            ) {
                viewModel.createPlan(
                    hashMapOf(
                        "planName" to args.plan.toString(),
                        "amount" to (binding.menuDepositAmount.editText as TextInputEditText).text.toString(),
                        "startDate" to LocalDate.parse(
                            (binding.datePickerStartDate.editText as MaterialAutoCompleteTextView).text.toString(),
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        ).format(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        )
                    )
                )
            } else {
                binding.run {
                    datePickerStartDate.error =
                        if ((datePickerStartDate.editText as MaterialAutoCompleteTextView).text.toString()
                                .isBlank()
                        ) "Please select a date" else ""
                    menuDepositAmount.error =
                        if ((menuDepositAmount.editText as TextInputEditText).text.toString()
                                .isBlank()
                        ) "Please enter amount" else ""
                }
            }
        }
    }

}