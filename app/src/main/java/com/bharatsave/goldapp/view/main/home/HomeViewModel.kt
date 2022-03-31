package com.bharatsave.goldapp.view.main.home

import androidx.lifecycle.*
import com.bharatsave.goldapp.data.repository.MainRepository
import com.bharatsave.goldapp.data.repository.PreferenceRepository
import com.bharatsave.goldapp.model.*
import com.bharatsave.goldapp.model.paytm.PaytmSubscriptionStatus
import com.bharatsave.goldapp.model.paytm.PaytmSubscriptionToken
import com.bharatsave.goldapp.model.paytm.PaytmTransactionStatus
import com.bharatsave.goldapp.model.paytm.PaytmTransactionToken
import com.squareup.moshi.Moshi
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

    private val _banksData = MutableLiveData<List<BankDetail>>()
    val banksData: LiveData<List<BankDetail>>
        get() = _banksData

    private val _bankCreateStatus = MutableLiveData<String>()
    val bankCreateStatus: LiveData<String>
        get() = _bankCreateStatus

    private val _sellGoldStatus = MutableLiveData<String>()
    val sellGoldStatus: LiveData<String>
        get() = _sellGoldStatus

    private val _productData = MutableLiveData<List<GoldCoin>>()
    val productData: LiveData<List<GoldCoin>>
        get() = _productData

    private val _addressData = MutableLiveData<List<AddressDetail>>()
    val addressData: LiveData<List<AddressDetail>>
        get() = _addressData

    private val _addressCreateStatus = MutableLiveData<String>()
    val addressCreateStatus: LiveData<String>
        get() = _addressCreateStatus

    private val _orderData = MutableLiveData<OrderDetail>()
    val orderData: LiveData<OrderDetail>
        get() = _orderData

    private val _orderStatus = MutableLiveData<String>()
    val orderStatus: LiveData<String>
        get() = _orderStatus

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
            try {
                val status = mainRepository.fetchTransactionStatus(orderId)
                _transactionStatus.value = status
            } catch (cause: Throwable) {
                when (cause) {
                    is IOException -> _transactionStatus.value = PaytmTransactionStatus(
                        status = "NETWORK_FAILURE",
                        message = cause.message!!
                    )
                    is HttpException -> withContext(Dispatchers.IO) {
                        val errorBody = cause.response()?.errorBody()?.string()
                        val errorMsg = errorBody?.apply {
                            val start = indexOf(':') + 2
                            val end = indexOf('"', start) - 1
                            substring(start..end)
                        }
                        _transactionStatus.postValue(
                            PaytmTransactionStatus(
                                status = "TXN_FAILURE",
                                message = errorMsg!!
                            )
                        )
                    }
                    else -> _transactionStatus.postValue(
                        PaytmTransactionStatus(
                            status = "FAILURE",
                            message = cause.message!!
                        )
                    )
                }
            }
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

    fun getUserBankList() {
        viewModelScope.launch {
            val banksList = mainRepository.getStoredBanksList()
            _banksData.value = banksList
        }
    }

    fun createUserBank(bodyMap: Map<String, String>) {
        viewModelScope.launch {
            try {
                val bankDetail = mainRepository.registerUserBank(bodyMap)
                mainRepository.saveUserBanks(listOf(bankDetail))
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

    fun getGoldDeliveryOptions() {
        viewModelScope.launch {
            val productList = mainRepository.fetchGoldProductList()
            _productData.value = productList
        }
    }

    fun getAddressList() {
        viewModelScope.launch {
            val addressList = mainRepository.getStoredAddresses()
            _addressData.value = addressList
        }
    }

    fun createUserAddress(bodyMap: Map<String, String>) {
        viewModelScope.launch {
            try {
                val addressDetail = mainRepository.registerUserAddress(bodyMap)
                mainRepository.saveAddresses(listOf(addressDetail))
                _addressCreateStatus.value = "SUCCESS"
            } catch (cause: Throwable) {
                when (cause) {
                    is IOException -> _addressCreateStatus.value = "FAILED: ${cause.message}"
                    is HttpException -> withContext(Dispatchers.IO) {
                        _addressCreateStatus.postValue(
                            "FAILED: ${
                                cause.response()?.errorBody()?.string()
                            }"
                        )
                    }
                    else -> _addressCreateStatus.value = cause.message
                }
            }
        }
    }

    fun placeProductOrder(bodyMap: Map<String, String>) {
        viewModelScope.launch {
            try {
                val orderResponse = mainRepository.placeOrder(bodyMap)
                _orderData.value = orderResponse.orderDetails
                _orderStatus.value = orderResponse.message
                mainRepository.updateGoldBalance(
                    orderResponse.goldBalance,
                    orderResponse.orderDetails.id
                )
            } catch (cause: Throwable) {
                when (cause) {
                    is IOException -> _orderStatus.value = "FAILED: ${cause.message}"
                    is HttpException -> withContext(Dispatchers.IO) {
                        _orderStatus.postValue(
                            "FAILED: ${
                                cause.response()?.errorBody()?.string()
                            }"
                        )
                    }
                    else -> _orderStatus.value = cause.message
                }
            }
        }
    }
}