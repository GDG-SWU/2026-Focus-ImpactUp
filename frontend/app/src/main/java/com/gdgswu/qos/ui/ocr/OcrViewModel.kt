package com.gdgswu.qos.ui.ocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdgswu.qos.data.local.UserProfilePrefs
import com.gdgswu.qos.data.remote.ApiResult
import com.gdgswu.qos.data.remote.QosRepository
import com.gdgswu.qos.data.remote.TokenManager
import com.gdgswu.qos.data.remote.model.OcrScanResponse
import com.gdgswu.qos.data.remote.model.RiskCheckResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.Executors

sealed class OcrUiState {
    object Idle : OcrUiState()
    object Processing : OcrUiState()
    data class Result(val ocr: OcrScanResponse, val risk: RiskCheckResponse?) : OcrUiState()
    data class ScanError(val message: String) : OcrUiState()
}

class OcrViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<OcrUiState>(OcrUiState.Idle)
    val uiState: StateFlow<OcrUiState> = _uiState

    var imageCapture: ImageCapture? = null
    private val executor = Executors.newSingleThreadExecutor()

    fun captureAndScan(context: Context) {
        val capture = imageCapture
        if (capture == null) {
            _uiState.value = OcrUiState.ScanError("Camera not ready")
            return
        }
        _uiState.value = OcrUiState.Processing

        capture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val bytes = extractJpegBytes(image)
                image.close()
                viewModelScope.launch { processImage(context, bytes) }
            }

            override fun onError(exception: ImageCaptureException) {
                _uiState.value = OcrUiState.ScanError(exception.message ?: "Capture failed")
            }
        })
    }

    // ImageCapture는 JPEG 포맷으로 반환 → planes[0] 버퍼가 곧 JPEG 바이트
    private fun extractJpegBytes(image: ImageProxy): ByteArray {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return compressImage(bytes)
    }

    // 이미지를 최대 1024px, JPEG 80% 품질로 압축 (OCR에 충분한 해상도 유지)
    private fun compressImage(bytes: ByteArray): ByteArray {
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return bytes
        val maxSize = 1024
        val scale = minOf(maxSize.toFloat() / bitmap.width, maxSize.toFloat() / bitmap.height, 1f)
        val scaled = if (scale < 1f) {
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scale).toInt(),
                (bitmap.height * scale).toInt(),
                true
            )
        } else bitmap
        val out = java.io.ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 80, out)
        return out.toByteArray()
    }

    private suspend fun processImage(context: Context, bytes: ByteArray) {
        val repository = QosRepository(context)

        val requestBody = bytes.toRequestBody("image/jpeg".toMediaType())
        val imagePart = MultipartBody.Part.createFormData("image", "scan.jpg", requestBody)
        val langCode = UserProfilePrefs.loadLanguage(context).code
        val langBody = langCode.toRequestBody("text/plain".toMediaType())

        val ocrResult = withTimeoutOrNull(15_000L) { repository.scanImage(imagePart, langBody) }
            ?: run { _uiState.value = OcrUiState.ScanError("Scan timed out — try again"); return }

        when (ocrResult) {
            is ApiResult.Success -> {
                if (ocrResult.data.raw_text.isNullOrBlank()) {
                    val msg = if (ocrResult.data.offline)
                        "Scanner service unavailable — try again later"
                    else
                        "No text detected — adjust the frame and try again"
                    _uiState.value = OcrUiState.ScanError(msg)
                    return
                }
                val userId = TokenManager.getUserId(context) ?: "unknown"
                val risk = withTimeoutOrNull(8_000L) {
                    when (val r = repository.checkRisk(ocrResult.data.raw_text, userId)) {
                        is ApiResult.Success -> r.data
                        else -> null
                    }
                }
                _uiState.value = OcrUiState.Result(ocrResult.data, risk)
            }
            is ApiResult.Error -> _uiState.value = OcrUiState.ScanError(friendlyError(ocrResult.message))
            else -> {}
        }
    }

    fun reset() {
        _uiState.value = OcrUiState.Idle
    }

    private fun friendlyError(raw: String): String = when {
        raw.contains("timeout", ignoreCase = true) ||
        raw.contains("timed out", ignoreCase = true) -> "Scan timed out — try again"
        raw.contains("Unable to resolve host") ||
        raw.contains("Failed to connect") -> "No connection — check your network"
        raw.contains("404") -> "Scanner service unavailable"
        raw.contains("500") || raw.contains("502") || raw.contains("503") -> "Server error — try again later"
        else -> "Scan failed — tap shutter to retry"
    }

    override fun onCleared() {
        super.onCleared()
        executor.shutdown()
    }
}
