package com.chaekingam.api.infra.translation;

import com.chaekingam.api.global.exception.CustomException;
import com.chaekingam.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TranslationService {

    private final WebClient webClient;

    @Value("${deepl.api-key:}")
    private String apiKey;

    public String translate(String text, String targetLang) {
        if (apiKey.isBlank()) {
            throw new CustomException(ErrorCode.TRANSLATION_FAILED);
        }
        try {
            Map<?, ?> response = webClient.post()
                    .uri("https://api-free.deepl.com/v2/translate")
                    .header("Authorization", "DeepL-Auth-Key " + apiKey)
                    .bodyValue(Map.of(
                            "text", List.of(text),
                            "target_lang", targetLang.toUpperCase()
                    ))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            @SuppressWarnings("unchecked")
            List<Map<String, String>> translations = (List<Map<String, String>>) response.get("translations");
            return translations.get(0).get("text");
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.TRANSLATION_FAILED);
        }
    }
}
