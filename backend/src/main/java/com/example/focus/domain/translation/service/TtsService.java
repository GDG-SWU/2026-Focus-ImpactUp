package com.example.focus.domain.translation.service;

import com.google.cloud.texttospeech.v1.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.InputStream;
import java.util.Map;

@Service
public class TtsService {

    private final WebClient webClient;

    public TtsService(WebClient.Builder webClientBuilder) {
        // AI 외부 API 연동 위한 WebClient 기본 빌드
        this.webClient = webClientBuilder.build();
    }

    public byte[] generateSpeech(String text, String languageCode) throws Exception {
//        switch (languageCode.toLowerCase()) {
//            case "wo": // 월로프어
//                return callWolofTtsEngine(text);
//
//            case "fu": // 풀라니어
//                return callFulaniTtsEngine(text);
//
//            case "ar": // 아랍어
//            case "fr": // 프랑스어
//                return callGoogleTtsEngine(text, languageCode);
//
//            default:
//                // 전용 모델이 없다면 Smartcat / TransWord.ai 통합 API로 대체
//                return callSmartcatOrTransWordApi(text, languageCode);
//        }
        // 임시
        try {
            ClassPathResource resource = new ClassPathResource("mock-audio.mp3");
            try (InputStream inputStream = resource.getInputStream()) {
                return StreamUtils.copyToByteArray(inputStream);
            }
        } catch (Exception e) {
            System.out.println("mock-audio.mp3 파일이 없습니다.");
            throw e;
        }
    }

//    // 월로프어
//    private byte[] callWolofTtsEngine(String text) {
//        String wolofAiServerUrl = "https://ai-engine.qos-refugee.internal/v1/wolof/tts";
//
//        return webClient.post()
//                .uri(wolofAiServerUrl)
//                .bodyValue(Map.of("text", text, "model", "xTTS-v2"))
//                .accept(org.springframework.http.MediaType.parseMediaType("audio/mpeg"))
//                .retrieve()
//                .bodyToMono(byte[].class)
//                .block();
//    }
//
//    // 풀라니어
//    private byte[] callFulaniTtsEngine(String text) {
//        String fulaniAiServerUrl = "https://ai-engine.qos-refugee.internal/v1/fulani/tts";
//
//        return webClient.post()
//                .uri(fulaniAiServerUrl)
//                .bodyValue(Map.of("text", text))
//                .accept(org.springframework.http.MediaType.parseMediaType("audio/mpeg"))
//                .retrieve()
//                .bodyToMono(byte[].class)
//                .block();
//    }
//
//    // Smartcat 또는 TransWord.ai
//    private byte[] callSmartcatOrTransWordApi(String text, String languageCode) {
//        // Smartcat 혹은 TransWord.ai 엔드포인트 예시
//        String commercialApiUrl = "https://api.smartcat.com/v1/speech/synthesize";
//
//        // 실제 운영 시에는 API Key를 GCP Secret Manager에 등록
//        String apiKey = "YOUR_SMARTCAT_OR_TRANSWORD_API_KEY";
//
//        return webClient.post()
//                .uri(commercialApiUrl)
//                .header("Authorization", "Bearer " + apiKey)
//                .bodyValue(Map.of(
//                        "text", text,
//                        "targetLanguage", languageCode,
//                        "voiceProfile", "natural_neutral"
//                ))
//                .accept(org.springframework.http.MediaType.parseMediaType("audio/mpeg"))
//                .retrieve()
//                .bodyToMono(byte[].class)
//                .block();
//    }
//
//    // 기존 구글 표준 클라우드 TTS 엔진 (아랍어, 프랑스어)
//    private byte[] callGoogleTtsEngine(String text, String languageCode) throws Exception {
//        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
//            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
//
//            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
//                    .setLanguageCode(languageCode)
//                    .setSsmlGender(SsmlVoiceGender.NEUTRAL)
//                    .build();
//
//            AudioConfig audioConfig = AudioConfig.newBuilder()
//                    .setAudioEncoding(AudioEncoding.MP3)
//                    .build();
//
//            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
//            return response.getAudioContent().toByteArray();
//        }
}