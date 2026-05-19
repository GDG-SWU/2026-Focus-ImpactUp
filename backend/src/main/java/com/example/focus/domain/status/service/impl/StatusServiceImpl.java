package com.example.focus.domain.status.service.impl;

import com.example.focus.domain.status.service.StatusService;
import com.example.focus.domain.status.dto.SurvivalActionsResponseDto;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class StatusServiceImpl implements StatusService {

    @Override
    public SurvivalActionsResponseDto getSurvivalActions(String stage, Double lat, Double lng) {
        List<Map<String, Object>> actions = new ArrayList<>();

        // 하드코딩 데이터 구축
        Map<String, Object> action = new LinkedHashMap<>();
        if ("dehydration_risk".equals(stage)) {
            action.put("priority", 1);
            action.put("label", "식수 보급소로 이동하세요.");
            action.put("action_type", "navigate");
            action.put("target_facility_category", "water");
        } else {
            action.put("priority", 1);
            action.put("label", "주변 안전 지대를 확인하고 NGO 구호소의 안내를 따르세요.");
            action.put("action_type", "info");
            action.put("target_facility_category", "ngo");
        }
        actions.add(action);

        return new SurvivalActionsResponseDto(stage, actions, false, LocalDateTime.now().toString());
    }
}
