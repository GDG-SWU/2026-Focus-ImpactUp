package com.gdgswu.qos.ui.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gdgswu.qos.R
import com.gdgswu.qos.data.remote.model.FacilityItem
import com.gdgswu.qos.ui.theme.QOSTheme
import com.gdgswu.qos.ui.theme.*

enum class MapFilter(val label: String, val iconRes: Int?, val iconPressedRes: Int?) {
    ALL("All", null, null),
    CAMP("Shelter", R.drawable.ic_header_shelter, R.drawable.ic_header_shelter_pressed),
    HOSPITAL("Hospital", R.drawable.ic_header_hospital, R.drawable.ic_header_hospital_pressed),
    NGO("Support", R.drawable.ic_header_support, R.drawable.ic_header_support_pressed),
    WATER("Water", R.drawable.ic_header_water, R.drawable.ic_header_water_pressed)
}

data class Institution(
    val id: Int,
    val name: String,
    val category: MapFilter,
    val distance: String,
    val isOpen: Boolean,
    val status: InstitutionStatus,
    val services: List<String>,
    val address: String
)

enum class InstitutionStatus(val label: String, val color: Color) {
    AVAILABLE("Available", Color(0xFF4CAF50)),
    CROWDED("Crowded", Color(0xFFFFC107)),
    UNAVAILABLE("Unavailable", Color(0xFFF44336))
}

// 샘플 데이터 - 실제로는 API/로컬 DB에서
val sampleInstitutions = listOf(
    Institution(1, "Cruz Roja Tenerife", MapFilter.NGO, "0.3 km", true, InstitutionStatus.AVAILABLE,
        listOf("Food", "Water", "First Aid"), "Av. de Anaga, 38001"),
    Institution(2, "Campo de Refugiados Sur", MapFilter.CAMP, "1.2 km", true, InstitutionStatus.CROWDED,
        listOf("Shelter", "Food", "Medical"), "Calle Sur 12"),
    Institution(3, "Hospital Universitario", MapFilter.HOSPITAL, "2.1 km", true, InstitutionStatus.AVAILABLE,
        listOf("Emergency", "Surgery", "Pharmacy"), "Ofra, s/n, 38320"),
    Institution(4, "Punto de Agua Gratuita", MapFilter.WATER, "0.5 km", true, InstitutionStatus.AVAILABLE,
        listOf("Drinking water", "Sanitation"), "Puerto de Santa Cruz"),
)

fun FacilityItem.toInstitution(index: Int): Institution {
    val category = when (this.category) {
        "hospital" -> MapFilter.HOSPITAL
        "water" -> MapFilter.WATER
        "camp" -> MapFilter.CAMP
        "ngo" -> MapFilter.NGO
        else -> MapFilter.NGO
    }
    val status = when (this.availability) {
        "available" -> InstitutionStatus.AVAILABLE
        "crowded" -> InstitutionStatus.CROWDED
        "unavailable" -> InstitutionStatus.UNAVAILABLE
        else -> InstitutionStatus.UNAVAILABLE
    }
    val distanceStr = if (distance_m < 1000) {
        "${distance_m.toInt()}m"
    } else {
        "${"%.1f".format(distance_m / 1000)} km"
    }
    return Institution(
        id = index,
        name = this.name,
        category = category,
        distance = distanceStr,
        isOpen = this.operating,
        status = status,
        services = emptyList(),
        address = ""
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    initialFilter: MapFilter? = null,
    viewModel: MapViewModel = viewModel()
) {
    var selectedFilter by remember { mutableStateOf(initialFilter) }
    var selectedInstitution by remember { mutableStateOf<Institution?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val detailSheetState = rememberModalBottomSheetState()
    var showDetailSheet by remember { mutableStateOf(false) }

    val apiFacilities by viewModel.facilities.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // API 카테고리 필터 변경 시 재로드
    LaunchedEffect(selectedFilter) {
        val categoryParam = when (selectedFilter) {
            MapFilter.HOSPITAL -> "hospital"
            MapFilter.WATER -> "water"
            MapFilter.CAMP -> "camp"
            MapFilter.NGO -> "ngo"
            else -> null
        }
        viewModel.loadFacilities(categoryParam)
    }

    // API 데이터가 있으면 사용, 실패 시 샘플 데이터 fallback
    val institutions = if (apiFacilities.isNotEmpty()) {
        apiFacilities.mapIndexed { index, item -> item.toInstitution(index) }
    } else {
        sampleInstitutions
    }

    val filtered = institutions
        .let { if (selectedFilter == null) it else it.filter { inst -> inst.category == selectedFilter } }
        .let { if (searchQuery.isBlank()) it else it.filter { inst -> inst.name.contains(searchQuery, ignoreCase = true) } }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 100.dp,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetContainerColor = Color.White,
        sheetShadowElevation = 8.dp,
        sheetDragHandle = null,
        sheetContent = {
            // ── 드래그 핸들 + 결과 수 ──────────────────────────────────────────
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFDDDDDD))
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${filtered.size} places nearby",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    if (selectedFilter != null) {
                        Text(
                            selectedFilter!!.label,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                // ── 기관 목록 ──────────────────────────────────────────────────
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp, bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtered, key = { it.id }) { inst ->
                        InstitutionListItem(
                            institution = inst,
                            onClick = {
                                selectedInstitution = inst
                                showDetailSheet = true
                            }
                        )
                    }
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // ── TODO: 지도 영역 (Mapbox 연동 후 교체) ──────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE8F0E8)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Map, contentDescription = null,
                        tint = Color(0xFFAAAAAA), modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Map area (Mapbox)", color = Color(0xFFAAAAAA), fontSize = 14.sp)
                    Text("TODO: offline tile 연동", color = Color(0xFFBBBBBB), fontSize = 12.sp)
                }
            }

            // ── 상단 검색바 + 필터칩 ────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
            ) {
                // 로고
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.95f))
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(R.drawable.ic_logo_qos),
                        contentDescription = "QOS Logo",
                        modifier = Modifier.height(22.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.White)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 15.sp,
                            color = TextPrimary
                        ),
                        decorationBox = { inner ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Search, contentDescription = null,
                                    tint = Color(0xFFAAAAAA), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Box {
                                    if (searchQuery.isEmpty()) {
                                        Text("Search...", fontSize = 15.sp, color = Color(0xFFAAAAAA))
                                    }
                                    inner()
                                }
                            }
                        }
                    )
                }
                CategoryFilterRow(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )
                // ── 오프라인 경고 배너 ─────────────────────────────────────
                Spacer(modifier = Modifier.height(8.dp))
                OfflineBanner()
            }
        }
    }

    // ── 기관 상세 바텀시트 ───────────────────────────────────────────────────────
    if (showDetailSheet && selectedInstitution != null) {
        ModalBottomSheet(
            onDismissRequest = { showDetailSheet = false },
            sheetState = detailSheetState
        ) {
            InstitutionDetailSheet(
                institution = selectedInstitution!!,
                onNavigate = { /* TODO: 길찾기 시작 */ },
                onDismiss = { showDetailSheet = false }
            )
        }
    }
}

@Composable
fun OfflineBanner(modifier: Modifier = Modifier) {
    // TODO: 온라인일 때 숨김
    Card(
        modifier = modifier.padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.WifiOff, contentDescription = null,
                tint = Color(0xFFFF6F00), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Information may not be up to date",
                fontSize = 12.sp, color = Color(0xFFFF6F00))
        }
    }
}

@Composable
fun CategoryFilterRow(
    selectedFilter: MapFilter?,
    onFilterSelected: (MapFilter?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ALL 제외하고 나머지만 표시
        items(MapFilter.entries.filter { it != MapFilter.ALL }) { filter ->
            val isSelected = selectedFilter == filter
            Image(
                painter = painterResource(
                    if (isSelected) filter.iconPressedRes!! else filter.iconRes!!
                ),
                contentDescription = filter.label,
                modifier = Modifier
                    .height(34.dp)
                    // 이미 선택된 칩 다시 누르면 선택 해제 (전체 보기)
                    .clickable { onFilterSelected(if (isSelected) null else filter) }
            )
        }
    }
}

@Composable
fun InstitutionListItem(institution: Institution, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(institution.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(institution.distance, fontSize = 12.sp, color = TextSecondary)
            }
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(institution.status.color)
            )
        }
    }
}

@Composable
fun InstitutionDetailSheet(
    institution: Institution,
    onNavigate: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(modifier = Modifier.padding(24.dp)) {
        // 이름 + 상태
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(institution.name, fontSize = 18.sp, fontWeight = FontWeight.Bold,
                color = TextPrimary, modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(institution.status.color.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(institution.status.label, fontSize = 12.sp, color = institution.status.color,
                    fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 거리 + 운영여부
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.LocationOn, contentDescription = null,
                tint = TextSecondary, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(institution.distance, fontSize = 13.sp, color = TextSecondary)
            Spacer(modifier = Modifier.width(16.dp))
            Icon(Icons.Filled.AccessTime, contentDescription = null,
                tint = if (institution.isOpen) StatusGreen else StatusRed,
                modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (institution.isOpen) "Open" else "Closed",
                fontSize = 13.sp, color = if (institution.isOpen) StatusGreen else StatusRed)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 서비스 목록
        Text("Services", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        institution.services.forEach { service ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                Icon(Icons.Filled.Check, contentDescription = null,
                    tint = StatusGreen, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(service, fontSize = 13.sp, color = TextPrimary)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 길찾기 버튼
        Button(
            onClick = onNavigate,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = QOSRed),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.Navigation, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Navigate", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true, name = "Map Screen")
@Composable
fun MapScreenPreview() {
    QOSTheme { MapScreen(navController = rememberNavController()) }
}

@Preview(showBackground = true, showSystemUi = true, name = "Map Screen – Shelter Filter")
@Composable
fun MapScreenShelterPreview() {
    QOSTheme { MapScreen(navController = rememberNavController(), initialFilter = MapFilter.CAMP) }
}

@Preview(showBackground = true, name = "Institution List Item")
@Composable
fun InstitutionListItemPreview() {
    QOSTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            InstitutionListItem(institution = sampleInstitutions[0], onClick = {})
        }
    }
}

@Preview(showBackground = true, name = "Institution Detail Sheet")
@Composable
fun InstitutionDetailSheetPreview() {
    QOSTheme {
        InstitutionDetailSheet(
            institution = sampleInstitutions[0],
            onNavigate = {},
            onDismiss = {}
        )
    }
}
