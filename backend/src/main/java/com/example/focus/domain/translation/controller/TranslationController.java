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

import com.example.focus.domain.translation.entity.MedicalTerm;
import com.example.focus.domain.translation.repository.MedicalTermRepository;

@RestController
@RequestMapping("/api/v1")
public class TranslationController {

    private final TtsService ttsService;
    private final UserRepository userRepository;
    private final MedicalTermRepository medicalTermRepository;

    public TranslationController(TtsService ttsService, UserRepository userRepository, MedicalTermRepository medicalTermRepository) {
        this.ttsService = ttsService;
        this.userRepository = userRepository;
        this.medicalTermRepository = medicalTermRepository;
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

//        response.put("blood_type", "O+");
//        response.put("companions", "Traveling with children");

        List<Map<String, Object>> conditionsTranslated = new ArrayList<>();
        List<Map<String, Object>> allergiesTranslated = new ArrayList<>();

        List<String> fixedLanguages = List.of("ar", "fr", "wo", "ma", "fu");

        Map<String, String> dynamicTranslations = new LinkedHashMap<>();

        Map<String, String> prefixMap = Map.of(
                "ar", "معلوماتي الصحية: ",
                "fr", "Mes informations medicales: ",
                "wo", "Mbaaxu wér-gi-yaram: ",
                "ma", "N ninsi nali: ",
                "fu", "Jam wér-gi-yaram: "
        );

        for (String lang : fixedLanguages) {
            dynamicTranslations.put(lang, prefixMap.getOrDefault(lang, ""));
        }

        if (user.getHealthProfile() != null) {

            // 기저질환
            if (user.getHealthProfile().getConditions() != null) {
                for (String conditionCode : user.getHealthProfile().getConditions()) {
                    Map<String, Object> conditionNode = new LinkedHashMap<>();
                    conditionNode.put("code", conditionCode);

                    List<MedicalTerm> terms = medicalTermRepository.findByTermCode(conditionCode);
                    Map<String, String> labelMap = new LinkedHashMap<>();
                    for (String lang : fixedLanguages) { labelMap.put(lang, ""); }

                    for (MedicalTerm term : terms) {
                        if (fixedLanguages.contains(term.getLangCode())) {
                            labelMap.put(term.getLangCode(), term.getTranslatedText());

                            dynamicTranslations.computeIfPresent(term.getLangCode(), (lang, sentence) -> sentence + term.getTranslatedText() + " ");
                        }
                    }
                    conditionNode.put("label", labelMap);
                    conditionsTranslated.add(conditionNode);
                }
            }

            // 알레르기
            if (user.getHealthProfile().getAllergies() != null) {
                for (String allergyCode : user.getHealthProfile().getAllergies()) {
                    Map<String, Object> allergyNode = new LinkedHashMap<>();
                    allergyNode.put("code", allergyCode);

                    List<MedicalTerm> terms = medicalTermRepository.findByTermCode(allergyCode);
                    Map<String, String> labelMap = new LinkedHashMap<>();
                    for (String lang : fixedLanguages) { labelMap.put(lang, ""); }

                    for (MedicalTerm term : terms) {
                        if (fixedLanguages.contains(term.getLangCode())) {
                            labelMap.put(term.getLangCode(), term.getTranslatedText());

                            dynamicTranslations.computeIfPresent(term.getLangCode(), (lang, sentence) -> sentence + term.getTranslatedText() + " ");
                        }
                    }
                    allergyNode.put("label", labelMap);
                    allergiesTranslated.add(allergyNode);
                }
            }
        }

        response.put("translations", dynamicTranslations);

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
