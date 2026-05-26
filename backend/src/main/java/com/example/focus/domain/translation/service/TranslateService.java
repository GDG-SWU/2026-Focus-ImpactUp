package com.example.focus.domain.translation.service;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.springframework.stereotype.Service;

@Service
public class TranslateService {

    /**
     * 텍스트를 대상 언어로 번역합니다.
     *
     * @param text          원문 텍스트
     * @param targetLangCode 내부 언어 코드 (ar, fr, wo, ma, fu)
     * @param sourceLangCode 소스 언어 코드 (null이면 자동 감지)
     * @return 번역된 텍스트, 미지원 언어면 null
     */
    public String translateText(String text, String targetLangCode, String sourceLangCode) {
        String googleCode = toGoogleLangCode(targetLangCode);
        if (googleCode == null) return null;

        try {
            Translate translate = TranslateOptions.newBuilder()
                    .setQuotaProjectId("project-c19a8e1f-a60d-4cdb-93f")
                    .build()
                    .getService();

            Translate.TranslateOption target = Translate.TranslateOption.targetLanguage(googleCode);
            Translation translation;

            if (sourceLangCode != null && !sourceLangCode.isBlank()) {
                translation = translate.translate(text, target,
                        Translate.TranslateOption.sourceLanguage(sourceLangCode));
            } else {
                translation = translate.translate(text, target);
            }

            return translation.getTranslatedText();

        } catch (Exception e) {
            System.out.println("[TranslateService] 번역 API 오류: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /** 영어 원문 → 대상 언어 번역 (소스 고정 en) */
    public String translateFromEnglish(String text, String targetLangCode) {
        return translateText(text, targetLangCode, "en");
    }

    /**
     * 내부 언어 코드 → Google Cloud Translation 언어 코드 변환
     * Mandinka(ma), Fula(fu)는 Google Translate 미지원 → null 반환
     */
    private String toGoogleLangCode(String code) {
        if (code == null) return null;
        switch (code.toLowerCase()) {
            case "ar": return "ar";   // 아랍어
            case "fr": return "fr";   // 프랑스어
            case "wo": return "wo";   // 월로프어 (Google 지원)
            case "ma": return null;   // 만딩카어 - 미지원
            case "fu": return null;   // 풀라니어 - 미지원
            default:   return null;
        }
    }
}
