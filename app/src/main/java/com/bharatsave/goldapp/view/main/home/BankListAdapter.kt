package com.bharatsave.goldapp.view.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bharatsave.goldapp.databinding.PagerBankItemBinding
import com.bharatsave.goldapp.model.BankDetail
import com.bharatsave.goldapp.util.UiUtils

class BankListAdapter(
    private val bankList: List<BankDetail>,
    private val purpose: BottomSheetPurpose,
    private val viewLifecycleOwner: LifecycleOwner,
    private val deleteBankAccountListener: BankSelectionBottomSheetFragment.DeleteBankAccountListener?
) : RecyclerView.Adapter<BankListAdapter.BankListViewHolder>() {

    private val isDeleteWrapperExpanded = MutableLiveData(false)

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

            if (purpose == BottomSheetPurpose.VIEW_ACCOUNTS) {
                observeData()
                binding.root.setOnClickListener {
                    if (isDeleteWrapperExpanded.value == false) {
                        isDeleteWrapperExpanded.value = true
                    }
                }
                binding.btnCancel.setOnClickListener {
                    if (isDeleteWrapperExpanded.value == true) {
                        isDeleteWrapperExpanded.value = false
                    }
                }
                binding.btnDelete.setOnClickListener {
                    deleteBankAccountListener?.onDeleteBankAccount(bank.userId, bankList.size)
                }
            }
        }

        private fun observeData() {
            isDeleteWrapperExpanded.observe(viewLifecycleOwner) { expand ->
                if (expand) {
                    UiUtils.expandVertical(binding.deleteWrapper, true, 750)
                } else {
                    UiUtils.collapseVertical(binding.deleteWrapper, true, 750)
                }
            }
        }
    }
}