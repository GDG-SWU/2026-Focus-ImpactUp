package com.gdgswu.qos.data.remote.network

import android.content.Context
import com.gdgswu.qos.data.remote.TokenManager
import com.gdgswu.qos.data.remote.api.QosApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // TODO: 백엔드 배포 후 실제 Base URL로 교체
    // 에뮬레이터 로컬 테스트: http://10.0.2.2:8080/api/v1/
    private const val BASE_URL = "http://10.0.2.2:8080/api/v1/"

    /**
     * 요청 인터셉터 — 저장된 JWT를 Authorization 헤더에 자동 추가
     */
    private fun authInterceptor(context: Context) = okhttp3.Interceptor { chain ->
        val token = TokenManager.getToken(context)
        val userId = TokenManager.getUserId(context)
        val builder = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
        if (token != null) builder.addHeader("Authorization", "Bearer $token")
        if (userId != null) builder.addHeader("X-User-Id", userId)
        chain.proceed(builder.build())
    }

    /**
     * 응답 인터셉터 — onboard 응답 헤더의 Authorization 값을 자동 저장
     * 서버가 "Authorization: Bearer eyJ..." 헤더로 토큰을 내려줌
     */
    private fun tokenSaveInterceptor(context: Context) = okhttp3.Interceptor { chain ->
        val response = chain.proceed(chain.request())
        val authHeader = response.header("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.removePrefix("Bearer ").trim()
            TokenManager.saveToken(context, token)
        }
        response
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    fun create(context: Context): QosApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor(context))
            .addInterceptor(tokenSaveInterceptor(context))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QosApiService::class.java)
    }
}
