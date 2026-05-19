package com.gdgswu.qos.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.atan2
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gdgswu.qos.ui.theme.*

enum class GazeDirection { CENTER, LEFT, RIGHT }

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route

    var menuExpanded by remember { mutableStateOf(false) }
    var gazeDirection by remember { mutableStateOf(GazeDirection.CENTER) }

    // 전체를 담는 Box: 네비바(72dp) + 캐릭터가 올라올 공간(32dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // ── 네비바 배경 (캐릭터 자리 오목 처리) ──────────────────────────────
        // Surface의 shadowElevation은 Outline.Generic에서 rect로 fallback → notch가 회색으로 덮임
        // shadow() + clip() + background() 조합으로 대체
        // ⚠️ notchDepth < notchHalfWidth 이어야 오목(concave) 형태가 됨
        // hw > 캐릭터반지름(28dp) 이어야 옆 곡선이 눈에 보임
        val notchedShape = navBarShape(notchHalfWidth = 24.dp, notchDepth = 32.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .align(Alignment.BottomCenter)
                .shadow(elevation = 10.dp, shape = notchedShape, clip = false)
                .clip(notchedShape)
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home
                QosNavItem(
                    label = "Home",
                    selected = currentRoute == Screen.Home.route,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                ) {
                    // TODO: SVG 아이콘으로 교체
                    Icon(
                        Icons.Filled.Home, null,
                        modifier = Modifier.size(24.dp),
                        tint = if (currentRoute == Screen.Home.route) QOSNavy else Color(0xFFAAAAAA)
                    )
                }
                // Map
                val isMapSelected = currentRoute?.startsWith(Screen.Map.route) == true
                QosNavItem(
                    label = "Map",
                    selected = isMapSelected,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.Map.route) { launchSingleTop = true } }
                ) {
                    Icon(
                        painter = painterResource(
                            if (isMapSelected) com.gdgswu.qos.R.drawable.ic_nav_map_selected
                            else com.gdgswu.qos.R.drawable.ic_nav_map
                        ),
                        contentDescription = "Map",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                }
                // 중앙 캐릭터 자리
                Spacer(modifier = Modifier.width(72.dp))
                // Guide
                val isGuideSelected = currentRoute == Screen.WhatToDo.route
                QosNavItem(
                    label = "Guide",
                    selected = isGuideSelected,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.WhatToDo.route) { launchSingleTop = true } }
                ) {
                    Icon(
                        painter = painterResource(
                            if (isGuideSelected) com.gdgswu.qos.R.drawable.ic_nav_guide_selected
                            else com.gdgswu.qos.R.drawable.ic_nav_guide
                        ),
                        contentDescription = "Guide",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                }
                // Setting
                val isSettingSelected = currentRoute == Screen.Setting.route
                QosNavItem(
                    label = "Setting",
                    selected = isSettingSelected,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.Setting.route) { launchSingleTop = true } }
                ) {
                    Icon(
                        painter = painterResource(
                            if (isSettingSelected) com.gdgswu.qos.R.drawable.ic_nav_setting_selected
                            else com.gdgswu.qos.R.drawable.ic_nav_setting
                        ),
                        contentDescription = "Setting",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        }

        // ── Pill 팝업 메뉴 ─ 캐릭터와 독립 배치 (Column 제거로 height 제약 해결) ──
        // offset 계산: character offset(36) + character size(56) + gap(8) = 100dp
        AnimatedVisibility(
            visible = menuExpanded,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-100).dp),
            enter = fadeIn(tween(200)) + slideInVertically(tween(200)) { it / 2 },
            exit = fadeOut(tween(150)) + slideOutVertically(tween(150)) { it / 2 }
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.Black)
                    .padding(horizontal = 22.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // OCR 카메라 버튼
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    gazeDirection = GazeDirection.LEFT
                                    tryAwaitRelease()
                                    gazeDirection = GazeDirection.CENTER
                                },
                                onTap = {
                                    menuExpanded = false
                                    navController.navigate(Screen.OcrScan.route)
                                }
                            )
                        }
                ) {
                    Icon(Icons.Filled.DocumentScanner, "Scan", tint = Color.White, modifier = Modifier.fillMaxSize())
                }
                // 번역 카드 버튼
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    gazeDirection = GazeDirection.RIGHT
                                    tryAwaitRelease()
                                    gazeDirection = GazeDirection.CENTER
                                },
                                onTap = {
                                    menuExpanded = false
                                    navController.navigate(Screen.Guide.route)
                                }
                            )
                        }
                ) {
                    Icon(Icons.Filled.Translate, "Translate", tint = Color.White, modifier = Modifier.fillMaxSize())
                }
            }
        }

        // ── 캐릭터 얼굴 ─ 독립 배치, 항상 56dp 고정 ──────────────────────────
        val faceRes = when {
            gazeDirection == GazeDirection.LEFT  -> com.gdgswu.qos.R.drawable.ic_face_left
            gazeDirection == GazeDirection.RIGHT -> com.gdgswu.qos.R.drawable.ic_face_right
            menuExpanded                         -> com.gdgswu.qos.R.drawable.ic_face_open
            else                                 -> com.gdgswu.qos.R.drawable.ic_face_normal
        }

        val gazeOffsetX by animateFloatAsState(
            targetValue = when (gazeDirection) {
                GazeDirection.LEFT   -> -5f
                GazeDirection.RIGHT  -> 5f
                GazeDirection.CENTER -> 0f
            },
            animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
            label = "gazeX"
        )

        val gazeOffsetY by animateFloatAsState(
            targetValue = if (menuExpanded) -4f else 0f,
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
            label = "menuY"
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-36).dp)           // 네비바 위로 띄움
                .size(56.dp)                    // 항상 고정 56dp
                .graphicsLayer {
                    translationX = gazeOffsetX.dp.toPx()
                    translationY = gazeOffsetY.dp.toPx()
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { menuExpanded = !menuExpanded }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Crossfade(
                targetState = faceRes,
                animationSpec = tween(durationMillis = 250),
                label = "face"
            ) { res ->
                Image(
                    painter = painterResource(res),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}


// ── 네비바 아이템 ─────────────────────────────────────────────────────────────
@Composable
fun QosNavItem(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon()
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) QOSNavy else Color(0xFFAAAAAA)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun BottomNavBarPreview() {
    BottomNavBar(navController = rememberNavController())
}

// ── 캐릭터 자리를 움푹 파는 커스텀 Shape (베지에 곡선 방식) ────────────────────────────────────
private fun navBarShape(notchHalfWidth: Dp, notchDepth: Dp): Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        with(density) {
            val cx = size.width / 2f
            val hw = notchHalfWidth.toPx()
            val dep = notchDepth.toPx()

            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(0f, 0f)
                lineTo(cx - hw, 0f)

                // 1. 왼쪽 부드러운 곡선 (평면 -> 바닥 중앙)
                cubicTo(
                    x1 = cx - hw * 1.1f, y1 = 0f,   // 곡선이 시작될 때 평면을 유지하려는 제어점
                    x2 = cx - hw * 1.1f, y2 = dep,  // 곡선이 바닥으로 부드럽게 떨어지게 하는 제어점
                    x3 = cx, y3 = dep               // 바닥 중앙 도착
                )

                // 2. 오른쪽 부드러운 곡선 (바닥 중앙 -> 평면)
                cubicTo(
                    x1 = cx + hw * 1.1f, y1 = dep,  // 바닥에서 부드럽게 올라가게 하는 제어점
                    x2 = cx + hw * 1.1f, y2 = 0f,   // 평면과 자연스럽게 만나게 하는 제어점
                    x3 = cx + hw, y3 = 0f           // 오른쪽 평면 도착
                )

                lineTo(size.width, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            return Outline.Generic(path)
        }
    }
}