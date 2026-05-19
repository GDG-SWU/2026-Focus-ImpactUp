package com.gdgswu.qos.ui.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gdgswu.qos.data.remote.ApiResult
import com.gdgswu.qos.data.remote.QosRepository
import com.gdgswu.qos.data.remote.model.FacilityItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QosRepository(application.applicationContext)

    private val _facilities = MutableStateFlow<List<FacilityItem>>(emptyList())
    val facilities: StateFlow<List<FacilityItem>> = _facilities

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadFacilities()
    }

    fun loadFacilities(category: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            when (val result = repository.getFacilities(category)) {
                is ApiResult.Success -> {
                    _facilities.value = result.data.facilities
                    _isLoading.value = false
                }
                is ApiResult.Error -> {
                    _errorMessage.value = result.message
                    _isLoading.value = false
                }
                is ApiResult.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }
}
