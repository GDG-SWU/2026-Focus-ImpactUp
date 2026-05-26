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
        this.webClient = webClientBuilder.build();
    }

    public byte[] generateSpeech(String text, String languageCode) throws Exception {
        switch (languageCode.toLowerCase()) {
            case "ar":
                return callGoogleTtsEngine(text, "ar-XA");
            case "fr":
                return callGoogleTtsEngine(text, "fr-FR");
            case "wo": // 월로프어
            case "ma": // 만딩카어
            case "fu": // 풀라니어
            default:
                return callSmartcatApi(text, languageCode);
        }
    }

    private byte[] callGoogleTtsEngine(String text, String languageCode) throws Exception {
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(languageCode)
                    .setSsmlGender(SsmlVoiceGender.FEMALE)
                    .build();

            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            try {
                System.out.println("====== [GCP TTS] 구글 공식 API 호출을 시작합니다. (Language: " + languageCode + ") ======");
                SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
                return response.getAudioContent().toByteArray();
            } catch (Exception e) {
                System.out.println("[구글 공식 TTS API 통신 예외 발생] mock-audio로 대체합니다.");
                e.printStackTrace();
                return getMockAudio();
            }
        }
    }

    private byte[] callSmartcatApi(String text, String languageCode) {
        String commercialApiUrl = "https://api.dashboard.talkai.info/v1/tts";

        String voiceModel = "en-US-JennyNeural";
        if (languageCode.equalsIgnoreCase("wo")) voiceModel = "fr-FR-EloiseNeural";
        else if (languageCode.equalsIgnoreCase("ma")) voiceModel = "en-US-GuyNeural";
        else if (languageCode.equalsIgnoreCase("fu")) voiceModel = "fr-FR-DeniseNeural";

        try {
            return webClient.post()
                    .uri(commercialApiUrl)
                    .bodyValue(Map.of(
                            "text", text,
                            "voice", voiceModel,
                            "speed", 1.0
                    ))
                    .accept(org.springframework.http.MediaType.parseMediaType("audio/mpeg"))
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
        } catch (Exception e) {
            System.out.println("[TTS 외부 API 통신 실패] mock-audio로 대체합니다.");
            e.printStackTrace();
            return getMockAudio();
        }
    }

    private byte[] getMockAudio() {
        try {
            ClassPathResource resource = new ClassPathResource("mock-audio.mp3");
            try (InputStream inputStream = resource.getInputStream()) {
                return StreamUtils.copyToByteArray(inputStream);
            }
        } catch (Exception e) {
            System.out.println("mock-audio.mp3 파일을 찾을 수 없습니다.");
            throw new RuntimeException("TTS 오디오 생성 실패", e);
        }
    }
}
