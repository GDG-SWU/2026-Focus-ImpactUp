package com.example.focus.domain.status.controller;

import com.example.focus.domain.status.service.StatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class StatusController {

    private final StatusService statusService;

    public StatusController(com.example.focus.domain.status.service.StatusService statusService) {
        this.statusService = statusService;
    }

    // 네트워크 상태 배너 조회
    @GetMapping("/status/network")
    public ResponseEntity<Map<String, Object>> getNetworkStatus() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("online", true);
        response.put("last_known_lat", 15.5517);
        response.put("last_known_lng", 32.5324);
        response.put("updated_at", LocalDateTime.now().toString());
        response.put("offline", false);

        return ResponseEntity.ok()
                .header("X-Offline-Cache", "false")
                .body(response);
    }

    // 긴급 퀵 버튼 카테고리 조회
    @GetMapping("/home/quick-categories")
    public ResponseEntity<Map<String, Object>> getQuickCategories() {
        List<Map<String, Object>> categories = new ArrayList<>();

        categories.add(createCategory("medical", "의료", "medical_cross", "app://map?category=medical"));
        categories.add(createCategory("food", "음식", "food_packet", "app://map?category=food"));
        categories.add(createCategory("water", "식수", "water_drop", "app://map?category=water"));
        categories.add(createCategory("shelter", "쉼터", "shelter_tent", "app://map?category=shelter"));

        Map<String, Object> response = new HashMap<>();
        response.put("categories", categories);
        response.put("offline", false);

        return ResponseEntity.ok().body(response);
    }

    // 생존 행동 제안 조회
    @GetMapping("/guide/survival-actions")
    public ResponseEntity<com.example.focus.domain.status.dto.SurvivalActionsResponseDto> getSurvivalActions(
            @RequestParam(required = false, defaultValue = "stable") String stage,
            @RequestParam(required = false) Double location_lat,
            @RequestParam(required = false) Double location_lng) {

        return ResponseEntity.ok(statusService.getSurvivalActions(stage, location_lat, location_lng));
    }

    private Map<String, Object> createCategory(String id, String label, String icon, String link) {
        Map<String, Object> cat = new LinkedHashMap<>();
        cat.put("id", id);
        cat.put("label", label);
        cat.put("icon", icon);
        cat.put("deep_link", link);
        return cat;
    }
}