package com.bharatsave.goldapp.view.main.home

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.FragmentWithdrawBinding
import com.bharatsave.goldapp.util.*
import com.bharatsave.goldapp.view.main.MainViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.DecimalFormat

@AndroidEntryPoint
class WithdrawFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentWithdrawBinding? = null
    private val binding get() = _binding!!

    private val normalDecimalFormat by lazy {
        DecimalFormat("#,##,##0.00")
    }

    private val longDecimalFormat by lazy {
        DecimalFormat("#0.0000")
    }

    private val mainViewModel by activityViewModels<MainViewModel>()

    private var sellAmount: Float = 0f
    private var sellQuantity: Float = 0f
    private var currentAmountBalance: Float = 0f
    private var currentWeightBalance: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWithdrawBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        mainViewModel.balanceData.observe(viewLifecycleOwner) {
            if (it != null) {
                currentWeightBalance = it.goldBalance.toFloat()
                binding.tvInvestment.text =
                    "₹${normalDecimalFormat.format(it.amountInvested.toFloat())}"
                binding.tvGoldWorth.text =
                    "${normalDecimalFormat.format(currentWeightBalance)}gm"
                mainViewModel.goldRateData.value?.first?.run {
                    currentAmountBalance = it.goldBalance.toFloat() * sellPrice.toFloat()
                    binding.tvPortfolioValue.text =
                        "₹${normalDecimalFormat.format(currentAmountBalance)}"
                }
            }
        }

        mainViewModel.goldRateData.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.tvDigitalGoldPrice.text =
                    "₹${normalDecimalFormat.format(it.first.buyPrice.toFloat())}/gm"
            }
        }
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    private fun setupViews() {
        binding.tvTitleSellGold.setCustomTypefaceSpanString(
            "sell",
            R.font.eina01_regular,
            " ",
            "gold",
            R.font.eina01_semi_bold
        )
        binding.btnBack.run {
            increaseHitArea(40f)
            setOnClickListener { findNavController().popBackStack() }
        }

        binding.toggleSellQuantity.text =
            SpannableString(resources.getString(R.string.sell_quantity_text)).apply {
                setSpan(UnderlineSpan(), 0, this.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

        binding.toggleGroupSellOption.setOnCheckedChangeListener { _, buttonId ->
            when (buttonId) {
                R.id.toggle_sell_currency -> {
                    binding.toggleSellCurrency.text =
                        resources.getString(R.string.sell_currency_text)
                    binding.toggleSellQuantity.text =
                        SpannableString(resources.getString(R.string.sell_quantity_text)).apply {
                            setSpan(
                                UnderlineSpan(),
                                0,
                                this.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    binding.svChipGroup.visibility = View.VISIBLE
                    binding.etSellAmount.prefixText = "₹"
                    binding.etSellAmount.suffixText = ""
                    binding.etSellAmount.hint = "Enter amount"
                    if (sellAmount != 0f) {
                        (binding.etSellAmount.editText as TextInputEditText).run {
                            setText(sellAmount.toString())
                            setSelection(
                                if (this.text.toString()
                                        .isNotBlank()
                                ) this.text.toString().length else 0
                            )
                        }
                    } else (binding.etSellAmount.editText as TextInputEditText).setText("")
                }
                R.id.toggle_sell_quantity -> {
                    binding.toggleSellQuantity.text =
                        resources.getString(R.string.sell_quantity_text)
                    binding.toggleSellCurrency.text =
                        SpannableString(resources.getString(R.string.sell_currency_text)).apply {
                            setSpan(
                                UnderlineSpan(),
                                0,
                                this.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    binding.svChipGroup.visibility = View.GONE
                    binding.etSellAmount.prefixText = ""
                    binding.etSellAmount.suffixText = "gm"
                    binding.etSellAmount.hint = "Enter quantity"
                    if (sellQuantity != 0f) {
                        (binding.etSellAmount.editText as TextInputEditText).run {
                            setText(sellQuantity.toString())
                            setSelection(
                                if (this.text.toString()
                                        .isNotBlank()
                                ) this.text.toString().length else 0
                            )
                        }
                    } else (binding.etSellAmount.editText as TextInputEditText).setText("")
                }
            }
        }

        (binding.etSellAmount.editText as TextInputEditText).textChanges().debounce(500)
            .onEach { text ->
                when (binding.toggleGroupSellOption.checkedRadioButtonId) {
                    R.id.toggle_sell_currency -> {
                        if (text.toString().isNotBlank()) {
                            sellAmount = text.toString().toFloat()
                            if (sellAmount > currentAmountBalance) {
                                binding.etSellAmount.error = "Amount exceeds balance"
                                binding.cardSellDetails.visibility = View.GONE
                            } else {
                                binding.etSellAmount.isErrorEnabled = false
                                mainViewModel.goldRateData.value?.first?.run {
                                    binding.tvCurrentRate.text = "at $sellPrice/gm"
                                    sellQuantity = sellAmount / sellPrice.toFloat()
                                    binding.tvSellAmount.text =
                                        "₹${normalDecimalFormat.format(sellAmount)}"
                                    binding.tvSellWeight.text =
                                        "${longDecimalFormat.format(sellQuantity)}gms"
                                }
                                binding.cardSellDetails.visibility = View.VISIBLE
                            }
                        } else {
                            sellAmount = 0f
                            binding.cardSellDetails.visibility = View.GONE
                        }
                    }
                    R.id.toggle_sell_quantity -> {
                        if (text.toString().isNotBlank()) {
                            sellQuantity = text.toString().toFloat()
                            if (sellQuantity > currentWeightBalance) {
                                binding.etSellAmount.error = "Weight exceeds balance"
                                binding.cardSellDetails.visibility = View.GONE
                            } else {
                                binding.etSellAmount.isErrorEnabled = false
                                mainViewModel.goldRateData.value?.first?.run {
                                    binding.tvCurrentRate.text = "at $sellPrice/gm"
                                    sellAmount = sellQuantity * sellPrice.toFloat()
                                    binding.tvSellAmount.text =
                                        "₹${normalDecimalFormat.format(sellAmount)}"
                                    binding.tvSellWeight.text =
                                        "${longDecimalFormat.format(sellQuantity)}gms"
                                }
                                binding.cardSellDetails.visibility = View.VISIBLE
                            }
                        } else {
                            sellQuantity = 0f
                            binding.cardSellDetails.visibility = View.GONE
                        }
                    }
                }
            }.launchIn(lifecycleScope)

        binding.chipAdd500.setOnClickListener(this)
        binding.chipAdd1000.setOnClickListener(this)
        binding.chipAdd5000.setOnClickListener(this)
        binding.chipAdd10000.setOnClickListener(this)

        binding.btnSellProceed.setOnClickListener {
            if (!binding.etSellAmount.error.isNullOrBlank()) {
                Toast.makeText(context, binding.etSellAmount.error.toString(), Toast.LENGTH_SHORT)
                    .show()
            } else if ((binding.etSellAmount.editText as TextInputEditText).text.isNullOrBlank()) {
                Toast.makeText(context, R.string.error_enter_something, Toast.LENGTH_SHORT).show()
            } else {
                findNavController().navigate(
                    WithdrawFragmentDirections.actionSelectBank(
                        stringHashMapOf(
                            when (binding.toggleGroupSellOption.checkedRadioButtonId) {
                                R.id.toggle_sell_currency -> "amount" to normalDecimalFormat.format(
                                    sellAmount
                                )
                                R.id.toggle_sell_quantity -> "quantity" to longDecimalFormat.format(
                                    sellQuantity
                                )
                                else -> throw UnsupportedOperationException("Neither of amount or quantity toggle selected")
                            },
                            "blockId" to mainViewModel.goldRateData.value?.first!!.blockId,
                            "lockPrice" to mainViewModel.goldRateData.value?.first!!.sellPrice
                        )
                    )
                )
//                findNavController().navigate(
//                    R.id.action_select_bank,
//                    bundleOf(
//                        when (binding.toggleGroupSellOption.checkedRadioButtonId) {
//                            R.id.toggle_sell_currency -> "amount" to normalDecimalFormat.format(
//                                sellAmount
//                            )
//                            R.id.toggle_sell_quantity -> "quantity" to longDecimalFormat.format(
//                                sellQuantity
//                            )
//                            else -> throw UnsupportedOperationException("Neither of amount or quantity toggle selected")
//                        },
//                        "blockId" to mainViewModel.goldRateData.value?.first!!.blockId,
//                        "lockPrice" to mainViewModel.goldRateData.value?.first!!.totalSellPrice
//                    )
//                )
            }
        }
    }

    override fun onClick(v: View?) {
        (binding.etSellAmount.editText as TextInputEditText).run {
            setText(
                (if ((binding.etSellAmount.editText as TextInputEditText).text.toString()
                        .isBlank()
                ) v?.tag.toString().toFloat() else binding.etSellAmount.editText?.text.toString()
                    .toFloat() + v?.tag.toString().toFloat()).toString()
            )
            setSelection(if (this.text.toString().isNotBlank()) this.text.toString().length else 0)
        }
    }
}