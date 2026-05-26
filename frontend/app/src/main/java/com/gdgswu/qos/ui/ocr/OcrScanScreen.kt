package com.gdgswu.qos.ui.ocr

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.os.Build
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.gdgswu.qos.data.remote.model.OcrScanResponse
import com.gdgswu.qos.data.remote.model.RiskCheckResponse
import com.gdgswu.qos.ui.theme.*

// 프레임 크기 상수
private val FRAME_W        = 260.dp
private val FRAME_H        = 260.dp
private val FRAME_CORNER   = 16.dp
private val FRAME_Y_OFFSET = 20.dp

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OcrScanScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: OcrViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)

    // 위험 감지 시 햅틱
    LaunchedEffect(uiState) {
        if (uiState is OcrUiState.Result) {
            val risk = (uiState as OcrUiState.Result).risk
            if (risk?.trigger_haptic == true) {
                triggerHaptic(context)
            }
        }
    }

    if (!cameraPermission.status.isGranted) {
        CameraPermissionScreen(
            shouldShowRationale = cameraPermission.status.shouldShowRationale,
            onRequest = { cameraPermission.launchPermissionRequest() },
            onBack = { navController.popBackStack() }
        )
        return
    }

    val scanState = when (val s = uiState) {
        is OcrUiState.Result -> if (s.risk?.risk_detected == true) OcrScanState.FOUND_DANGER else OcrScanState.FOUND_SAFE
        else -> OcrScanState.SCANNING
    }

    var flashOn      by remember { mutableStateOf(false) }
    var shutterPulse by remember { mutableStateOf(false) }

    val frameColor by animateColorAsState(
        targetValue = when (scanState) {
            OcrScanState.SCANNING     -> Color.White
            OcrScanState.FOUND_SAFE   -> Color(0xFF4CAF50)
            OcrScanState.FOUND_DANGER -> Color(0xFFE53935)
        },
        animationSpec = tween(400), label = "frame"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // ── 카메라 프리뷰 ───────────────────────────────────────────────────
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            flashOn = flashOn,
            onImageCaptureReady = { capture -> viewModel.imageCapture = capture }
        )

        // ── 딤 오버레이 ──────────────────────────────────────────────────────
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        ) {
            drawRect(Color.Black.copy(alpha = 0.60f))
            val fw = FRAME_W.toPx(); val fh = FRAME_H.toPx()
            val fx = (size.width - fw) / 2f
            val fy = (size.height - fh) / 2f - FRAME_Y_OFFSET.toPx()
            drawRoundRect(
                color = Color.Black, topLeft = Offset(fx, fy),
                size = Size(fw, fh), cornerRadius = CornerRadius(FRAME_CORNER.toPx()),
                blendMode = BlendMode.Clear
            )
        }

        // ── 상태별 엣지 그라디언트 ────────────────────────────────────────────
        if (scanState != OcrScanState.SCANNING) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val c = if (scanState == OcrScanState.FOUND_SAFE) Color(0xFF4CAF50) else Color(0xFFE53935)
                val ew = size.width * 0.05f; val eh = size.height * 0.05f
                listOf(
                    Brush.horizontalGradient(listOf(c.copy(0.4f), Color.Transparent), 0f, ew),
                    Brush.horizontalGradient(listOf(Color.Transparent, c.copy(0.4f)), size.width - ew, size.width),
                    Brush.verticalGradient(listOf(c.copy(0.4f), Color.Transparent), 0f, eh),
                    Brush.verticalGradient(listOf(Color.Transparent, c.copy(0.4f)), size.height - eh, size.height)
                ).forEach { drawRect(brush = it, size = size) }
            }
        }

        // ── 결과 팝업 카드 ────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = uiState is OcrUiState.Result,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = -(FRAME_H / 2 + FRAME_Y_OFFSET + 28.dp + 60.dp)),
            enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { -it / 2 },
            exit  = fadeOut(tween(200)) + slideOutVertically(tween(200)) { -it / 2 }
        ) {
            if (uiState is OcrUiState.Result) {
                val r = uiState as OcrUiState.Result
                ScanResultCard(
                    ocr = r.ocr,
                    risk = r.risk,
                    scanState = scanState,
                    onDismiss = { viewModel.reset() }
                )
            }
        }

        // ── 처리 중 인디케이터 ────────────────────────────────────────────────
        AnimatedVisibility(
            visible = uiState is OcrUiState.Processing,
            modifier = Modifier.align(Alignment.Center).offset(y = -(FRAME_H / 2 + FRAME_Y_OFFSET + 16.dp))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                Text("Analyzing...", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }

        // ── 스캔 중 텍스트 ────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = uiState is OcrUiState.Idle,
            modifier = Modifier.align(Alignment.Center).offset(y = -(FRAME_H / 2 + FRAME_Y_OFFSET + 16.dp))
        ) {
            Text("Place text inside the frame", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }

        // ── 에러 스낵바 ───────────────────────────────────────────────────────
        if (uiState is OcrUiState.ScanError) {
            Box(modifier = Modifier.align(Alignment.Center).offset(y = -(FRAME_H / 2 + FRAME_Y_OFFSET + 16.dp))) {
                Text(
                    (uiState as OcrUiState.ScanError).message,
                    color = Color(0xFFFF5252), fontSize = 13.sp, textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(Color.Black.copy(0.7f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // ── 프레임 ────────────────────────────────────────────────────────────
        Box(modifier = Modifier.align(Alignment.Center).offset(y = -FRAME_Y_OFFSET), contentAlignment = Alignment.Center) {
            CornerBracketFrame(width = FRAME_W, height = FRAME_H, color = frameColor, strokeWidth = 4.dp, cornerLength = 36.dp)
            if (scanState == OcrScanState.SCANNING) {
                ScanLine(width = FRAME_W - 8.dp, height = FRAME_H - 8.dp)
            }
        }

        // ── 상단 바 ──────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
            }
            Text(
                text = "Scan medicine label",
                color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f), textAlign = TextAlign.Center
            )
            IconButton(onClick = { flashOn = !flashOn }) {
                Icon(
                    imageVector = if (flashOn) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                    contentDescription = "Flash",
                    tint = if (flashOn) Color.Yellow else Color.White
                )
            }
        }

        // ── 하단 컨트롤 ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val shutterScale by animateFloatAsState(
                targetValue = if (shutterPulse) 0.88f else 1f,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "shutter",
                finishedListener = { shutterPulse = false }
            )
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .graphicsLayer { scaleX = shutterScale; scaleY = shutterScale }
                    .border(3.dp, Color.White, CircleShape)
                    .clip(CircleShape)
                    .clickable(enabled = uiState is OcrUiState.Idle || uiState is OcrUiState.ScanError) {
                        shutterPulse = true
                        viewModel.captureAndScan(context)
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = if (shutterPulse) 0.6f else 0.25f))
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (uiState is OcrUiState.Result) {
                Text(
                    "Tap anywhere to scan again",
                    color = Color.White.copy(0.7f), fontSize = 12.sp,
                    modifier = Modifier.clickable { viewModel.reset() }
                )
            } else {
                Text("Tap shutter to scan", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

// ── 카메라 프리뷰 ──────────────────────────────────────────────────────────────
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    flashOn: Boolean = false,
    onImageCaptureReady: (ImageCapture) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var cameraRef by remember { mutableStateOf<Camera?>(null) }

    // flashOn 상태가 바뀌거나 카메라가 바인딩될 때 torch 제어
    LaunchedEffect(flashOn, cameraRef) {
        cameraRef?.cameraControl?.enableTorch(flashOn)
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build()
                onImageCaptureReady(imageCapture)
                cameraProvider.unbindAll()
                cameraRef = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        }
    )
}

// ── 결과 팝업 카드 ─────────────────────────────────────────────────────────────
@Composable
fun ScanResultCard(
    ocr: OcrScanResponse,
    risk: RiskCheckResponse?,
    scanState: OcrScanState,
    onDismiss: () -> Unit
) {
    val isSafe = scanState == OcrScanState.FOUND_SAFE
    val accentColor = if (isSafe) Color(0xFF4CAF50) else Color(0xFFE53935)
    val bgColor     = if (isSafe) Color(0xFFF0FFF4) else Color(0xFFFFF3F0)
    val statusText  = if (isSafe) "Safe" else "Danger"
    val statusIcon  = if (isSafe) Icons.Filled.CheckCircle else Icons.Filled.Cancel

    // 약 이름: OCR 텍스트 첫 줄 또는 전체 (짧으면)
    val medName = ocr.raw_text?.lines()?.firstOrNull()?.trim() ?: ""

    // 설명 한 줄: 위험이면 경고 메시지, 안전이면 안내
    val description = if (!isSafe) {
        risk?.matched_risks?.firstOrNull()?.warning_message
            ?: "Risk detected. Check with a medical professional."
    } else {
        "This medication is not in your allergy profile.\nVerified for dosage and expiration."
    }

    Card(
        modifier  = Modifier.width(300.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = bgColor),
        border    = androidx.compose.foundation.BorderStroke(1.5.dp, accentColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                statusIcon,
                contentDescription = statusText,
                tint = accentColor,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (medName.isNotBlank()) {
                    Text(medName, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                }
                Text(statusText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = accentColor)
                Spacer(modifier = Modifier.height(6.dp))
                Text(description, fontSize = 12.sp, color = Color(0xFF666666), lineHeight = 17.sp)
            }
            IconButton(onClick = onDismiss, modifier = Modifier.size(20.dp)) {
                Icon(Icons.Filled.Close, contentDescription = "Dismiss", tint = Color(0xFFAAAAAA), modifier = Modifier.size(14.dp))
            }
        }
    }
}

// ── 카메라 권한 화면 ───────────────────────────────────────────────────────────
@Composable
fun CameraPermissionScreen(
    shouldShowRationale: Boolean,
    onRequest: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart).statusBarsPadding().padding(8.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Back", tint = Color.White)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(64.dp))
            Text(
                if (shouldShowRationale) "Camera access is needed to scan medicine labels."
                else "Please allow camera access to use the label scanner.",
                color = Color.White, fontSize = 15.sp, textAlign = TextAlign.Center, lineHeight = 22.sp
            )
            Button(
                onClick = onRequest,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Allow Camera", color = Color.Black, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ── 햅틱 ────────────────────────────────────────────────────────────────────
private fun triggerHaptic(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v?.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            v?.vibrate(400)
        }
    }
}

// ── 스캔 상태 ────────────────────────────────────────────────────────────────
enum class OcrScanState { SCANNING, FOUND_SAFE, FOUND_DANGER }

// ── 스캔 라인 애니메이션 ────────────────────────────────────────────────────────
@Composable
fun ScanLine(width: Dp, height: Dp) {
    val transition = rememberInfiniteTransition(label = "scan")
    val yFraction by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing), RepeatMode.Reverse),
        label = "scanY"
    )
    Canvas(modifier = Modifier.size(width, height)) {
        val y = yFraction * size.height
        drawLine(Color(0xFF29B6F6), Offset(0f, y), Offset(size.width, y), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
    }
}

// ── 4 모서리 꺾쇠 브라켓 ───────────────────────────────────────────────────────
@Composable
fun CornerBracketFrame(width: Dp, height: Dp, color: Color, strokeWidth: Dp, cornerLength: Dp) {
    Canvas(modifier = Modifier.size(width, height)) {
        val sw = strokeWidth.toPx(); val cl = cornerLength.toPx()
        val r = 16.dp.toPx(); val w = size.width; val h = size.height
        val paint = Paint().apply {
            this.color = color; this.strokeWidth = sw
            this.style = PaintingStyle.Stroke; this.strokeCap = StrokeCap.Round
            this.strokeJoin = StrokeJoin.Round; this.isAntiAlias = true
        }
        fun corner(path: Path) = drawContext.canvas.drawPath(path, paint)
        corner(Path().apply {
            moveTo(0f, cl); lineTo(0f, r)
            arcTo(androidx.compose.ui.geometry.Rect(0f, 0f, r * 2, r * 2), 180f, 90f, false)
            lineTo(cl, 0f)
        })
        corner(Path().apply {
            moveTo(w - cl, 0f); lineTo(w - r, 0f)
            arcTo(androidx.compose.ui.geometry.Rect(w - r * 2, 0f, w, r * 2), 270f, 90f, false)
            lineTo(w, cl)
        })
        corner(Path().apply {
            moveTo(cl, h); lineTo(r, h)
            arcTo(androidx.compose.ui.geometry.Rect(0f, h - r * 2, r * 2, h), 90f, 90f, false)
            lineTo(0f, h - cl)
        })
        corner(Path().apply {
            moveTo(w, h - cl); lineTo(w, h - r)
            arcTo(androidx.compose.ui.geometry.Rect(w - r * 2, h - r * 2, w, h), 0f, 90f, false)
            lineTo(w - cl, h)
        })
    }
}
