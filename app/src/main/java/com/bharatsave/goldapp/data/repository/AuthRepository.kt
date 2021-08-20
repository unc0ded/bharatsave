package com.bharatsave.goldapp.data.repository

import com.bharatsave.goldapp.data.db.UserDao
import com.bharatsave.goldapp.data.service.AugmontService
import com.bharatsave.goldapp.data.service.UserService
import com.bharatsave.goldapp.model.AuthResponse
import com.bharatsave.goldapp.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val augmontService: AugmontService,
    private val userService: UserService,
    private val userDao: UserDao
) {

    suspend fun signUp(user: User): AuthResponse {
        return augmontService.signUp(user)
    }

    suspend fun login(phoneNumber: String): AuthResponse {
        return augmontService.login(hashMapOf("mobileNumber" to phoneNumber))
    }

    suspend fun insertUser(user: User) {
        return userDao.insertUser(user)
    }

    suspend fun getUser(updated: Boolean): User {
        if (!updated) {
            return userDao.getUser()
        }
        return userService.userDetails()
    }
}