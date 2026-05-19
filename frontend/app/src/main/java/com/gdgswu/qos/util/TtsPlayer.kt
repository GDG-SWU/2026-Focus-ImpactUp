package com.gdgswu.qos.util

import android.content.Context
import android.media.MediaPlayer
import com.gdgswu.qos.data.remote.ApiResult
import com.gdgswu.qos.data.remote.QosRepository

/**
 * 백엔드 TTS API를 호출해 audio_url을 받아 MediaPlayer로 재생합니다.
 * Composable에서 remember { TtsPlayer(context) } 로 생성하고
 * DisposableEffect로 release() 호출하세요.
 */
class TtsPlayer(context: Context) {

    private val repository = QosRepository(context)
    private var mediaPlayer: MediaPlayer? = null

    /** text를 languageCode 언어로 읽어줍니다. 실패 시 조용히 무시합니다. */
    suspend fun play(text: String, languageCode: String) {
        val result = repository.getTts(text = text, language = languageCode)
        if (result is ApiResult.Success) {
            val audioUrl = result.data.audio_url
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioUrl)
                setOnPreparedListener { start() }
                setOnCompletionListener { release() }
                prepareAsync()
            }
        }
        // ApiResult.Error 는 조용히 무시 (TTS 실패가 앱을 멈추면 안 됨)
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
