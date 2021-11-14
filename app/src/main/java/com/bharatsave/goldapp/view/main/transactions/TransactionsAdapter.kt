package com.bharatsave.goldapp.view.main.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bharatsave.goldapp.databinding.ItemSectionHeaderBinding
import com.bharatsave.goldapp.databinding.ItemTransactionBinding
import com.bharatsave.goldapp.model.Transaction
import java.lang.ClassCastException
import java.time.format.DateTimeFormatter
import java.util.*

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class TransactionsAdapter(private val transactionList: List<Transaction>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (transactionList[position]) {
            is Transaction.TransactionItem -> ITEM_VIEW_TYPE_ITEM
            is Transaction.TransactionHeader -> ITEM_VIEW_TYPE_HEADER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> SectionViewHolder(
                ItemSectionHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            ITEM_VIEW_TYPE_ITEM -> TransactionsViewHolder(
                ItemTransactionBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw ClassCastException("Unknown viewType $viewType")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ITEM_VIEW_TYPE_HEADER -> (holder as SectionViewHolder).bind(transactionList[position] as Transaction.TransactionHeader)
            ITEM_VIEW_TYPE_ITEM -> (holder as TransactionsViewHolder).bind(transactionList[position] as Transaction.TransactionItem)
        }
    }

    override fun getItemCount(): Int = transactionList.size

    inner class TransactionsViewHolder(val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal fun bind(transactionItem: Transaction.TransactionItem) {
            binding.run {
                tvPlanName.text = transactionItem.plan.toString()
                tvTime.text =
                    transactionItem.dateTime.format(DateTimeFormatter.ofPattern("h:mm a", Locale.US))
                tvGoldWorth.text = transactionItem.goldWorth.toString() + " gm"
                tvTxnAmount.text =
                    (if (transactionItem.amount > 0) "+ ₹" + transactionItem.amount else "- ₹" + transactionItem.amount*-1)
            }
        }
    }

    inner class SectionViewHolder(val binding: ItemSectionHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal fun bind(dateItem: Transaction.TransactionHeader) {
            binding.run {
                tvDate.text = dateItem.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            }
        }
    }
}