package com.gdgswu.qos.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gdgswu.qos.data.remote.ApiResult
import com.gdgswu.qos.data.remote.QosRepository
import com.gdgswu.qos.data.remote.model.SurvivalActionsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QosRepository(application.applicationContext)

    // null = unknown, true = online, false = offline
    private val _isOnline = MutableStateFlow<Boolean?>(null)
    val isOnline: StateFlow<Boolean?> = _isOnline

    private val _survivalActions = MutableStateFlow<SurvivalActionsResponse?>(null)
    val survivalActions: StateFlow<SurvivalActionsResponse?> = _survivalActions

    init {
        refreshNetworkStatus()
        loadSurvivalActions()
    }

    fun refreshNetworkStatus() {
        viewModelScope.launch {
            when (val result = repository.getNetworkStatus()) {
                is ApiResult.Success -> _isOnline.value = result.data.online
                else -> _isOnline.value = null
            }
        }
    }

    private fun loadSurvivalActions() {
        viewModelScope.launch {
            when (val result = repository.getSurvivalActions()) {
                is ApiResult.Success -> _survivalActions.value = result.data
                else -> {}
            }
        }
    }
}
