package com.osrsflip.service;

import com.osrsflip.model.dto.AddToWatchlistRequest;
import com.osrsflip.model.dto.FlipOpportunityDto;
import com.osrsflip.model.dto.WatchlistItemDto;
import com.osrsflip.model.entity.User;
import com.osrsflip.model.entity.WatchlistItem;
import com.osrsflip.repository.UserRepository;
import com.osrsflip.repository.WatchlistItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WatchlistService {

    private final WatchlistItemRepository watchlistRepo;
    private final UserRepository userRepo;
    private final PriceService priceService;

    public WatchlistService(WatchlistItemRepository watchlistRepo,
                            UserRepository userRepo,
                            PriceService priceService) {
        this.watchlistRepo = watchlistRepo;
        this.userRepo = userRepo;
        this.priceService = priceService;
    }

    public List<WatchlistItemDto> getWatchlist(String username) {
        Map<Integer, FlipOpportunityDto> prices = priceService.getFlipOpportunities(2000, 0)
                .stream()
                .collect(Collectors.toMap(FlipOpportunityDto::id, Function.identity()));

        return watchlistRepo.findByUserUsernameOrderByCreatedAtDesc(username)
                .stream()
                .map(item -> {
                    FlipOpportunityDto p = prices.get(item.getItemId());
                    return new WatchlistItemDto(
                            item.getItemId(),
                            item.getItemName(),
                            item.getIcon(),
                            p != null ? p.high() : null,
                            p != null ? p.low() : null,
                            p != null ? p.margin() : null,
                            p != null ? p.flipScore() : null,
                            item.getCreatedAt()
                    );
                })
                .toList();
    }

    public WatchlistItemDto addItem(String username, AddToWatchlistRequest req) {
        if (watchlistRepo.existsByUserUsernameAndItemId(username, req.itemId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Item already in watchlist");
        }
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        WatchlistItem item = new WatchlistItem();
        item.setUser(user);
        item.setItemId(req.itemId());
        item.setItemName(req.itemName());
        item.setIcon(req.icon());
        item.setCreatedAt(OffsetDateTime.now());
        watchlistRepo.save(item);

        return new WatchlistItemDto(req.itemId(), req.itemName(), req.icon(),
                null, null, null, null, item.getCreatedAt());
    }

    @Transactional
    public void removeItem(String username, int itemId) {
        watchlistRepo.deleteByUserUsernameAndItemId(username, itemId);
    }

    public Set<Integer> getWatchlistIds(String username) {
        return watchlistRepo.findByUserUsernameOrderByCreatedAtDesc(username)
                .stream()
                .map(WatchlistItem::getItemId)
                .collect(java.util.stream.Collectors.toSet());
    }
}
