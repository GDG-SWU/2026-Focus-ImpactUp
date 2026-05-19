package com.example.focus.domain.map.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/facilities")
public class FacilityController {

    // 기관 목록 조회 (반경 반경 검색 필터)
    @GetMapping
    public ResponseEntity<Map<String, Object>> getFacilities(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false, defaultValue = "5000") Integer radius) {

        Map<String, Object> response = new LinkedHashMap<>();

        // TODO: MySQL 활용하여 실제 DB 조회 연동
        List<Map<String, Object>> facilities = new ArrayList<>();
        Map<String, Object> mockFacility = new LinkedHashMap<>();
        mockFacility.put("id", UUID.randomUUID().toString());
        mockFacility.put("name", "Al-Hilal Camp Medical Post");
        mockFacility.put("category", "hospital");
        mockFacility.put("lat", 15.5530);
        mockFacility.put("lng", 32.5340);
        mockFacility.put("distance_m", 320);
        mockFacility.put("availability", "available");
        mockFacility.put("operating", true);
        facilities.add(mockFacility);

        response.put("facilities", facilities);
        response.put("total", facilities.size());
        response.put("offline", false);
        response.put("cached_at", LocalDateTime.now().toString());

        return ResponseEntity.ok().body(response);
    }

    // 기관 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getFacilityDetail(@PathVariable UUID id) {
        // TODO: DB에서 ID로 조회 실패 시 FACILITY_NOT_FOUND(404) 커스텀 에러 처리
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", id.toString());
        response.put("name", "Al-Hilal Camp Medical Post");
        response.put("category", "hospital");
        response.put("lat", 15.5530);
        response.put("lng", 32.5340);
        response.put("distance_m", 320);
        response.put("address", "Block 4, Al-Hilal Camp, Khartoum");
        response.put("operating", true);
        response.put("operating_hours", "08:00-20:00");
        response.put("availability", "available");
        response.put("services", Arrays.asList("emergency_care", "pharmacy"));
        response.put("contact_phone", "+249-900-123456");
        response.put("offline", false);
        response.put("cached_at", LocalDateTime.now().toString());

        return ResponseEntity.ok().body(response);
    }

    // 기관 가용 상태 조회
    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> getFacilityStatus(@PathVariable UUID id) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("facility_id", id.toString());
        response.put("availability", "crowded");
        response.put("capacity_current", 87);
        response.put("capacity_max", 120);
        response.put("wait_time_min", 25);
        response.put("last_updated", LocalDateTime.now().toString());
        response.put("offline", false);
        response.put("stale_warning", false); // 온라인 실시간 데이터이므로 false

        return ResponseEntity.ok().body(response);
    }
}