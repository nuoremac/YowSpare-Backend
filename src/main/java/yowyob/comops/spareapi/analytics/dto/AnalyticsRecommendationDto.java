package yowyob.comops.spareapi.analytics.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AnalyticsRecommendationDto(
        UUID productId,
        BigDecimal avgDailyUsage,
        BigDecimal daysOfCover,
        String abcClass,
        BigDecimal stockoutRisk,
        Integer suggestedReorderQty,
        Instant computedAt
) {
}

