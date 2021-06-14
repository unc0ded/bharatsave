package com.dev.`in`.drogon.view.register.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.`in`.drogon.data.repository.AuthRepository
import com.dev.`in`.drogon.data.repository.PreferenceRepository
import com.dev.`in`.drogon.model.AuthResponse
import com.dev.`in`.drogon.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    private val _response = MutableLiveData<AuthResponse>()
    val response: LiveData<AuthResponse>
        get() = _response

    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    fun login(phoneNumber: String) {
        viewModelScope.launch {
            try {
                val authResponse = authRepository.login(phoneNumber)
                _response.value = authResponse
            } catch (cause: Throwable) {
                when (cause) {
                    is IOException -> _message.value = cause.message
                    is HttpException -> withContext(Dispatchers.IO) {
                        _message.postValue(cause.response()?.errorBody()?.string())
                    }
                    else -> _message.value = cause.message
                }
            }
        }
    }

    fun signUp(user: User) {
        viewModelScope.launch {
            try {
                val authResponse = authRepository.signUp(user)
                _response.value = authResponse
            } catch (cause: Throwable) {
                when (cause) {
                    is IOException -> _message.value = cause.message
                    is HttpException -> withContext(Dispatchers.IO) {
                        _message.postValue(cause.response()?.errorBody()?.string())
                    }
                    else -> _message.value = cause.message
                }
            }
        }
    }

    fun saveTokens(authToken: String, refreshToken: String) {
        viewModelScope.launch {
            preferenceRepository.saveAuthToken(authToken)
            preferenceRepository.saveRefreshToken(refreshToken)
        }
    }
}