package com.bharatsave.goldapp.view.main.home

import androidx.lifecycle.*
import com.bharatsave.goldapp.data.repository.MainRepository
import com.bharatsave.goldapp.data.repository.PreferenceRepository
import com.bharatsave.goldapp.model.BalanceDetail
import com.bharatsave.goldapp.model.PaytmTransaction
import com.bharatsave.goldapp.model.PlanDetail
import com.bharatsave.goldapp.model.augmont.GoldRate
import com.bharatsave.goldapp.model.paytm.PaytmSubscriptionStatus
import com.bharatsave.goldapp.model.paytm.PaytmSubscriptionToken
import com.bharatsave.goldapp.model.paytm.PaytmTransactionStatus
import com.bharatsave.goldapp.model.paytm.PaytmTransactionToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
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

    private val _subscriptionToken = MutableLiveData<PaytmSubscriptionToken>()
    val subscriptionToken: LiveData<PaytmSubscriptionToken>
        get() = _subscriptionToken

    private val _subscriptionStatus = MutableLiveData<PaytmSubscriptionStatus>()
    val subscriptionStatus: LiveData<PaytmSubscriptionStatus>
        get() = _subscriptionStatus

    private val _transactionToken = MutableLiveData<PaytmTransactionToken>()
    val transactionToken: LiveData<PaytmTransactionToken>
        get() = _transactionToken

    private val _transactionStatus = MutableLiveData<PaytmTransactionStatus>()
    val transactionStatus: LiveData<PaytmTransactionStatus>
        get() = _transactionStatus

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
        }
        viewModelScope.launch {
            val balance = mainRepository.fetchBalanceData()
            mainRepository.updateUserBalance(balance, preferenceRepository.getPhoneNumber())
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

    fun createPlan(bodyMap: Map<String, String>) {
        viewModelScope.launch {
            val tokenData = mainRepository.startPlan(bodyMap)
            _subscriptionToken.value = tokenData
        }
    }

    fun getSubscriptionStatus(subscriptionId: String) {
        viewModelScope.launch {
            val status = mainRepository.fetchSubscriptionStatus(subscriptionId)
            _subscriptionStatus.value = status
        }
    }

    fun updateUserPlanDetails(plan: PlanDetail) {
        viewModelScope.launch {
            mainRepository.updatePlan(plan)
        }
    }

    fun startTransaction(bodyMap: Map<String, String>) {
        viewModelScope.launch {
            val tokenData = mainRepository.initiateTransaction(bodyMap)
            _transactionToken.value = tokenData
        }
    }

    fun getTransactionStatus(orderId: String) {
        viewModelScope.launch {
            val status = mainRepository.fetchTransactionStatus(orderId)
            _transactionStatus.value = status
        }
    }

    fun saveTransaction(transaction: PaytmTransaction) {
        viewModelScope.launch {
            mainRepository.saveUserTransaction(transaction)
        }
    }

    fun buyGold(bodyMap: Map<String, String>) {
        viewModelScope.launch {
            val balanceData = mainRepository.startGoldPurchase(bodyMap)
            mainRepository.updateUserBalance(balanceData, preferenceRepository.getPhoneNumber())
        }
    }
}