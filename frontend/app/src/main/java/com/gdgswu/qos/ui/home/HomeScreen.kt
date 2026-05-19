package com.gdgswu.qos.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gdgswu.qos.R
import com.gdgswu.qos.ui.theme.QOSTheme
import com.gdgswu.qos.ui.map.MapFilter
import com.gdgswu.qos.ui.navigation.Screen
import com.gdgswu.qos.ui.theme.*

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .verticalScroll(rememberScrollState())
    ) {
        // QOS 로고
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_logo_qos),
                contentDescription = "QOS Logo",
                modifier = Modifier.height(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 긴급 액션 카드
        EmergencyActionCard(
            onWhatToDoClick = { navController.navigate(Screen.WhatToDo.route) },
            onGoToMapClick = { navController.navigate(Screen.Map.route) }
        )

        Spacer(modifier = Modifier.height(36.dp))

        // My Profile Card
        ProfileCard(onClick = { navController.navigate(Screen.SosCard.route) })

        Spacer(modifier = Modifier.height(25.dp))

        // Quick Actions
        QuickActionsSection(navController = navController)

        // 하단 네비바 높이만큼 여백 확보 (스크롤 가능하도록)
        Spacer(modifier = Modifier.height(96.dp))
    }
}

@Composable
fun EmergencyActionCard(onWhatToDoClick: () -> Unit, onGoToMapClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // 상태 배너 (위치 왼쪽 / WiFi 오른쪽)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tenerife Port", fontSize = 14.sp, color = TextSecondary)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Filled.Wifi,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 긴급 메시지
            Text(
                text = "Find safe shelter",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = QOSRed
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Open the map,\nlook for the nearest shelter.",
                fontSize = 14.sp,
                color = TextSecondary,
                lineHeight = 20.sp
            )

            // 일러스트 이미지
            Image(
                painter = painterResource(R.drawable.img_shelter_search),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 버튼 두 개
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onWhatToDoClick,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E9E9E))
                ) {
                    Text("What to do", fontSize = 13.sp, color = Color.White)
                }
                Button(
                    onClick = onGoToMapClick,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = QOSRed)
                ) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Go to map", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun ProfileCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(QOSNavy)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 22.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "My profile card",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White)
    }
}

@Composable
fun QuickActionsSection(navController: NavController) {
    fun navigateToMap(filter: MapFilter) =
        navController.navigate("${Screen.Map.route}?filter=${filter.name}")

    Column {
        Text(
            text = "Quick Actions",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                QuickActionItem(
                    label = "Water",
                    bgColor = QOSLightBlue,
                    modifier = Modifier.width(130.dp),
                    onClick = { navigateToMap(MapFilter.WATER) }
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_quickactions_water),
                        contentDescription = "Water",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            item {
                QuickActionItem(
                    label = "Shelter",
                    bgColor = QOSLightPurple,
                    modifier = Modifier.width(130.dp),
                    onClick = { navigateToMap(MapFilter.CAMP) }
                ) {
                    Icon(Icons.Filled.Home, contentDescription = "Shelter",
                        tint = Color(0xFFAB47BC), modifier = Modifier.size(40.dp))
                }
            }
            item {
                QuickActionItem(
                    label = "Medical",
                    bgColor = QOSLightPink,
                    modifier = Modifier.width(130.dp),
                    onClick = { navigateToMap(MapFilter.HOSPITAL) }
                ) {
                    Icon(Icons.Filled.LocalHospital, contentDescription = "Medical",
                        tint = QOSRed, modifier = Modifier.size(40.dp))
                }
            }
            item {
                QuickActionItem(
                    label = "Food",
                    bgColor = QOSLightGreen,
                    modifier = Modifier.width(130.dp),
                    onClick = { navigateToMap(MapFilter.NGO) }
                ) {
                    Icon(Icons.Filled.RestaurantMenu, contentDescription = "Food",
                        tint = Color(0xFF66BB6A), modifier = Modifier.size(40.dp))
                }
            }
        }
    }
}

@Composable
fun QuickActionItem(
    label: String,
    bgColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 컬러 아이콘 영역
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor)
                .padding(vertical = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        // 흰 배경 라벨 영역
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(label, fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true, name = "Home Screen")
@Composable
fun HomeScreenPreview() {
    QOSTheme { HomeScreen(navController = rememberNavController()) }
}

@Preview(showBackground = true, name = "Quick Action Item")
@Composable
fun QuickActionItemPreview() {
    QOSTheme {
        androidx.compose.foundation.layout.Row(modifier = androidx.compose.ui.Modifier.padding(16.dp)) {
            QuickActionItem(label = "Water", bgColor = QOSLightBlue, onClick = {}) {
                androidx.compose.material3.Icon(
                    androidx.compose.material.icons.Icons.Filled.WaterDrop,
                    contentDescription = null, tint = QOSCyan,
                    modifier = androidx.compose.ui.Modifier.size(28.dp)
                )
            }
        }
    }
}
