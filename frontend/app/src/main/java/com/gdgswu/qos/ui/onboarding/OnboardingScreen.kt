package com.gdgswu.qos.ui.onboarding

import android.content.Context
import androidx.compose.foundation.background
import com.gdgswu.qos.data.local.UserProfilePrefs
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
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
import com.gdgswu.qos.ui.theme.*
import com.gdgswu.qos.ui.theme.QOSTheme
import java.util.Locale

// 온보딩 단계
enum class OnboardingStep { LANGUAGE, HEALTH, COMPANION }

@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var currentStep by remember { mutableStateOf(OnboardingStep.LANGUAGE) }
    var selectedLanguage by remember { mutableStateOf<SupportedLanguage?>(null) }
    var selectedAllergies by remember { mutableStateOf(setOf<String>()) }
    var selectedConditions by remember { mutableStateOf(setOf<String>()) }
    var selectedBloodType by remember { mutableStateOf("Unknown") }
    var hasCompanion by remember { mutableStateOf<Boolean?>(null) }
    var hasChildren by remember { mutableStateOf(false) }
    var hasPregnant by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // API 결과 처리
    LaunchedEffect(uiState) {
        when (uiState) {
            is OnboardingUiState.Success -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            }
            is OnboardingUiState.Error -> {
                snackbarHostState.showSnackbar((uiState as OnboardingUiState.Error).message)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            // 진행 표시
            StepIndicator(currentStep = currentStep)

            Spacer(modifier = Modifier.height(32.dp))

            when (currentStep) {
                OnboardingStep.LANGUAGE -> LanguageStep(
                    selectedLanguage = selectedLanguage,
                    onLanguageSelected = { selectedLanguage = it },
                    onNext = { if (selectedLanguage != null) currentStep = OnboardingStep.HEALTH }
                )
                OnboardingStep.HEALTH -> HealthStep(
                    selectedAllergies = selectedAllergies,
                    selectedConditions = selectedConditions,
                    selectedBloodType = selectedBloodType,
                    onAllergyToggle = {
                        selectedAllergies = if (it in selectedAllergies)
                            selectedAllergies - it else selectedAllergies + it
                    },
                    onConditionToggle = {
                        selectedConditions = if (it in selectedConditions)
                            selectedConditions - it else selectedConditions + it
                    },
                    onBloodTypeSelected = { selectedBloodType = it },
                    onNext = { currentStep = OnboardingStep.COMPANION },
                    onSkip = { currentStep = OnboardingStep.COMPANION }
                )
                OnboardingStep.COMPANION -> CompanionStep(
                    hasCompanion = hasCompanion,
                    hasChildren = hasChildren,
                    hasPregnant = hasPregnant,
                    onCompanionSelected = { hasCompanion = it },
                    onChildrenToggle = { hasChildren = it },
                    onPregnantToggle = { hasPregnant = it },
                    isLoading = uiState is OnboardingUiState.Loading,
                    onDone = {
                        val lang = selectedLanguage ?: SupportedLanguage.ENGLISH

                        // 로컬 저장 유지
                        UserProfilePrefs.saveProfile(
                            context = context,
                            language = lang,
                            allergies = selectedAllergies,
                            conditions = selectedConditions,
                            bloodType = selectedBloodType,
                            hasChildren = hasChildren,
                            hasPregnant = hasPregnant,
                            markOnboardingDone = true
                        )

                        // 동반자 목록 구성
                        val companions = mutableListOf<String>().apply {
                            if (hasChildren) add("child")
                            if (hasPregnant) add("pregnant")
                        }

                        // API 호출
                        viewModel.onboard(
                            locale = Locale.getDefault().toLanguageTag(),
                            preferredLanguage = lang.code,
                            conditions = selectedConditions.toList(),
                            allergies = selectedAllergies.toList(),
                            companions = companions
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun StepIndicator(currentStep: OnboardingStep) {
    val steps = OnboardingStep.entries
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            Box(
                modifier = Modifier
                    .size(if (step == currentStep) 10.dp else 8.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(if (step == currentStep) QOSRed else Color(0xFFDDDDDD))
            )
            if (index < steps.lastIndex) Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

// ─── Step 1: 언어 선택 ───────────────────────────────────────────────────────
@Composable
fun LanguageStep(
    selectedLanguage: SupportedLanguage?,
    onLanguageSelected: (SupportedLanguage) -> Unit,
    onNext: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            // TODO: 디자인 확정 시 텍스트 스타일 교체
            "Choose your language",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Select the language you understand",
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        SupportedLanguage.entries.forEach { lang ->
            val isSelected = selectedLanguage == lang
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) QOSRed else Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onLanguageSelected(lang) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(lang.displayName, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextPrimary, modifier = Modifier.weight(1f))
                if (isSelected) Icon(Icons.Filled.Check, contentDescription = null, tint = QOSRed)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = selectedLanguage != null,
            colors = ButtonDefaults.buttonColors(containerColor = QOSRed),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Next", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ─── Step 2: 건강 프로필 ──────────────────────────────────────────────────────
val commonAllergies = listOf("Penicillin", "Aspirin", "Ibuprofen", "Latex", "Sulfa drugs")
val commonConditions = listOf("Diabetes", "Hypertension", "Asthma", "Heart disease", "Epilepsy")

val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "Unknown")

@Composable
fun HealthStep(
    selectedAllergies: Set<String>,
    selectedConditions: Set<String>,
    selectedBloodType: String,
    onAllergyToggle: (String) -> Unit,
    onConditionToggle: (String) -> Unit,
    onBloodTypeSelected: (String) -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 스크롤 가능한 콘텐츠 영역
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Health profile", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("This helps detect dangerous medicines", fontSize = 14.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(24.dp))

            // 알레르기
            Text("Allergies", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            commonAllergies.forEach { allergy ->
                CheckboxRow(
                    label = allergy,
                    checked = allergy in selectedAllergies,
                    onToggle = { onAllergyToggle(allergy) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 혈액형
            var showBloodDropdown by remember { mutableStateOf(false) }
            Text("Blood type", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
                        .clickable { showBloodDropdown = true }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(selectedBloodType, fontSize = 15.sp, color = TextPrimary)
                    Icon(androidx.compose.material.icons.Icons.Filled.KeyboardArrowDown,
                        contentDescription = null, tint = TextSecondary)
                }
                DropdownMenu(
                    expanded = showBloodDropdown,
                    onDismissRequest = { showBloodDropdown = false }
                ) {
                    bloodTypes.forEach { bt ->
                        DropdownMenuItem(
                            text = { Text(bt) },
                            onClick = { onBloodTypeSelected(bt); showBloodDropdown = false },
                            leadingIcon = if (bt == selectedBloodType) {
                                { Icon(androidx.compose.material.icons.Icons.Filled.Check,
                                    contentDescription = null, tint = QOSRed) }
                            } else null
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 기저질환
            Text("Medical conditions", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            commonConditions.forEach { condition ->
                CheckboxRow(
                    label = condition,
                    checked = condition in selectedConditions,
                    onToggle = { onConditionToggle(condition) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 버튼은 항상 하단에 고정
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onSkip,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Skip") }
            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f).height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = QOSRed),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Next", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun CheckboxRow(label: String, checked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(checkedColor = QOSRed)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, fontSize = 14.sp, color = TextPrimary)
    }
}

// ─── Step 3: 동반자 여부 ──────────────────────────────────────────────────────
@Composable
fun CompanionStep(
    hasCompanion: Boolean?,
    hasChildren: Boolean,
    hasPregnant: Boolean,
    onCompanionSelected: (Boolean) -> Unit,
    onChildrenToggle: (Boolean) -> Unit,
    onPregnantToggle: (Boolean) -> Unit,
    isLoading: Boolean = false,
    onDone: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Companions", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Are you traveling with others?", fontSize = 14.sp, color = TextSecondary)

        Spacer(modifier = Modifier.height(32.dp))

        // 예/아니오
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(true to "Yes", false to "No").forEach { (value, label) ->
                val isSelected = hasCompanion == value
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) QOSRed else Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onCompanionSelected(value) }
                        .padding(vertical = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, fontSize = 16.sp, fontWeight = FontWeight.Medium,
                        color = if (isSelected) QOSRed else TextPrimary)
                }
            }
        }

        if (hasCompanion == true) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Who is with you?", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            CheckboxRow("Children", hasChildren, { onChildrenToggle(!hasChildren) })
            CheckboxRow("Pregnant woman", hasPregnant, { onPregnantToggle(!hasPregnant) })
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = QOSRed),
            shape = RoundedCornerShape(12.dp),
            enabled = hasCompanion != null && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Get Started", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true, name = "Onboarding Screen")
@Composable
fun OnboardingScreenPreview() {
    QOSTheme { OnboardingScreen(navController = rememberNavController()) }
}
