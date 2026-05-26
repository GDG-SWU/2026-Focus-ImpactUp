package com.gdgswu.qos.ui.translation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gdgswu.qos.R
import com.gdgswu.qos.ui.theme.QOSTheme
import com.gdgswu.qos.data.model.*
import com.gdgswu.qos.ui.navigation.Screen
import com.gdgswu.qos.ui.theme.*
import com.gdgswu.qos.util.TtsPlayer
import java.util.UUID

@Composable
fun TranslationScreen(navController: NavController, vm: TranslationViewModel = viewModel()) {
    val context = LocalContext.current
    var selectedLanguage by remember { mutableStateOf(SupportedLanguage.FRENCH) }
    var selectedCategory by remember { mutableStateOf<PhraseCategory?>(null) }
    var inputText by remember { mutableStateOf("") }
    var isCustomSaved by remember { mutableStateOf(false) }

    // 번역 상태 (ViewModel)
    val translatedResult by vm.translatedText.collectAsState()
    val isTranslating by vm.isTranslating.collectAsState()
    val translateError by vm.translateError.collectAsState()

    // API 카드 (있으면 samplePhrases 대체)
    val apiPhrases by vm.apiPhrases.collectAsState()
    val baseList = if (apiPhrases.isNotEmpty()) apiPhrases else samplePhrases

    // 백엔드 TTS 플레이어
    val ttsPlayer = remember { TtsPlayer(context) }
    val ttsScope = rememberCoroutineScope()
    DisposableEffect(Unit) { onDispose { ttsPlayer.release() } }

    fun playTts(text: String, langCode: String) {
        ttsScope.launch { ttsPlayer.play(text, langCode) }
    }

    fun resetAll() {
        inputText = ""
        vm.clearTranslation()
        isCustomSaved = false
        selectedCategory = null
        vm.loadPhrasebook()
    }

    // 카테고리 선택 시 API 재호출
    LaunchedEffect(selectedCategory) {
        val categoryParam = when (selectedCategory) {
            PhraseCategory.MEDICAL   -> "medical"
            PhraseCategory.SHELTER   -> "shelter"
            PhraseCategory.FOOD      -> "food"
            PhraseCategory.EMERGENCY -> null   // 백엔드 미지원 → 전체 조회
            null                     -> null
        }
        vm.loadPhrasebook(categoryParam)
    }

    // EMERGENCY는 백엔드 미지원 → samplePhrases 고정 사용
    val filteredPhrases = when (selectedCategory) {
        PhraseCategory.EMERGENCY -> samplePhrases.filter { it.category == PhraseCategory.EMERGENCY }
        null -> baseList
        else -> baseList.filter { it.category == selectedCategory }
    }

    // 언어가 바뀌면 번역 결과 초기화
    LaunchedEffect(selectedLanguage) {
        vm.clearTranslation()
        isCustomSaved = false
    }
    // 입력 텍스트가 바뀌면 이전 번역 결과 초기화
    LaunchedEffect(inputText) {
        vm.clearTranslation()
        isCustomSaved = false
    }

    // 전체를 하나의 LazyColumn으로 — 상단 입력 영역과 문장 목록이 함께 스크롤됨
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // ── 상단 바 ────────────────────────────────────────────────────────────
        item { TopBar(navController = navController, onReset = ::resetAll) }

        // ── 언어 선택 ──────────────────────────────────────────────────────────
        item {
            LanguageSelector(
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { selectedLanguage = it }
            )
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        // ── 텍스트 입력 ────────────────────────────────────────────────────────
        item {
            TextInputArea(text = inputText, onTextChange = { inputText = it })
        }

        // ── Translate 버튼 (텍스트 있을 때만) ────────────────────────────────
        if (inputText.isNotBlank()) {
            item { Spacer(modifier = Modifier.height(12.dp)) }
            item {
                Button(
                    onClick = {
                        isCustomSaved = false
                        vm.translate(inputText, selectedLanguage)
                    },
                    enabled = !isTranslating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = QOSCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isTranslating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Translating...", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    } else {
                        Icon(Icons.Filled.Translate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Translate", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }

        // ── 번역 오류 메시지 ───────────────────────────────────────────────────
        if (translateError != null) {
            item { Spacer(modifier = Modifier.height(10.dp)) }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFF3F3))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = null,
                        tint = Color(0xFFE57373),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = translateError!!,
                        fontSize = 13.sp,
                        color = Color(0xFFE57373)
                    )
                }
            }
        }

        // ── 번역 결과 카드 ─────────────────────────────────────────────────────
        if (translatedResult != null) {
            item { Spacer(modifier = Modifier.height(12.dp)) }
            item {
                TranslationResultCard(
                    original = inputText,
                    translated = translatedResult!!,
                    language = selectedLanguage,
                    isSaved = isCustomSaved,
                    onSave = {
                        if (!isCustomSaved) {
                            FavoritesState.addCustom(
                                CustomTranslation(
                                    id = UUID.randomUUID().toString(),
                                    english = inputText,
                                    translated = translatedResult!!,
                                    language = selectedLanguage
                                )
                            )
                            isCustomSaved = true
                        }
                    }
                )
            }
        }

        // ── 구분선 + SOS 섹션 ─────────────────────────────────────────────────
        item {
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 1.dp,
                color = Color(0xFFEEEEEE)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // ── SOS 카드 + 즐겨찾기 버튼 ──────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { navController.navigate(Screen.SosCard.route) },
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = QOSRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("SOS card", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                OutlinedButton(
                    onClick = { navController.navigate(Screen.SavedPhrases.route) },
                    modifier = Modifier.size(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                ) {
                    val hasSaved = FavoritesState.savedPhrases.isNotEmpty() || FavoritesState.customTranslations.isNotEmpty()
                    Icon(
                        imageVector = if (hasSaved) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = "Saved Phrases",
                        tint = if (hasSaved) Color(0xFFFFC107) else TextSecondary
                    )
                }
            }
        }

        // ── 구분선 + Quick Phrases 섹션 헤더 ──────────────────────────────────
        item {
            Spacer(modifier = Modifier.height(28.dp))
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 1.dp,
                color = Color(0xFFEEEEEE)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Quick Phrases",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // ── 카테고리 필터 ──────────────────────────────────────────────────────
        item {
            CategoryFilter(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = if (selectedCategory == it) null else it }
            )
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        // ── 문장 리스트 ────────────────────────────────────────────────────────
        items(filteredPhrases, key = { it.id }) { phrase ->
            PhraseCard(
                phrase = phrase,
                language = selectedLanguage,
                onPlayTts = { text -> playTts(text, selectedLanguage.code) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

// ── 번역 결과 카드 ─────────────────────────────────────────────────────────────
@Composable
fun TranslationResultCard(
    original: String,
    translated: String,
    language: SupportedLanguage,
    isSaved: Boolean,
    onSave: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F8FF)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = original,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = translated,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
            IconButton(
                onClick = onSave,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = if (isSaved) "Saved" else "Save",
                    tint = if (isSaved) Color(0xFFFFC107) else Color(0xFFCCCCCC),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun TextInputArea(text: String, onTextChange: (String) -> Unit) {
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF2F2F2))
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .heightIn(min = 100.dp)
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            textStyle = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            ),
            cursorBrush = SolidColor(QOSNavy),
            decorationBox = { innerTextField ->
                Column {
                    if (text.isEmpty()) {
                        Text(
                            text = "Type or paste text",
                            fontSize = 15.sp,
                            color = Color(0xFFAAAAAA)
                        )
                    }
                    innerTextField()
                }
            }
        )

        // 지우기 버튼 — 텍스트 있을 때만
        if (text.isNotEmpty()) {
            IconButton(
                onClick = { onTextChange("") },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(28.dp)
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Clear",
                    tint = Color(0xFFAAAAAA),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun TopBar(navController: NavController, onReset: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.navigate(Screen.OcrScan.route) }) {
            Icon(Icons.Filled.CameraAlt, contentDescription = "Scan", tint = TextPrimary)
        }
        Text(
            "Translation Cards",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        IconButton(onClick = onReset) {
            Icon(Icons.Filled.Refresh, contentDescription = "Reset", tint = TextPrimary)
        }
    }
}

@Composable
fun LanguageSelector(
    selectedLanguage: SupportedLanguage,
    onLanguageSelected: (SupportedLanguage) -> Unit
) {
    var showDropdown by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 소스 언어 (영어 고정)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("English", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
        }

        // 스왑 버튼
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(QOSNavy),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.SwapHoriz, contentDescription = "Swap", tint = Color.White, modifier = Modifier.size(20.dp))
        }

        // 타겟 언어 (선택 가능)
        Box {
            Row(
                modifier = Modifier.clickable { showDropdown = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selectedLanguage.displayName, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
            }
            DropdownMenu(expanded = showDropdown, onDismissRequest = { showDropdown = false }) {
                SupportedLanguage.entries.filter { it != SupportedLanguage.ENGLISH }.forEach { lang ->
                    DropdownMenuItem(
                        text = { Text(lang.displayName) },
                        onClick = { onLanguageSelected(lang); showDropdown = false },
                        leadingIcon = if (lang == selectedLanguage) {
                            { Icon(Icons.Filled.Check, contentDescription = null, tint = QOSCyan) }
                        } else null
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryFilter(
    selectedCategory: PhraseCategory?,
    onCategorySelected: (PhraseCategory) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(PhraseCategory.entries) { category ->
            val isSelected = selectedCategory == category
            val iconRes = when (category) {
                PhraseCategory.MEDICAL   -> R.drawable.ic_translate_hospital
                PhraseCategory.SHELTER   -> R.drawable.ic_translate_shelter
                PhraseCategory.FOOD      -> R.drawable.ic_translate_food
                PhraseCategory.EMERGENCY -> R.drawable.ic_translate_support
            }
            val iconPressedRes = when (category) {
                PhraseCategory.MEDICAL   -> R.drawable.ic_translate_hospital_pressed
                PhraseCategory.SHELTER   -> R.drawable.ic_translate_shelter_pressed
                PhraseCategory.FOOD      -> R.drawable.ic_translate_food_pressed
                PhraseCategory.EMERGENCY -> R.drawable.ic_translate_support_pressed
            }
            Image(
                painter = painterResource(if (isSelected) iconPressedRes else iconRes),
                contentDescription = category.displayName,
                modifier = Modifier
                    .height(34.dp)
                    .clickable { onCategorySelected(category) }
            )
        }
    }
}

@Composable
fun PhraseCard(
    phrase: Phrase,
    language: SupportedLanguage,
    onPlayTts: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val translation = phrase.getTranslation(language)
    val isRtl = language == SupportedLanguage.ARABIC
    val isStarred = FavoritesState.isSaved(phrase.id)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // 영어 (소스)
                Text(
                    text = phrase.english,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                // 번역 (타겟 언어)
                Text(
                    text = translation,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = if (isRtl) TextAlign.End else TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // 별표 (즐겨찾기) 버튼
            IconButton(
                onClick = { FavoritesState.togglePhrase(phrase) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isStarred) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = if (isStarred) "Starred" else "Star",
                    tint = if (isStarred) Color(0xFFFFC107) else Color(0xFFCCCCCC),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // TTS 재생 버튼
            IconButton(
                onClick = { onPlayTts(translation) },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(QOSLightBlue)
            ) {
                Icon(
                    Icons.Filled.VolumeUp,
                    contentDescription = "Play",
                    tint = QOSCyan,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true, name = "Translation Screen")
@Composable
fun TranslationScreenPreview() {
    QOSTheme { TranslationScreen(navController = rememberNavController()) }
}

@Preview(showBackground = true, name = "Phrase Card")
@Composable
fun PhraseCardPreview() {
    QOSTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            PhraseCard(phrase = samplePhrases[0], language = SupportedLanguage.FRENCH)
            Spacer(modifier = Modifier.height(8.dp))
            PhraseCard(phrase = samplePhrases[1], language = SupportedLanguage.ARABIC)
        }
    }
}

@Preview(showBackground = true, name = "Translation Result Card")
@Composable
fun TranslationResultCardPreview() {
    QOSTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            TranslationResultCard(
                original = "I need help",
                translated = "[Français] J'ai besoin d'aide",
                language = SupportedLanguage.FRENCH,
                isSaved = false,
                onSave = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Text Input Area")
@Composable
fun TextInputAreaPreview() {
    QOSTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            TextInputArea(text = "", onTextChange = {})
            Spacer(modifier = Modifier.height(12.dp))
            TextInputArea(text = "Where is the hospital?", onTextChange = {})
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Saved Phrases Screen")
@Composable
fun SavedPhrasesScreenPreview() {
    QOSTheme { SavedPhrasesScreen(navController = rememberNavController()) }
}
