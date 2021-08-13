package com.bharatsave.goldapp.data.repository

import com.bharatsave.goldapp.data.db.UserDao
import com.bharatsave.goldapp.data.service.AuthService
import com.bharatsave.goldapp.model.AuthResponse
import com.bharatsave.goldapp.model.User
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