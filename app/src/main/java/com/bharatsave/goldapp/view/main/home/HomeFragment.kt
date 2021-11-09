package com.bharatsave.goldapp.view.main.home

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.FragmentHomeBinding
import com.bharatsave.goldapp.model.PaytmTransaction
import com.bharatsave.goldapp.util.getThemeColorFromAttr
import com.bharatsave.goldapp.util.setCustomSpanString
import com.bharatsave.goldapp.util.setCustomTypefaceSpanString
import com.bharatsave.goldapp.util.textChanges
import com.bharatsave.goldapp.view.main.MainViewModel
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.textfield.TextInputEditText
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.DecimalFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class HomeFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val homeViewModel by viewModels<HomeViewModel>()

    private val normalDecimalFormat by lazy {
        DecimalFormat("#,##,##0.00")
    }
    private val longDecimalFormat by lazy {
        DecimalFormat("##0.0000")
    }
    private var buyAmount: Float = 0f
    private var buyQuantity: Float = 0f

    private lateinit var orderId: String

    private val TRANSACTION_REQUEST_CODE = 102

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
        setupObservers()
        setupViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TRANSACTION_REQUEST_CODE && data != null) {
            Toast.makeText(
                context,
                data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"),
                Toast.LENGTH_SHORT
            ).show()
            homeViewModel.getTransactionStatus(orderId)
        }
    }

    private fun setupObservers() {
        mainViewModel.goldRateData.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.tvLiveGoldPrice.text =
                    "₹${normalDecimalFormat.format(it.first.buyPrice.toFloat())}/gm"
                binding.tvDigitalGoldPrice.text =
                    "₹${normalDecimalFormat.format(it.first.buyPrice.toFloat())}/gm"
                binding.tvGoldBalancePrice.text =
                    "₹${normalDecimalFormat.format(it.first.buyPrice.toFloat())}/gm"
            }
        }

        mainViewModel.balanceData.observe(viewLifecycleOwner) {
            if (it != null && it.goldBalance.toFloat() != 0f) {
                binding.cardLiveGold.isVisible = false
                mainViewModel.goldRateData.value?.run {
                    binding.tvGoldCurrentValue.text =
                        "₹${normalDecimalFormat.format(first.sellPrice.toFloat() * it.goldBalance.toFloat())}"
                    binding.tvGoldCurrentValueChange.text = "${normalDecimalFormat.format(second)}%"
                    binding.tvGoldCurrentValueChange.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        if (second > 0) R.drawable.ic_arrow_drop_up_black_24dp else R.drawable.ic_arrow_drop_down_black_24dp,
                        0,
                        0,
                        0
                    )
                    TextViewCompat.setCompoundDrawableTintList(
                        binding.tvGoldCurrentValueChange,
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                if (second > 0) R.color.icon_green else R.color.icon_red
                            )
                        )
                    )
                }
                binding.tvGoldBalance.text =
                    "${longDecimalFormat.format(it.goldBalance.toFloat())}gms"
                binding.cardGoldBalance.isVisible = true
                binding.ivFloatingLogo.isVisible = true
                binding.btnSellGold.isVisible = true
                binding.btnRequestDelivery.isVisible = true
            } else {
                binding.cardLiveGold.isVisible = true
                binding.cardGoldBalance.isVisible = false
                binding.ivFloatingLogo.isVisible = false
                binding.btnSellGold.isVisible = false
                binding.btnRequestDelivery.isVisible = false
            }
        }

        homeViewModel.transactionToken.observe(viewLifecycleOwner) {
            if (it != null) {
                orderId = it.orderId
                val paytmOrder = PaytmOrder(
                    it.orderId,
                    it.merchantId,
                    it.transactionToken,
                    it.amount,
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
                            homeViewModel.getTransactionStatus(it.orderId)
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
                transactionManager.startTransaction(activity, TRANSACTION_REQUEST_CODE)
            }
        }

        homeViewModel.transactionStatus.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.status.contains("success", true)) {
                    Toast.makeText(
                        context,
                        "Transaction complete!",
                        Toast.LENGTH_SHORT
                    ).show()
                    homeViewModel.buyGold(
                        hashMapOf(
                            "amount" to it.transactionAmount,
                            "buyPrice" to mainViewModel.goldRateData.value!!.first.buyPrice,
                            "blockId" to mainViewModel.goldRateData.value!!.first.blockId
                        )
                    )
                    homeViewModel.saveTransaction(
                        PaytmTransaction(
                            it.orderId,
                            it.transactionId,
                            it.bankTransactionId,
                            it.transactionAmount,
                            ZonedDateTime.parse(
                                it.date,
                                DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss")
                            ).toInstant().toEpochMilli()
                        )
                    )
                } else {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    private fun setupViews() {
        binding.tvPlansHeading.setCustomTypefaceSpanString(
            "save",
            R.font.eina01_light,
            "",
            "Plans",
            R.font.eina01_semi_bold
        )
        binding.tvKnowGoldHeading.setCustomTypefaceSpanString(
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
        binding.tvBuyGoldHeading.setCustomTypefaceSpanString(
            "buy",
            R.font.eina01_regular,
            " ",
            "gold",
            R.font.eina01_semi_bold
        )
        binding.toggleBuyQuantity.text =
            SpannableString(resources.getString(R.string.buy_quantity_text)).apply {
                setSpan(UnderlineSpan(), 0, this.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

        binding.btnPlanPeriodic.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionAddPlan())
        }

        binding.btnPlanRoundUp.setOnClickListener {
            Toast.makeText(context, "Coming soon!", Toast.LENGTH_SHORT).show()
//            findNavController().navigate(HomeFragmentDirections.actionRoundUpDetails())
        }

        binding.toggleGroupBuyOption.setOnCheckedChangeListener { _, buttonId ->
            when (buttonId) {
                R.id.toggle_buy_currency -> {
                    binding.toggleBuyCurrency.text = resources.getString(R.string.buy_currency_text)
                    binding.toggleBuyQuantity.text =
                        SpannableString(resources.getString(R.string.buy_quantity_text)).apply {
                            setSpan(
                                UnderlineSpan(),
                                0,
                                this.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    binding.svChipGroup.visibility = View.VISIBLE
                    binding.etBuyAmount.prefixText = "₹"
                    binding.etBuyAmount.suffixText = ""
                    binding.etBuyAmount.hint = getString(R.string.enter_amount)
                    if (buyAmount != 0f) {
                        (binding.etBuyAmount.editText as TextInputEditText).run {
                            setText(buyAmount.toString())
                            setSelection(
                                if (this.text.toString()
                                        .isNotBlank()
                                ) this.text.toString().length else 0
                            )
                        }
                    } else (binding.etBuyAmount.editText as TextInputEditText).setText("")
                }
                R.id.toggle_buy_quantity -> {
                    binding.toggleBuyQuantity.text = resources.getString(R.string.buy_quantity_text)
                    binding.toggleBuyCurrency.text =
                        SpannableString(resources.getString(R.string.buy_currency_text)).apply {
                            setSpan(
                                UnderlineSpan(),
                                0,
                                this.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    binding.svChipGroup.visibility = View.GONE
                    binding.etBuyAmount.prefixText = ""
                    binding.etBuyAmount.suffixText = "gm"
                    binding.etBuyAmount.hint = getString(R.string.enter_quantity)
                    if (buyQuantity != 0f) {
                        (binding.etBuyAmount.editText as TextInputEditText).run {
                            setText(buyQuantity.toString())
                            setSelection(
                                if (this.text.toString()
                                        .isNotBlank()
                                ) this.text.toString().length else 0
                            )
                        }
                    } else (binding.etBuyAmount.editText as TextInputEditText).setText("")
                }
            }
        }

        (binding.etBuyAmount.editText as TextInputEditText).textChanges().debounce(500)
            .onEach { text ->
                when (binding.toggleGroupBuyOption.checkedRadioButtonId) {
                    R.id.toggle_buy_currency -> {
                        if (text.toString().isNotBlank()) {
                            buyAmount = text.toString().toFloat()
                            if (buyAmount > 1000000) {
                                binding.etBuyAmount.error = getString(R.string.error_lower_amount)
                                binding.cardCheckoutDetails.visibility = View.GONE
                            } else {
                                binding.etBuyAmount.isErrorEnabled = false
                                mainViewModel.goldRateData.value?.first?.run {
                                    buyQuantity = buyAmount / (buyPrice + buyGst).toFloat()
                                    binding.tvCheckoutAmount.text =
                                        "₹${normalDecimalFormat.format(buyAmount)}"
                                    binding.tvCheckoutWeight.text =
                                        "${longDecimalFormat.format(buyQuantity)}gms"
                                }
                                binding.cardCheckoutDetails.visibility = View.VISIBLE
                            }
                        } else {
                            buyAmount = 0f
                            binding.cardCheckoutDetails.visibility = View.GONE
                        }
                    }
                    R.id.toggle_buy_quantity -> {
                        if (text.toString().isNotBlank()) {
                            buyQuantity = text.toString().toFloat()
                            if (buyQuantity > 250) {
                                binding.etBuyAmount.error = getString(R.string.error_lower_quantity)
                                binding.cardCheckoutDetails.visibility = View.GONE
                            } else {
                                binding.etBuyAmount.isErrorEnabled = false
                                mainViewModel.goldRateData.value?.first?.run {
                                    buyAmount = buyQuantity * (buyPrice + buyGst).toFloat()
                                    binding.tvCheckoutAmount.text =
                                        "₹${normalDecimalFormat.format(buyAmount)}"
                                    binding.tvCheckoutWeight.text =
                                        "${longDecimalFormat.format(buyQuantity)}gms"
                                }
                                binding.cardCheckoutDetails.visibility = View.VISIBLE
                            }
                        } else {
                            buyQuantity = 0f
                            binding.cardCheckoutDetails.visibility = View.GONE
                        }
                    }
                }
            }.launchIn(lifecycleScope)

        binding.chipAdd500.setOnClickListener(this)
        binding.chipAdd1000.setOnClickListener(this)
        binding.chipAdd5000.setOnClickListener(this)
        binding.chipAdd10000.setOnClickListener(this)

        binding.btnBuyGold.setOnClickListener {
            if (!binding.etBuyAmount.error.isNullOrBlank()) {
                Toast.makeText(context, binding.etBuyAmount.error.toString(), Toast.LENGTH_SHORT)
                    .show()
            } else if ((binding.etBuyAmount.editText as TextInputEditText).text.isNullOrBlank()) {
                Toast.makeText(context, R.string.error_enter_something, Toast.LENGTH_SHORT).show()
            } else {
                homeViewModel.startTransaction(
                    hashMapOf(
                        "amount" to normalDecimalFormat.format(
                            buyAmount
                        )
                    )
                )
            }
        }

        binding.btnSellGold.setOnClickListener { findNavController().navigate(HomeFragmentDirections.actionWithdraw()) }
        binding.btnRequestDelivery.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionRequestDelivery()
            )
        }
        binding.btnLearn.setOnClickListener { findNavController().navigate(HomeFragmentDirections.actionSaveLearn()) }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun initBadge(view: View, notificationCount: Int) {
        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                BadgeDrawable.create(requireContext()).run {
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

    override fun onClick(v: View?) {
        (binding.etBuyAmount.editText as TextInputEditText).run {
            setText(
                (if ((binding.etBuyAmount.editText as TextInputEditText).text.toString()
                        .isBlank()
                ) v?.tag.toString().toFloat() else binding.etBuyAmount.editText?.text.toString()
                    .toFloat() + v?.tag.toString().toFloat()).toString()
            )
            setSelection(if (this.text.toString().isNotBlank()) this.text.toString().length else 0)
        }
    }
}