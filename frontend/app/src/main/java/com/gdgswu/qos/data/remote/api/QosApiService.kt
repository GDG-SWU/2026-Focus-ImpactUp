package com.gdgswu.qos.data.remote.api

import com.gdgswu.qos.data.remote.model.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface QosApiService {

    // ── users ──────────────────────────────────────────────────────────────────

    /** 온보딩 (최초 1회) — JWT 발급 */
    @POST("users/onboard")
    suspend fun onboard(
        @Body request: OnboardRequest
    ): Response<UserProfileResponse>

    /** 프로필 전체 조회 */
    @GET("users/profile")
    suspend fun getProfile(): Response<UserProfileResponse>

    /** 프로필 수정 (변경 항목만 전송) */
    @PATCH("users/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Response<UserProfileResponse>

    // ── status ─────────────────────────────────────────────────────────────────

    /** 네트워크 상태 배너 */
    @GET("status/network")
    suspend fun getNetworkStatus(): Response<NetworkStatusResponse>

    // ── guide ──────────────────────────────────────────────────────────────────

    /** 생존 행동 제안 조회 */
    @GET("guide/survival-actions")
    suspend fun getSurvivalActions(
        @Query("stage") stage: String? = null,
        @Query("location_lat") lat: Double? = null,
        @Query("location_lng") lng: Double? = null
    ): Response<SurvivalActionsResponse>

    // ── home ───────────────────────────────────────────────────────────────────

    /** 홈 긴급 퀵버튼 카테고리 */
    @GET("home/quick-categories")
    suspend fun getQuickCategories(): Response<QuickCategoriesResponse>

    // ── map / tiles ────────────────────────────────────────────────────────────

    /** 오프라인 지도 타일 조회 */
    @GET("map/tiles")
    suspend fun getMapTiles(
        @Query("bbox") bbox: String,    // "minLng,minLat,maxLng,maxLat"
        @Query("zoom") zoom: Int
    ): Response<MapTilesResponse>

    // ── location ───────────────────────────────────────────────────────────────

    /** 현재 위치 조회 (GPS) */
    @GET("location/current")
    suspend fun getCurrentLocation(): Response<LocationResponse>

    /** 추측 항법 위치 추정 (GPS 유실 시) */
    @POST("location/dead-reckoning")
    suspend fun estimateByDeadReckoning(
        @Body request: DeadReckoningRequest
    ): Response<LocationResponse>

    /** 수동 위치 핀 수정 */
    @PUT("location/pin")
    suspend fun pinLocation(
        @Body body: Map<String, Double>   // { "lat": ..., "lng": ... }
    ): Response<LocationResponse>

    // ── facilities ─────────────────────────────────────────────────────────────
    // ※ map/ 접두사 없음 — 백엔드 v2 명세 기준

    /** 기관 목록 조회 (카테고리·위치 반경 필터) */
    @GET("facilities")
    suspend fun getFacilities(
        @Query("category") category: String? = null,  // "hospital"|"water"|"camp"|"ngo"
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null,
        @Query("radius") radius: Int? = null           // 검색 반경 (미터, 기본 5000)
    ): Response<List<FacilityItem>>

    /** 기관 상세 조회 */
    @GET("facilities/{id}")
    suspend fun getFacilityDetail(
        @Path("id") id: String
    ): Response<FacilityDetailResponse>

    /** 기관 가용 상태 조회 */
    @GET("facilities/{id}/status")
    suspend fun getFacilityStatus(
        @Path("id") id: String
    ): Response<FacilityStatusResponse>

    // ── cards ──────────────────────────────────────────────────────────────────

    /** 오프라인 번역 카드 목록 */
    @GET("cards/phrasebook")
    suspend fun getPhrasebook(
        @Query("category") category: String? = null
    ): Response<PhrasebookResponse>

    /** 최근 사용 카드 */
    @GET("cards/recent")
    suspend fun getRecentCards(): Response<RecentCardsResponse>

    /** SOS 카드 정보 */
    @GET("cards/sos")
    suspend fun getSosCard(): Response<SosCardResponse>

    // ── tts ────────────────────────────────────────────────────────────────────

    /** 번역 텍스트 TTS 오디오 (audio/mpeg 바이너리 직접 반환) */
    @POST("tts/card")
    suspend fun getTtsAudio(
        @Body request: TtsRequest    // text + language
    ): Response<ResponseBody>

    // ── ocr ────────────────────────────────────────────────────────────────────

    /** 카메라 라벨 스캔 (이미지 + 대상 언어 업로드) */
    @Multipart
    @POST("ocr/scan")
    suspend fun scanImage(
        @Part image: MultipartBody.Part,
        @Part("target_language") targetLanguage: okhttp3.RequestBody? = null
    ): Response<OcrScanResponse>

    /** OCR 텍스트 위험 감지 (알레르기 확인) */
    @POST("ocr/risk-check")
    suspend fun checkRisk(
        @Body request: RiskCheckRequest    // ocr_text + user_id
    ): Response<RiskCheckResponse>
}
