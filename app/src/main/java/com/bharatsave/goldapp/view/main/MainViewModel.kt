package com.bharatsave.goldapp.view.main

import android.util.Log
import androidx.lifecycle.*
import com.bharatsave.goldapp.data.repository.MainRepository
import com.bharatsave.goldapp.data.repository.PreferenceRepository
import com.bharatsave.goldapp.model.BalanceDetail
import com.bharatsave.goldapp.model.BankDetail
import com.bharatsave.goldapp.model.BuyTransactionListItem
import com.bharatsave.goldapp.model.SellTransactionListItem
import com.bharatsave.goldapp.model.augmont.GoldRate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _goldRateData =
        mainRepository.getStoredGoldRates()
            .map {
                if (it.isNotEmpty()) {
                    if (it.size == 2) {
                        val percentChange =
                            ((it[0].goldPrice.toFloat() - it[1].goldPrice.toFloat()) / it[1].goldPrice.toFloat()) * 100
                        Pair(it[0], percentChange)
                    } else Pair(it[0], 0f)
                } else null
            }
            .asLiveData(Dispatchers.Default + viewModelScope.coroutineContext)
    val goldRateData: LiveData<Pair<GoldRate, Float>?>
        get() = _goldRateData

    private val _balanceData =
        mainRepository.getStoredBalanceDetails().asLiveData(viewModelScope.coroutineContext)
    val balanceData: LiveData<BalanceDetail>
        get() = _balanceData

    private val _banksData = MutableLiveData<List<BankDetail>>()
    val banksData: LiveData<List<BankDetail>>
        get() = _banksData

    private val _moneyInvested = MutableLiveData<Float>()
    val moneyInvested : LiveData<Float>
        get() = _moneyInvested

    private val _goldHeld = MutableLiveData<Float>()
    val goldHeld : LiveData<Float>
        get() = _goldHeld

    private val _portfolioValue = MutableLiveData<Float>()
    val portfolioValue : LiveData<Float>
        get() = _portfolioValue

    private val _buyList = MutableLiveData<List<BuyTransactionListItem>>()
    val buyList: LiveData<List<BuyTransactionListItem>>
        get() = _buyList

    private val _sellList = MutableLiveData<List<SellTransactionListItem>>()
    val sellList: LiveData<List<SellTransactionListItem>>
        get() = _sellList

    init {
        viewModelScope.launch {
            val existingRateData = mainRepository.getLastGoldRate()
            if (existingRateData != null) {
                if (Instant.now() > Instant.ofEpochMilli(existingRateData.timeStamp)
                        .plus(5, ChronoUnit.MINUTES)
                ) {
                    val freshRateData = mainRepository.fetchGoldRates()
                    mainRepository.saveGoldRate(freshRateData)
                }
            } else {
                val freshRateData = mainRepository.fetchGoldRates()
                mainRepository.saveGoldRate(freshRateData)
            }
            val recordCount = mainRepository.getRecordCount()
            if (recordCount > 10) {
                mainRepository.clearExtraRecords()
            }
        }
        viewModelScope.launch {
            val balance = mainRepository.fetchBalanceData()
            mainRepository.updateUserBalance(balance, preferenceRepository.getPhoneNumber())
        }
        viewModelScope.launch {
            val banksList = mainRepository.fetchUserBanksList()
            mainRepository.saveUserBanks(banksList)
        }
        viewModelScope.launch {
            //make local database
            _buyList.value = mainRepository.getBuyList()
        }
        viewModelScope.launch {
            //make local database
            _sellList.value = mainRepository.getSellList()
        }
        viewModelScope.launch {
            var totalMoneyInvested = 0F
            var totalGoldHeld = 0F
            val buyListOfStuff = mainRepository.getBuyList()
            for( transaction in buyListOfStuff) {
                totalMoneyInvested += transaction.inclTaxRate
                totalGoldHeld += transaction.qty
            }
            val sellListOfStuff = mainRepository.getSellList()
            for( transaction in sellListOfStuff) {
                totalMoneyInvested -= transaction.rate
                totalGoldHeld -= transaction.qty
            }
            _moneyInvested.value =  totalMoneyInvested
            _goldHeld.value = totalGoldHeld
            _portfolioValue.value = totalGoldHeld * (goldRateData.value?.second ?: 0F)
        }

    }

    fun getGoldRates() {
        viewModelScope.launch {
            val existingRateData = mainRepository.getLastGoldRate()
            if (existingRateData != null) {
                if (Instant.now() > Instant.ofEpochMilli(existingRateData.timeStamp)
                        .plus(5, ChronoUnit.MINUTES)
                ) {
                    val freshRateData = mainRepository.fetchGoldRates()
                    mainRepository.saveGoldRate(freshRateData)
                }
            } else {
                val freshRateData = mainRepository.fetchGoldRates()
                mainRepository.saveGoldRate(freshRateData)
            }
        }
    }

    fun getBalanceData() {
        viewModelScope.launch {
            val balance = mainRepository.fetchBalanceData()
            mainRepository.updateUserBalance(balance, preferenceRepository.getPhoneNumber())
        }
    }

    fun getUserBankList() {
        viewModelScope.launch {
            val banksList = mainRepository.getStoredBanksList()
            _banksData.value = banksList
        }
    }


//    fun getBuyList() {
//        viewModelScope.launch {
//            val buyListTemp = mainRepository.getBuyList()
//            _buyList.value = buyListTemp
//        }
//    }
}