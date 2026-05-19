package com.gdgswu.qos.data.remote

import android.content.Context
import com.gdgswu.qos.data.remote.model.*
import com.gdgswu.qos.data.remote.network.RetrofitClient

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

    suspend fun getFacilities(category: String? = null): ApiResult<FacilitiesResponse> {
        return try {
            val response = api.getFacilities(category = category)
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

    suspend fun getTts(text: String, language: String): ApiResult<TtsResponse> {
        return try {
            val response = api.getTtsAudio(TtsRequest(text = text, language = language))
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
