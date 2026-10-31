package com.chaekdojang.api.domain.admin;

/**
 * 관리자 방문 집계의 유입 경로 이름. 검색 엔진과 AI 답변 서비스에서 온 방문을 따로 셀 수 있게 묶는다.
 * Gemini 는 google 주소라 "구글"보다 먼저 본다.
 */
final class ReferrerLabels {

    private ReferrerLabels() {
    }

    static String of(String referrer) {
        if (referrer == null || referrer.isBlank()) return "직접 방문";
        if (referrer.equals("chaekdojang://invite/kakao-reading-group")) return "카카오 독서모임 초대 유입";
        if (referrer.equals("chaekdojang://invite/reading-group-link")) return "독서모임 초대 링크 유입";
        String value = referrer.toLowerCase();
        if (value.contains("brunch")) return "브런치";
        if (value.contains("chatgpt.com") || value.contains("chat.openai.com")) return "ChatGPT";
        if (value.contains("perplexity.ai")) return "Perplexity";
        if (value.contains("claude.ai")) return "Claude";
        if (value.contains("gemini.google.com") || value.contains("bard.google.com")) return "Gemini";
        if (value.contains("copilot.microsoft.com") || value.contains("copilot.cloud.microsoft")) return "Copilot";
        if (value.contains("google")) return "구글";
        if (value.contains("bing.com")) return "빙";
        if (value.contains("naver")) return "네이버";
        if (value.contains("daum.net")) return "다음";
        if (value.contains("instagram")) return "인스타그램";
        if (value.contains("chaekdojang")) return "책도장 내부";
        return referrer;
    }
}
