package com.dev.`in`.drogon.custom_widget

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
import com.dev.`in`.drogon.R
import com.dev.`in`.drogon.databinding.CheckablePlanCardBinding
import com.dev.`in`.drogon.util.getThemeColorFromAttr
import com.dev.`in`.drogon.util.toPx
import com.google.android.material.card.MaterialCardView

class CheckablePlanCard(context: Context) : MaterialCardView(context) {

    private var _binding: CheckablePlanCardBinding? = null
    private val binding: CheckablePlanCardBinding
        get() = _binding!!

    private var params: ViewGroup.LayoutParams? = null

    init {
        _binding = CheckablePlanCardBinding.inflate(LayoutInflater.from(context), this, true)
        apply {
            isCheckable = true
            setCardForegroundColor(
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,
                        android.R.color.transparent
                    )
                )
            )
            checkedIconTint =
                ColorStateList.valueOf(context.getThemeColorFromAttr(R.attr.colorSurface))
            backgroundTintList =
                ContextCompat.getColorStateList(context, R.color.card_background_tint_list)
            rippleColor = ContextCompat.getColorStateList(context, R.color.card_ripple)
            setOnClickListener {
                this.isChecked = !this.isChecked

                // Button visibility toggle
                (parent.parent as ConstraintLayout).getViewById(R.id.btn_continue).visibility =
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
                    context.getThemeColorFromAttr(if (isChecked) R.attr.colorSurface else R.attr.colorPrimary)
                )
                binding.tvPlanDescription.setTextColor(
                    context.getThemeColorFromAttr(if (isChecked) R.attr.colorSurface else R.attr.colorPrimary)
                )
                binding.ivPlanImage.backgroundTintList =
                    ColorStateList.valueOf(context.getThemeColorFromAttr(if (isChecked) R.attr.colorSurface else R.attr.colorPrimary))
            }
        }
    }

    fun setPlanName(name: String): CheckablePlanCard {
        binding.tvPlanName.text = name
        return this
    }

    fun setPlanDescription(description: String): CheckablePlanCard {
        binding.tvPlanDescription.text = description
        return this
    }

    fun useLinearLayout(): CheckablePlanCard {
        params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            .apply { this@CheckablePlanCard.layoutParams = this }
        return this
    }

    fun useGridLayout(): CheckablePlanCard {
        params = GridLayout.LayoutParams(
            GridLayout.spec(GridLayout.UNDEFINED, 1f),
            GridLayout.spec(GridLayout.UNDEFINED, 1f)
        ).apply {
            this@CheckablePlanCard.layoutParams = this
        }
        return this
    }

    fun setMarginsDp(
        start: Int = 0,
        end: Int = 0,
        top: Int = 0,
        bottom: Int = 0
    ): CheckablePlanCard {
        when (params) {
            is LinearLayout.LayoutParams -> {
                (params as LinearLayout.LayoutParams).apply {
                    marginStart = start.toPx.toInt()
                    marginEnd = end.toPx.toInt()
                    topMargin = top.toPx.toInt()
                    bottomMargin = bottom.toPx.toInt()
                    layoutParams = this
                    return this@CheckablePlanCard
                }
            }
            is GridLayout.LayoutParams -> {
                (params as GridLayout.LayoutParams).apply {
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    width = 0
                    marginStart = start.toPx.toInt()
                    marginEnd = end.toPx.toInt()
                    topMargin = top.toPx.toInt()
                    bottomMargin = bottom.toPx.toInt()
                    layoutParams = this
                    return this@CheckablePlanCard
                }
            }
            else -> {
                Log.e(this.javaClass.simpleName, "Unknown params type")
                return this
            }
        }
    }
}