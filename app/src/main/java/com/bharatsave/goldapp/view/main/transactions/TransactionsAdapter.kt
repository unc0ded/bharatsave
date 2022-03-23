package com.bharatsave.goldapp.view.main.transactions

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.ItemSectionHeaderBinding
import com.bharatsave.goldapp.databinding.ItemTransactionBinding
import com.bharatsave.goldapp.model.Transaction
import com.bharatsave.goldapp.model.TransactionType
import java.lang.ClassCastException
import java.time.format.DateTimeFormatter
import java.util.*

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class TransactionsAdapter(private val transactionList: List<Transaction>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (transactionList[position]) {
            is Transaction.TransactionDetail -> ITEM_VIEW_TYPE_ITEM
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
            ITEM_VIEW_TYPE_ITEM -> (holder as TransactionsViewHolder).bind(transactionList[position] as Transaction.TransactionDetail)
        }
    }

    override fun getItemCount(): Int = transactionList.size

    inner class TransactionsViewHolder(val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal fun bind(transactionDetail: Transaction.TransactionDetail) {
            binding.run {
                tvTime.text =
                    transactionDetail.dateTime.format(DateTimeFormatter.ofPattern("h:mm a", Locale.US))
                tvGoldWeight.text = "${transactionDetail.goldWeight} gm"
                when (transactionDetail.type) {
                    TransactionType.BUY -> {
                        tvTxnAmount.text = "₹${transactionDetail.amount}"
                        tvTxnDescription.text = "Direct Gold Purchase"
                        tvTxnType.text = "BUY"
                        tvTxnType.backgroundTintList =
                            ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.light_green))
                        tvTxnType.setTextColor(ContextCompat.getColor(binding.root.context, R.color.focus_green))
                    }
                    TransactionType.SELL -> {
                        tvTxnAmount.text = "₹${transactionDetail.amount}"
                        tvTxnDescription.text = "Withdraw Money"
                        tvTxnType.text = "SELL"
                        tvTxnType.backgroundTintList =
                            ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.light_red))
                        tvTxnType.setTextColor(ContextCompat.getColor(binding.root.context, R.color.focus_red))
                    }
                    TransactionType.REDEEM -> {
                        tvTxnAmount.isVisible = false
                        tvTxnDescription.text = transactionDetail.description
                        tvTxnType.text = "ORDER"
                        tvTxnType.backgroundTintList =
                            ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, R.color.light_blue))
                        tvTxnType.setTextColor(ContextCompat.getColor(binding.root.context, R.color.focus_blue))
                    }
                }
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