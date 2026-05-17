package com.gdgswu.qos.data.model

import androidx.compose.runtime.mutableStateListOf

/** 앱 전체 공유 북마크 상태 (ViewModel 없이 싱글톤으로 관리) */
object FavoritesState {
    /** 별표 된 predefined phrase IDs */
    val savedPhraseIds = mutableStateListOf<Int>()

    /** 유저가 직접 번역 후 저장한 커스텀 문장 */
    val customTranslations = mutableStateListOf<CustomTranslation>()

    fun togglePhrase(id: Int) {
        if (savedPhraseIds.contains(id)) savedPhraseIds.remove(id)
        else savedPhraseIds.add(id)
    }

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
