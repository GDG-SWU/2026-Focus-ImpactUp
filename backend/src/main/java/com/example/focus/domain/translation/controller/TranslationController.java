package com.example.focus.domain.translation.controller;

import com.example.focus.domain.translation.service.TtsService;
import com.example.focus.domain.user.entity.User;
import com.example.focus.domain.user.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class TranslationController {

    private final TtsService ttsService;
    private final UserRepository userRepository;

    public TranslationController(TtsService ttsService, UserRepository userRepository) {
        this.ttsService = ttsService;
        this.userRepository = userRepository;
    }

    // 오프라인 번역 카드 목록 조회
    @GetMapping("/cards/phrasebook")
    public ResponseEntity<Map<String, Object>> getPhrasebook(@RequestParam(required = false) String category) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("category", category != null ? category : "all");

        List<Map<String, Object>> cards = new ArrayList<>();
        Map<String, Object> card1 = new LinkedHashMap<>();
        card1.put("id", "card_medical_001");
        card1.put("situation", "나는 당뇨병이 있습니다");

        Map<String, String> translations = new HashMap<>();
        translations.put("ar", "أنا مريض بالسكري");
        translations.put("fr", "Je suis diabétique");
        translations.put("wo", "Dafa am sukar");
        card1.put("translations", translations);
        card1.put("tts_available", true);
        card1.put("usage_count", 0);
        cards.add(card1);

        response.put("cards", cards);
        response.put("offline", false);

        return ResponseEntity.ok().body(response);
    }

    // 내 정보 SOS 카드 조회
    @GetMapping("/cards/sos")
    public ResponseEntity<Map<String, Object>> getSosCard(
            @RequestHeader("X-User-Id") String userIdStr) {

        User user = userRepository.findById(userIdStr)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. ID: " + userIdStr));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("user_id", user.getId());
        response.put("generated_at", LocalDateTime.now().toString());

        Map<String, String> translationMap = new HashMap<>();
        translationMap.put("asthma", "Asma");
        translationMap.put("diabetes", "Diabetes");
        translationMap.put("peanut_allergy", "Alergia al maní");

        List<String> conditionsTranslated = new ArrayList<>();
        List<String> allergiesTranslated = new ArrayList<>();

        if (user.getHealthProfile() != null) {
            if (user.getHealthProfile().getConditions() != null) {
                for (String condition : user.getHealthProfile().getConditions()) {
                    conditionsTranslated.add(translationMap.getOrDefault(condition, condition));
                }
            }
            if (user.getHealthProfile().getAllergies() != null) {
                for (String allergy : user.getHealthProfile().getAllergies()) {
                    allergiesTranslated.add(translationMap.getOrDefault(allergy, allergy));
                }
            }
        }

        Map<String, String> translations = new HashMap<>();
        translations.put("ar", "لدي مرض السكري وحساسية من البنسلين.");
        translations.put("fr", "J'ai le diabète et une allergie à la pénicilline.");
        response.put("translations", translations);

        response.put("conditions_translated", conditionsTranslated);
        response.put("allergies_translated", allergiesTranslated);

        response.put("offline", false);

        return ResponseEntity.ok().body(response);
    }

    // 번역 카드 TTS 재생
    @PostMapping("/tts/card")
    public ResponseEntity<byte[]> generateTts(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        String language = request.get("language");

        // 입력값 유효성 검사
        if (text == null || language == null || text.trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            byte[] audioBytes = ttsService.generateSpeech(text, language);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
            headers.setContentLength(audioBytes.length);
            headers.add("X-Offline-Cache", "false");

            return new ResponseEntity<>(audioBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            // 503
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    // 최근 사용 카드 조회
    @GetMapping("/cards/recent")
    public ResponseEntity<Map<String, Object>> getRecentCards(
            @RequestHeader("X-User-Id") String userIdStr) {

        User user = userRepository.findById(userIdStr)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. ID: " + userIdStr));

        Map<String, Object> response = new HashMap<>();

        List<com.example.focus.domain.translation.dto.PhraseCardDto> mockCards = new ArrayList<>();

        Map<String, String> translation1 = new HashMap<>();
        translation1.put("ar", "أحتاج إلى دواء");
        translation1.put("wo", "Dama soxla garab");

        Map<String, String> translation2 = new HashMap<>();
        translation2.put("ar", "أين 유동인구 구호소?");
        translation2.put("wo", "Ana bërëb u ndimëll mi?");

        mockCards.add(new com.example.focus.domain.translation.dto.PhraseCardDto("card_01", "의료 요청", translation1, true));
        mockCards.add(new com.example.focus.domain.translation.dto.PhraseCardDto("card_02", "위치 문의", translation2, true));

        response.put("user_id", user.getId());
        response.put("cards", mockCards);
        response.put("offline", false);

        return ResponseEntity.ok().body(response);
    }
}
