package com.gdgswu.qos.ui.translation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import com.gdgswu.qos.data.local.FavoritesPrefs
import com.gdgswu.qos.data.model.FavoritesState
import com.gdgswu.qos.ui.theme.QOSTheme
import com.gdgswu.qos.ui.theme.*

@Composable
fun SavedPhrasesScreen(navController: NavController) {
    val context        = LocalContext.current
    val savedPhrases   = FavoritesState.savedPhrases
    val customPhrases  = FavoritesState.customTranslations
    val isEmpty        = savedPhrases.isEmpty() && customPhrases.isEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        // 상단 바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Text(
                "Saved Phrases",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.size(48.dp))
        }

        if (isEmpty) {
            // 빈 상태
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.StarBorder, contentDescription = null,
                        tint = Color(0xFFCCCCCC), modifier = Modifier.size(56.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No saved phrases yet", color = Color(0xFFAAAAAA), fontSize = 15.sp)
                    Text("Star phrases to save them here",
                        color = Color(0xFFCCCCCC), fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 커스텀 번역 섹션
                if (customPhrases.isNotEmpty()) {
                    items(customPhrases, key = { it.id }) { item ->
                        SavedPhraseCard(
                            english = item.english,
                            translated = item.translated,
                            languageLabel = item.language.displayName,
                            onRemove = {
                                FavoritesState.removeCustom(item.id)
                                FavoritesPrefs.save(context)
                            }
                        )
                    }
                }

                // 별표 predefined 문장 섹션 (헤더 없이 바로 목록)
                if (savedPhrases.isNotEmpty()) {
                    items(savedPhrases, key = { it.id }) { phrase ->
                        SavedPhraseCard(
                            english = phrase.english,
                            translated = "— ${phrase.category.displayName}",
                            languageLabel = phrase.category.displayName,
                            onRemove = {
                                FavoritesState.togglePhrase(phrase)
                                FavoritesPrefs.save(context)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedPhraseCard(
    english: String,
    translated: String,
    languageLabel: String,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(english, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(translated, fontSize = 13.sp, color = TextSecondary)
            }
            IconButton(onClick = onRemove, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Remove",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true, name = "Saved Phrases – Empty")
@Composable
fun SavedPhrasesEmptyPreview() {
    QOSTheme { SavedPhrasesScreen(navController = rememberNavController()) }
}
