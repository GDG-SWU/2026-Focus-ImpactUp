package com.example.focus.service.impl;

import com.example.focus.domain.Facility;
import com.example.focus.dto.DeadReckoningRequestDto;
import com.example.focus.dto.DeadReckoningResponseDto;
import com.example.focus.dto.FacilityListResponseDto;
import com.example.focus.dto.FacilitySummaryDto;
import com.example.focus.repository.FacilityRepository;
import com.example.focus.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapServiceImpl implements MapService {

    private final FacilityRepository facilityRepository;

    @Override
    public DeadReckoningResponseDto calculateDeadReckoning(DeadReckoningRequestDto request) {
        double stride = (request.getStrideLengthM() != null) ? request.getStrideLengthM() : 0.75;
        double totalDistanceM = request.getSteps() * stride;

        double headingRad = Math.toRadians(request.getHeading());
        double deltaLat = (totalDistanceM * Math.cos(headingRad)) / 111000.0;
        double deltaLng = (totalDistanceM * Math.sin(headingRad)) / (111000.0 * Math.cos(Math.toRadians(request.getLastLat())));

        return new DeadReckoningResponseDto(
                request.getLastLat() + deltaLat,
                request.getLastLng() + deltaLng,
                0.72,
                "dead_reckoning",
                true
        );
    }

    @Override
    @Transactional(readOnly = true)
    public FacilityListResponseDto getFacilities(String category, Double lat, Double lng, int radius) {
        // 1. 카테고리 필터 조건에 따라 DB에서 대피소 데이터 인출
        List<Facility> facilities = (category == null || category.isBlank())
                ? facilityRepository.findAll()
                : facilityRepository.findByCategory(category);

        // 2. 엔티티 구조를 DTO 구조로 스트림 변환 처리
        List<FacilitySummaryDto> dtos = facilities.stream()
                .map(f -> new FacilitySummaryDto(
                        f.getId(),
                        f.getName(),
                        f.getCategory(),
                        f.getLatitude(),
                        f.getLongitude(),
                        320, // 하버사인 공식을 적용한 실제 거리 연산 대체 가능 (우선 320미터 하드코딩 고정)
                        f.getAvailability(),
                        f.isOperating()
                ))
                .collect(Collectors.toList());

        return new FacilityListResponseDto(dtos, dtos.size(), false, Instant.now().toString());
    }
}