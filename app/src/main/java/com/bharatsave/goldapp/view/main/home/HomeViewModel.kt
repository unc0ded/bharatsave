package com.bharatsave.goldapp.view.main.home

import androidx.lifecycle.*
import com.bharatsave.goldapp.data.repository.MainRepository
import com.bharatsave.goldapp.data.repository.PreferenceRepository
import com.bharatsave.goldapp.model.PaytmTransaction
import com.bharatsave.goldapp.model.PlanDetail
import com.bharatsave.goldapp.model.paytm.PaytmSubscriptionStatus
import com.bharatsave.goldapp.model.paytm.PaytmSubscriptionToken
import com.bharatsave.goldapp.model.paytm.PaytmTransactionStatus
import com.bharatsave.goldapp.model.paytm.PaytmTransactionToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val mainRepository: MainRepository
) : ViewModel() {

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

    private val _bankCreateStatus = MutableLiveData<String>()
    val bankCreateStatus: LiveData<String>
        get() = _bankCreateStatus

    private val _sellGoldStatus = MutableLiveData<String>()
    val sellGoldStatus: LiveData<String>
        get() = _sellGoldStatus

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

    fun sellGold(bodyMap: HashMap<String, String>) {
        viewModelScope.launch {
            try {
                bodyMap["mobileNumber"] = preferenceRepository.getPhoneNumber()
                val balanceData = mainRepository.withdrawMoney(bodyMap)
                mainRepository.updateUserBalance(balanceData, preferenceRepository.getPhoneNumber())
                _sellGoldStatus.value = "SUCCESS"
            } catch (cause: Throwable) {
                when (cause) {
                    is IOException -> _sellGoldStatus.value = "FAILED: ${cause.message}"
                    is HttpException -> withContext(Dispatchers.IO) {
                        _sellGoldStatus.postValue(
                            "FAILED: ${
                                cause.response()?.errorBody()?.string()
                            }"
                        )
                    }
                    else -> _sellGoldStatus.value = cause.message
                }
            }
        }
    }

    fun createUserBank(bodyMap: Map<String, String>) {
        viewModelScope.launch {
            try {
                val bankDetail = mainRepository.registerUserBank(bodyMap)
                mainRepository.addUserBank(bankDetail)
                _bankCreateStatus.value = "SUCCESS"
            } catch (cause: Throwable) {
                when (cause) {
                    is IOException -> _bankCreateStatus.value = "FAILED: ${cause.message}"
                    is HttpException -> withContext(Dispatchers.IO) {
                        _bankCreateStatus.postValue(
                            "FAILED: ${
                                cause.response()?.errorBody()?.string()
                            }"
                        )
                    }
                    else -> _bankCreateStatus.value = cause.message
                }
            }
        }
    }
}