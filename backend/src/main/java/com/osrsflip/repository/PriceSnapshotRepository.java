package com.osrsflip.repository;

import com.osrsflip.model.entity.PriceSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface PriceSnapshotRepository extends JpaRepository<PriceSnapshot, Long> {

    List<PriceSnapshot> findByItemIdAndFetchedAtAfterOrderByFetchedAtAsc(
            int itemId, OffsetDateTime after);
}
