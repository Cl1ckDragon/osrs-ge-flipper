package com.osrsflip.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "A single historical price snapshot for one item")
public record PriceHistoryDto(
        @Schema(description = "When this snapshot was taken") OffsetDateTime fetchedAt,
        @Schema(description = "Instant-sell price at snapshot time") int high,
        @Schema(description = "Instant-buy price at snapshot time") int low,
        @Schema(description = "Margin at snapshot time: high - low") int margin,
        @Schema(description = "Flip score at snapshot time") long flipScore
) {}
