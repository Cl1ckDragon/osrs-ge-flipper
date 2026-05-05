package com.osrsflip.controller;

import com.osrsflip.model.dto.FlipOpportunityDto;
import com.osrsflip.model.dto.PriceHistoryDto;
import com.osrsflip.service.PriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;

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

    @GetMapping("/search")
    @Operation(summary = "Search items by name", description = "Searches the full item dataset — not limited to top results.")
    @ApiResponse(responseCode = "200", description = "Matching items sorted by flip score")
    public ResponseEntity<List<FlipOpportunityDto>> searchItems(
            @Parameter(description = "Partial item name", example = "abyssal")
            @RequestParam @NotBlank String q,

            @Parameter(description = "Max results (1–20)", example = "5")
            @RequestParam(defaultValue = "5") @Min(1) @Max(20) int limit
    ) {
        return ResponseEntity.ok(priceService.searchItems(q, limit));
    }

    @GetMapping("/history/{itemId}")
    @Operation(
            summary = "Get price history for an item",
            description = "Returns margin snapshots for the given item, taken every 5 minutes."
    )
    @ApiResponse(responseCode = "200", description = "List of historical price snapshots")
    public ResponseEntity<List<PriceHistoryDto>> getHistory(
            @Parameter(description = "OSRS item ID") @PathVariable int itemId,

            @Parameter(description = "Hours of history to return (1–168)", example = "24")
            @RequestParam(defaultValue = "24") @Min(1) @Max(168) int hours
    ) {
        return ResponseEntity.ok(priceService.getHistory(itemId, hours));
    }
}
