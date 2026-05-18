package com.example.focus.service.impl;

import com.example.focus.domain.OcrDangerLexicon;
import com.example.focus.domain.User;
import com.example.focus.domain.HealthProfile;
import com.example.focus.dto.MatchedRiskDto;
import com.example.focus.dto.RiskCheckRequestDto;
import com.example.focus.dto.RiskCheckResponseDto;
import com.example.focus.repository.OcrDangerLexiconRepository;
import com.example.focus.repository.UserRepository;
import com.example.focus.service.OcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OcrServiceImpl implements OcrService {

    private final OcrDangerLexiconRepository ocrDangerLexiconRepository;
    private final UserRepository userRepository; // UUID 기반 유저 조회용

    @Override
    @Transactional(readOnly = true)
    public RiskCheckResponseDto evaluateOcrTextWithUserProfile(RiskCheckRequestDto request) {
        String upperOcrText = request.getOcrText().toUpperCase();
        List<MatchedRiskDto> matchedRisks = new ArrayList<>();

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));

        HealthProfile healthProfile = user.getHealthProfile();

        if (healthProfile == null) {
            return new RiskCheckResponseDto(false, "none", matchedRisks, false, false, true);
        }

        List<String> userHealthCodes = new ArrayList<>();

        if (healthProfile.getConditions() != null) {
            userHealthCodes.addAll(healthProfile.getConditions());
        }
        if (healthProfile.getAllergies() != null) {
            userHealthCodes.addAll(healthProfile.getAllergies());
        }

        // 4. 합쳐진 코드가 존재할 때만 DB 쿼리 수행
        if (!userHealthCodes.isEmpty()) {
            // DB에서 해당 유저의 질환/알레르기와 매핑되는 실시간 위험 성분 단어 사전을 단 한 번의 쿼리로 인출
            List<OcrDangerLexicon> dangerLexicons = ocrDangerLexiconRepository.findByMappingCodes(userHealthCodes);

            // 5. 교차 매핑 루프 연산 실행
            for (OcrDangerLexicon lexicon : dangerLexicons) {
                String dangerSubstance = lexicon.getSubstanceName().toUpperCase(); // 데이터베이스의 성분명 (ex: "AMOXICILLIN")

                // OCR 인식 글자 안에 내 디비에 등록된 위험 성분명이 포함되어 있는지 실시간 스캔
                if (upperOcrText.contains(dangerSubstance)) {

                    // 명세서에 명시된 Enum 형태("allergy", "condition")로 소문자 치환 대응
                    String matchedField = lexicon.getTargetType().toLowerCase();

                    // 다국어 맞춤형 경고 메시지 조립 (실무에서는 언어 헤더에 따라 다국어 프로퍼티 파일 매핑 처리 가능)
                    String warningMsg = String.format("%s와(과) 연관된 위험 항목이 감지되었습니다. 본 약물/성분의 복용 및 접근에 주의하십시오.", lexicon.getSubstanceName());
                    if ("allergy".equals(matchedField)) {
                        warningMsg = String.format("%s 알레르기가 있습니다. 이 약물을 복용하지 마세요.", lexicon.getSubstanceName());
                    }

                    matchedRisks.add(new MatchedRiskDto(
                            lexicon.getSubstanceName(), // 감지된 키워드 (원문 형태)
                            matchedField,               // 매칭된 프로필 필드 유형 (allergy / condition)
                            lexicon.getMappingCode(),   // 매칭된 프로필 내부 세부 값 (ex: penicillin)
                            warningMsg                  // 클라이언트에 띄워줄 현지어 경고 메시지
                    ));
                }
            }
        }

        // 6. 최종 위험 수위(Risk Level) 판정 및 삼위일체 데이터 응답 생성
        boolean riskDetected = !matchedRisks.isEmpty();
        String riskLevel = riskDetected ? "critical" : "none"; // 위험 발견 시 즉시 critical 수위 부여 (명세서 규격)

        return new RiskCheckResponseDto(
                riskDetected,
                riskLevel,
                matchedRisks,
                riskDetected, // triggerHaptic: 위험할 때 폰에 햅틱 진동 강제 발생 플래그
                riskDetected, // triggerAlertBanner: 화면 상단 빨간 경고 배너 활성화 플래그
                true          // 온디바이스 로컬 캐시 결합 완벽 지원 플래그
        );
    }
}