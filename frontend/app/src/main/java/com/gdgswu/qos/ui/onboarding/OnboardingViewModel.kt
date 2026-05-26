package com.gdgswu.qos.ui.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gdgswu.qos.data.remote.ApiResult
import com.gdgswu.qos.data.remote.QosRepository
import com.gdgswu.qos.data.remote.model.HealthInfoRequest
import com.gdgswu.qos.data.remote.model.OnboardRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class OnboardingUiState {
    object Idle : OnboardingUiState()
    object Loading : OnboardingUiState()
    object Success : OnboardingUiState()
    data class Error(val message: String) : OnboardingUiState()
}

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QosRepository(application.applicationContext)

    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Idle)
    val uiState: StateFlow<OnboardingUiState> = _uiState

    fun onboard(
        locale: String,
        preferredLanguage: String,
        conditions: List<String>,
        allergies: List<String>,
        companions: List<String>,
        bloodType: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = OnboardingUiState.Loading

            val request = OnboardRequest(
                locale = locale,
                preferred_language = preferredLanguage,
                health_info = HealthInfoRequest(
                    conditions = conditions,
                    allergies = allergies,
                    blood_type = bloodType
                ),
                companions = companions
            )

            when (val result = repository.onboard(request)) {
                is ApiResult.Success -> {
                    // 토큰 및 userId는 QosRepository.onboard() 내부에서 저장됨
                    _uiState.value = OnboardingUiState.Success
                }
                is ApiResult.Error -> {
                    _uiState.value = OnboardingUiState.Error(result.message)
                }
                is ApiResult.Loading -> {
                    _uiState.value = OnboardingUiState.Loading
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = OnboardingUiState.Idle
    }
}
