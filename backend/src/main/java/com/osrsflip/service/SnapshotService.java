package com.osrsflip.service;

import com.osrsflip.model.entity.PriceSnapshot;
import com.osrsflip.repository.PriceSnapshotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class SnapshotService {

    private static final Logger log = LoggerFactory.getLogger(SnapshotService.class);

    private final PriceService priceService;
    private final PriceSnapshotRepository snapshotRepo;

    public SnapshotService(PriceService priceService, PriceSnapshotRepository snapshotRepo) {
        this.priceService = priceService;
        this.snapshotRepo = snapshotRepo;
    }

    @Scheduled(fixedRateString = "${snapshot.interval-ms:300000}")
    public void takeSnapshot() {
        try {
            OffsetDateTime now = OffsetDateTime.now();
            List<PriceSnapshot> snapshots = priceService.getFlipOpportunities(100, 0)
                    .stream()
                    .map(dto -> {
                        PriceSnapshot s = new PriceSnapshot();
                        s.setItemId(dto.id());
                        s.setItemName(dto.name());
                        s.setHigh(dto.high());
                        s.setLow(dto.low());
                        s.setBuyLimit(dto.buyLimit());
                        s.setFlipScore(dto.flipScore());
                        s.setFetchedAt(now);
                        return s;
                    })
                    .toList();

            snapshotRepo.saveAll(snapshots);
            log.info("Snapshot saved: {} items at {}", snapshots.size(), now);
        } catch (Exception e) {
            log.error("Snapshot failed: {}", e.getMessage());
        }
    }
}
