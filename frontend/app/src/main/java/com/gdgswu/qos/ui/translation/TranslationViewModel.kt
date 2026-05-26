package com.gdgswu.qos.ui.translation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gdgswu.qos.data.model.Phrase
import com.gdgswu.qos.data.model.PhraseCategory
import com.gdgswu.qos.data.model.SupportedLanguage
import com.gdgswu.qos.data.model.samplePhrases
import com.gdgswu.qos.data.remote.ApiResult
import com.gdgswu.qos.data.remote.QosRepository
import com.gdgswu.qos.data.remote.model.PhraseCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class TranslationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QosRepository(application.applicationContext)

    /** API에서 받은 카드 (있으면 samplePhrases 앞에 표시) */
    private val _apiPhrases = MutableStateFlow<List<Phrase>>(emptyList())
    val apiPhrases: StateFlow<List<Phrase>> = _apiPhrases

    /** 최근 사용 카드 */
    private val _recentCards = MutableStateFlow<List<PhraseCard>>(emptyList())
    val recentCards: StateFlow<List<PhraseCard>> = _recentCards

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /** 번역 결과 */
    private val _translatedText = MutableStateFlow<String?>(null)
    val translatedText: StateFlow<String?> = _translatedText

    /** 번역 중 여부 */
    private val _isTranslating = MutableStateFlow(false)
    val isTranslating: StateFlow<Boolean> = _isTranslating

    /** 번역 오류 메시지 (미지원 언어 등) */
    private val _translateError = MutableStateFlow<String?>(null)
    val translateError: StateFlow<String?> = _translateError

    init {
        loadPhrasebook()
        loadRecentCards()
    }

    fun loadPhrasebook(category: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = withTimeoutOrNull(8_000L) { repository.getPhrasebook(category) }
            if (result is ApiResult.Success && result.data.cards.isNotEmpty()) {
                _apiPhrases.value = result.data.cards.mapIndexedNotNull { idx, card ->
                    card.toPhrase(startId = 1000 + idx)
                }
            }
            _isLoading.value = false
        }
    }

    fun translate(text: String, language: SupportedLanguage) {
        viewModelScope.launch {
            _isTranslating.value = true
            _translatedText.value = null
            _translateError.value = null
            val result = withTimeoutOrNull(10_000L) {
                repository.translate(text, language.code)
            }
            when (result) {
                is ApiResult.Success -> {
                    if (result.data.success) {
                        _translatedText.value = result.data.translated_text
                    } else {
                        _translateError.value = "${language.displayName} translation is not yet supported"
                    }
                }
                else -> _translateError.value = "Translation failed. Check your connection."
            }
            _isTranslating.value = false
        }
    }

    fun clearTranslation() {
        _translatedText.value = null
        _translateError.value = null
    }

    fun loadRecentCards() {
        viewModelScope.launch {
            val result = withTimeoutOrNull(8_000L) { repository.getRecentCards() }
            if (result is ApiResult.Success) {
                _recentCards.value = result.data.cards
            }
        }
    }
}

// ── PhraseCard → Phrase 변환 ──────────────────────────────────────────────────

/**
 * API PhraseCard를 로컬 Phrase 모델로 변환합니다.
 * category는 card ID prefix로 추론합니다.
 */
fun PhraseCard.toPhrase(startId: Int): Phrase? {
    // situation이 빈 경우 건너뜀
    if (situation.isBlank()) return null
    val category = inferCategory(id)
    return Phrase(
        id       = startId,
        category = category,
        english  = situation,
        french   = translations["fr"] ?: situation,
        arabic   = translations["ar"] ?: situation,
        wolof    = translations["wo"] ?: situation,
        mandinka = translations["ma"] ?: situation,
        fula     = translations["fu"] ?: situation
    )
}

private fun inferCategory(cardId: String): PhraseCategory = when {
    cardId.contains("medical", ignoreCase = true) ||
    cardId.contains("health",  ignoreCase = true)  -> PhraseCategory.MEDICAL
    cardId.contains("shelter", ignoreCase = true) ||
    cardId.contains("camp",    ignoreCase = true)  -> PhraseCategory.SHELTER
    cardId.contains("food",    ignoreCase = true) ||
    cardId.contains("water",   ignoreCase = true)  -> PhraseCategory.FOOD
    else                                            -> PhraseCategory.EMERGENCY
}

/** PhraseCard에서 선택 언어 번역 텍스트를 가져옵니다 */
fun PhraseCard.getTranslation(language: SupportedLanguage): String =
    translations[language.code] ?: situation
