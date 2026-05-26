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
import com.gdgswu.qos.data.remote.model.MatchedRisk
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

    // ImageCapture는 JPEG 포맷으로 반환 + 회전 보정 (CameraX 센서 방향 → 실제 표시 방향)
    private fun extractJpegBytes(image: ImageProxy): ByteArray {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        val rotation = image.imageInfo.rotationDegrees
        return compressImage(bytes, rotation)
    }

    // 이미지를 최대 1280px, JPEG 90% 품질로 압축 + 회전 보정 (Vision API 인식률 향상)
    private fun compressImage(bytes: ByteArray, rotationDegrees: Int = 0): ByteArray {
        val maxSize = 1280

        // 1단계: 이미지 크기만 읽어서 OOM 없이 적정 sampleSize 계산
        val boundsOpts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, boundsOpts)
        var sampleSize = 1
        var w = boundsOpts.outWidth; var h = boundsOpts.outHeight
        while (w / (sampleSize * 2) >= maxSize || h / (sampleSize * 2) >= maxSize) {
            sampleSize *= 2
        }

        // 2단계: sampleSize 적용해서 메모리 안전하게 디코딩
        val decodeOpts = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, decodeOpts) ?: return bytes

        // CameraX 센서 회전 보정 (0/90/180/270도)
        if (rotationDegrees != 0) {
            val matrix = android.graphics.Matrix().apply { postRotate(rotationDegrees.toFloat()) }
            val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            bitmap = rotated
        }

        // 3단계: 최종 크기 조정 (sampleSize로도 여전히 크면 스케일 다운)
        val scale = minOf(maxSize.toFloat() / bitmap.width, maxSize.toFloat() / bitmap.height, 1f)
        val final = if (scale < 1f) {
            val scaled = Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scale).toInt(),
                (bitmap.height * scale).toInt(),
                true
            )
            bitmap.recycle()
            scaled
        } else bitmap

        val out = java.io.ByteArrayOutputStream()
        final.compress(Bitmap.CompressFormat.JPEG, 90, out)
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
                val backendRisk = withTimeoutOrNull(8_000L) {
                    when (val r = repository.checkRisk(ocrResult.data.raw_text, userId)) {
                        is ApiResult.Success -> r.data
                        else -> null
                    }
                }
                // 백엔드 risk가 없거나 위험 미감지 시 로컬 알러지/질환으로 직접 체크
                val risk = if (backendRisk?.risk_detected == true) backendRisk
                           else localRiskCheck(context, ocrResult.data.raw_text.orEmpty()) ?: backendRisk
                _uiState.value = OcrUiState.Result(ocrResult.data, risk)
            }
            is ApiResult.Error -> _uiState.value = OcrUiState.ScanError(friendlyError(ocrResult.message))
            else -> {}
        }
    }

    fun reset() {
        _uiState.value = OcrUiState.Idle
    }

    // 로컬 SharedPreferences 알러지/질환으로 OCR 텍스트 직접 위험 체크
    private fun localRiskCheck(context: Context, ocrText: String): RiskCheckResponse? {
        val upper = ocrText.uppercase()
        val allergies  = UserProfilePrefs.loadAllergies(context)
        val conditions = UserProfilePrefs.loadConditions(context)
        val matched = mutableListOf<MatchedRisk>()

        for (allergy in allergies) {
            if (upper.contains(allergy.uppercase())) {
                matched.add(MatchedRisk(
                    keyword = allergy,
                    matched_profile_field = "allergy",
                    matched_value = allergy,
                    warning_message = "You have a $allergy allergy. Do not take this medication."
                ))
            }
        }
        for (condition in conditions) {
            if (upper.contains(condition.uppercase())) {
                matched.add(MatchedRisk(
                    keyword = condition,
                    matched_profile_field = "condition",
                    matched_value = condition,
                    warning_message = "$condition-related risk detected. Use with caution."
                ))
            }
        }
        if (matched.isEmpty()) return null
        return RiskCheckResponse(
            risk_detected   = true,
            risk_level      = "critical",
            matched_risks   = matched,
            trigger_haptic  = true,
            trigger_alert_banner = true,
            offline         = true
        )
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
