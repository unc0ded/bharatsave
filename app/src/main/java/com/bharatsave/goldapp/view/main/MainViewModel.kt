package com.bharatsave.goldapp.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bharatsave.goldapp.data.repository.MainRepository
import com.bharatsave.goldapp.data.repository.PreferenceRepository
import com.bharatsave.goldapp.model.augmont.GoldRate
import com.bharatsave.goldapp.view.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val mainRepository: MainRepository
) : ViewModel() {

    private val TAG = "MainViewModel"

    private val _goldRateData =
        mainRepository.getStoredGoldRates()
            .map {
                if (it.isNotEmpty()) {
                    if (it.size == 2) {
                        val percentChange =
                            ((it[0].buyPrice.toFloat() - it[1].buyPrice.toFloat())
                                    / it[1].buyPrice.toFloat()) * 100
                        Pair(it[0], percentChange)
                    } else Pair(it[0], 0f)
                } else null
            }
            .asLiveData(Dispatchers.Default + viewModelScope.coroutineContext)
    val goldRateData: LiveData<Pair<GoldRate, Float>?>
        get() = _goldRateData

    private val _balanceData =
        mainRepository.getStoredBalanceDetails().asLiveData(viewModelScope.coroutineContext)
    val balanceData: LiveData<String>
        get() = _balanceData

    init {
        viewModelScope.launchIO(
            action = {
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
            },
            onError = {
                Log.e(TAG, "#init.rateData: ${it.message}")
            }
        )

        viewModelScope.launchIO(
            action = {
                val balance = mainRepository.fetchBalanceData()
                val phoneNumber = preferenceRepository.getPhoneNumber()
                mainRepository.updateUserBalance(balance, phoneNumber)
                Log.d(TAG, "#init.balanceData ${balance.goldBalance} $phoneNumber")
            },
            onError = {
                Log.e(TAG, "#init.balanceData ${it.message}")
            }
        )

        viewModelScope.launchIO(
            action = {
                val banksList = mainRepository.fetchUserBanksList()
                mainRepository.saveUserBanks(banksList)
            },
            onError = {
                Log.e(TAG, "#init.userBankList ${it.message}")
            }
        )

        viewModelScope.launchIO(
            action = {
                val addressList = mainRepository.fetchUserAddresses()
                mainRepository.saveAddresses(addressList)
            },
            onError = {
                Log.e(TAG, "#init.userAddress ${it.message}")
            }
        )

        viewModelScope.launchIO(
            action = {
                val transactionList = mainRepository.fetchTransactionsList()
                mainRepository.saveUserTransactions(transactionList)
            },
            onError = {
                Log.e(TAG, "#init.transactionList ${it.message}")
            }
        )
    }

    fun getGoldRates() {
        viewModelScope.launchIO(
            action = {
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
            },
            onError = {
                Log.e(TAG, "#getGoldRates: ${it.message}")
            }
        )
    }

    fun getBalanceData() {
        viewModelScope.launchIO(
            action = {
                val balance = mainRepository.fetchBalanceData()
                mainRepository.updateUserBalance(balance, preferenceRepository.getPhoneNumber())
            },
            onError = {
                Log.e(TAG, "#getBalanceData: ${it.message}")
            }
        )
    }
}