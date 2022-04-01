package com.bharatsave.goldapp.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bharatsave.goldapp.data.repository.AuthRepository
import com.bharatsave.goldapp.data.repository.PreferenceRepository
import com.bharatsave.goldapp.model.AuthResponse
import com.bharatsave.goldapp.model.User
import com.bharatsave.goldapp.view.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
        viewModelScope.launchIO(
            action = {
                val authResponse = authRepository.login(phoneNumber)
                _response.postValue(authResponse)
            },
            onError = { error ->
                when (error) {
                    is IOException -> _message.value = error.message
                    is HttpException -> _message.value = error.response()?.errorBody()?.string()
                    else -> _message.value = error.message
                }
            }
        )
    }

    fun signUp(user: User) {
        viewModelScope.launchIO(
            action = {
                val authResponse = authRepository.signUp(user)
                _response.postValue(authResponse)
            },
            onError = { error ->
                when (error) {
                    is IOException -> _message.value = error.message
                    is HttpException -> _message.value = error.response()?.errorBody()?.string()
                    else -> _message.value = error.message
                }
            }
        )
    }

    fun saveAuthData(authToken: String, phoneNumber: String, firebaseId: String) {
        viewModelScope.launch {
            preferenceRepository.saveAuthToken(authToken)
            preferenceRepository.saveUid(firebaseId)
            preferenceRepository.savePhone(phoneNumber)
//            preferenceRepository.saveRefreshToken(refreshToken)
        }
    }

    suspend fun checkTokens(): Boolean {
        val authToken = preferenceRepository.getAuthToken()
//        val refreshToken = preferenceRepository.getRefreshToken().first()
        if (authToken.isBlank()) {
            return false
        }
        return true
    }

    fun saveUser(user: User) {
        viewModelScope.launchIO(
            action = {
                authRepository.insertUser(user)
            },
            onError = { error ->
                when (error) {
                    is IOException -> _message.value = error.message
                    is HttpException -> _message.value = error.response()?.errorBody()?.string()
                    else -> _message.value = error.message
                }
            }
        )
    }
}