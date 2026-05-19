package com.gdgswu.qos.ui.ocr

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gdgswu.qos.ui.theme.*
import com.gdgswu.qos.ui.theme.QOSTheme

// 프레임 크기 상수 (딤 오버레이와 공유)
private val FRAME_W        = 260.dp
private val FRAME_H        = 260.dp   // 정사각형
private val FRAME_CORNER   = 16.dp
private val FRAME_Y_OFFSET = 20.dp   // 화면 중심에서 위로 띄우는 정도 (작을수록 아래)
private val BOTTOM_CTRL    = 140.dp  // 하단 컨트롤 영역 높이 (하단 패딩 계산용)

enum class OcrScanState { SCANNING, FOUND_SAFE, FOUND_DANGER }

@Composable
fun OcrScanScreen(navController: NavController) {
    var scanState    by remember { mutableStateOf(OcrScanState.SCANNING) }
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

        // ── 카메라 프리뷰 플레이스홀더 ───────────────────────────────────────
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1A1A1A)))

        // ── 딤 오버레이: 프레임 영역만 투명하게 뚫음 ─────────────────────────
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        ) {
            // 전체를 어둡게
            drawRect(Color.Black.copy(alpha = 0.60f))

            // 프레임 위치와 동일한 구멍 뚫기 (BlendMode.Clear)
            val fw = FRAME_W.toPx()
            val fh = FRAME_H.toPx()
            val fx = (size.width - fw) / 2f
            // 프레임 중심 = 화면 중심 - FRAME_Y_OFFSET
            val fy = (size.height - fh) / 2f - FRAME_Y_OFFSET.toPx()
            drawRoundRect(
                color        = Color.Black,
                topLeft      = Offset(fx, fy),
                size         = Size(fw, fh),
                cornerRadius = CornerRadius(FRAME_CORNER.toPx()),
                blendMode    = BlendMode.Clear
            )
        }

        // ── 상태별 엣지 그라디언트 (SAFE/DANGER) ─────────────────────────────
        if (scanState != OcrScanState.SCANNING) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val c = if (scanState == OcrScanState.FOUND_SAFE)
                    Color(0xFF4CAF50) else Color(0xFFE53935)
                val ew = size.width  * 0.05f
                val eh = size.height * 0.05f
                listOf(
                    Brush.horizontalGradient(listOf(c.copy(0.4f), Color.Transparent), 0f, ew),
                    Brush.horizontalGradient(listOf(Color.Transparent, c.copy(0.4f)), size.width - ew, size.width),
                    Brush.verticalGradient(listOf(c.copy(0.4f), Color.Transparent), 0f, eh),
                    Brush.verticalGradient(listOf(Color.Transparent, c.copy(0.4f)), size.height - eh, size.height)
                ).forEach { drawRect(brush = it, size = size) }
            }
        }

        // ── 팝업 카드: 프레임 바로 위에 고정 배치 ────────────────────────────
        AnimatedVisibility(
            visible = scanState != OcrScanState.SCANNING,
            modifier = Modifier
                .align(Alignment.Center)
                // 프레임 위: 프레임 상단 - gap(16dp) - 카드(≈60dp half-height)
                .offset(y = -(FRAME_H / 2 + FRAME_Y_OFFSET + 28.dp + 60.dp)),
            enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { -it / 2 },
            exit  = fadeOut(tween(200)) + slideOutVertically(tween(200)) { -it / 2 }
        ) {
            ScanResultCard(scanState = scanState)
        }

        // ── 스캔 중 텍스트: 프레임 바로 위 ──────────────────────────────────
        AnimatedVisibility(
            visible = scanState == OcrScanState.SCANNING,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = -(FRAME_H / 2 + FRAME_Y_OFFSET + 16.dp))
        ) {
            Text(
                "Detecting text...",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // ── 프레임 (딤 오버레이 구멍과 같은 위치) ────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = -FRAME_Y_OFFSET),
            contentAlignment = Alignment.Center
        ) {
            CornerBracketFrame(
                width = FRAME_W, height = FRAME_H,
                color = frameColor, strokeWidth = 4.dp, cornerLength = 36.dp
            )
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
                text = "Place text inside the frame",
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
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 셔터 버튼
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
                    .clickable { shutterPulse = true },
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
            Text("Auto scan is on", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

// ── 결과 팝업 카드 ─────────────────────────────────────────────────────────────
@Composable
fun ScanResultCard(scanState: OcrScanState) {
    val isSafe      = scanState == OcrScanState.FOUND_SAFE
    val accentColor = if (isSafe) Color(0xFF4CAF50) else Color(0xFFE53935)
    val statusText  = if (isSafe) "Safe to use" else "Danger"
    val statusIcon  = if (isSafe) Icons.Filled.CheckCircle else Icons.Filled.Cancel

    Card(
        modifier  = Modifier.width(300.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 아이콘 + 약품명/상태 (같은 행)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(statusIcon, contentDescription = statusText,
                    tint = accentColor, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Amoxicillin", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    Text(statusText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = accentColor)
                }
            }
            // 설명은 전체 너비 (아이콘 영역 침범 안 함)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "This medication is not in your allergy profile.\nVerified for dosage and expiration.",
                fontSize = 12.sp, color = Color(0xFF888888), lineHeight = 17.sp
            )
        }
    }
}

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
        drawLine(Color(0xFF29B6F6), Offset(0f, y), Offset(size.width, y),
            strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
    }
}

// ── 4 모서리 꺾쇠 브라켓 (둥근 모서리) ────────────────────────────────────────
@Composable
fun CornerBracketFrame(width: Dp, height: Dp, color: Color, strokeWidth: Dp, cornerLength: Dp) {
    Canvas(modifier = Modifier.size(width, height)) {
        val sw = strokeWidth.toPx()
        val cl = cornerLength.toPx()
        val r  = 16.dp.toPx()
        val w  = size.width
        val h  = size.height
        val paint = Paint().apply {
            this.color       = color
            this.strokeWidth = sw
            this.style       = PaintingStyle.Stroke
            this.strokeCap   = StrokeCap.Round
            this.strokeJoin  = StrokeJoin.Round
            this.isAntiAlias = true
        }
        fun corner(path: Path) = drawContext.canvas.drawPath(path, paint)

        corner(Path().apply {   // 상단 왼쪽
            moveTo(0f, cl); lineTo(0f, r)
            arcTo(androidx.compose.ui.geometry.Rect(0f, 0f, r*2, r*2), 180f, 90f, false)
            lineTo(cl, 0f)
        })
        corner(Path().apply {   // 상단 오른쪽
            moveTo(w-cl, 0f); lineTo(w-r, 0f)
            arcTo(androidx.compose.ui.geometry.Rect(w-r*2, 0f, w, r*2), 270f, 90f, false)
            lineTo(w, cl)
        })
        corner(Path().apply {   // 하단 왼쪽
            moveTo(cl, h); lineTo(r, h)
            arcTo(androidx.compose.ui.geometry.Rect(0f, h-r*2, r*2, h), 90f, 90f, false)
            lineTo(0f, h-cl)
        })
        corner(Path().apply {   // 하단 오른쪽
            moveTo(w, h-cl); lineTo(w, h-r)
            arcTo(androidx.compose.ui.geometry.Rect(w-r*2, h-r*2, w, h), 0f, 90f, false)
            lineTo(w-cl, h)
        })
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true, name = "OCR – Scanning")
@Composable
fun OcrScanningPreview() {
    QOSTheme { OcrScanScreen(navController = rememberNavController()) }
}

@Preview(showBackground = true, name = "Scan Result – Safe")
@Composable
fun ScanResultSafePreview() {
    QOSTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ScanResultCard(scanState = OcrScanState.FOUND_SAFE)
        }
    }
}

@Preview(showBackground = true, name = "Scan Result – Danger")
@Composable
fun ScanResultDangerPreview() {
    QOSTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ScanResultCard(scanState = OcrScanState.FOUND_DANGER)
        }
    }
}
