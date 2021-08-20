package com.bharatsave.goldapp.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bharatsave.goldapp.data.repository.AuthRepository
import com.bharatsave.goldapp.data.repository.PreferenceRepository
import com.bharatsave.goldapp.model.AuthResponse
import com.bharatsave.goldapp.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
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

    fun saveAuthData(authToken: String, firebaseId: String) {
        viewModelScope.launch {
            preferenceRepository.saveAuthToken(authToken)
            preferenceRepository.saveUid(firebaseId)
//            preferenceRepository.saveRefreshToken(refreshToken)
        }
    }

    suspend fun checkTokens(): Boolean {
        val authToken = preferenceRepository.getAuthToken().first()
//        val refreshToken = preferenceRepository.getRefreshToken().first()
        if (authToken.isBlank()) {
            return false
        }
        return true
    }

    fun saveUser(user: User) {
        viewModelScope.launch {
            authRepository.insertUser(user)
        }
    }
}