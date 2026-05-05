package com.osrsflip.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddToWatchlistRequest(
        @NotNull int itemId,
        @NotBlank String itemName,
        @NotBlank String icon
) {}
