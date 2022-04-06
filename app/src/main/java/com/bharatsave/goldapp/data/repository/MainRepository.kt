package com.bharatsave.goldapp.data.repository

import com.bharatsave.goldapp.data.db.MainDao
import com.bharatsave.goldapp.data.db.UserDao
import com.bharatsave.goldapp.data.service.AugmontService
import com.bharatsave.goldapp.data.service.PaytmService
import com.bharatsave.goldapp.data.service.UserService
import com.bharatsave.goldapp.model.*
import com.bharatsave.goldapp.model.augmont.GoldRate
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val userService: UserService,
    private val augmontService: AugmontService,
    private val paytmService: PaytmService,
    private val userDao: UserDao,
    private val mainDao: MainDao
) {
    suspend fun getBuyList() = augmontService.getBuyList()

    suspend fun getSellList() = augmontService.getSellList()

    suspend fun fetchGoldRates() = augmontService.goldRate()

    suspend fun getLastGoldRate() = mainDao.getLastGoldRate()

    suspend fun saveGoldRate(goldRate: GoldRate) {
        mainDao.insertGoldRate(goldRate)
    }

    fun getStoredGoldRates() = mainDao.getGoldRates()

    suspend fun getRecordCount() = mainDao.getGoldRateEntryCount()

    suspend fun clearExtraRecords() {
        mainDao.deleteOldRates()
    }

    suspend fun fetchBalanceData() = userService.balanceDetails()

    suspend fun updateUserBalance(balanceDetail: BalanceDetail, phoneNumber: String) {
        userDao.updateBalanceDetails(
            balanceDetail.goldBalance,
            phoneNumber
        )
    }

    fun getStoredBalanceDetails() = userDao.getBalanceDetails()

    suspend fun updateGoldBalance(goldBalance: String, userId: String) {
        userDao.updateGoldBalance(goldBalance, userId)
    }

    suspend fun startPlan(bodyMap: Map<String, String>) = paytmService.createSubscription(bodyMap)

    suspend fun fetchSubscriptionStatus(subscriptionId: String) =
        paytmService.subscriptionStatus(subscriptionId)

    suspend fun updatePlan(planDetail: PlanDetail) {
        userDao.saveUserPlan(planDetail)
    }

    // is this for one day thing?
    suspend fun initiateManualCollect(bodyMap: Map<String, String>) =
        paytmService.manualCollect(bodyMap)

    suspend fun initiateTransaction(bodyMap: Map<String, String>) =
        paytmService.initiateTransaction(bodyMap)

    suspend fun fetchTransactionStatus(orderId: String) = paytmService.transactionStatus(orderId)

    suspend fun updateUserDetails(user: User) = userService.updateDetails(user)

    suspend fun saveUserDetails(user: User) {
        userDao.updateUser(user.phoneNumber, user.name, user.email, user.pinCode)
    }

    suspend fun startGoldPurchase(bodyMap: Map<String, String>) =
        augmontService.purchaseGold(bodyMap)

    suspend fun withdrawMoney(bodyMap: Map<String, String>) = augmontService.sellGold(bodyMap)

    suspend fun fetchUserBanksList() = userService.userBankDetails()

    suspend fun getStoredBanksList() = userDao.getUserBanks()

    suspend fun registerUserBank(bodyMap: Map<String, String>) = augmontService.createBank(bodyMap)

    suspend fun updateUserBank(bankId: String, bodyMap: Map<String, String>) =
        augmontService.updateBank(bankId, bodyMap)

    suspend fun deleteUserBank(bankId: String) = augmontService.deleteBank(bankId)

    suspend fun saveUserBanks(bankDetails: List<BankDetail>) =
        userDao.insertBanks(*bankDetails.toTypedArray())

    suspend fun updateSavedBank(bankDetail: BankDetail) = userDao.updateBankDetail(
        bankDetail.id,
        bankDetail.accountNo,
        bankDetail.accountName,
        bankDetail.ifscCode
    )

    suspend fun deleteSavedBank(bankId: String) = userDao.deleteBankEntry(bankId)

    suspend fun fetchGoldProductList() = augmontService.goldProducts()

    suspend fun fetchUserAddresses() = userService.userAddresses()

    suspend fun getStoredAddresses() = userDao.getAddresses()

    suspend fun saveAddresses(addresses: List<AddressDetail>) =
        userDao.insertAddresses(*addresses.toTypedArray())

    suspend fun registerUserAddress(bodyMap: Map<String, String>) =
        augmontService.createAddress(bodyMap)

    suspend fun fetchTransactionsList() = userService.userTransactions()

    suspend fun getStoredTransactions() = userDao.getTransactions()

    suspend fun saveUserTransactions(transactions: List<TransactionItem>) =
        userDao.insertTransactions(transactions)

    suspend fun placeOrder(bodyMap: Map<String, String>) = augmontService.orderProduct(bodyMap)

    suspend fun clearUser() = userDao.deleteUser()

    suspend fun clearBanks() = userDao.clearBanksTable()

    suspend fun clearAddresses() = userDao.clearAddressTable()

    suspend fun clearTransactions() = userDao.clearTransactionsTable()

    suspend fun clearPlans() = userDao.clearPlansTable()

    suspend fun clearRates() = mainDao.clearRateTable()
}