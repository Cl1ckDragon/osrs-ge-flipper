package com.osrsflip.model.dto;

public record LivePriceData(
        int high,
        long highTime,
        int low,
        long lowTime
) {}
