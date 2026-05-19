package com.example.focus.domain.ocr.service.impl;

import com.example.focus.domain.ocr.entity.OcrDangerLexicon;
import com.example.focus.domain.user.entity.User;
import com.example.focus.domain.user.entity.HealthProfile;
import com.example.focus.domain.ocr.dto.MatchedRiskDto;
import com.example.focus.domain.ocr.dto.RiskCheckRequestDto;
import com.example.focus.domain.ocr.dto.RiskCheckResponseDto;
import com.example.focus.domain.ocr.repository.OcrDangerLexiconRepository;
import com.example.focus.domain.user.repository.UserRepository;
import com.example.focus.domain.ocr.service.OcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OcrServiceImpl implements OcrService {

    private final OcrDangerLexiconRepository ocrDangerLexiconRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public RiskCheckResponseDto evaluateOcrTextWithUserProfile(RiskCheckRequestDto request) {
        String upperOcrText = request.getOcrText().toUpperCase();
        List<MatchedRiskDto> matchedRisks = new ArrayList<>();

        User user = userRepository.findById(request.getUserId().toString())
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

        if (!userHealthCodes.isEmpty()) {
            List<OcrDangerLexicon> dangerLexicons = ocrDangerLexiconRepository.findByMappingCodeIn(userHealthCodes);

            // 교차 매핑 루프 연산 실행
            for (OcrDangerLexicon lexicon : dangerLexicons) {
                String dangerSubstance = lexicon.getSubstanceName().toUpperCase();


                if (upperOcrText.contains(dangerSubstance)) {

                    String matchedField = lexicon.getTargetType().toLowerCase();

                    String warningMsg = String.format("%s와(과) 연관된 위험 항목이 감지되었습니다. 본 약물/성분의 복용 및 접근에 주의하십시오.", lexicon.getSubstanceName());
                    if ("allergy".equals(matchedField)) {
                        warningMsg = String.format("%s 알레르기가 있습니다. 이 약물을 복용하지 마세요.", lexicon.getSubstanceName());
                    }

                    matchedRisks.add(new MatchedRiskDto(
                            lexicon.getSubstanceName(),
                            matchedField,
                            lexicon.getMappingCode(),
                            warningMsg
                    ));
                }
            }
        }

        boolean riskDetected = !matchedRisks.isEmpty();
        String riskLevel = riskDetected ? "critical" : "none";

        return new RiskCheckResponseDto(
                riskDetected,
                riskLevel,
                matchedRisks,
                riskDetected,
                riskDetected,
                true
        );
    }
}