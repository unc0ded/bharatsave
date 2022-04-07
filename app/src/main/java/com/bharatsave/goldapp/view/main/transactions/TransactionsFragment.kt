package com.bharatsave.goldapp.view.main.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bharatsave.goldapp.R
import com.bharatsave.goldapp.databinding.FragmentTransactionsBinding
import com.bharatsave.goldapp.model.Transaction
import com.bharatsave.goldapp.model.TransactionType
import com.bharatsave.goldapp.view.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@AndroidEntryPoint
class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private val transactionViewModel by viewModels<TransactionsViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()

    private val normalDecimalFormat by lazy {
        DecimalFormat("#,##,##0.00")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // for initial loading view
        binding.rvTxns.visibility = View.GONE
        binding.tvEmptyList.visibility = View.VISIBLE
        binding.tvEmptyList.text = "Fetching your transactions..."

        val list: MutableList<Transaction.TransactionDetail> = mutableListOf()
        binding.chipGroupFilters.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.filter_week -> {
                    val finalList = filterAndGroup(list, 7)
                    if (finalList.isEmpty()) {
                        binding.rvTxns.isVisible = false
                        binding.tvEmptyList.text = "No transactions in the last 7 days."
                        binding.tvEmptyList.isVisible = true
                    } else {
                        binding.tvEmptyList.isVisible = false
                        binding.rvTxns.adapter = TransactionsAdapter(finalList)
                        binding.rvTxns.isVisible = true
                    }
                }
                R.id.filter_month -> {
                    val finalList = filterAndGroup(list, 31)
                    if (finalList.isEmpty()) {
                        binding.rvTxns.isVisible = false
                        binding.tvEmptyList.text = "No transactions in the last month."
                        binding.tvEmptyList.isVisible = true
                    } else {
                        binding.tvEmptyList.isVisible = false
                        binding.rvTxns.adapter = TransactionsAdapter(finalList)
                        binding.rvTxns.isVisible = true
                    }
                }
                R.id.filter_year -> {
                    val finalList = filterAndGroup(list, 365)
                    if (finalList.isEmpty()) {
                        binding.rvTxns.isVisible = false
                        binding.tvEmptyList.text = "No transactions in the last year."
                        binding.tvEmptyList.isVisible = true
                    } else {
                        binding.tvEmptyList.isVisible = false
                        binding.rvTxns.adapter = TransactionsAdapter(finalList)
                        binding.rvTxns.isVisible = true
                    }
                }
                else -> {
                    val finalList = filterAndGroup(list)
                    if (finalList.isEmpty()) {
                        binding.rvTxns.isVisible = false
                        binding.tvEmptyList.text = "No transactions yet."
                        binding.tvEmptyList.isVisible = true
                    } else {
                        binding.tvEmptyList.isVisible = false
                        binding.rvTxns.adapter = TransactionsAdapter(finalList)
                        binding.rvTxns.isVisible = true
                    }
                }
            }
        }

        mainViewModel.balanceData.observe(viewLifecycleOwner) {
            if (!it.isNullOrBlank()) {
                binding.tvGoldHeld.text =
                    "${normalDecimalFormat.format(it.toFloat())}gm"
                mainViewModel.goldRateData.value?.first?.run {
                    binding.tvPortfolioValue.text =
                        "₹${normalDecimalFormat.format(it.toFloat() * this.sellPrice.toFloat())}"
                }
            }
        }

        transactionViewModel.transactionList.observe(viewLifecycleOwner) {
            if (it != null && it.isNotEmpty()) {
                for (transaction in it) {
                    val dateTime =
                        Instant.parse(transaction.createdAt).atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                    when (transaction.txnType) {
                        TransactionType.BUY -> list.add(
                            Transaction.TransactionDetail(
                                dateTime,
                                transaction.totalAmount,
                                transaction.quantity,
                                type = TransactionType.BUY
                            )
                        )
                        TransactionType.SELL -> list.add(
                            Transaction.TransactionDetail(
                                dateTime,
                                transaction.totalAmount,
                                transaction.quantity,
                                type = TransactionType.SELL
                            )
                        )
                        TransactionType.REDEEM -> list.add(
                            Transaction.TransactionDetail(
                                dateTime,
                                goldWeight = transaction.quantity,
                                description = transaction.productName,
                                type = TransactionType.REDEEM
                            )
                        )
                    }
                }

                val days = when (binding.chipGroupFilters.checkedChipId) {
                    R.id.filter_week -> 7
                    R.id.filter_month -> 30
                    R.id.filter_year -> 365
                    else -> null
                }
                val finalList = filterAndGroup(list, days)
                binding.rvTxns.adapter = TransactionsAdapter(finalList)

            } else if (it != null && it.isEmpty()) {
                binding.rvTxns.visibility = View.GONE
                binding.tvEmptyList.visibility = View.VISIBLE
                binding.tvEmptyList.text = "No transactions to display."
            } else {
                binding.rvTxns.visibility = View.GONE
                binding.tvEmptyList.visibility = View.VISIBLE
                binding.tvEmptyList.text = "An error occurred while fetching!"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun filterAndGroup(
        list: MutableList<Transaction.TransactionDetail>,
        filterType: Int? = null
    ): List<Transaction> {
        val groupedList = mutableListOf<Transaction>()
        val currentDate = LocalDate.now()
        for ((index, transaction) in list.withIndex()) {
            if (filterType != null && currentDate.minusDays(filterType.toLong())
                    .isAfter(transaction.dateTime.toLocalDate())
            ) {
                break
            }
            if (index == 0) {
                groupedList.add(Transaction.TransactionHeader(transaction.dateTime.toLocalDate()))
                groupedList.add(transaction)
                continue
            }
            if (!transaction.dateTime.toLocalDate()
                    .equals(list[index - 1].dateTime.toLocalDate())
            ) {
                groupedList.add(Transaction.TransactionHeader(transaction.dateTime.toLocalDate()))
            }
            groupedList.add(transaction)
        }
        return groupedList
    }
}