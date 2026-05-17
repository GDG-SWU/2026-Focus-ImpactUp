package com.gdgswu.qos.data.local

import android.content.Context
import com.gdgswu.qos.data.model.SupportedLanguage

object UserProfilePrefs {

    private const val PREFS_NAME = "qos_prefs"

    private const val KEY_ONBOARDING_DONE = "onboarding_done"
    private const val KEY_LANGUAGE        = "language"
    private const val KEY_ALLERGIES       = "allergies"
    private const val KEY_CONDITIONS      = "conditions"
    private const val KEY_BLOOD_TYPE      = "blood_type"
    private const val KEY_HAS_CHILDREN    = "has_children"
    private const val KEY_HAS_PREGNANT    = "has_pregnant"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ── 온보딩 완료 플래그 ──────────────────────────────────────────────
    fun isOnboardingDone(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ONBOARDING_DONE, false)

    fun setOnboardingDone(context: Context) {
        prefs(context).edit().putBoolean(KEY_ONBOARDING_DONE, true).apply()
    }

    // ── 언어 ────────────────────────────────────────────────────────────
    fun loadLanguage(context: Context): SupportedLanguage {
        val name = prefs(context).getString(KEY_LANGUAGE, SupportedLanguage.ENGLISH.name)
        return SupportedLanguage.entries.firstOrNull { it.name == name }
            ?: SupportedLanguage.ENGLISH
    }

    // ── 알레르기 ─────────────────────────────────────────────────────────
    fun loadAllergies(context: Context): Set<String> {
        val raw = prefs(context).getString(KEY_ALLERGIES, "") ?: ""
        return if (raw.isBlank()) emptySet() else raw.split(",").toSet()
    }

    // ── 기저질환 ─────────────────────────────────────────────────────────
    fun loadConditions(context: Context): Set<String> {
        val raw = prefs(context).getString(KEY_CONDITIONS, "") ?: ""
        return if (raw.isBlank()) emptySet() else raw.split(",").toSet()
    }

    // ── 혈액형 ───────────────────────────────────────────────────────────
    fun loadBloodType(context: Context): String =
        prefs(context).getString(KEY_BLOOD_TYPE, "Unknown") ?: "Unknown"

    // ── 동반자 ───────────────────────────────────────────────────────────
    fun loadHasChildren(context: Context): Boolean =
        prefs(context).getBoolean(KEY_HAS_CHILDREN, false)

    fun loadHasPregnant(context: Context): Boolean =
        prefs(context).getBoolean(KEY_HAS_PREGNANT, false)

    // ── 전체 프로필 저장 (온보딩 완료 & 설정 저장 버튼 공용) ──────────────
    fun saveProfile(
        context: Context,
        language: SupportedLanguage,
        allergies: Set<String>,
        conditions: Set<String>,
        bloodType: String,
        hasChildren: Boolean,
        hasPregnant: Boolean,
        markOnboardingDone: Boolean = false
    ) {
        prefs(context).edit()
            .putString(KEY_LANGUAGE, language.name)
            .putString(KEY_ALLERGIES, allergies.joinToString(","))
            .putString(KEY_CONDITIONS, conditions.joinToString(","))
            .putString(KEY_BLOOD_TYPE, bloodType)
            .putBoolean(KEY_HAS_CHILDREN, hasChildren)
            .putBoolean(KEY_HAS_PREGNANT, hasPregnant)
            .also { if (markOnboardingDone) it.putBoolean(KEY_ONBOARDING_DONE, true) }
            .apply()
    }
}
