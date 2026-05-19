package com.gdgswu.qos.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gdgswu.qos.data.remote.ApiResult
import com.gdgswu.qos.data.remote.QosRepository
import com.gdgswu.qos.data.remote.model.HealthInfoRequest
import com.gdgswu.qos.data.remote.model.UpdateProfileRequest
import com.gdgswu.qos.data.remote.model.UserProfileResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QosRepository(application.applicationContext)

    private val _profile = MutableStateFlow<UserProfileResponse?>(null)
    val profile: StateFlow<UserProfileResponse?> = _profile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true

            when (val result = repository.getProfile()) {
                is ApiResult.Success -> {
                    _profile.value = result.data
                    _isLoading.value = false
                }
                is ApiResult.Error -> {
                    _isLoading.value = false
                }
                is ApiResult.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }

    fun updateProfile(
        conditions: List<String>,
        allergies: List<String>,
        companions: List<String>
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _updateSuccess.value = false

            val request = UpdateProfileRequest(
                health_info = HealthInfoRequest(
                    conditions = conditions,
                    allergies = allergies
                ),
                companions = companions
            )

            when (val result = repository.updateProfile(request)) {
                is ApiResult.Success -> {
                    _profile.value = result.data
                    _updateSuccess.value = true
                    _isLoading.value = false
                }
                is ApiResult.Error -> {
                    _isLoading.value = false
                    _updateSuccess.value = false
                }
                is ApiResult.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }

    fun resetUpdateSuccess() {
        _updateSuccess.value = false
    }
}
