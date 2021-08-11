package com.dev.`in`.drogon.view.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.`in`.drogon.databinding.PagerViewHomeBinding
import com.dev.`in`.drogon.model.GoalDetails

class GoalsAdapter(private val goalsList: List<GoalDetails>) :
    RecyclerView.Adapter<GoalsAdapter.GoalsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalsViewHolder =
        GoalsViewHolder(
            PagerViewHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: GoalsViewHolder, position: Int) {
        holder.bind(goalsList[position])
    }

    override fun getItemCount(): Int = goalsList.size

    class GoalsViewHolder(val binding: PagerViewHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal fun bind(goal: GoalDetails) {
            binding.tvCurrentValue.text = "₹ " + goal.currentValue
            binding.tvGoldWeight.text = goal.goldWeight.toString() + " gm"
        }
    }
}