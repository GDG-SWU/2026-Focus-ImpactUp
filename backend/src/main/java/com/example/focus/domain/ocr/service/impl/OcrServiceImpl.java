package com.example.focus.domain.ocr.service.impl;

import com.example.focus.domain.ocr.dto.*;
import com.example.focus.domain.ocr.entity.OcrDangerLexicon;
import com.example.focus.domain.user.entity.User;
import com.example.focus.domain.user.entity.HealthProfile;
import com.example.focus.domain.ocr.repository.OcrDangerLexiconRepository;
import com.example.focus.domain.user.repository.UserRepository;
import com.example.focus.domain.ocr.service.OcrService;
import com.example.focus.domain.translation.service.TranslateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OcrServiceImpl implements OcrService {

    private final OcrDangerLexiconRepository ocrDangerLexiconRepository;
    private final UserRepository userRepository;
    private final TranslateService translateService;
    private final WebClient.Builder webClientBuilder;

    @Override
    @Transactional
    public OcrScanResponseDto processOcrScan(MultipartFile image, String targetLanguage) {

        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("업로드된 이미지 파일이 없습니다.");
        }

        String extractedRawText = "";

        try {
            ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                    .setQuotaProjectId("project-c19a8e1f-a60d-4cdb-93f")
                    .build();

            try (ImageAnnotatorClient visionClient = ImageAnnotatorClient.create(settings)) {

                ByteString imgBytes = ByteString.readFrom(image.getInputStream());
                Image img = Image.newBuilder().setContent(imgBytes).build();

                Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();

                AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                        .addFeatures(feat)
                        .setImage(img)
                        .build();

                System.out.println("====== [GCP Vision] 구글 공식 OCR 엔진으로 이미지 송출을 시작합니다. ======");

                BatchAnnotateImagesResponse response = visionClient.batchAnnotateImages(List.of(request));
                List<AnnotateImageResponse> responses = response.getResponsesList();

                for (AnnotateImageResponse res : responses) {
                    if (res.hasError()) {
                        System.out.printf("구글 비전 내부 에러: %s%n", res.getError().getMessage());
                        // 에러가 있어도 빈 텍스트로 graceful 처리 (500 대신 빈 결과 반환)
                        break;
                    }

                    if (!res.getTextAnnotationsList().isEmpty()) {
                        extractedRawText = res.getTextAnnotationsList().get(0).getDescription();
                        break;
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("이미지 스트림 처리 중 예외 발생: " + e.getMessage());
            // 이미지 읽기 실패 → 빈 텍스트로 처리
        } catch (Exception e) {
            System.out.println("OCR API 통신 중 예외 발생 (권한 미설정 등): " + e.getMessage());
            // GCP 권한 오류 등 → 500 crash 대신 빈 텍스트로 graceful 처리
        }

        if (extractedRawText.isBlank()) {
            extractedRawText = "";
        }

        // 실제 번역 수행 (소스 언어 자동 감지 → targetLanguage)
        // 미지원 언어(ma, fu)이거나 API 오류 시 null 반환 → 프론트에서 번역 섹션 미표시
        String translatedText = null;
        if (!extractedRawText.isBlank() && targetLanguage != null && !targetLanguage.isBlank()) {
            translatedText = translateService.translateText(extractedRawText, targetLanguage, null);
        }

        List<HighlightedKeywordDto> dynamicKeywords = new ArrayList<>();
        String upperRawText = extractedRawText.toUpperCase();

        List<OcrDangerLexicon> allLexicons = ocrDangerLexiconRepository.findAll();
        for (OcrDangerLexicon lexicon : allLexicons) {
            String substance = lexicon.getSubstanceName().toUpperCase();

            if (upperRawText.contains(substance)) {
                dynamicKeywords.add(new HighlightedKeywordDto(
                        lexicon.getSubstanceName(),
                        "warning",
                        true,
                        "red"
                ));
            }
        }

        return new OcrScanResponseDto(
                extractedRawText,
                translatedText,
                dynamicKeywords,
                "참고용으로만 사용하세요. 의료 판단에 사용하지 마세요.",
                true
        );
    }


    @Override
    @Transactional(readOnly = true)
    public RiskCheckResponseDto evaluateOcrTextWithUserProfile(RiskCheckRequestDto request) {
        String upperOcrText = request.getOcrText().toUpperCase();
        List<MatchedRiskDto> matchedRisks = new ArrayList<>();

            User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + request.getUserId()));

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