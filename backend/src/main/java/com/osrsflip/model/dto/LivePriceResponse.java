package com.osrsflip.model.dto;

import java.util.Map;

public record LivePriceResponse(
        Map<String, LivePriceData> data
) {}
