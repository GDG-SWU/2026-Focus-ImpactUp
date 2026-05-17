package com.gdgswu.qos.ui.guide

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gdgswu.qos.ui.theme.QOSTheme

private val TutorialPurple      = Color(0xFF7C4DFF)
private val TutorialPurpleLight = Color(0xFFB39DDB)

private const val RIPPLE_DURATION = 1400   // ms for one full ripple cycle
private const val RING_DELAY      = 460    // ms offset between each ring

@Composable
fun TutorialOverlay(
    step: TutorialStep,
    stepIndex: Int,
    totalSteps: Int,
    onStepTapped: () -> Unit,
    onCancel: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sonar")

    // 3개의 동심원 — 각각 RING_DELAY ms 씩 시간차
    val ring1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(RIPPLE_DURATION, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(0)
        ),
        label = "ring1"
    )
    val ring2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(RIPPLE_DURATION, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(RING_DELAY)
        ),
        label = "ring2"
    )
    val ring3 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(RIPPLE_DURATION, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(RING_DELAY * 2)
        ),
        label = "ring3"
    )

    // 메인 원 깜빡임
    val circleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.80f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "circleAlpha"
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenW = maxWidth
        val screenH = maxHeight

        val circleSize    = 24.dp
        val circleRadius  = circleSize / 2          // 18.dp — 메인 원 반지름
        val maxRipple     = 90.dp                   // 최대 ripple 반지름

        // 목표 지점의 중심 좌표 (dp)
        val cx = screenW * step.targetXFraction
        val cy = screenH * step.targetYFraction

        // offset: Canvas는 좌상단 기준이므로 중심에서 maxRipple만큼 빼줌
        val canvasOffset = maxRipple
        val offsetX = cx - canvasOffset
        val offsetY = cy - canvasOffset
        val canvasSize = maxRipple * 2

        // ── 1. 다크 블로킹 오버레이 ──────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.32f))
                .pointerInput(Unit) { detectTapGestures { /* 모든 터치 차단 */ } }
        )

        // ── 2. 힌트 카드 (화면 중앙) ─────────────────────────────────────────
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 36.dp)
                .offset(y = (-60).dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Step ${stepIndex + 1} / $totalSteps",
                    fontSize = 11.sp,
                    color = TutorialPurple,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    step.hint,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
            }
        }

        // ── 3. Sonar 동심원 (Canvas) ─────────────────────────────────────────
        Canvas(
            modifier = Modifier
                .offset(x = offsetX, y = offsetY)
                .size(canvasSize)
        ) {
            val baseRadius = circleRadius.toPx()
            val maxRadius  = maxRipple.toPx()

            // 단계가 올라갈수록 링 개수 감소 (1단계=3개, 2단계=2개, 3단계=1개)
            val ringCount = (totalSteps - stepIndex).coerceIn(1, 3)
            listOf(ring1, ring2, ring3).take(ringCount).forEach { progress ->
                val ringRadius = baseRadius + (maxRadius - baseRadius) * progress
                // 바깥으로 갈수록 점점 투명해짐
                val ringAlpha  = (1f - progress) * 0.40f
                drawCircle(
                    color  = TutorialPurpleLight.copy(alpha = ringAlpha),
                    radius = ringRadius,
                    center = center
                )
            }
        }

        // ── 4. "Click!" 레이블 ────────────────────────────────────────────────
        Text(
            "Click!",
            color = TutorialPurple,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            modifier = Modifier.offset(
                x = cx - 18.dp,
                y = cy - circleRadius - 26.dp
            )
        )

        // ── 5. 메인 보라색 원 (터치 가능) ────────────────────────────────────
        Box(
            modifier = Modifier
                .offset(x = cx - circleRadius, y = cy - circleRadius)
                .size(circleSize)
                .graphicsLayer { alpha = circleAlpha }
                .clip(CircleShape)
                .background(TutorialPurple)
                .clickable { onStepTapped() },
        )

        // ── 6. 취소 버튼 (좌상단) ─────────────────────────────────────────────
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 8.dp, top = 4.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(TutorialPurple.copy(alpha = 0.85f))
                .clickable { onCancel() }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Cancel",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                "Cancel tutorial",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true, name = "Tutorial – Step 1 (Map tab)")
@Composable
fun TutorialOverlayStep1Preview() {
    QOSTheme {
        TutorialOverlay(
            step = TutorialStep("Open the map", 0.32f, 0.935f),
            stepIndex = 0, totalSteps = 3,
            onStepTapped = {}, onCancel = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Tutorial – Step 2 (Filter chip)")
@Composable
fun TutorialOverlayStep2Preview() {
    QOSTheme {
        TutorialOverlay(
            step = TutorialStep("Tap the shelter filter", 0.32f, 0.175f),
            stepIndex = 1, totalSteps = 3,
            onStepTapped = {}, onCancel = {}
        )
    }
}
