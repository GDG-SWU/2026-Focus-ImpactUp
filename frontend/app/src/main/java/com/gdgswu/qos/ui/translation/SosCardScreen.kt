package com.gdgswu.qos.ui.translation

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import com.gdgswu.qos.data.local.UserProfilePrefs
import com.gdgswu.qos.util.TtsPlayer
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gdgswu.qos.data.model.SupportedLanguage
import com.gdgswu.qos.ui.navigation.Screen
import com.gdgswu.qos.ui.theme.QOSTheme
import com.gdgswu.qos.ui.theme.*

@Composable
fun SosCardScreen(
    navController: NavController,
    viewModel: SosCardViewModel = viewModel()
) {
    val context = LocalContext.current

    // API 데이터
    val apiSosCard by viewModel.sosCard.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 백엔드 TTS 플레이어
    val ttsPlayer = remember { TtsPlayer(context) }
    val ttsScope = rememberCoroutineScope()
    DisposableEffect(Unit) { onDispose { ttsPlayer.release() } }

    // SharedPreferences에서 실제 프로필 로드 (fallback용)
    val allergies = UserProfilePrefs.loadAllergies(context)
    val conditions = UserProfilePrefs.loadConditions(context)
    val bloodType = UserProfilePrefs.loadBloodType(context)
    val hasChildren = UserProfilePrefs.loadHasChildren(context)
    val hasPregnant = UserProfilePrefs.loadHasPregnant(context)

    val companionText = when {
        hasChildren && hasPregnant -> "Children + Pregnant woman"
        hasChildren -> "Traveling with children"
        hasPregnant -> "Pregnant woman in group"
        else -> "None"
    }

    val localProfile = listOf(
        "Allergies" to allergies.joinToString(", ").ifBlank { "None" },
        "Conditions" to conditions.joinToString(", ").ifBlank { "None" },
        "Blood type" to bloodType,
        "Companions" to companionText
    )

    var selectedLanguage by remember { mutableStateOf(SupportedLanguage.ENGLISH) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // 상단 바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Text(
                "My SOS Card",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            IconButton(onClick = {
                val shareText = buildString {
                    appendLine("🆘 SOS Medical Info")
                    localProfile.forEach { (k, v) -> appendLine("$k: $v") }
                }
                context.startActivity(
                    Intent.createChooser(
                        Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }, "Share SOS Card"
                    )
                )
            }) {
                Icon(Icons.Filled.Share, contentDescription = "Share", tint = TextPrimary)
            }
        }

        // 언어 선택
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(SupportedLanguage.entries) { lang ->
                val isSelected = selectedLanguage == lang
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedLanguage = lang },
                    label = { Text(lang.displayName, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = QOSNavy,
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFF5EDE0),   // 베이지
                        labelColor = Color(0xFF7A5C3A)        // 어두운 베이지 텍스트
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = Color(0xFFBF9870),      // 어두운 베이지 테두리
                        selectedBorderColor = Color.Transparent,
                        borderWidth = 1.dp,
                        selectedBorderWidth = 0.dp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SOS 카드 본문 (큰 글씨)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocalHospital, contentDescription = null,
                        tint = QOSRed, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SOS — Medical Info",
                        fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = QOSRed)
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = QOSRed)
                    }
                } else if (apiSosCard != null) {
                    // API 데이터: 항상 개별 행으로 표시 (번역 있으면 번역, 없으면 영어 그대로)
                    val card = apiSosCard!!
                    val langCode = selectedLanguage.code

                    // 알레르기 — 번역 label 우선, 빈 문자열이거나 없으면 code(영어) 표시
                    val allergyLabels = card.allergies_translated.orEmpty()
                        .map { item -> item.label[langCode]?.takeIf { it.isNotBlank() } ?: item.code }
                        .ifEmpty { allergies }
                    SosCardRow(
                        label = "Allergies",
                        value = allergyLabels.joinToString(", ").ifBlank { "None" }
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // 컨디션 — 번역 label 우선, 빈 문자열이거나 없으면 code(영어) 표시
                    val conditionLabels = card.conditions_translated.orEmpty()
                        .map { item -> item.label[langCode]?.takeIf { it.isNotBlank() } ?: item.code }
                        .ifEmpty { conditions }
                    SosCardRow(
                        label = "Conditions",
                        value = conditionLabels.joinToString(", ").ifBlank { "None" }
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // 혈액형 — 국제 표기 그대로 (번역 불필요)
                    val bt = card.blood_type ?: bloodType
                    if (bt.isNotBlank() && bt != "Unknown") {
                        SosCardRow(label = "Blood type", value = bt)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // 동반자
                    SosCardRow(label = "Companions", value = companionText)
                    Spacer(modifier = Modifier.height(12.dp))
                } else {
                    // API 실패 시 로컬 데이터 fallback
                    localProfile.forEach { (key, value) ->
                        SosCardRow(label = key, value = value)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 언어 표시
                Text(
                    "Language: ${selectedLanguage.displayName}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // TTS 버튼
        Button(
            onClick = {
                val speakText = apiSosCard?.translations?.get(selectedLanguage.code)
                    ?: localProfile.joinToString(". ") { (k, v) -> "$k: $v" }
                ttsScope.launch { ttsPlayer.play(speakText, selectedLanguage.code) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = QOSRed),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Read aloud", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 프로필 수정 바로가기
        TextButton(
            onClick = { navController.navigate(Screen.Setting.route) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(Icons.Filled.Edit, contentDescription = null,
                modifier = Modifier.size(14.dp), tint = TextSecondary)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Edit profile", fontSize = 13.sp, color = TextSecondary)
        }

        Spacer(modifier = Modifier.height(96.dp))
    }
}

@Composable
fun SosCardRow(label: String, value: String) {
    Column {
        Text(label.uppercase(), fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true, name = "SOS Card Screen")
@Composable
fun SosCardScreenPreview() {
    QOSTheme { SosCardScreen(navController = rememberNavController()) }
}
