package com.osrsflip.service;

import com.osrsflip.client.OsrsWikiClient;
import com.osrsflip.model.dto.FlipOpportunityDto;
import com.osrsflip.model.dto.ItemMappingDto;
import com.osrsflip.model.dto.LivePriceData;
import com.osrsflip.model.dto.LivePriceResponse;
import com.osrsflip.util.FlipScoreCalculator;
import org.springframework.stereotype.Service;

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

    public PriceService(OsrsWikiClient wikiClient, FlipScoreCalculator calculator) {
        this.wikiClient = wikiClient;
        this.calculator = calculator;
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
        int margin = price.high() - price.low();
        int buyLimit = mapping.limit();

        return Optional.of(new FlipOpportunityDto(
                itemId,
                mapping.name(),
                price.high(),
                price.low(),
                margin,
                buyLimit,
                calculator.calculateScore(margin, buyLimit),
                calculator.calculatePotentialProfit(margin, buyLimit)
        ));
    }
}
