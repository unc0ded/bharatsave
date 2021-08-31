package com.bharatsave.goldapp.data.service

import com.bharatsave.goldapp.model.paytm.*
import retrofit2.http.*

interface PaytmService {

    @POST("/paytm/initiateTransaction")
    suspend fun initiateTransaction(@Body map: Map<String, String>): PaytmTransactionToken

    @GET("/paytm/transactionStatus")
    suspend fun transactionStatus(@Query("orderId") orderId: String): PaytmTransactionStatus

    @POST("/paytm/createSubscription")
    suspend fun createSubscription(@Body map: Map<String, String>): PaytmSubscriptionToken

    @POST("/paytm/manualCollect")
    suspend fun manualCollect(@Body map: Map<String, String>): PaytmManualCollectResponse

    @GET("/paytm/subscriptionStatus")
    suspend fun subscriptionStatus(@Query("subscriptionId") subscriptionId: String): PaytmSubscriptionStatus
}