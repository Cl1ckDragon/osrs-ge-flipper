package com.osrsflip.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAlertRequest(
        @NotNull int itemId,
        @NotBlank String itemName,
        @NotBlank String icon,
        @Min(1) int targetMargin
) {}
