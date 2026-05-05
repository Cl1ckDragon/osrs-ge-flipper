package com.osrsflip.controller;

import com.osrsflip.model.dto.CreateAlertRequest;
import com.osrsflip.model.dto.PriceAlertDto;
import com.osrsflip.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@Tag(name = "Alerts", description = "Price alerts — get notified when a margin target is hit")
@SecurityRequirement(name = "bearer-jwt")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    @Operation(summary = "Get your price alerts")
    public ResponseEntity<List<PriceAlertDto>> getAlerts(Authentication auth) {
        return ResponseEntity.ok(alertService.getAlerts(auth.getName()));
    }

    @PostMapping
    @Operation(summary = "Create a price alert")
    public ResponseEntity<PriceAlertDto> createAlert(
            @Valid @RequestBody CreateAlertRequest req, Authentication auth) {
        return ResponseEntity.ok(alertService.createAlert(auth.getName(), req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an alert")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id, Authentication auth) {
        alertService.deleteAlert(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/dismiss")
    @Operation(summary = "Dismiss a triggered alert without deleting it")
    public ResponseEntity<PriceAlertDto> dismissAlert(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(alertService.dismissAlert(id, auth.getName()));
    }
}
