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
        if (healthProfile.getConditions() != null) userHealthCodes.addAll(healthProfile.getConditions());
        if (healthProfile.getAllergies() != null) userHealthCodes.addAll(healthProfile.getAllergies());

        if (!userHealthCodes.isEmpty()) {
            List<OcrDangerLexicon> dangerLexicons = ocrDangerLexiconRepository.findByMappingCodeIn(userHealthCodes);

            for (OcrDangerLexicon lexicon : dangerLexicons) {
                String dangerSubstance = lexicon.getSubstanceName().toUpperCase();
                boolean isMatched = upperOcrText.contains(dangerSubstance);

                // OCR 오탈자 방지: 퍼지 매칭 알고리즘
                if (!isMatched) {
                    String[] ocrWords = upperOcrText.split("[\\s,\\.\\-]+");
                    for (String word : ocrWords) {
                        if (Math.abs(word.length() - dangerSubstance.length()) <= 3) {
                            int distance = calculateLevenshteinDistance(word, dangerSubstance);
                            int allowedError = Math.max(1, (int)(dangerSubstance.length() * 0.2)); // 길이의 20% 오차 허용
                            if (distance <= allowedError) {
                                isMatched = true;
                                break;
                            }
                        }
                    }
                }

                if (isMatched) {
                    String matchedField = lexicon.getTargetType().toLowerCase();
                    String warningMsg = "allergy".equals(matchedField)
                            ? String.format("%s 알레르기가 있습니다. 이 약물을 복용하지 마세요.", lexicon.getSubstanceName())
                            : String.format("%s와(과) 연관된 위험 항목이 감지되었습니다. 주의하십시오.", lexicon.getSubstanceName());

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
        return new RiskCheckResponseDto(
                riskDetected,
                riskDetected ? "critical" : "none",
                matchedRisks,
                riskDetected,
                riskDetected,
                true
        );
    }

    // 편집 거리 계산 메서드
    private int calculateLevenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) dp[i][j] = j;
                else if (j == 0) dp[i][j] = i;
                else dp[i][j] = s1.charAt(i - 1) == s2.charAt(j - 1) ? dp[i - 1][j - 1]
                        : 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
            }
        }
        return dp[s1.length()][s2.length()];
    }
}