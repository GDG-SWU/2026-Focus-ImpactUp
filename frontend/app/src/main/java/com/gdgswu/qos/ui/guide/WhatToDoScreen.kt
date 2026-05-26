package com.gdgswu.qos.ui.guide

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gdgswu.qos.ui.home.HomeViewModel
import com.gdgswu.qos.ui.navigation.Screen
import com.gdgswu.qos.ui.theme.QOSTheme
import com.gdgswu.qos.ui.theme.*

// ── Data ─────────────────────────────────────────────────────────────────────

data class Mission(
    val id: Int,
    val icon: ImageVector,
    val iconBgColor: Color,
    val iconColor: Color,
    val title: String,
    val steps: List<String>,
    val targetRoute: String
)

data class DayPlan(
    val dayNumber: Int,       // 1, 2, …
    val label: String,        // "Today", "Tomorrow", …
    val timeRange: String,    // "0 – 24 h", "24 – 48 h", …
    val missions: List<Mission>
)

// Day 1: 0–24h — 즉각 생존
internal val day1Missions = listOf(
    Mission(
        id = 1,
        icon = Icons.Filled.Home,
        iconBgColor = Color(0xFFF3E5F5),
        iconColor = Color(0xFFAB47BC),
        title = "Find safe shelter",
        steps = listOf("Open the map", "Tap the shelter filter", "Check the nearest available shelter"),
        targetRoute = Screen.Map.route
    ),
    Mission(
        id = 2,
        icon = Icons.Filled.WaterDrop,
        iconBgColor = Color(0xFFE3F2FD),
        iconColor = Color(0xFF42A5F5),
        title = "Find clean water",
        steps = listOf("Open the map", "Tap the water filter", "Go to the nearest water point"),
        targetRoute = Screen.Map.route
    ),
    Mission(
        id = 3,
        icon = Icons.Filled.Help,
        iconBgColor = Color(0xFFF5F5F5),
        iconColor = Color(0xFF9E9E9E),
        title = "Ask for local support",
        steps = listOf("Open translation cards", "Choose 'Ask for help'", "Show the card to a local person"),
        targetRoute = Screen.Guide.route
    ),
    Mission(
        id = 4,
        icon = Icons.Filled.MedicalServices,
        iconBgColor = Color(0xFFFCE4EC),
        iconColor = QOSRed,
        title = "Register health info",
        steps = listOf("Go to Settings", "Enter allergies & conditions", "Save your health profile"),
        targetRoute = Screen.Setting.route
    ),
    Mission(
        id = 5,
        icon = Icons.Filled.CameraAlt,
        iconBgColor = Color(0xFFFFF8E1),
        iconColor = Color(0xFFFFB300),
        title = "Scan medicine label",
        steps = listOf("Open OCR scanner", "Point camera at medicine label", "Check for allergy warnings"),
        targetRoute = Screen.OcrScan.route
    ),
)

// Day 2: 24–48h — 안정화
internal val day2Missions = listOf(
    Mission(
        id = 11,
        icon = Icons.Filled.RestaurantMenu,
        iconBgColor = Color(0xFFF1F8E9),
        iconColor = Color(0xFF66BB6A),
        title = "Find food distribution",
        steps = listOf("Open the map", "Tap the food / NGO filter", "Go to the distribution point and bring any ID if available"),
        targetRoute = Screen.Map.route
    ),
    Mission(
        id = 12,
        icon = Icons.Filled.LocalHospital,
        iconBgColor = Color(0xFFFCE4EC),
        iconColor = QOSRed,
        title = "Get a medical check-up",
        steps = listOf("Open the map", "Tap the medical filter", "Visit the nearest clinic — show your SOS card if needed"),
        targetRoute = Screen.Map.route
    ),
    Mission(
        id = 13,
        icon = Icons.Filled.Phone,
        iconBgColor = Color(0xFFE8EAF6),
        iconColor = Color(0xFF5C6BC0),
        title = "Contact family & authorities",
        steps = listOf("Ask shelter staff for a phone or Wi-Fi", "Call or message your emergency contact", "Register with local authorities or NGO if required"),
        targetRoute = Screen.Setting.route
    ),
    Mission(
        id = 14,
        icon = Icons.Filled.BatteryChargingFull,
        iconBgColor = Color(0xFFFFF8E1),
        iconColor = Color(0xFFFFB300),
        title = "Charge your devices",
        steps = listOf("Ask shelter staff for a charging station", "Keep battery above 30% at all times", "Download offline maps or content if Wi-Fi is available"),
        targetRoute = Screen.Home.route
    ),
    Mission(
        id = 15,
        icon = Icons.Filled.Translate,
        iconBgColor = Color(0xFFE3F2FD),
        iconColor = Color(0xFF42A5F5),
        title = "Learn key local phrases",
        steps = listOf("Open translation cards", "Browse the 'Emergency' and 'Shelter' categories", "Practice phrases you may need to say aloud"),
        targetRoute = Screen.Guide.route
    ),
)

val dayPlans = listOf(
    DayPlan(dayNumber = 1, label = "Today",    timeRange = "0 – 24 h",  missions = day1Missions),
    DayPlan(dayNumber = 2, label = "Tomorrow", timeRange = "24 – 48 h", missions = day2Missions),
)

// TutorialState에서 참조하는 기존 missions 변수 유지 (호환성)
val missions = day1Missions

// ── Emergency contacts ────────────────────────────────────────────────────────

private data class EmergencyContact(val name: String, val number: String, val desc: String, val color: Color)

private val emergencyContacts = listOf(
    EmergencyContact("112",                "112",       "Spain Emergency (Police / Ambulance / Fire)", Color(0xFFD32F2F)),
    EmergencyContact("Cruz Roja",          "900221122", "Red Cross Spain — free 24h helpline",        Color(0xFFC62828)),
    EmergencyContact("Salvamento Marítimo","900202202", "Maritime Rescue — sea emergency (free)",     Color(0xFF1565C0)),
    EmergencyContact("CEAR",               "915980535", "Spanish Refugee Aid Commission",             Color(0xFF6A1B9A)),
    EmergencyContact("ACNUR / UNHCR",      "915563634", "UN Refugee Agency Spain",                   Color(0xFF0277BD)),
)

@Composable
private fun EmergencyContactsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Emergency Contacts", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                emergencyContacts.forEach { contact ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(contact.color.copy(alpha = 0.08f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(contact.color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(contact.number.take(3), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = contact.color)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(contact.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = contact.color)
                            Text(contact.number, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF212121))
                            Text(contact.desc, fontSize = 11.sp, color = TextSecondary, lineHeight = 14.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun WhatToDoScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel(),
    whatToDoViewModel: WhatToDoViewModel = viewModel()
) {
    val isOnline by homeViewModel.isOnline.collectAsState()
    val survivalActions by homeViewModel.survivalActions.collectAsState()
    var selectedDayIndex by remember { mutableStateOf(0) }
    var expandedMissionId by remember { mutableStateOf<Int?>(null) }
    var showEmergencyDialog by remember { mutableStateOf(false) }
    val completedIds = whatToDoViewModel.completedMissionIds

    val plan = dayPlans[selectedDayIndex]
    val dayMissions = plan.missions
    val dayCompletedCount = dayMissions.count { completedIds.contains(it.id) }

    if (showEmergencyDialog) {
        EmergencyContactsDialog(onDismiss = { showEmergencyDialog = false })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 상단 바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            IconButton(
                onClick = { showEmergencyDialog = true },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(Icons.Filled.Warning, contentDescription = "Emergency contacts", tint = Color(0xFFD32F2F))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ── 날짜 네비게이션 ────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (selectedDayIndex > 0) { selectedDayIndex--; expandedMissionId = null } },
                    enabled = selectedDayIndex > 0
                ) {
                    Icon(
                        Icons.Filled.ChevronLeft,
                        contentDescription = "Previous day",
                        tint = if (selectedDayIndex > 0) TextPrimary else Color(0xFFDDDDDD)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        plan.label,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        plan.timeRange,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                IconButton(
                    onClick = { if (selectedDayIndex < dayPlans.lastIndex) { selectedDayIndex++; expandedMissionId = null } },
                    enabled = selectedDayIndex < dayPlans.lastIndex
                ) {
                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = "Next day",
                        tint = if (selectedDayIndex < dayPlans.lastIndex) TextPrimary else Color(0xFFDDDDDD)
                    )
                }
            }

            // ── Survival stage 뱃지 (API) ────────────────────────────────────
            if (survivalActions != null) {
                val stage = survivalActions!!.stage.replace("_", " ").replaceFirstChar { it.uppercase() }
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFFFF3E0))
                            .padding(horizontal = 14.dp, vertical = 5.dp)
                    ) {
                        Text("Stage: $stage", fontSize = 12.sp, color = Color(0xFFE65100), fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // ── 온라인 상태 ───────────────────────────────────────────────────
            if (isOnline != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val onlineColor = if (isOnline == true) StatusGreen else Color(0xFFFF6F00)
                    Icon(
                        if (isOnline == true) Icons.Filled.Wifi else Icons.Filled.WifiOff,
                        contentDescription = null,
                        tint = onlineColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (isOnline == true) "Online" else "Offline — cached data",
                        fontSize = 12.sp, color = onlineColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── 진행 게이지 카드 ───────────────────────────────────────────────
            TodayGuideCard(
                dayPlan = plan,
                completedCount = dayCompletedCount,
                totalCount = dayMissions.size
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "${plan.label}'s tasks",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            dayMissions.forEach { mission ->
                val isExpanded = expandedMissionId == mission.id
                val isCompleted = completedIds.contains(mission.id)

                MissionItem(
                    mission = mission,
                    isExpanded = isExpanded,
                    isCompleted = isCompleted,
                    onToggle = {
                        expandedMissionId = if (isExpanded) null else mission.id
                    },
                    onComplete = { whatToDoViewModel.toggleMission(mission.id) },
                    onStart = { TutorialState.start(mission) }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}

// ── Today's guide gauge ───────────────────────────────────────────────────────

@Composable
fun TodayGuideCard(dayPlan: DayPlan, completedCount: Int, totalCount: Int) {
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f
    val isDay2 = dayPlan.dayNumber == 2
    val bgColor = if (isDay2) Color(0xFFE8EAF6) else QOSLightGreen
    val trackColor = if (isDay2) Color(0xFFC5CAE9) else Color(0xFFD0E8C8)
    val fillColor = if (isDay2) Color(0xFF5C6BC0) else Color(0xFF4CAF50)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 원형 게이지
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(88.dp)) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(88.dp)) {
                    val strokeWidth = 7.dp.toPx()
                    val gapAngle  = 52f
                    val arcStart  = 90f + gapAngle / 2f
                    val arcSweep  = 360f - gapAngle
                    drawArc(
                        color = trackColor,
                        startAngle = arcStart, sweepAngle = arcSweep,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = fillColor,
                        startAngle = arcStart, sweepAngle = arcSweep * progress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                // 숫자 + 레이블
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(y = (-6).dp)
                ) {
                    Text(
                        "$completedCount/$totalCount",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text("tasks", fontSize = 10.sp, color = TextSecondary)
                }
                // 아이콘
                Icon(
                    imageVector = if (isDay2) Icons.Filled.Shield else Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = fillColor,
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.BottomCenter)
                        .offset(y = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    if (isDay2) "Stabilization phase" else "Survival phase",
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    if (isDay2)
                        "Focus on food, medical, and\nstaying connected with others."
                    else
                        "Secure shelter, water, and\ncommunicate your needs.",
                    fontSize = 13.sp, color = TextSecondary, lineHeight = 18.sp
                )
            }
        }
    }
}

// ── Mission item ──────────────────────────────────────────────────────────────

@Composable
fun MissionItem(
    mission: Mission,
    isExpanded: Boolean,
    isCompleted: Boolean,
    onToggle: () -> Unit,
    onComplete: () -> Unit,
    onStart: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // ── 왼쪽: 아이콘 박스 ──────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(mission.iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                mission.icon,
                contentDescription = null,
                tint = mission.iconColor,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // ── 오른쪽: 카드 콘텐츠 ──────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isExpanded) mission.iconBgColor else Color(0xFFF5F5F5))
                .border(
                    width = if (isExpanded) 1.dp else 0.dp,
                    color = if (isExpanded) mission.iconColor.copy(alpha = 0.4f) else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            // 헤더 (토글)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    mission.title,
                    modifier = Modifier.weight(1f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isCompleted) TextSecondary else TextPrimary
                )
                Icon(
                    if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }

            // 펼쳐진 내용
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(
                        start = 14.dp, end = 14.dp, bottom = 14.dp
                    )
                ) {
                    // 단계별 가이드
                    mission.steps.forEachIndexed { index, step ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEEEEEE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${index + 1}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(step, fontSize = 13.sp, color = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Complete / Start 버튼
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onComplete,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCompleted) StatusGreen else Color(0xFFBDBDBD)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                if (isCompleted) "✓ Done" else "Complete",
                                fontSize = 13.sp, color = Color.White
                            )
                        }

                        Button(
                            onClick = onStart,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCompleted) Color(0xFFBDBDBD) else QOSRed
                            ),
                            enabled = !isCompleted,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Start", fontSize = 13.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true, name = "WhatToDo Screen")
@Composable
fun WhatToDoScreenPreview() {
    QOSTheme { WhatToDoScreen(navController = rememberNavController()) }
}

@Preview(showBackground = true, name = "Guide Card – Day 1 empty")
@Composable
fun TodayGuideCardEmptyPreview() {
    QOSTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            TodayGuideCard(dayPlan = dayPlans[0], completedCount = 0, totalCount = 5)
        }
    }
}

@Preview(showBackground = true, name = "Guide Card – Day 2")
@Composable
fun Day2GuideCardPreview() {
    QOSTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            TodayGuideCard(dayPlan = dayPlans[1], completedCount = 2, totalCount = 5)
        }
    }
}

@Preview(showBackground = true, name = "Guide Card – Day 1 partial")
@Composable
fun TodayGuideCardPartialPreview() {
    QOSTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            TodayGuideCard(dayPlan = dayPlans[0], completedCount = 2, totalCount = 5)
        }
    }
}

@Preview(showBackground = true, name = "Mission Item – Collapsed")
@Composable
fun MissionItemCollapsedPreview() {
    QOSTheme {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            MissionItem(
                mission = missions[0],
                isExpanded = false,
                isCompleted = false,
                onToggle = {}, onComplete = {}, onStart = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Mission Item – Expanded")
@Composable
fun MissionItemExpandedPreview() {
    QOSTheme {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            MissionItem(
                mission = missions[0],
                isExpanded = true,
                isCompleted = false,
                onToggle = {}, onComplete = {}, onStart = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Mission Item – Completed")
@Composable
fun MissionItemCompletedPreview() {
    QOSTheme {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            MissionItem(
                mission = missions[1],
                isExpanded = true,
                isCompleted = true,
                onToggle = {}, onComplete = {}, onStart = {}
            )
        }
    }
}
