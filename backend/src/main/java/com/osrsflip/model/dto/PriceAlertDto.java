package com.osrsflip.model.dto;

import java.time.OffsetDateTime;

public record PriceAlertDto(
        Long id,
        int itemId,
        String itemName,
        String icon,
        int targetMargin,
        Integer currentMargin,
        boolean triggered,
        boolean dismissed,
        OffsetDateTime triggeredAt,
        OffsetDateTime createdAt
) {}
