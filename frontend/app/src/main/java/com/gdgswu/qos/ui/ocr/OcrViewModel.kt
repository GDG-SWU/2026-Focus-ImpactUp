package com.gdgswu.qos.ui.ocr

import android.content.Context
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
        return bytes
    }

    private suspend fun processImage(context: Context, bytes: ByteArray) {
        val repository = QosRepository(context)

        val requestBody = bytes.toRequestBody("image/jpeg".toMediaType())
        val imagePart = MultipartBody.Part.createFormData("image", "scan.jpg", requestBody)
        val langCode = UserProfilePrefs.loadLanguage(context).code
        val langBody = langCode.toRequestBody("text/plain".toMediaType())

        when (val ocrResult = repository.scanImage(imagePart, langBody)) {
            is ApiResult.Success -> {
                val userId = TokenManager.getUserId(context) ?: "unknown"
                val risk = when (val r = repository.checkRisk(ocrResult.data.raw_text, userId)) {
                    is ApiResult.Success -> r.data
                    else -> null
                }
                _uiState.value = OcrUiState.Result(ocrResult.data, risk)
            }
            is ApiResult.Error -> _uiState.value = OcrUiState.ScanError(ocrResult.message)
            else -> {}
        }
    }

    fun reset() {
        _uiState.value = OcrUiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        executor.shutdown()
    }
}
