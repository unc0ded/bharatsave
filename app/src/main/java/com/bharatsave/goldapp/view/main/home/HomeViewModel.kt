package com.bharatsave.goldapp.view.main.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bharatsave.goldapp.data.repository.MainRepository
import com.bharatsave.goldapp.data.repository.PreferenceRepository
import com.bharatsave.goldapp.model.*
import com.bharatsave.goldapp.model.paytm.PaytmSubscriptionStatus
import com.bharatsave.goldapp.model.paytm.PaytmSubscriptionToken
import com.bharatsave.goldapp.model.paytm.PaytmTransactionStatus
import com.bharatsave.goldapp.model.paytm.PaytmTransactionToken
import com.bharatsave.goldapp.view.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.collections.set

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val mainRepository: MainRepository
) : ViewModel() {

    private val TAG = "HomeViewModel"

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
        viewModelScope.launchIO(
            action = {
                val tokenData = mainRepository.startPlan(bodyMap)
                _subscriptionToken.postValue(tokenData)
            },
            onError = {
                Log.e(TAG, "#createPlan: ${it.message}")
            }
        )
    }

    fun getSubscriptionStatus(subscriptionId: String) {
        viewModelScope.launchIO(
            action = {
                val status = mainRepository.fetchSubscriptionStatus(subscriptionId)
                _subscriptionStatus.postValue(status)
            },
            onError = {
                Log.e(TAG, "#getSubscriptionStatus: ${it.message}")
            }
        )
    }

    fun updateUserPlanDetails(plan: PlanDetail) {
        viewModelScope.launchIO(
            action = {
                mainRepository.updatePlan(plan)
            },
            onError = {
                Log.e(TAG, "#updateUserPlanDetails: ${it.message}")
            }
        )
    }

    fun startTransaction(bodyMap: Map<String, String>) {
        viewModelScope.launchIO(
            action = {
                val tokenData = mainRepository.initiateTransaction(bodyMap)
                _transactionToken.postValue(tokenData)
            },
            onError = {
                Log.e(TAG, "#startTransaction: ${it.message}")
            }
        )
    }

    fun getTransactionStatus(orderId: String) {
        viewModelScope.launchIO(
            action = {
                val status = mainRepository.fetchTransactionStatus(orderId)
                _transactionStatus.postValue(status)
            },
            onError = { cause ->
                when (cause) {
                    is IOException -> _transactionStatus.value = PaytmTransactionStatus(
                        status = "NETWORK_FAILURE",
                        message = cause.message!!
                    )
                    is HttpException -> {
                        val errorBody = cause.response()?.errorBody()?.string()
                        val errorMsg = errorBody?.apply {
                            val start = indexOf(':') + 2
                            val end = indexOf('"', start) - 1
                            substring(start..end)
                        }
                        _transactionStatus.value =
                            PaytmTransactionStatus(status = "TXN_FAILURE", message = errorMsg!!)
                    }
                    else -> _transactionStatus.value =
                        PaytmTransactionStatus(status = "FAILURE", message = cause.message!!)
                }
            }
        )
    }

    fun buyGold(bodyMap: Map<String, String>) {
        viewModelScope.launchIO(
            action = {
                val balanceData = mainRepository.startGoldPurchase(bodyMap)
                mainRepository.updateUserBalance(balanceData, preferenceRepository.getPhoneNumber())
            },
            onError = {
                Log.e(TAG, "#buyGold: ${it.message}")
            }
        )
    }

    fun sellGold(bodyMap: HashMap<String, String>) {
        viewModelScope.launchIO(
            action = {
                bodyMap["mobileNumber"] = preferenceRepository.getPhoneNumber()
                val balanceData = mainRepository.withdrawMoney(bodyMap)
                mainRepository.updateUserBalance(balanceData, preferenceRepository.getPhoneNumber())
                _sellGoldStatus.postValue("SUCCESS")
            },
            onError = {
                when (it) {
                    is IOException -> _sellGoldStatus.value = "FAILED: ${it.message}"
                    is HttpException -> _sellGoldStatus.value =
                        "FAILED: ${it.response()?.errorBody()?.string()}"
                    else -> _sellGoldStatus.value = it.message
                }
            }
        )
    }

    fun getUserBankList() {
        viewModelScope.launchIO(
            action = {
                val banksList = mainRepository.getStoredBanksList()
                _banksData.postValue(banksList)
            },
            onError = {
                Log.e(TAG, "#getUserBankList: ${it.message}")
            }
        )
    }

    fun createUserBank(bodyMap: Map<String, String>) {
        viewModelScope.launchIO(
            action = {
                val bankDetail = mainRepository.registerUserBank(bodyMap)
                mainRepository.saveUserBanks(listOf(bankDetail))
                _bankCreateStatus.postValue("SUCCESS")
            },
            onError = {
                when (it) {
                    is IOException -> _bankCreateStatus.value = "FAILED: ${it.message}"
                    is HttpException ->
                        _bankCreateStatus.value = "FAILED: ${it.response()?.errorBody()?.string()}"
                    else -> _bankCreateStatus.value = it.message
                }
            }
        )
    }

    fun getGoldDeliveryOptions() {
        viewModelScope.launchIO(
            action = {
                val productList = mainRepository.fetchGoldProductList()
                _productData.postValue(productList)
            },
            onError = {
                Log.e(TAG, "#getGoldDeliveryOptions: ${it.message}")
            }
        )
    }

    fun getAddressList() {
        viewModelScope.launchIO(
            action = {
                val addressList = mainRepository.getStoredAddresses()
                _addressData.postValue(addressList)
            },
            onError = {
                Log.e(TAG, "#getAddressList ${it.message}")
            }
        )
    }

    fun createUserAddress(bodyMap: Map<String, String>) {
        viewModelScope.launchIO(
            action = {
                val addressDetail = mainRepository.registerUserAddress(bodyMap)
                mainRepository.saveAddresses(listOf(addressDetail))
                _addressCreateStatus.postValue("SUCCESS")
            },
            onError = {
                when (it) {
                    is IOException -> _addressCreateStatus.value = "FAILED: ${it.message}"
                    is HttpException ->
                        _addressCreateStatus.value =
                            "FAILED: ${it.response()?.errorBody()?.string()}"
                    else -> _addressCreateStatus.value = it.message
                }
            }
        )
    }

    fun placeProductOrder(bodyMap: Map<String, String>) {
        viewModelScope.launchIO(
            action = {
                val orderResponse = mainRepository.placeOrder(bodyMap)
                _orderData.postValue(orderResponse.orderDetails)
                _orderStatus.postValue(orderResponse.message)
                mainRepository.updateGoldBalance(
                    orderResponse.goldBalance,
                    orderResponse.orderDetails.id
                )
            },
            onError = {
                when (it) {
                    is IOException -> _orderStatus.value = "FAILED: ${it.message}"
                    is HttpException ->
                        _orderStatus.value = "FAILED: ${it.response()?.errorBody()?.string()}"
                    else -> _orderStatus.value = it.message
                }
            }
        )
    }
}