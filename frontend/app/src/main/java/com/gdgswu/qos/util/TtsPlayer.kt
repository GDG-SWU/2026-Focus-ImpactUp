package com.gdgswu.qos.util

import android.content.Context
import android.media.MediaPlayer
import com.gdgswu.qos.data.remote.ApiResult
import com.gdgswu.qos.data.remote.QosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 백엔드 TTS API를 호출해 audio/mpeg 바이너리를 캐시 파일로 저장한 뒤
 * MediaPlayer로 재생합니다.
 * Composable에서 remember { TtsPlayer(context) } 로 생성하고
 * DisposableEffect로 release() 호출하세요.
 */
class TtsPlayer(private val context: Context) {

    private val repository = QosRepository(context)
    private var mediaPlayer: MediaPlayer? = null

    /** text를 languageCode 언어로 읽어줍니다. 실패 시 조용히 무시합니다. */
    suspend fun play(text: String, languageCode: String) {
        val result = repository.getTtsBytes(text = text, language = languageCode)
        if (result !is ApiResult.Success) return

        // 파일 쓰기는 IO 스레드에서
        val tempFile = withContext(Dispatchers.IO) {
            File(context.cacheDir, "tts_audio.mp3").also { it.writeBytes(result.data) }
        }

        // MediaPlayer는 Main 스레드에서
        withContext(Dispatchers.Main) {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(tempFile.absolutePath)
                    setOnCompletionListener { it.release() }
                    prepare()
                    start()
                }
            } catch (e: Exception) {
                // 재생 실패 시 조용히 무시
                mediaPlayer = null
            }
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
