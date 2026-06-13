package com.chaekdojang.api.infra.translation;

import com.chaekdojang.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/translate")
@RequiredArgsConstructor
public class TranslationController {

    private final TranslationService translationService;

    @PostMapping
    public ApiResponse<TranslationResponse> translate(@RequestBody TranslationRequest request) {
        String translated = translationService.translate(request.text(), request.targetLang());
        return ApiResponse.ok(new TranslationResponse(translated, request.targetLang()));
    }
}
