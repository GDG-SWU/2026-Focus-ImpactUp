package com.gdgswu.qos.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gdgswu.qos.data.local.UserProfilePrefs
import com.gdgswu.qos.ui.theme.QOSTheme
import com.gdgswu.qos.data.model.SupportedLanguage
import com.gdgswu.qos.ui.navigation.Screen
import com.gdgswu.qos.ui.theme.*

@Composable
fun SettingScreen(
    navController: NavController,
    viewModel: SettingViewModel = viewModel()
) {
    val context = LocalContext.current

    val isLoading by viewModel.isLoading.collectAsState()
    val updateSuccess by viewModel.updateSuccess.collectAsState()

    // SharedPreferences에서 온보딩 선택값 로드
    var selectedLanguage by remember { mutableStateOf(UserProfilePrefs.loadLanguage(context)) }
    var selectedAllergies by remember { mutableStateOf(UserProfilePrefs.loadAllergies(context)) }
    var selectedConditions by remember { mutableStateOf(UserProfilePrefs.loadConditions(context)) }
    var selectedBloodType by remember { mutableStateOf(UserProfilePrefs.loadBloodType(context)) }
    var hasChildren by remember { mutableStateOf(UserProfilePrefs.loadHasChildren(context)) }
    var hasPregnant by remember { mutableStateOf(UserProfilePrefs.loadHasPregnant(context)) }
    var showSaved by remember { mutableStateOf(false) }

    // API 업데이트 성공 처리
    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            showSaved = true
            viewModel.resetUpdateSuccess()
        }
    }

    // 항목 변경 시 "Saved" 표시 초기화
    LaunchedEffect(selectedLanguage, selectedAllergies, selectedConditions, selectedBloodType, hasChildren, hasPregnant) {
        showSaved = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        // 헤더
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text("Setting", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary,
                modifier = Modifier.align(Alignment.Center))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // SOS 카드 바로가기
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Screen.SosCard.route) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = QOSNavy)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.LocalHospital, contentDescription = null,
                        tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("My SOS Card", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Show your health info to rescuers",
                            fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.White)
                }
            }

            // 언어 설정
            SettingSection(title = "Language") {
                SupportedLanguage.entries.forEach { lang ->
                    SettingRadioRow(
                        label = lang.displayName,
                        selected = selectedLanguage == lang,
                        onClick = { selectedLanguage = lang }
                    )
                }
            }

            // 혈액형 설정
            SettingSection(title = "Blood Type") {
                val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "Unknown")
                bloodTypes.forEach { bt ->
                    SettingRadioRow(
                        label = bt,
                        selected = selectedBloodType == bt,
                        onClick = { selectedBloodType = bt }
                    )
                }
            }

            // 알레르기 설정
            SettingSection(title = "Allergies") {
                listOf("Penicillin", "Aspirin", "Ibuprofen", "Latex", "Sulfa drugs").forEach { item ->
                    SettingCheckRow(
                        label = item,
                        checked = item in selectedAllergies,
                        onToggle = {
                            selectedAllergies = if (item in selectedAllergies)
                                selectedAllergies - item else selectedAllergies + item
                        }
                    )
                }
            }

            // 기저질환 설정
            SettingSection(title = "Medical Conditions") {
                listOf("Diabetes", "Hypertension", "Asthma", "Heart disease", "Epilepsy").forEach { item ->
                    SettingCheckRow(
                        label = item,
                        checked = item in selectedConditions,
                        onToggle = {
                            selectedConditions = if (item in selectedConditions)
                                selectedConditions - item else selectedConditions + item
                        }
                    )
                }
            }

            // 동반자 설정
            SettingSection(title = "Companions") {
                SettingCheckRow("Traveling with children", hasChildren, { hasChildren = it })
                SettingCheckRow("Pregnant woman in group", hasPregnant, { hasPregnant = it })
            }

            // 저장 버튼
            Button(
                onClick = {
                    // 로컬 저장
                    UserProfilePrefs.saveProfile(
                        context = context,
                        language = selectedLanguage,
                        allergies = selectedAllergies,
                        conditions = selectedConditions,
                        bloodType = selectedBloodType,
                        hasChildren = hasChildren,
                        hasPregnant = hasPregnant
                    )

                    // API 업데이트 호출
                    val companions = mutableListOf<String>().apply {
                        if (hasChildren) add("child")
                        if (hasPregnant) add("pregnant")
                    }
                    viewModel.updateProfile(
                        conditions = selectedConditions.toList(),
                        allergies = selectedAllergies.toList(),
                        companions = companions
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = QOSRed),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        if (showSaved) "Saved ✓" else "Save",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 하단 네비바에 가리지 않도록 여백
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun SettingSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                color = TextSecondary, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun SettingRadioRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = QOSRed)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, fontSize = 15.sp, color = TextPrimary)
    }
}

@Composable
fun SettingCheckRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onToggle,
            colors = CheckboxDefaults.colors(checkedColor = QOSRed)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, fontSize = 14.sp, color = TextPrimary)
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true, name = "Setting Screen")
@Composable
fun SettingScreenPreview() {
    QOSTheme { SettingScreen(navController = rememberNavController()) }
}
