package com.osrsflip.controller;

import com.osrsflip.model.dto.AddToWatchlistRequest;
import com.osrsflip.model.dto.WatchlistItemDto;
import com.osrsflip.service.WatchlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
@Tag(name = "Watchlist", description = "Manage your saved items")
@SecurityRequirement(name = "bearer-jwt")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping
    @Operation(summary = "Get your watchlist with current prices")
    public ResponseEntity<List<WatchlistItemDto>> getWatchlist(Authentication auth) {
        return ResponseEntity.ok(watchlistService.getWatchlist(auth.getName()));
    }

    @PostMapping
    @Operation(summary = "Add an item to your watchlist")
    public ResponseEntity<WatchlistItemDto> addItem(
            @Valid @RequestBody AddToWatchlistRequest req, Authentication auth) {
        return ResponseEntity.ok(watchlistService.addItem(auth.getName(), req));
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "Remove an item from your watchlist")
    public ResponseEntity<Void> removeItem(@PathVariable int itemId, Authentication auth) {
        watchlistService.removeItem(auth.getName(), itemId);
        return ResponseEntity.noContent().build();
    }
}
