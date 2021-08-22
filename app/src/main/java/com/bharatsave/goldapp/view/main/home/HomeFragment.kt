package com.bharatsave.goldapp.view.main.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.FragmentHomeBinding
import com.bharatsave.goldapp.util.getThemeColorFromAttr
import com.bharatsave.goldapp.util.setCustomSpanString
import com.bharatsave.goldapp.util.setCustomTypefaceSpanString
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class HomeFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>()

    private val decimalFormat by lazy {
        DecimalFormat("#,##0.00")
    }
    private var buyAmount: Float = 0f
    private var buyQuantity: Float = 0f

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

    private fun setupObservers() {
        viewModel.goldRateData.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.tvLiveGoldPrice.text =
                    "₹${decimalFormat.format(it.first.goldPrice.toFloat())}/gm"
                binding.tvDigitalGoldPrice.text =
                    "₹${decimalFormat.format(it.first.goldPrice.toFloat())}/gm"
                binding.tvGoldBalancePrice.text =
                    "₹${decimalFormat.format(it.first.goldPrice.toFloat())}/gm"
            }
        }
        viewModel.balanceData.observe(viewLifecycleOwner) {
            if (it.goldBalance.toFloat() != 0f) {
                binding.cardLiveGold.visibility = View.GONE
                viewModel.goldRateData.value?.run {
                    binding.tvGoldCurrentValue.text =
                        "₹${decimalFormat.format(first.totalSellPrice.toFloat() * it.goldBalance.toFloat())}/gm"
                    binding.tvGoldCurrentValueChange.text = "${decimalFormat.format(second)}%"
                    binding.tvGoldCurrentValueChange.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        if (second > 0) R.drawable.ic_arrow_drop_up_black_24dp else R.drawable.ic_arrow_drop_down_black_24dp,
                        0,
                        0,
                        0
                    )
                }
                binding.tvGoldBalance.text = "${decimalFormat.format(it.goldBalance.toFloat())}gms"
                binding.cardGoldBalance.visibility = View.VISIBLE
                binding.ivFloatingLogo.visibility = View.VISIBLE
                binding.btnSellGold.visibility = View.VISIBLE
                binding.btnRequestDelivery.visibility = View.VISIBLE
            } else {
                binding.cardLiveGold.visibility = View.VISIBLE
                binding.cardGoldBalance.visibility = View.GONE
                binding.ivFloatingLogo.visibility = View.GONE
                binding.btnSellGold.visibility = View.GONE
                binding.btnRequestDelivery.visibility = View.GONE
            }
        }
    }

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
        binding.tvLiveGoldPrice.text = "₹4,654.78/gm"

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
                    binding.etBuyAmount.hint = "Enter amount"
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
                    binding.etBuyAmount.hint = "Enter quantity"
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

        binding.etBuyAmount.editText?.doOnTextChanged { text, _, _, _ ->
            when (binding.toggleGroupBuyOption.checkedRadioButtonId) {
                R.id.toggle_buy_currency -> buyAmount =
                    if (text.toString().isNotBlank()) text.toString().toFloat() else 0f
                R.id.toggle_buy_quantity -> buyQuantity =
                    if (text.toString().isNotBlank()) text.toString().toFloat() else 0f
            }
        }

        binding.chipAdd500.setOnClickListener(this)
        binding.chipAdd1000.setOnClickListener(this)
        binding.chipAdd5000.setOnClickListener(this)
        binding.chipAdd10000.setOnClickListener(this)

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