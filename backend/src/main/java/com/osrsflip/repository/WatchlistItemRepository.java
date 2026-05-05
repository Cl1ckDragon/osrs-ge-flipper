package com.osrsflip.repository;

import com.osrsflip.model.entity.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, Long> {
    List<WatchlistItem> findByUserUsernameOrderByCreatedAtDesc(String username);
    boolean existsByUserUsernameAndItemId(String username, int itemId);
    void deleteByUserUsernameAndItemId(String username, int itemId);
}
