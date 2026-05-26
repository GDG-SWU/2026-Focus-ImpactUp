package com.gdgswu.qos.data.local

import android.content.Context
import com.gdgswu.qos.data.model.CustomTranslation
import com.gdgswu.qos.data.model.FavoritesState
import com.gdgswu.qos.data.model.SupportedLanguage
import com.gdgswu.qos.data.model.samplePhrases
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FavoritesPrefs {

    private const val PREFS_NAME = "favorites_prefs"
    private const val KEY_PHRASE_IDS = "saved_phrase_ids"
    private const val KEY_CUSTOM = "custom_translations"

    private val gson = Gson()
    private var loaded = false

    fun load(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // 저장된 predefined phrase ID 목록 복원
        val idsJson = prefs.getString(KEY_PHRASE_IDS, null)
        if (idsJson != null) {
            val type = object : TypeToken<Set<Int>>() {}.type
            val ids: Set<Int> = gson.fromJson(idsJson, type) ?: emptySet()
            val phrases = samplePhrases.filter { it.id in ids }
            FavoritesState.savedPhrases.clear()
            FavoritesState.savedPhrases.addAll(phrases)
        }

        // 저장된 커스텀 번역 목록 복원
        val customJson = prefs.getString(KEY_CUSTOM, null)
        if (customJson != null) {
            runCatching {
                val type = object : TypeToken<List<CustomTranslationRaw>>() {}.type
                val raws: List<CustomTranslationRaw> = gson.fromJson(customJson, type) ?: emptyList()
                val customs = raws.mapNotNull { it.toModel() }
                FavoritesState.customTranslations.clear()
                FavoritesState.customTranslations.addAll(customs)
            }
        }

        loaded = true
    }

    fun save(context: Context) {
        if (!loaded) return
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val ids = FavoritesState.savedPhrases.map { it.id }.toSet()
        val raws = FavoritesState.customTranslations.map { CustomTranslationRaw.from(it) }
        prefs.edit()
            .putString(KEY_PHRASE_IDS, gson.toJson(ids))
            .putString(KEY_CUSTOM, gson.toJson(raws))
            .apply()
    }

    // Gson 직렬화용 중간 모델 (enum을 String name으로 저장)
    private data class CustomTranslationRaw(
        val id: String,
        val english: String,
        val translated: String,
        val languageName: String
    ) {
        fun toModel(): CustomTranslation? {
            val lang = SupportedLanguage.entries.firstOrNull { it.name == languageName } ?: return null
            return CustomTranslation(id = id, english = english, translated = translated, language = lang)
        }

        companion object {
            fun from(c: CustomTranslation) = CustomTranslationRaw(
                id = c.id,
                english = c.english,
                translated = c.translated,
                languageName = c.language.name
            )
        }
    }
}
