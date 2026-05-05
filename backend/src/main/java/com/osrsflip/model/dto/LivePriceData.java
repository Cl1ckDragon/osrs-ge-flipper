package com.osrsflip.model.dto;

public record LivePriceData(
        Integer avgHighPrice,
        int highPriceVolume,
        Integer avgLowPrice,
        int lowPriceVolume,
        long timestamp
) {}
