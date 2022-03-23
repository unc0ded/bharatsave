package com.bharatsave.goldapp.view.main.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bharatsave.goldapp.data.repository.MainRepository
import com.bharatsave.goldapp.model.BuyTransactionListItem
import com.bharatsave.goldapp.model.SellTransactionListItem
import com.bharatsave.goldapp.model.TransactionItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

    private val _transactionList = MutableLiveData<List<TransactionItem>>()
    val transactionList: LiveData<List<TransactionItem>>
        get() = _transactionList

    init {
        viewModelScope.launch {
            _transactionList.value = mainRepository.getStoredTransactions()
        }
    }
}