package com.gdgswu.qos.data.model

import androidx.compose.runtime.mutableStateListOf

/** 앱 전체 공유 북마크 상태 (ViewModel 없이 싱글톤으로 관리) */
object FavoritesState {
    /** 별표 된 Phrase 객체 목록 (predefined + API 카드 모두 포함) */
    val savedPhrases = mutableStateListOf<Phrase>()

    /** 유저가 직접 번역 후 저장한 커스텀 문장 */
    val customTranslations = mutableStateListOf<CustomTranslation>()

    fun togglePhrase(phrase: Phrase) {
        val existing = savedPhrases.find { it.id == phrase.id }
        if (existing != null) savedPhrases.remove(existing)
        else savedPhrases.add(phrase)
    }

    fun isSaved(phraseId: Int) = savedPhrases.any { it.id == phraseId }

    fun addCustom(item: CustomTranslation) {
        if (customTranslations.none { it.id == item.id }) customTranslations.add(item)
    }

    fun removeCustom(id: String) = customTranslations.removeIf { it.id == id }
}

data class CustomTranslation(
    val id: String,
    val english: String,
    val translated: String,
    val language: SupportedLanguage
)
