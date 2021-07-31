package com.dev.`in`.drogon.view.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.`in`.drogon.data.repository.AuthRepository
import com.dev.`in`.drogon.data.repository.PreferenceRepository
import com.dev.`in`.drogon.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User>
        get() = _userData

    init {
        viewModelScope.launch {
            val user = authRepository.getUser()
            _userData.value = user
        }
    }

    fun clearUserData() {
        viewModelScope.launch {
            preferenceRepository.clearTokens()
        }
    }

    fun getUserData() {
        viewModelScope.launch {
            val user = authRepository.getUser()
            _userData.value = user
        }
    }
}