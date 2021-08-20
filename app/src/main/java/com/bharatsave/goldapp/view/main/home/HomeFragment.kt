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
import androidx.navigation.fragment.findNavController
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.FragmentHomeBinding
import com.bharatsave.goldapp.util.getThemeColorFromAttr
import com.bharatsave.goldapp.util.setCustomSpanString
import com.bharatsave.goldapp.util.setCustomTypefaceSpanString
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.textfield.TextInputEditText

class HomeFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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
        setupViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        binding.tvTitleBuyGold.setCustomTypefaceSpanString(
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
        binding.btnRequestDelivery.setOnClickListener { findNavController().navigate(HomeFragmentDirections.actionRequestDelivery()) }
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