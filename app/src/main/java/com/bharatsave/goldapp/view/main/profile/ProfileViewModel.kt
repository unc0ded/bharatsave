package com.bharatsave.goldapp.view.main.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bharatsave.goldapp.data.repository.AuthRepository
import com.bharatsave.goldapp.data.repository.MainRepository
import com.bharatsave.goldapp.data.repository.PreferenceRepository
import com.bharatsave.goldapp.model.User
import com.bharatsave.goldapp.view.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val authRepository: AuthRepository,
    private val mainRepository: MainRepository
) : ViewModel() {

    private val TAG = "ProfileViewModel"

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User>
        get() = _userData

    private val _updateStatus = MutableLiveData<String>()
    val updateStatus: LiveData<String>
        get() = _updateStatus

    init {
        viewModelScope.launchIO(
            action = {
                val user = authRepository.getUser(false)
                _userData.postValue(user)
            },
            onError = {
                Log.e(TAG, "#init ${it.message}")
            }
        )
    }

    fun clearUserData() {
        viewModelScope.launchIO(
            action = {
                preferenceRepository.clearUserData()
            },
            onError = {
                Log.e(TAG, "#clearUserData ${it.message}")
            }
        )
    }

    fun loadUserData() {
        viewModelScope.launchIO(
            action = {
                val user = authRepository.getUser(false)
                _userData.postValue(user)
            },
            onError = {
                Log.e(TAG, "#loadUserData ${it.message}")
            }
        )
    }

    fun saveUserDetails(user: User) {
        viewModelScope.launchIO(
            action = {
                val updatedUser = mainRepository.updateUserDetails(user)
                mainRepository.saveUserDetails(updatedUser)
                _updateStatus.postValue("SUCCESS")
            },
            onError = {
                when (it) {
                    is IOException -> _updateStatus.value = "FAILED: ${it.message}"
                    is HttpException -> _updateStatus.value =
                        "FAILED: ${it.response()?.errorBody()?.string()}"
                    else -> _updateStatus.value = it.message
                }
            }
        )
    }
}