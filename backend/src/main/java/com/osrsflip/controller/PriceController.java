package com.osrsflip.controller;

import com.osrsflip.model.dto.FlipOpportunityDto;
import com.osrsflip.service.PriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/prices")
@Validated
@Tag(name = "Prices", description = "OSRS GE live price and flip opportunity endpoints")
public class PriceController {

    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping
    @Operation(
            summary = "Get flip opportunities",
            description = "Fetches live OSRS GE prices from the Wiki API, calculates flip scores " +
                    "(margin × buyLimit), and returns items sorted by score descending."
    )
    @ApiResponse(responseCode = "200", description = "Flip opportunities sorted by score descending")
    @ApiResponse(responseCode = "400", description = "Invalid query parameters")
    public ResponseEntity<List<FlipOpportunityDto>> getFlipOpportunities(
            @Parameter(description = "Maximum number of results (1–200)", example = "50")
            @RequestParam(defaultValue = "50") @Min(1) @Max(200) int limit,

            @Parameter(description = "Minimum margin in GP to include", example = "100")
            @RequestParam(defaultValue = "100") @Min(0) int minMargin
    ) {
        return ResponseEntity.ok(priceService.getFlipOpportunities(limit, minMargin));
    }
}
