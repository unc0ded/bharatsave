package com.dev.`in`.drogon.view.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.`in`.drogon.data.repository.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    fun clearUserData() {
        viewModelScope.launch {
            preferenceRepository.clearTokens()
        }
    }
}