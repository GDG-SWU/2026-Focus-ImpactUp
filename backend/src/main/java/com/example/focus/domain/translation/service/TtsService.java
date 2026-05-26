package com.example.focus.domain.translation.service;

import com.google.cloud.texttospeech.v1.*;
import org.springframework.beans.factory.annotation.Value;
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
            case "ar": // 아랍어
            case "fr": // 프랑스어
                return callGoogleTtsEngine(text, languageCode);

            case "wo": // 월로프어
            case "ma": // 만딩카어
            case "fu": // 풀라니어
            default:
                return callSmartcatApi(text, languageCode);
        }
    }

    private byte[] callGoogleTtsEngine(String text, String languageCode) throws Exception {
        TextToSpeechSettings settings = TextToSpeechSettings.newBuilder()
                .setQuotaProjectId("project-c19a8e1f-a60d-4cdb-93f")
                .build();

        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create(settings)) {
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
                System.out.println("[구글 공식 TTS API 통신 예외 발생]");
                e.printStackTrace();
                throw e;
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
            System.out.println("[TTS 외부 API 통신 실패] 임시 더미 음성 스트림으로 대체합니다.");
            e.printStackTrace();

            return "dummy_audio_stream_bytes".getBytes();
        }
    }
}
