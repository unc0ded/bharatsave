package com.bharatsave.goldapp.custom_widget

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.CheckablePlanCardBinding
import com.bharatsave.goldapp.util.getThemeColorFromAttr
import com.bharatsave.goldapp.util.toPx
import com.google.android.material.card.MaterialCardView

class CheckablePlanCard(context: Context) : MaterialCardView(context) {

    private var _binding: CheckablePlanCardBinding? = null
    private val binding: CheckablePlanCardBinding
        get() = _binding!!

    private var params: ViewGroup.LayoutParams? = null

    init {
        _binding = CheckablePlanCardBinding.inflate(LayoutInflater.from(context), this, true)
        run {
            isCheckable = true
            setCardForegroundColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,
                        android.R.color.transparent
                    )
                )
            )
            setCheckedIconResource(R.drawable.ic_done_black_24dp)
            checkedIconTint =
                ColorStateList.valueOf(context.getThemeColorFromAttr(R.attr.colorPrimary))
            backgroundTintList =
                ColorStateList.valueOf(context.getThemeColorFromAttr(R.attr.colorSurface))
            setOnClickListener {
                this.isChecked = !this.isChecked

                // Button visibility toggle
                (parent.parent as ConstraintLayout).getViewById(R.id.btn_setup_autopay).visibility =
                    if (isChecked) View.VISIBLE else View.GONE

                // Scroll to button on card selection, or to top on deselection
                (parent.parent.parent as NestedScrollView).post {
                    (parent.parent.parent as NestedScrollView).fullScroll(
                        if (isChecked) View.FOCUS_DOWN else View.FOCUS_UP
                    )
                }

                // Deselecting other cards
                (parent as ViewGroup).children.filterNot { it == this }
                    .forEach { if ((it as CheckablePlanCard).isChecked) it.isChecked = false }
            }
            setOnCheckedChangeListener { _, isChecked ->
                binding.tvPlanName.setTextColor(
                    context.getThemeColorFromAttr(if (isChecked) R.attr.colorPrimary else R.attr.colorOnBackground)
                )
                binding.tvPlanDescription.setTextColor(
                    context.getThemeColorFromAttr(if (isChecked) R.attr.colorPrimary else R.attr.colorOnBackground)
                )
                binding.ivPlanImage.elevation = (if (isChecked) 0 else 4).toPx
                binding.ivPlanImage.backgroundTintList =
                    ColorStateList.valueOf(context.getThemeColorFromAttr(if (isChecked) R.attr.colorPrimary else R.attr.colorSurface))
            }
        }
    }

    fun setPlanName(name: String) {
        binding.tvPlanName.text = name
    }

    fun setPlanDescription(description: String) {
        binding.tvPlanDescription.text = description
    }

    fun useLinearLayout() {
        params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            .apply { this@CheckablePlanCard.layoutParams = this }
    }

    fun useGridLayout() {
        params = GridLayout.LayoutParams(
            GridLayout.spec(GridLayout.UNDEFINED, 1f),
            GridLayout.spec(GridLayout.UNDEFINED, 1f)
        ).apply {
            this@CheckablePlanCard.layoutParams = this
        }
    }

    fun setMarginsDp(start: Int = 0, end: Int = 0, top: Int = 0, bottom: Int = 0) {
        when (params) {
            is LinearLayout.LayoutParams -> {
                (params as LinearLayout.LayoutParams).run {
                    marginStart = start.toPx.toInt()
                    marginEnd = end.toPx.toInt()
                    topMargin = top.toPx.toInt()
                    bottomMargin = bottom.toPx.toInt()
                    layoutParams = this
                }
            }
            is GridLayout.LayoutParams -> {
                (params as GridLayout.LayoutParams).run {
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    width = 0
                    marginStart = start.toPx.toInt()
                    marginEnd = end.toPx.toInt()
                    topMargin = top.toPx.toInt()
                    bottomMargin = bottom.toPx.toInt()
                    layoutParams = this
                }
            }
            else -> {
                throw ClassCastException("Parent Layout is neither LinearLayout nor GridLayout")
            }
        }
    }
}