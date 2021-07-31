package com.dev.`in`.drogon.data.repository

import com.dev.`in`.drogon.data.db.UserDao
import com.dev.`in`.drogon.data.service.AuthService
import com.dev.`in`.drogon.model.AuthResponse
import com.dev.`in`.drogon.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val userDao: UserDao
) {

    suspend fun signUp(user: User): AuthResponse {
        return authService.signUp(user)
    }

    suspend fun login(phoneNumber: String): AuthResponse {
        return authService.login(hashMapOf("phone_number" to phoneNumber))
    }

    suspend fun insertUser(user: User) {
        return userDao.insertUser(user)
    }

    suspend fun getUser(): User {
        val user = userDao.getUser()
        if (user != null) {
            return user
        }
        // TODO if does not exist, fetch from backend
        return User("Nudge Admin", "admin@nudge.money", "")
    }
}