package com.bharatsave.goldapp.data.repository

import com.bharatsave.goldapp.data.db.MainDao
import com.bharatsave.goldapp.data.db.UserDao
import com.bharatsave.goldapp.data.service.AugmontService
import com.bharatsave.goldapp.data.service.PaytmService
import com.bharatsave.goldapp.data.service.UserService
import com.bharatsave.goldapp.model.BalanceDetail
import com.bharatsave.goldapp.model.PaytmTransaction
import com.bharatsave.goldapp.model.PlanDetail
import com.bharatsave.goldapp.model.augmont.GoldRate
import com.bharatsave.goldapp.model.User
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val userService: UserService,
    private val augmontService: AugmontService,
    private val paytmService: PaytmService,
    private val userDao: UserDao,
    private val mainDao: MainDao
) {
    suspend fun fetchGoldRates() = augmontService.goldRate()

    suspend fun getLastGoldRate() = mainDao.getLastGoldRate()

    suspend fun saveGoldRate(goldRate: GoldRate) {
        mainDao.insertGoldRate(goldRate)
    }

    fun getStoredGoldRates() = mainDao.getGoldRates()

    suspend fun fetchBalanceData() = userService.balanceDetails()

    suspend fun updateUserBalance(balanceDetail: BalanceDetail, phoneNumber: String) {
        userDao.updateBalanceDetails(
            balanceDetail.amountInvested,
            balanceDetail.goldBalance,
            phoneNumber
        )
    }

    fun getStoredBalanceDetails() = userDao.getBalanceDetails()

    suspend fun startPlan(bodyMap: Map<String, String>) = paytmService.createSubscription(bodyMap)

    suspend fun fetchSubscriptionStatus(subscriptionId: String) =
        paytmService.subscriptionStatus(subscriptionId)

    suspend fun updatePlan(planDetail: PlanDetail) {
        userDao.saveUserPlan(planDetail)
    }

    suspend fun initiateManualCollect(bodyMap: Map<String, String>) =
        paytmService.manualCollect(bodyMap)

    suspend fun initiateTransaction(bodyMap: Map<String, String>) =
        paytmService.initiateTransaction(bodyMap)

    suspend fun fetchTransactionStatus(orderId: String) = paytmService.transactionStatus(orderId)

    suspend fun saveUserTransaction(transaction: PaytmTransaction) {
        userDao.saveTransaction(transaction)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun startGoldPurchase(bodyMap: Map<String, String>) =
        augmontService.purchaseGold(bodyMap)
}