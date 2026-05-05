package com.osrsflip.model.dto;

import java.time.OffsetDateTime;

public record WatchlistItemDto(
        int itemId,
        String itemName,
        String icon,
        Integer high,
        Integer low,
        Integer margin,
        Long flipScore,
        OffsetDateTime addedAt
) {}
