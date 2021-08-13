package com.bharatsave.goldapp.view.register

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.PagerViewOnboardingBinding
import com.bharatsave.goldapp.model.OnBoardingItem
import com.bharatsave.goldapp.util.getThemeColorFromAttr
import com.bharatsave.goldapp.util.setMultipleColorSpanString

class OnBoardingAdapter(private val itemList: List<OnBoardingItem>, private val context: Context) :
    RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder =
        OnBoardingViewHolder(
            PagerViewOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        holder.bind(itemList[position], position, context)
    }

    override fun getItemCount(): Int = itemList.size

    class OnBoardingViewHolder(val binding: PagerViewOnboardingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal fun bind(item: OnBoardingItem, position: Int, context: Context) {
            binding.tvSubtitle.text = item.subText
            binding.tvFocusedTitle.setMultipleColorSpanString(
                item.primaryText, context.getThemeColorFromAttr(
                    R.attr.colorPrimary
                ), item.highlightedText, context.getThemeColorFromAttr(R.attr.colorSecondary)
            )
            binding.ivBrandImage.setImageResource(R.drawable.ic_brand_logo)
            if (position == 2) {
                binding.ivBrandImage.rotationY = 180f
            }
            binding.ivMascot.setImageResource(item.imgRes)
        }
    }
}