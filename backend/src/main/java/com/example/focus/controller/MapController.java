package com.example.focus.controller;

import com.example.focus.dto.*;
import com.example.focus.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    /**
     * GET /map/tiles (오프라인 Mapbox 벡터 타일 정보 및 GeoJSON 반환)
     */
    @GetMapping("/map/tiles")
    public ResponseEntity<MapTilesResponseDto> getMapTiles(
            @RequestParam(required = false) String bbox,
            @RequestParam(required = false) Integer zoom) {

        String tileTemplate = "https://cdn.example.com/tiles/{z}/{x}/{y}.pbf"; // Mapbox 오프라인 템플릿
        // 가짜 GeoJSON FeatureCollection 구조 생성
        Object mockGeoJson = Collections.singletonMap("type", "FeatureCollection");

        return ResponseEntity.ok(new MapTilesResponseDto(tileTemplate, mockGeoJson, true));
    }

    /**
     * GET /api/v1/location/current (GPS 기반 현재 위치 조회)
     */
    @GetMapping("/location/current")
    public ResponseEntity<CurrentLocationResponseDto> getCurrentLocation() {
        // 프론트엔드가 즉시 파란 점 마커를 그릴 수 있도록 현재 추정 좌표 반환
        CurrentLocationResponseDto response = new CurrentLocationResponseDto(
                15.5517, 32.5324, 12.5, "gps", false
        );
        return ResponseEntity.ok(response);
    }

    /**
     * POST /location/dead-reckoning (추측 항법 기반 좌표 실시간 추정)
     */
    @PostMapping("/location/dead-reckoning")
    public ResponseEntity<DeadReckoningResponseDto> calculateDeadReckoning(
            @RequestBody DeadReckoningRequestDto request) {

        DeadReckoningResponseDto response = mapService.calculateDeadReckoning(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /facilities (주요 피난 기관 대피소 필터링 검색)
     */
    @GetMapping("/facilities")
    public ResponseEntity<FacilityListResponseDto> getFacilities(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(defaultValue = "5000") int radius) {

        FacilityListResponseDto response = mapService.getFacilities(category, lat, lng, radius);
        return ResponseEntity.ok(response);
    }
}