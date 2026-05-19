package com.gdgswu.qos.ui.map

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.content.Context
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gdgswu.qos.data.remote.ApiResult
import com.gdgswu.qos.data.remote.QosRepository
import com.gdgswu.qos.data.remote.model.DeadReckoningRequest
import com.gdgswu.qos.data.remote.model.FacilityDetailResponse
import com.gdgswu.qos.data.remote.model.FacilityItem
import com.gdgswu.qos.data.remote.model.FacilityStatusResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

data class DeviceLocation(val lat: Double, val lng: Double, val source: String = "gps")

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QosRepository(application.applicationContext)

    private val _facilities = MutableStateFlow<List<FacilityItem>>(emptyList())
    val facilities: StateFlow<List<FacilityItem>> = _facilities

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isOnline = MutableStateFlow<Boolean?>(null)
    val isOnline: StateFlow<Boolean?> = _isOnline

    private val _deviceLocation = MutableStateFlow<DeviceLocation?>(null)
    val deviceLocation: StateFlow<DeviceLocation?> = _deviceLocation

    private val _facilityDetail = MutableStateFlow<FacilityDetailResponse?>(null)
    val facilityDetail: StateFlow<FacilityDetailResponse?> = _facilityDetail

    private val _facilityStatus = MutableStateFlow<FacilityStatusResponse?>(null)
    val facilityStatus: StateFlow<FacilityStatusResponse?> = _facilityStatus

    private val _isDetailLoading = MutableStateFlow(false)
    val isDetailLoading: StateFlow<Boolean> = _isDetailLoading

    private val _isLocationLoading = MutableStateFlow(false)
    val isLocationLoading: StateFlow<Boolean> = _isLocationLoading

    init {
        loadNetworkStatus()
        loadDeviceLocation()
        loadFacilities()
    }

    private fun loadNetworkStatus() {
        viewModelScope.launch {
            when (val result = repository.getNetworkStatus()) {
                is ApiResult.Success -> _isOnline.value = result.data.online
                else -> _isOnline.value = null
            }
        }
    }

    fun loadDeviceLocation() {
        viewModelScope.launch {
            _isLocationLoading.value = true
            val ctx = getApplication<Application>().applicationContext

            val hasPermission = hasLocationPermission(ctx)

            if (hasPermission) {
                // 즉시 캐시된 위치로 빠르게 표시 (지도 중심용)
                getLastKnownLocation(ctx)?.let { cached ->
                    _deviceLocation.value = cached
                }

                // 실제 신선한 GPS 픽스 요청 (최대 10초 대기)
                val fresh = requestFreshLocation(ctx)
                if (fresh != null) {
                    _deviceLocation.value = fresh
                    _isLocationLoading.value = false
                    loadFacilities(lat = fresh.lat, lng = fresh.lng)
                    return@launch
                }
            }

            // 디바이스 GPS 실패 → 백엔드 fallback
            when (val result = repository.getCurrentLocation()) {
                is ApiResult.Success -> {
                    val loc = DeviceLocation(result.data.lat, result.data.lng, result.data.source)
                    _deviceLocation.value = loc
                    loadFacilities(lat = loc.lat, lng = loc.lng)
                }
                is ApiResult.Error -> {
                    // dead-reckoning fallback
                    val last = _deviceLocation.value
                    if (last != null) {
                        val dr = repository.estimateByDeadReckoning(
                            DeadReckoningRequest(
                                last_lat = last.lat, last_lng = last.lng,
                                heading = 0, steps = 0, stride_length_m = 0.75
                            )
                        )
                        if (dr is ApiResult.Success) {
                            _deviceLocation.value = DeviceLocation(dr.data.lat, dr.data.lng, "dead_reckoning")
                        }
                    }
                    loadFacilities()
                }
                else -> loadFacilities()
            }
            _isLocationLoading.value = false
        }
    }

    /** GPS/Network provider에 실제 위치 요청 — 즉시 캐시 확인 후, 변화 대기(최대 8초) */
    private suspend fun requestFreshLocation(context: Context): DeviceLocation? {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val provider = when {
            lm.isProviderEnabled(LocationManager.GPS_PROVIDER)     -> LocationManager.GPS_PROVIDER
            lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> return null
        }

        return withTimeoutOrNull(8_000L) {
            suspendCancellableCoroutine { cont ->
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        if (cont.isActive) {
                            lm.removeUpdates(this)
                            cont.resume(
                                DeviceLocation(location.latitude, location.longitude, location.provider ?: provider)
                            )
                        }
                    }
                    override fun onProviderDisabled(p: String) {
                        if (cont.isActive) { lm.removeUpdates(this); cont.resume(null) }
                    }
                }

                try {
                    @Suppress("MissingPermission")
                    lm.requestLocationUpdates(provider, 0L, 0f, listener, Looper.getMainLooper())
                    cont.invokeOnCancellation { lm.removeUpdates(listener) }

                    // 리스너 등록 직후 캐시 확인 —
                    // Extended Controls로 이미 위치가 세팅된 경우 콜백이 안 오므로 여기서 즉시 반환
                    @Suppress("MissingPermission")
                    val cached = lm.getLastKnownLocation(provider)
                    if (cached != null && cont.isActive) {
                        lm.removeUpdates(listener)
                        cont.resume(DeviceLocation(cached.latitude, cached.longitude, provider))
                    }
                } catch (e: Exception) {
                    if (cont.isActive) cont.resume(null)
                }
            }
        }
    }

    /** 캐시된 마지막 위치 즉시 반환 (빠른 초기 표시용) */
    private fun getLastKnownLocation(context: Context): DeviceLocation? {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        for (provider in listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)) {
            try {
                if (!lm.isProviderEnabled(provider)) continue
                @Suppress("MissingPermission")
                val loc = lm.getLastKnownLocation(provider) ?: continue
                return DeviceLocation(loc.latitude, loc.longitude, provider)
            } catch (_: Exception) {}
        }
        return null
    }

    private fun hasLocationPermission(context: Context) =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    fun loadFacilityDetail(facilityId: String) {
        viewModelScope.launch {
            _isDetailLoading.value = true
            _facilityDetail.value = null
            _facilityStatus.value = null
            val detailJob = launch {
                if (facilityId.isNotBlank()) {
                    when (val r = repository.getFacilityDetail(facilityId)) {
                        is ApiResult.Success -> _facilityDetail.value = r.data
                        else -> {}
                    }
                }
            }
            val statusJob = launch {
                if (facilityId.isNotBlank()) {
                    when (val r = repository.getFacilityStatus(facilityId)) {
                        is ApiResult.Success -> _facilityStatus.value = r.data
                        else -> {}
                    }
                }
            }
            detailJob.join(); statusJob.join()
            _isDetailLoading.value = false
        }
    }

    fun clearFacilityDetail() {
        _facilityDetail.value = null
        _facilityStatus.value = null
    }

    fun loadFacilities(category: String? = null, lat: Double? = null, lng: Double? = null) {
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
                is ApiResult.Loading -> _isLoading.value = true
            }
        }
    }
}
