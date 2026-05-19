package com.example.focus.domain.map.service;

import com.example.focus.domain.map.entity.Facility;
import com.example.focus.domain.map.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public List<Map<String, Object>> getAllFacilitiesForMap() {
        List<Facility> facilityList = facilityRepository.findAll();

        List<Map<String, Object>> resultList = new ArrayList<>();

        for (Facility f : facilityList) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", f.getId());
            map.put("name", f.getName());
            map.put("category", f.getCategory());
            map.put("lat", f.getLatitude());     // latitude -> lat
            map.put("lng", f.getLongitude());    // longitude -> lng
            map.put("distance_m", 0);
            map.put("availability", f.getAvailability());
            map.put("operating", f.isOperating());

            resultList.add(map);
        }

        return resultList;
    }

    /**
     * 1. 기관 상세 조회 로직 (DB 연동)
     */
    public Map<String, Object> getFacilityDetail(String id) {
        // DB에서 ID로 조회, 없을 경우 404 에러
        Facility f = facilityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FACILITY_NOT_FOUND"));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", f.getId());
        response.put("name", f.getName());
        response.put("category", f.getCategory());
        response.put("lat", f.getLatitude());
        response.put("lng", f.getLongitude());
        response.put("distance_m", 0); // 거리 계산용 임시 값

        response.put("address", "Avenida Juan Carlos I, Los Cristianos, Arona, Tenerife");
        response.put("operating", f.isOperating());
        response.put("operating_hours", "08:00-20:00"); // 하드코딩 필드는 나중에 엔티티 확장 가능
        response.put("availability", f.getAvailability());
        response.put("services", Arrays.asList("emergency_care", "pharmacy"));
        response.put("contact_phone", "+34-922-752000");
        response.put("offline", false);
        response.put("cached_at", LocalDateTime.now().toString());

        return response;
    }

    /**
     * 2. 기관 가용 상태 조회 로직 (DB 연동)
     */
    public Map<String, Object> getFacilityStatus(String id) {
        Facility f = facilityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FACILITY_NOT_FOUND"));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("facility_id", f.getId());
        response.put("availability", f.getAvailability()); // DB에 저장된 상태값 연동 ('available', 'crowded' 등)
        response.put("capacity_current", f.getAvailability().equals("crowded") ? 87 : 20); // 상태 기반 동적 가상 데이터
        response.put("capacity_max", 120);
        response.put("wait_time_min", f.getAvailability().equals("crowded") ? 25 : 5);
        response.put("last_updated", LocalDateTime.now().toString());
        response.put("offline", false);
        response.put("stale_warning", false);

        return response;
    }
}