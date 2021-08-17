package com.bharatsave.goldapp.view.main.home

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.FragmentWithdrawBinding
import com.bharatsave.goldapp.util.increaseHitArea
import com.bharatsave.goldapp.util.setCustomTypefaceSpanString
import com.google.android.material.textfield.TextInputEditText

class WithdrawFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentWithdrawBinding? = null
    private val binding get() = _binding!!

    private var sellAmount: Float = 0f
    private var sellQuantity: Float = 0f

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

        setupViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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
                    binding.toggleSellCurrency.text = resources.getString(R.string.sell_currency_text)
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
                    binding.toggleSellQuantity.text = resources.getString(R.string.sell_quantity_text)
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

        binding.etSellAmount.editText?.doOnTextChanged { text, _, _, _ ->
            when (binding.toggleGroupSellOption.checkedRadioButtonId) {
                R.id.toggle_sell_currency -> sellAmount =
                    if (text.toString().isNotBlank()) text.toString().toFloat() else 0f
                R.id.toggle_sell_quantity -> sellQuantity =
                    if (text.toString().isNotBlank()) text.toString().toFloat() else 0f
            }
        }

        binding.chipAdd500.setOnClickListener(this)
        binding.chipAdd1000.setOnClickListener(this)
        binding.chipAdd5000.setOnClickListener(this)
        binding.chipAdd10000.setOnClickListener(this)
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