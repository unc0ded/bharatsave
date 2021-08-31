package com.bharatsave.goldapp.data.service

import android.util.Log
import com.bharatsave.goldapp.data.repository.PreferenceRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val preferenceRepository: PreferenceRepository) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authToken = runBlocking {
            preferenceRepository.getAuthToken()
        }
        Log.d("authToken", authToken)
        return chain.proceed(
            if (authToken.isNotEmpty()) request.newBuilder()
                .header("Authorization", "Bearer $authToken")
                .build()
            else request
        )
    }
}