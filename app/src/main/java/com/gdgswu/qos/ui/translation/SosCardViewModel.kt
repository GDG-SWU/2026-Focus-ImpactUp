package com.gdgswu.qos.ui.translation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gdgswu.qos.data.remote.ApiResult
import com.gdgswu.qos.data.remote.QosRepository
import com.gdgswu.qos.data.remote.model.SosCardResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SosCardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QosRepository(application.applicationContext)

    private val _sosCard = MutableStateFlow<SosCardResponse?>(null)
    val sosCard: StateFlow<SosCardResponse?> = _sosCard

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadSosCard()
    }

    fun loadSosCard() {
        viewModelScope.launch {
            _isLoading.value = true

            when (val result = repository.getSosCard()) {
                is ApiResult.Success -> {
                    _sosCard.value = result.data
                    _isLoading.value = false
                }
                is ApiResult.Error -> {
                    _sosCard.value = null
                    _isLoading.value = false
                }
                is ApiResult.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }
}
