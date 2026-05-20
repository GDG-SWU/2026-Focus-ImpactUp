package com.gdgswu.qos.data.remote.model

// ── 공통 에러 응답 ─────────────────────────────────────────────────────────────
data class ApiError(
    val code: String,
    val message: String,
    val offline: Boolean = false
)

// ══════════════════════════════════════════════════════════════════════════════
// users
// ══════════════════════════════════════════════════════════════════════════════

data class OnboardRequest(
    val locale: String,
    val preferred_language: String,
    val health_info: HealthInfoRequest,
    val companions: List<String>          // ["child", "pregnant"] — 최상위로 이동
)

data class HealthInfoRequest(
    val conditions: List<String>,
    val allergies: List<String>
    // companion_info 제거됨
)

data class UpdateProfileRequest(
    val preferred_language: String? = null,
    val health_info: HealthInfoRequest? = null,
    val companions: List<String>? = null  // 최상위 필드
    // locale 제거됨
)

// POST /users/onboard, GET /users/profile, PATCH /users/profile 공통 응답
data class UserProfileResponse(
    val user_id: String?,
    val preferred_language: String,
    val locale: String?,
    val onboarding_completed: Boolean = false,
    val created_at: String?,
    val updated_at: String?,
    val offline: Boolean = false,
    val health: SimpleHealthResponse?,
    val companions: List<String>?
)

// health 객체 — user_id/updated_at/offline 제거됨
data class SimpleHealthResponse(
    val conditions: List<String>,
    val allergies: List<String>
)

// ══════════════════════════════════════════════════════════════════════════════
// status
// ══════════════════════════════════════════════════════════════════════════════

data class NetworkStatusResponse(
    val online: Boolean,
    val last_known_lat: Double,
    val last_known_lng: Double,
    val updated_at: String,
    val offline: Boolean
)

// ══════════════════════════════════════════════════════════════════════════════
// guide
// ══════════════════════════════════════════════════════════════════════════════

data class SurvivalActionsResponse(
    val stage: String,
    val actions: List<SurvivalAction>,
    val offline: Boolean,
    val cached_at: String
)

data class SurvivalAction(
    val priority: Int,
    val label: String,
    val action_type: String,
    val target_facility_category: String
)

// ══════════════════════════════════════════════════════════════════════════════
// home
// ══════════════════════════════════════════════════════════════════════════════

data class QuickCategoriesResponse(
    val categories: List<QuickCategory>,
    val offline: Boolean
)

data class QuickCategory(
    val id: String,
    val label: String,
    val icon: String,
    val deep_link: String
)

// ══════════════════════════════════════════════════════════════════════════════
// map / tiles
// ══════════════════════════════════════════════════════════════════════════════

data class MapTilesResponse(
    val tile_url_template: String,
    val geojson: Map<String, Any>,
    val offline: Boolean
)

// ══════════════════════════════════════════════════════════════════════════════
// location
// ══════════════════════════════════════════════════════════════════════════════

data class LocationResponse(
    val lat: Double,
    val lng: Double,
    val accuracy_m: Double,
    val source: String,     // "gps" | "dead_reckoning" | "cache"
    val offline: Boolean
)

data class DeadReckoningRequest(
    val last_lat: Double,
    val last_lng: Double,
    val heading: Int,
    val steps: Int,
    val stride_length_m: Double
)

// ══════════════════════════════════════════════════════════════════════════════
// facilities  (경로: /facilities — map/ 접두사 없음)
// ══════════════════════════════════════════════════════════════════════════════

data class FacilitiesResponse(
    val facilities: List<FacilityItem>,
    val total: Int,
    val offline: Boolean,
    val cached_at: String
)

data class FacilityItem(
    val id: String,
    val name: String,
    val category: String,        // "hospital" | "water" | "camp" | "ngo"
    val lat: Double,
    val lng: Double,
    val distance_m: Double,
    val availability: String,    // "available" | "crowded" | "unavailable"
    val operating: Boolean
)

data class FacilityDetailResponse(
    val id: String,
    val name: String,
    val category: String,
    val lat: Double,
    val lng: Double,
    val distance_m: Double,
    val address: String,
    val operating: Boolean,
    val operating_hours: String,
    val availability: String,
    val services: List<String>,
    val contact_phone: String,
    val offline: Boolean,
    val cached_at: String
)

data class FacilityStatusResponse(
    val facility_id: String,
    val availability: String,
    val capacity_current: Int,
    val capacity_max: Int,
    val wait_time_min: Int,
    val last_updated: String,
    val offline: Boolean,
    val stale_warning: Boolean
)

// ══════════════════════════════════════════════════════════════════════════════
// cards
// ══════════════════════════════════════════════════════════════════════════════

data class PhrasebookResponse(
    val category: String,
    val cards: List<PhraseCard>,
    val offline: Boolean
)

data class PhraseCard(
    val id: String,
    val situation: String,
    val translations: Map<String, String>,
    val tts_available: Boolean,
    val usage_count: Int
)

data class RecentCardsResponse(
    val cards: List<PhraseCard>,
    val offline: Boolean
)

data class SosCardResponse(
    val user_id: String,
    val generated_at: String,
    val translations: Map<String, String>?,
    val conditions_translated: List<TranslatedHealthItem>?,
    val allergies_translated: List<TranslatedHealthItem>?,
    val offline: Boolean = false
)

data class TranslatedHealthItem(
    val code: String,
    val label: Map<String, String> = emptyMap()
)

// ══════════════════════════════════════════════════════════════════════════════
// tts
// ══════════════════════════════════════════════════════════════════════════════

data class TtsRequest(
    val text: String,
    val language: String
)

data class TtsResponse(
    val audio_url: String,
    val tts_engine: String,
    val duration_ms: Int,
    val offline: Boolean
)

// ══════════════════════════════════════════════════════════════════════════════
// ocr
// ══════════════════════════════════════════════════════════════════════════════

data class OcrScanResponse(
    val raw_text: String,
    val translated_text: String,
    val highlighted_keywords: List<HighlightedKeyword>,
    val disclaimer: String,
    val offline: Boolean
)

data class HighlightedKeyword(
    val keyword: String,
    val type: String,
    val bold: Boolean,
    val highlight_color: String
)

data class RiskCheckRequest(
    val ocr_text: String,
    val user_id: String
)

data class RiskCheckResponse(
    val risk_detected: Boolean,
    val risk_level: String,
    val matched_risks: List<MatchedRisk>,
    val trigger_haptic: Boolean,
    val trigger_alert_banner: Boolean,
    val offline: Boolean
)

data class MatchedRisk(
    val keyword: String,
    val matched_profile_field: String,
    val matched_value: String,
    val warning_message: String
)
