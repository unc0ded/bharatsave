package com.bharatsave.goldapp.view.main.transactions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.FragmentTransactionsBinding
import com.bharatsave.goldapp.model.SavePlan
import com.bharatsave.goldapp.model.Transaction
import java.time.LocalDateTime

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.chipGroupFilters.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.filter_week -> {
                }
                R.id.filter_month -> {
                }
                R.id.filter_year -> {
                }
            }
        }

        // For testing
        val list = mutableListOf(
            Transaction.TransactionItem(
                LocalDateTime.now(),
                250.24f,
                2.26f,
                SavePlan.DAILY_SAVINGS
            ), Transaction.TransactionItem(
                LocalDateTime.now().minusDays(1), 5.89f, 0.76f, SavePlan.ROUND_UP
            )
        )
        val finalList = sortAndGroup(list)

        binding.rvTxns.adapter = TransactionsAdapter(finalList)
    }

    private fun sortAndGroup(list: MutableList<Transaction.TransactionItem>): List<Transaction> {
        list.sortByDescending { it.dateTime }
        val sortedList = mutableListOf<Transaction>()
        for ((index, transaction) in list.withIndex()) {
            if (index == 0) {
                sortedList.add(Transaction.TransactionHeader(transaction.dateTime.toLocalDate()))
                sortedList.add(transaction)
                continue
            }
            if (!transaction.dateTime.toLocalDate().equals(list[index - 1].dateTime.toLocalDate())) {
                sortedList.add(Transaction.TransactionHeader(transaction.dateTime.toLocalDate()))
            }
            sortedList.add(transaction)
        }
        return sortedList
    }
}