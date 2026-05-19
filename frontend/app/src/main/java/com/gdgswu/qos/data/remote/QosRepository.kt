package com.gdgswu.qos.data.remote

import android.content.Context
import com.gdgswu.qos.data.remote.model.*
import com.gdgswu.qos.data.remote.network.RetrofitClient
import okhttp3.MultipartBody
import okhttp3.RequestBody

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

class QosRepository(private val context: Context) {

    private val api = RetrofitClient.create(context)

    suspend fun onboard(request: OnboardRequest): ApiResult<UserProfileResponse> {
        return try {
            val response = api.onboard(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error("Empty response body")
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getProfile(): ApiResult<UserProfileResponse> {
        return try {
            val response = api.getProfile()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error("Empty response body")
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun updateProfile(request: UpdateProfileRequest): ApiResult<UserProfileResponse> {
        return try {
            val response = api.updateProfile(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error("Empty response body")
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    // ── location ───────────────────────────────────────────────────────────────

    suspend fun getCurrentLocation(): ApiResult<LocationResponse> {
        return try {
            val response = api.getCurrentLocation()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error("Empty response body")
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun estimateByDeadReckoning(request: DeadReckoningRequest): ApiResult<LocationResponse> {
        return try {
            val response = api.estimateByDeadReckoning(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error("Empty response body")
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun pinLocation(lat: Double, lng: Double): ApiResult<LocationResponse> {
        return try {
            val response = api.pinLocation(mapOf("lat" to lat, "lng" to lng))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error("Empty response body")
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    // ── facilities ─────────────────────────────────────────────────────────────

    suspend fun getFacilities(
        category: String? = null,
        lat: Double? = null,
        lng: Double? = null
    ): ApiResult<FacilitiesResponse> {
        return try {
            val response = api.getFacilities(category = category, lat = lat, lng = lng)
            if (response.isSuccessful) {
                val list = response.body()
                if (list != null) ApiResult.Success(
                    FacilitiesResponse(facilities = list, total = list.size, offline = false, cached_at = "")
                )
                else ApiResult.Error("Empty response body")
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getFacilityDetail(id: String): ApiResult<FacilityDetailResponse> {
        return try {
            val response = api.getFacilityDetail(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error("Empty response body")
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getFacilityStatus(id: String): ApiResult<FacilityStatusResponse> {
        return try {
            val response = api.getFacilityStatus(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error("Empty response body")
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getQuickCategories(): ApiResult<QuickCategoriesResponse> {
        return try {
            val response = api.getQuickCategories()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error("Empty response body")
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getSosCard(): ApiResult<SosCardResponse> {
        return try {
            val response = api.getSosCard()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error("Empty response body")
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getNetworkStatus(): ApiResult<NetworkStatusResponse> {
        return try {
            val response = api.getNetworkStatus()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error("Empty response body")
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getSurvivalActions(): ApiResult<SurvivalActionsResponse> {
        return try {
            val response = api.getSurvivalActions()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error("Empty response body")
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun scanImage(
        image: MultipartBody.Part,
        targetLanguage: RequestBody? = null
    ): ApiResult<OcrScanResponse> {
        return try {
            val response = api.scanImage(image, targetLanguage)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error("Empty response body")
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun checkRisk(ocrText: String, userId: String): ApiResult<RiskCheckResponse> {
        return try {
            val response = api.checkRisk(RiskCheckRequest(ocr_text = ocrText, user_id = userId))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error("Empty response body")
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    /** TTS — 백엔드가 audio/mpeg 바이너리를 직접 반환 */
    suspend fun getTtsBytes(text: String, language: String): ApiResult<ByteArray> {
        return try {
            val response = api.getTtsAudio(TtsRequest(text = text, language = language))
            if (response.isSuccessful) {
                val bytes = response.body()?.bytes()
                if (bytes != null && bytes.isNotEmpty()) ApiResult.Success(bytes)
                else ApiResult.Error("Empty audio response")
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getPhrasebook(category: String? = null): ApiResult<PhrasebookResponse> {
        return try {
            val response = api.getPhrasebook(category)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error("Empty response body")
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getRecentCards(): ApiResult<RecentCardsResponse> {
        return try {
            val response = api.getRecentCards()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error("Empty response body")
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }
}
