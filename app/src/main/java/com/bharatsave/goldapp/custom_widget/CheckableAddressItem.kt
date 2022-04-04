package com.bharatsave.goldapp.custom_widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Checkable
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.AddressListItemBinding

class CheckableAddressItem @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs), Checkable {

    private var _binding: AddressListItemBinding? = null
    private val binding: AddressListItemBinding
        get() = _binding!!

    private var checked: Boolean = false

    private val CheckedStateSet = intArrayOf(android.R.attr.state_checked)

    init {
        _binding = AddressListItemBinding.inflate(LayoutInflater.from(context), this, true)
        background =
            AppCompatResources.getDrawable(context, R.drawable.rounded_outline_padded_background)
    }

    fun setAddress(name: String, addressText: String, label: String, pinCode: String) {
        binding.tvAddressName.text = name
        binding.tvFullAddress.text = addressText
        binding.tvAddressLabel.text = label
        binding.tvPinCode.text = pinCode
    }

    override fun setChecked(check: Boolean) {
        if (checked != check) {
            toggle()
        }
    }

    override fun isChecked(): Boolean = checked

    override fun toggle() {
        if (isEnabled) {
            checked = !checked
            refreshDrawableState()
        }
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            mergeDrawableStates(drawableState, CheckedStateSet)
        }

        return drawableState
    }
}