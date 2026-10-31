package com.chaekdojang.api.domain.admin;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReferrerLabelsTest {

    @Test
    void aiAnswerServicesHaveTheirOwnLabels() {
        assertThat(ReferrerLabels.of("https://chatgpt.com/")).isEqualTo("ChatGPT");
        assertThat(ReferrerLabels.of("https://chat.openai.com/c/abc")).isEqualTo("ChatGPT");
        assertThat(ReferrerLabels.of("https://www.perplexity.ai/search/abc")).isEqualTo("Perplexity");
        assertThat(ReferrerLabels.of("https://claude.ai/chat/abc")).isEqualTo("Claude");
        assertThat(ReferrerLabels.of("https://copilot.microsoft.com/")).isEqualTo("Copilot");
    }

    @Test
    void geminiIsNotCountedAsGoogleSearch() {
        assertThat(ReferrerLabels.of("https://gemini.google.com/app")).isEqualTo("Gemini");
        assertThat(ReferrerLabels.of("https://www.google.com/")).isEqualTo("구글");
    }

    @Test
    void searchEnginesAndExistingSourcesKeepTheirLabels() {
        assertThat(ReferrerLabels.of("https://www.bing.com/")).isEqualTo("빙");
        assertThat(ReferrerLabels.of("https://search.naver.com/search.naver")).isEqualTo("네이버");
        assertThat(ReferrerLabels.of("https://search.daum.net/search")).isEqualTo("다음");
        assertThat(ReferrerLabels.of("https://brunch.co.kr/@reader/1")).isEqualTo("브런치");
        assertThat(ReferrerLabels.of("https://www.chaekdojang.com/books/1")).isEqualTo("책도장 내부");
        assertThat(ReferrerLabels.of("chaekdojang://invite/kakao-reading-group")).isEqualTo("카카오 독서모임 초대 유입");
    }

    @Test
    void blankAndUnknownReferrers() {
        assertThat(ReferrerLabels.of(null)).isEqualTo("직접 방문");
        assertThat(ReferrerLabels.of(" ")).isEqualTo("직접 방문");
        assertThat(ReferrerLabels.of("https://example.org/post")).isEqualTo("https://example.org/post");
    }
}
