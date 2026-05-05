package com.osrsflip.service;

import com.osrsflip.model.dto.CreateAlertRequest;
import com.osrsflip.model.dto.FlipOpportunityDto;
import com.osrsflip.model.dto.PriceAlertDto;
import com.osrsflip.model.entity.PriceAlert;
import com.osrsflip.model.entity.User;
import com.osrsflip.repository.PriceAlertRepository;
import com.osrsflip.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    private final PriceAlertRepository alertRepo;
    private final UserRepository userRepo;
    private final PriceService priceService;

    public AlertService(PriceAlertRepository alertRepo, UserRepository userRepo, PriceService priceService) {
        this.alertRepo = alertRepo;
        this.userRepo = userRepo;
        this.priceService = priceService;
    }

    public List<PriceAlertDto> getAlerts(String username) {
        Map<Integer, Integer> currentMargins = priceService.getFlipOpportunities(2000, 0)
                .stream()
                .collect(Collectors.toMap(FlipOpportunityDto::id, FlipOpportunityDto::margin));

        return alertRepo.findByUserUsernameOrderByCreatedAtDesc(username)
                .stream()
                .map(a -> toDto(a, currentMargins.get(a.getItemId())))
                .toList();
    }

    public PriceAlertDto createAlert(String username, CreateAlertRequest req) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        PriceAlert alert = new PriceAlert();
        alert.setUser(user);
        alert.setItemId(req.itemId());
        alert.setItemName(req.itemName());
        alert.setIcon(req.icon());
        alert.setTargetMargin(req.targetMargin());
        alert.setCreatedAt(OffsetDateTime.now());
        alertRepo.save(alert);

        return toDto(alert, null);
    }

    public void deleteAlert(Long id, String username) {
        PriceAlert alert = alertRepo.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found"));
        alertRepo.delete(alert);
    }

    public PriceAlertDto dismissAlert(Long id, String username) {
        PriceAlert alert = alertRepo.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found"));
        alert.setDismissed(true);
        alertRepo.save(alert);
        return toDto(alert, null);
    }

    // Called by SnapshotService after each price snapshot
    public void checkAndTrigger(List<FlipOpportunityDto> opportunities) {
        Map<Integer, Integer> marginById = opportunities.stream()
                .collect(Collectors.toMap(FlipOpportunityDto::id, FlipOpportunityDto::margin));

        List<PriceAlert> untriggered = alertRepo.findByTriggeredFalse();
        untriggered.forEach(alert -> {
            Integer margin = marginById.get(alert.getItemId());
            if (margin != null && margin >= alert.getTargetMargin()) {
                alert.setTriggered(true);
                alert.setTriggeredAt(OffsetDateTime.now());
                alertRepo.save(alert);
                log.info("Alert triggered: {} margin {} >= target {}",
                        alert.getItemName(), margin, alert.getTargetMargin());
            }
        });
    }

    private PriceAlertDto toDto(PriceAlert a, Integer currentMargin) {
        return new PriceAlertDto(
                a.getId(), a.getItemId(), a.getItemName(), a.getIcon(),
                a.getTargetMargin(), currentMargin,
                a.getTriggered(), a.getDismissed(),
                a.getTriggeredAt(), a.getCreatedAt()
        );
    }
}
