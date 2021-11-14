package com.bharatsave.goldapp.view.main.transactions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.FragmentTransactionsBinding
import com.bharatsave.goldapp.model.SavePlan
import com.bharatsave.goldapp.model.Transaction
import com.bharatsave.goldapp.view.main.MainViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TransactionsFragment : Fragment() {
    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val list : MutableList<Transaction.TransactionItem> = mutableListOf()
        binding.chipGroupFilters.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.filter_week -> {
                    val finalList = sortFilterAndGroup(list,7)
                    binding.rvTxns.adapter = TransactionsAdapter(finalList)
                }
                R.id.filter_month -> {
                    val finalList = sortFilterAndGroup(list,31)
                    binding.rvTxns.adapter = TransactionsAdapter(finalList)
                }
                R.id.filter_year -> {
                    val finalList = sortFilterAndGroup(list,365)
                    binding.rvTxns.adapter = TransactionsAdapter(finalList)
                }
            }
        }
        mainViewModel.buyList.observe(viewLifecycleOwner){
            for(transaction in it) {
                var timedatestring = transaction.createdAt
                timedatestring = timedatestring.replace("T", " ")
                timedatestring = timedatestring.removeRange(16 until timedatestring.length)
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                val dt = LocalDateTime.parse(timedatestring, formatter)
                list.add(
                    Transaction.TransactionItem(
                        dt,
                        transaction.inclTaxRate,
                        transaction.qty,
                        SavePlan.DAILY_SAVINGS
                    )
                )
            }
            val finalList = sortFilterAndGroup(list,7)
            binding.rvTxns.adapter = TransactionsAdapter(finalList)
        }
        mainViewModel.sellList.observe(viewLifecycleOwner){
            for(transaction in it) {
                var timedatestring = transaction.createdAt
                timedatestring = timedatestring.replace("T", " ")
                timedatestring = timedatestring.removeRange(16 until timedatestring.length)
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                val dt = LocalDateTime.parse(timedatestring, formatter)
                list.add(
                    Transaction.TransactionItem(
                        dt,
                        -1*transaction.rate,
                        transaction.qty,
                        SavePlan.DAILY_SAVINGS
                    )
                )
            }
            val finalList = sortFilterAndGroup(list,7)
            binding.rvTxns.adapter = TransactionsAdapter(finalList)
        }
        mainViewModel.moneyInvested.observe(viewLifecycleOwner) {
            binding.tvInvestment.text = it.toString()
        }
        mainViewModel.goldHeld.observe(viewLifecycleOwner) {
            binding.tvGoldWorth.text = it.toString()
        }
        mainViewModel.portfolioValue.observe(viewLifecycleOwner) {
            binding.tvPortfolioValue.text = it.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sortFilterAndGroup(list: MutableList<Transaction.TransactionItem>, filterType: Long): List<Transaction> {
        list.sortByDescending { it.dateTime }
        val sortedList = mutableListOf<Transaction>()
        val currentDate : LocalDateTime? = getCurrentDate()
        for ((index, transaction) in list.withIndex()) {
            if(currentDate?.minusDays(filterType)!!.isAfter(transaction.dateTime)) {
                break
            }
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

    private fun getCurrentDate(): LocalDateTime? {
        var current = LocalDateTime.now().toString()
        current = current.replace("T", " ")
        current = current.removeRange(16 until current.length)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return LocalDateTime.parse(current, formatter)
    }
}