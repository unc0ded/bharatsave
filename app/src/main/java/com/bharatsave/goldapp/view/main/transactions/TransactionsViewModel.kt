package com.bharatsave.goldapp.view.main.transactions

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bharatsave.goldapp.data.repository.MainRepository
import com.bharatsave.goldapp.model.TransactionItem
import com.bharatsave.goldapp.view.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

    private val TAG = "TransactionsViewModel"

    private val _transactionList = MutableLiveData<List<TransactionItem>>()
    val transactionList: LiveData<List<TransactionItem>>
        get() = _transactionList

    init {
        viewModelScope.launchIO(
            action = {
                _transactionList.postValue(mainRepository.getStoredTransactions())
            },
            onError = {
                Log.e(TAG, "#init ${it.message}")
            }
        )
    }
}