package com.bharatsave.goldapp.view.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bharatsave.goldapp.databinding.PagerBankItemBinding
import com.bharatsave.goldapp.model.BankDetail

class BankListAdapter(private val bankList: List<BankDetail>) :
    RecyclerView.Adapter<BankListAdapter.BankListViewHolder>() {

    fun getItem(position: Int) = bankList[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankListViewHolder =
        BankListViewHolder(
            PagerBankItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: BankListViewHolder, position: Int) {
        holder.bind(bankList[position])
    }

    override fun getItemCount(): Int = bankList.size

    inner class BankListViewHolder(val binding: PagerBankItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal fun bind(bank: BankDetail) {
            binding.tvAccountNumber.text = "••••${bank.accountNo.takeLast(4)}"
            binding.tvAccountName.text = bank.accountName
        }
    }
}