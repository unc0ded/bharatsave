package com.bharatsave.goldapp.view.main.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bharatsave.goldapp.data.repository.MainRepository
import com.bharatsave.goldapp.model.BuyTransactionListItem
import com.bharatsave.goldapp.model.SellTransactionListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

    private val _buyList = MutableLiveData<List<BuyTransactionListItem>>()
    val buyList: LiveData<List<BuyTransactionListItem>>
        get() = _buyList

    private val _sellList = MutableLiveData<List<SellTransactionListItem>>()
    val sellList: LiveData<List<SellTransactionListItem>>
        get() = _sellList

    init {
        viewModelScope.launch {
            //make local database
            _buyList.value = mainRepository.getBuyList()
        }
        viewModelScope.launch {
            //make local database
            _sellList.value = mainRepository.getSellList()
        }
    }

    fun getBuyList() {
        viewModelScope.launch {
            val buyData = mainRepository.getBuyList()
            _buyList.value = buyData
        }
    }

    fun getSellList() {
        viewModelScope.launch {
            val sellData = mainRepository.getSellList()
            _sellList.value = sellData
        }
    }
}