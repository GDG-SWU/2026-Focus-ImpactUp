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
        
        // 프론트엔드가 보낸 카테고리 판별 (없으면 "all")
        String currentCategory = category != null ? category : "all";
        response.put("category", currentCategory);

        List<Map<String, Object>> cards = new ArrayList<>();

        // 1. 의료(medical) 관련 카드 분기
        if (currentCategory.equals("all") || currentCategory.equals("medical")) {
            Map<String, Object> cardMedical1 = new LinkedHashMap<>();
            cardMedical1.put("id", "card_medical_001");
            cardMedical1.put("situation", "나는 당뇨병이 있습니다");
            Map<String, String> trans1 = new HashMap<>();
            trans1.put("ar", "أنا مريض بالسكري");
            trans1.put("fr", "Je suis diabétique");
            trans1.put("wo", "Dafa am sukar");
            cardMedical1.put("translations", trans1);
            cardMedical1.put("tts_available", true);
            cardMedical1.put("usage_count", 0);
            cards.add(cardMedical1);

            Map<String, Object> cardMedical2 = new LinkedHashMap<>();
            cardMedical2.put("id", "card_medical_002");
            cardMedical2.put("situation", "의사가 필요합니다. 도와주세요.");
            Map<String, String> trans2 = new HashMap<>();
            trans2.put("ar", "أحتاج إلى طبيب. ساعدني");
            trans2.put("fr", "J'ai besoin d'un médecin. S'il vous plaît, aidez-moi.");
            trans2.put("wo", "Dama soxla dotoor. Dimbali ma");
            cardMedical2.put("translations", trans2);
            cardMedical2.put("tts_available", true);
            cardMedical2.put("usage_count", 0);
            cards.add(cardMedical2);
        }

        // 2. 쉼터/대피소(shelter) 관련 카드 분기
        if (currentCategory.equals("all") || currentCategory.equals("shelter")) {
            Map<String, Object> cardShelter = new LinkedHashMap<>();
            cardShelter.put("id", "card_shelter_001");
            cardShelter.put("situation", "가장 가까운 대피소나 쉼터는 어디에 있습니까?");
            Map<String, String> transShelter = new HashMap<>();
            transShelter.put("ar", "أين أقرب ملجأ؟");
            transShelter.put("fr", "Où se trouve le refuge le plus proche?");
            transShelter.put("wo", "Ana bërëb u taax mi gën jége?");
            cardShelter.put("translations", transShelter);
            cardShelter.put("tts_available", true);
            cardShelter.put("usage_count", 0);
            cards.add(cardShelter);
        }

        // 3. 음식/식수(food/water) 관련 카드 분기
        if (currentCategory.equals("all") || currentCategory.equals("food") || currentCategory.equals("water")) {
            Map<String, Object> cardFood = new LinkedHashMap<>();
            cardFood.put("id", "card_food_001");
            cardFood.put("situation", "식수와 음식을 받을 수 있는 곳이 어디인가요?");
            Map<String, String> transFood = new HashMap<>();
            transFood.put("ar", "أين يمكنني الحصول على الماء والطعام؟");
            transFood.put("fr", "Où puis-je obtenir de l'eau et de la nourriture?");
            transFood.put("wo", "Ana bërëb bu may jote ndox ak ñam?");
            cardFood.put("translations", transFood);
            cardFood.put("tts_available", true);
            cardFood.put("usage_count", 0);
            cards.add(cardFood);
        }

        /* * [참고: 추후 실제 DB 데이터 완전 자동 연동 시]
         * 현재 프로젝트에 등록되어 있는 MedicalTerm 엔티티 및 레포지토리를 활용해 
         * 데이터를 동적으로 긁어오고 싶다면 아래 형태로 코드를 확장할 수 있습니다.
         * * List<MedicalTerm> terms = medicalTermRepository.findByCategory(currentCategory);
         * // 이후 반복문을 돌며 프론트엔드가 요구하는 JSON 구조(Map)로 빌드하여 cards.add() 수행
         */

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

        if (user.getHealthProfile() != null) {
            response.put("blood_type", user.getHealthProfile().getBloodType());
        } else {
            response.put("blood_type", "unknown"); // 정보가 없을 때
        }

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
