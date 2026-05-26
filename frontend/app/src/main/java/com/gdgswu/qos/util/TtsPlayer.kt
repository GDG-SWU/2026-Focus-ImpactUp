package com.gdgswu.qos.util

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

/**
 * 기기 내장 TextToSpeech로 텍스트를 읽어줍니다.
 * Composable에서 remember { TtsPlayer(context) }로 생성하고
 * DisposableEffect로 release() 호출하세요.
 */
class TtsPlayer(private val context: Context) {

    private var ttsEngine: TextToSpeech? = null
    private var ttsReady = false
    private var mediaPlayer: MediaPlayer? = null

    init {
        ttsEngine = TextToSpeech(context) { status ->
            ttsReady = status == TextToSpeech.SUCCESS
        }
    }

    /**
     * text를 languageCode 언어로 읽어줍니다.
     * TTS 재생이 끝날 때까지 suspend됩니다 (isPlaying 상태 추적 가능).
     */
    suspend fun play(text: String, languageCode: String) {
        val locale = langCodeToLocale(languageCode) ?: Locale.ENGLISH

        withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { cont ->
                val engine = ttsEngine
                if (engine == null || !ttsReady) {
                    cont.resume(Unit)
                    return@suspendCancellableCoroutine
                }

                val langResult = engine.setLanguage(locale)
                if (langResult == TextToSpeech.LANG_MISSING_DATA ||
                    langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    engine.setLanguage(Locale.ENGLISH)
                }

                engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) {
                        if (cont.isActive) cont.resume(Unit)
                    }
                    override fun onError(utteranceId: String?) {
                        if (cont.isActive) cont.resume(Unit)
                    }
                })

                val speakResult = engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "qos_tts")
                if (speakResult != TextToSpeech.SUCCESS) {
                    if (cont.isActive) cont.resume(Unit)
                }

                cont.invokeOnCancellation { engine.stop() }
            }
        }
    }

    fun release() {
        ttsEngine?.stop()
        ttsEngine?.shutdown()
        ttsEngine = null
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun langCodeToLocale(code: String): Locale? = when (code.lowercase()) {
        "ar" -> Locale("ar")
        "fr" -> Locale.FRENCH
        "wo" -> Locale("wo")
        "ma" -> Locale("man")
        "fu" -> Locale("ff")
        else -> null
    }
}
