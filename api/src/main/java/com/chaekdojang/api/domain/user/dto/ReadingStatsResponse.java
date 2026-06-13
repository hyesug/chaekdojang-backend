package com.chaekdojang.api.domain.user.dto;

import java.util.List;

public record ReadingStatsResponse(
        int totalFinished,
        List<MonthlyCount> monthly,
        List<GenreCount> genres
) {
    public record MonthlyCount(int year, int month, int count) {}
    public record GenreCount(String genre, int count) {}
}
