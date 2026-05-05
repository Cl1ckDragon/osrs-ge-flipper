package com.osrsflip.service;

import com.osrsflip.client.OsrsWikiClient;
import com.osrsflip.model.dto.FlipOpportunityDto;
import com.osrsflip.model.dto.ItemMappingDto;
import com.osrsflip.model.dto.LivePriceData;
import com.osrsflip.model.dto.LivePriceResponse;
import com.osrsflip.util.FlipScoreCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private OsrsWikiClient wikiClient;

    private PriceService priceService;

    @BeforeEach
    void setUp() {
        priceService = new PriceService(wikiClient, new FlipScoreCalculator());
    }

    @Test
    void getFlipOpportunities_sortsByFlipScoreDescending() {
        // Abyssal whip: margin 500k × buyLimit 2  → score 1_000_000
        // Granite maul: margin 200k × buyLimit 10 → score 2_000_000  (should rank first)
        when(wikiClient.fetchLatestPrices()).thenReturn(new LivePriceResponse(Map.of(
                "4151", new LivePriceData(2_500_000, 0L, 2_000_000, 0L),
                "4153", new LivePriceData(1_000_000, 0L,   800_000, 0L)
        )));
        when(wikiClient.fetchItemMappings()).thenReturn(List.of(
                new ItemMappingDto(4151, "Abyssal whip",  2, true, 96_000, 144_000, 240_000, "", ""),
                new ItemMappingDto(4153, "Granite maul", 10, true,  1_000,   2_000,   3_000, "", "")
        ));

        List<FlipOpportunityDto> result = priceService.getFlipOpportunities(10, 0);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Granite maul");
        assertThat(result.get(1).name()).isEqualTo("Abyssal whip");
    }

    @Test
    void getFlipOpportunities_filtersItemsBelowMinMargin() {
        when(wikiClient.fetchLatestPrices()).thenReturn(new LivePriceResponse(Map.of(
                "4151", new LivePriceData(2_500_000, 0L, 2_499_950, 0L)  // 50 gp margin
        )));
        when(wikiClient.fetchItemMappings()).thenReturn(List.of(
                new ItemMappingDto(4151, "Abyssal whip", 2, true, 96_000, 144_000, 240_000, "", "")
        ));

        List<FlipOpportunityDto> result = priceService.getFlipOpportunities(10, 100);

        assertThat(result).isEmpty();
    }

    @Test
    void getFlipOpportunities_filtersItemsWithZeroBuyLimit() {
        when(wikiClient.fetchLatestPrices()).thenReturn(new LivePriceResponse(Map.of(
                "4151", new LivePriceData(2_500_000, 0L, 2_000_000, 0L)
        )));
        when(wikiClient.fetchItemMappings()).thenReturn(List.of(
                new ItemMappingDto(4151, "Abyssal whip", 0, true, 96_000, 144_000, 240_000, "", "")
        ));

        List<FlipOpportunityDto> result = priceService.getFlipOpportunities(10, 0);

        assertThat(result).isEmpty();
    }

    @Test
    void getFlipOpportunities_respectsLimit() {
        when(wikiClient.fetchLatestPrices()).thenReturn(new LivePriceResponse(Map.of(
                "4151", new LivePriceData(2_500_000, 0L, 2_000_000, 0L),
                "4153", new LivePriceData(1_000_000, 0L,   800_000, 0L)
        )));
        when(wikiClient.fetchItemMappings()).thenReturn(List.of(
                new ItemMappingDto(4151, "Abyssal whip",  2, true, 96_000, 144_000, 240_000, "", ""),
                new ItemMappingDto(4153, "Granite maul", 10, true,  1_000,   2_000,   3_000, "", "")
        ));

        List<FlipOpportunityDto> result = priceService.getFlipOpportunities(1, 0);

        assertThat(result).hasSize(1);
    }

    @Test
    void getFlipOpportunities_skipsItemsWithNoMapping() {
        when(wikiClient.fetchLatestPrices()).thenReturn(new LivePriceResponse(Map.of(
                "99999", new LivePriceData(1_000_000, 0L, 500_000, 0L)  // no mapping
        )));
        when(wikiClient.fetchItemMappings()).thenReturn(List.of());

        List<FlipOpportunityDto> result = priceService.getFlipOpportunities(10, 0);

        assertThat(result).isEmpty();
    }
}
