package com.osrsflip.controller;

import com.osrsflip.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@Tag(name = "Stats", description = "Public app statistics")
public class StatsController {

    private final UserRepository userRepo;

    public StatsController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping
    @Operation(summary = "Get public app statistics")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(Map.of("registeredUsers", userRepo.count()));
    }
}
