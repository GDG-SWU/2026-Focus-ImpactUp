package com.gdgswu.qos.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import com.gdgswu.qos.data.remote.ApiResult
import com.gdgswu.qos.data.remote.QosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale
import kotlin.coroutines.resume

/**
 * 백엔드 TTS API를 호출해 audio/mpeg 바이너리를 재생합니다.
 * 백엔드 실패 시 Android 기기 내장 TextToSpeech로 자동 fallback합니다.
 */
class TtsPlayer(private val context: Context) {

    private val repository = QosRepository(context)
    private var mediaPlayer: MediaPlayer? = null

    /** text를 languageCode 언어로 읽어줍니다. Android 기기 TTS를 사용합니다. */
    suspend fun play(text: String, languageCode: String) {
        // 백엔드 GCP TTS 미설정 → Android 내장 TTS 직접 사용
        tryAndroidTts(text, languageCode)
    }

    // ── 백엔드 GCP TTS ──────────────────────────────────────────────────────────

    private suspend fun tryBackendTts(text: String, languageCode: String): Boolean {
        val result = repository.getTtsBytes(text = text, language = languageCode)
        if (result !is ApiResult.Success) {
            println("[TtsPlayer] 백엔드 TTS 실패, Android TTS로 전환")
            return false
        }

        val tempFile = withContext(Dispatchers.IO) {
            File(context.cacheDir, "tts_audio.mp3").also { it.writeBytes(result.data) }
        }

        return withContext(Dispatchers.Main) {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(tempFile.absolutePath)
                    setOnCompletionListener { it.release() }
                    prepare()
                    start()
                }
                true
            } catch (e: Exception) {
                println("[TtsPlayer] MediaPlayer 재생 실패: ${e.message}")
                mediaPlayer = null
                false
            }
        }
    }

    // ── Android 내장 TextToSpeech fallback ────────────────────────────────────

    private suspend fun tryAndroidTts(text: String, languageCode: String) {
        val locale = langCodeToLocale(languageCode) ?: Locale.ENGLISH

        suspendCancellableCoroutine<Unit> { cont ->
            var tts: TextToSpeech? = null
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val setResult = tts?.setLanguage(locale)
                    if (setResult == TextToSpeech.LANG_MISSING_DATA ||
                        setResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // 언어 미지원 시 영어로 fallback
                        tts?.setLanguage(Locale.ENGLISH)
                    }
                    tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {}
                        override fun onDone(utteranceId: String?) {
                            tts?.stop()
                            tts?.shutdown()
                            if (cont.isActive) cont.resume(Unit)
                        }
                        override fun onError(utteranceId: String?) {
                            tts?.stop()
                            tts?.shutdown()
                            if (cont.isActive) cont.resume(Unit)
                        }
                    })
                    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "qos_tts")
                    println("[TtsPlayer] Android TTS 재생 시작: $locale")
                } else {
                    println("[TtsPlayer] Android TTS 초기화 실패")
                    if (cont.isActive) cont.resume(Unit)
                }
            }

            cont.invokeOnCancellation {
                tts?.stop()
                tts?.shutdown()
            }
        }
    }

    /** 내부 언어 코드 → Android Locale 변환 */
    private fun langCodeToLocale(code: String): Locale? = when (code.lowercase()) {
        "ar" -> Locale("ar")           // 아랍어
        "fr" -> Locale.FRENCH          // 프랑스어
        "wo" -> Locale("wo")           // 월로프어 (기기 미지원 시 영어 fallback)
        "ma" -> Locale("man")          // 만딩카어 (기기 미지원 시 영어 fallback)
        "fu" -> Locale("ff")           // 풀라니어 (기기 미지원 시 영어 fallback)
        else -> null
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
