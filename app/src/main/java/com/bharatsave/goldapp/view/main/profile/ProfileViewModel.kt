package com.bharatsave.goldapp.view.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bharatsave.goldapp.data.repository.AuthRepository
import com.bharatsave.goldapp.data.repository.MainRepository
import com.bharatsave.goldapp.data.repository.PreferenceRepository
import com.bharatsave.goldapp.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val authRepository: AuthRepository,
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User>
        get() = _userData

    private val _updateStatus = MutableLiveData<String>()
    val updateStatus: LiveData<String>
        get() = _updateStatus

    init {
        viewModelScope.launch {
            val user = authRepository.getUser(false)
            _userData.value = user
        }
    }

    fun clearUserData() {
        viewModelScope.launch {
            preferenceRepository.clearUserData()
        }
    }

    fun loadUserData() {
        viewModelScope.launch {
            val user = authRepository.getUser(false)
            _userData.value = user
        }
    }

    fun saveUserDetails(user: User) {
        viewModelScope.launch {
            try {
                val updatedUser = mainRepository.updateUserDetails(user)
                mainRepository.saveUserDetails(updatedUser)
                _updateStatus.value = "SUCCESS"
            } catch (cause: Throwable) {
                when (cause) {
                    is IOException -> _updateStatus.value = "FAILED: ${cause.message}"
                    is HttpException -> withContext(Dispatchers.IO) {
                        _updateStatus.postValue(
                            "FAILED: ${
                                cause.response()?.errorBody()?.string()
                            }"
                        )
                    }
                    else -> _updateStatus.value = cause.message
                }
            }
        }
    }
}