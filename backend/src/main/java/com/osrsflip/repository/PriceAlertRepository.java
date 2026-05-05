package com.osrsflip.repository;

import com.osrsflip.model.entity.PriceAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PriceAlertRepository extends JpaRepository<PriceAlert, Long> {
    List<PriceAlert> findByUserUsernameOrderByCreatedAtDesc(String username);
    List<PriceAlert> findByTriggeredFalse();
    Optional<PriceAlert> findByIdAndUserUsername(Long id, String username);
}
