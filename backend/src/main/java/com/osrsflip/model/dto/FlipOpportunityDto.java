package com.osrsflip.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A single GE flip opportunity with calculated score and profit estimate")
public record FlipOpportunityDto(
        @Schema(description = "OSRS item ID") int id,
        @Schema(description = "Item name") String name,
        @Schema(description = "Icon filename from the OSRS Wiki mapping API") String icon,
        @Schema(description = "5-minute avg instant-sell price") int high,
        @Schema(description = "5-minute avg instant-buy price") int low,
        @Schema(description = "Margin: high - low") int margin,
        @Schema(description = "GE buy limit") int buyLimit,
        @Schema(description = "Flip score: margin × buyLimit") long flipScore,
        @Schema(description = "Estimated profit after 1% GE tax: margin × buyLimit × 0.99") long potentialProfit
) {}
