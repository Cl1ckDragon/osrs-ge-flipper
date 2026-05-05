package com.osrsflip.service;

import com.osrsflip.client.OsrsWikiClient;
import com.osrsflip.model.dto.FlipOpportunityDto;
import com.osrsflip.model.dto.ItemMappingDto;
import com.osrsflip.model.dto.LivePriceData;
import com.osrsflip.model.dto.LivePriceResponse;
import com.osrsflip.model.dto.PriceHistoryDto;
import com.osrsflip.repository.PriceSnapshotRepository;
import com.osrsflip.util.FlipScoreCalculator;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PriceService {

    private final OsrsWikiClient wikiClient;
    private final FlipScoreCalculator calculator;
    private final PriceSnapshotRepository snapshotRepo;

    public PriceService(OsrsWikiClient wikiClient, FlipScoreCalculator calculator,
                        PriceSnapshotRepository snapshotRepo) {
        this.wikiClient = wikiClient;
        this.calculator = calculator;
        this.snapshotRepo = snapshotRepo;
    }

    public List<FlipOpportunityDto> getFlipOpportunities(int limit, int minMargin) {
        LivePriceResponse priceResponse = wikiClient.fetchLatestPrices();
        List<ItemMappingDto> mappings = wikiClient.fetchItemMappings();

        Map<Integer, ItemMappingDto> mappingById = mappings.stream()
                .collect(Collectors.toMap(ItemMappingDto::id, Function.identity()));

        return priceResponse.data().entrySet().stream()
                .flatMap(e -> toFlipOpportunity(e, mappingById).stream())
                .filter(dto -> dto.margin() >= minMargin && dto.buyLimit() > 0)
                .sorted(Comparator.comparingLong(FlipOpportunityDto::flipScore).reversed())
                .limit(limit)
                .toList();
    }

    public List<FlipOpportunityDto> searchItems(String query, int limit) {
        LivePriceResponse priceResponse = wikiClient.fetchLatestPrices();
        List<ItemMappingDto> mappings = wikiClient.fetchItemMappings();

        Map<Integer, ItemMappingDto> mappingById = mappings.stream()
                .collect(Collectors.toMap(ItemMappingDto::id, Function.identity()));

        String lowerQuery = query.strip().toLowerCase();

        return priceResponse.data().entrySet().stream()
                .flatMap(e -> toFlipOpportunity(e, mappingById).stream())
                .filter(dto -> dto.buyLimit() > 0 && dto.name().toLowerCase().contains(lowerQuery))
                .sorted(Comparator.comparingLong(FlipOpportunityDto::flipScore).reversed())
                .limit(limit)
                .toList();
    }

    public List<PriceHistoryDto> getHistory(int itemId, int hours) {
        OffsetDateTime since = OffsetDateTime.now().minusHours(hours);
        return snapshotRepo
                .findByItemIdAndFetchedAtAfterOrderByFetchedAtAsc(itemId, since)
                .stream()
                .map(s -> new PriceHistoryDto(
                        s.getFetchedAt(),
                        s.getHigh(),
                        s.getLow(),
                        s.getHigh() - s.getLow(),
                        s.getFlipScore()
                ))
                .toList();
    }

    private Optional<FlipOpportunityDto> toFlipOpportunity(
            Map.Entry<String, LivePriceData> entry,
            Map<Integer, ItemMappingDto> mappingById) {

        int itemId;
        try {
            itemId = Integer.parseInt(entry.getKey());
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        ItemMappingDto mapping = mappingById.get(itemId);
        if (mapping == null) return Optional.empty();

        LivePriceData price = entry.getValue();
        if (price.avgHighPrice() == null || price.avgLowPrice() == null) return Optional.empty();

        int margin = price.avgHighPrice() - price.avgLowPrice();
        int buyLimit = mapping.limit();

        return Optional.of(new FlipOpportunityDto(
                itemId,
                mapping.name(),
                mapping.icon(),
                price.avgHighPrice(),
                price.avgLowPrice(),
                margin,
                buyLimit,
                calculator.calculateScore(margin, buyLimit),
                calculator.calculatePotentialProfit(margin, buyLimit)
        ));
    }
}
