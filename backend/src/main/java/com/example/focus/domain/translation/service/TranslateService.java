package com.example.focus.domain.translation.service;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class TranslateService {

    private final WebClient webClient = WebClient.builder().build();

    /**
     * 텍스트를 대상 언어로 번역합니다.
     * GCP Cloud Translation 시도 → 실패 시 MyMemory API fallback
     *
     * @param text           원문 텍스트
     * @param targetLangCode 내부 언어 코드 (ar, fr, wo, ma, fu)
     * @param sourceLangCode 소스 언어 코드 (null이면 자동 감지)
     * @return 번역된 텍스트, 미지원 언어면 null
     */
    public String translateText(String text, String targetLangCode, String sourceLangCode) {
        String googleCode = toGoogleLangCode(targetLangCode);
        if (googleCode == null) return null;

        // 1차: GCP Cloud Translation
        String gcpResult = tryGcpTranslate(text, googleCode, sourceLangCode);
        if (gcpResult != null) return gcpResult;

        // 2차: MyMemory API fallback (무료, 인증 불필요)
        String sourceForMemory = (sourceLangCode != null && !sourceLangCode.isBlank()) ? sourceLangCode : "en";
        return tryMyMemoryTranslate(text, sourceForMemory, googleCode);
    }

    /** 영어 원문 → 대상 언어 번역 (소스 고정 en) */
    public String translateFromEnglish(String text, String targetLangCode) {
        return translateText(text, targetLangCode, "en");
    }

    // ── GCP Cloud Translation ─────────────────────────────────────────────────

    private String tryGcpTranslate(String text, String googleCode, String sourceLangCode) {
        try {
            // Cloud Run에서 Application Default Credentials 자동 사용 (quotaProjectId 제거)
            Translate translate = TranslateOptions.getDefaultInstance().getService();

            Translate.TranslateOption target = Translate.TranslateOption.targetLanguage(googleCode);
            Translation translation;

            if (sourceLangCode != null && !sourceLangCode.isBlank()) {
                translation = translate.translate(text, target,
                        Translate.TranslateOption.sourceLanguage(sourceLangCode));
            } else {
                translation = translate.translate(text, target);
            }

            String result = translation.getTranslatedText();
            System.out.println("[TranslateService] GCP 번역 성공: " + googleCode);
            return result;

        } catch (Exception e) {
            System.out.println("[TranslateService] GCP 번역 실패, MyMemory로 전환: " + e.getMessage());
            return null;
        }
    }

    // ── MyMemory API fallback ─────────────────────────────────────────────────

    private String tryMyMemoryTranslate(String text, String sourceLang, String targetLang) {
        // Wolof(wo)는 MyMemory에서 지원하지 않음
        if ("wo".equals(targetLang)) return null;

        try {
            // langpair의 | 기호는 URL 인코딩하지 않음 (MyMemory 요구사항)
            // 공백은 + 유지 (MyMemory는 %20 미지원), 아포스트로피만 복원
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8)
                    .replace("%27", "'");   // apostrophe: %27 → ' (MyMemory 호환)
            String url = "https://api.mymemory.translated.net/get?q=" + encodedText
                    + "&langpair=" + sourceLang + "|" + targetLang;

            Map<?, ?> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(java.time.Duration.ofSeconds(8));

            if (response == null) return null;

            // responseStatus 200 확인
            Object responseStatus = response.get("responseStatus");
            if (responseStatus == null || !responseStatus.toString().equals("200")) {
                System.out.println("[TranslateService] MyMemory 응답 오류: status=" + responseStatus);
                return null;
            }

            Object responseData = response.get("responseData");
            if (!(responseData instanceof Map)) return null;

            Object translatedText = ((Map<?, ?>) responseData).get("translatedText");
            if (translatedText == null) return null;

            String result = translatedText.toString().trim();
            // 번역 실패 시 원문 반환 또는 오류 메시지 걸러냄
            if (result.isBlank() || result.equalsIgnoreCase(text.trim())) return null;
            if (result.toUpperCase().startsWith("INVALID")) return null;

            System.out.println("[TranslateService] MyMemory 번역 성공: " + sourceLang + "|" + targetLang);
            return result;

        } catch (Exception e) {
            System.out.println("[TranslateService] MyMemory 번역 실패: " + e.getMessage());
            return null;
        }
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
            case "wo": return "wo";   // 월로프어 (Google 지원, MyMemory 미지원)
            case "ma": return null;   // 만딩카어 - 미지원
            case "fu": return null;   // 풀라니어 - 미지원
            default:   return null;
        }
    }
}
