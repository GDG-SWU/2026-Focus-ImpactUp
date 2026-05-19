package com.example.focus.domain.map.controller;

import com.example.focus.domain.map.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/facilities")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    // 기관 목록 조회
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getFacilities() {
        List<Map<String, Object>> facilities = facilityService.getAllFacilitiesForMap();

        return ResponseEntity.ok(facilities);
    }

    /**
     * 기관 상세 조회 (GET /api/v1/facilities/{id})
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getFacilityDetail(@PathVariable UUID id) {
        try {
            Map<String, Object> response = facilityService.getFacilityDetail(id.toString());
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            // TODO: 추후 전역 ExceptionHandler(@ControllerAdvice) 구조로 공통화할 수도..
            if ("FACILITY_NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(404).build(); // 404
            }
            throw e;
        }
    }

    /**
     * 기관 가용 상태 조회 (GET /api/v1/facilities/{id}/status)
     */
    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> getFacilityStatus(@PathVariable UUID id) {
        try {
            Map<String, Object> response = facilityService.getFacilityStatus(id.toString());
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            if ("FACILITY_NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(404).build();
            }
            throw e;
        }
    }
}