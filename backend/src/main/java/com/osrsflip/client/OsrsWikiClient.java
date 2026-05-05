package com.osrsflip.client;

import com.osrsflip.model.dto.ItemMappingDto;
import com.osrsflip.model.dto.LivePriceResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class OsrsWikiClient {

    private final RestClient restClient;

    public OsrsWikiClient(RestClient osrsWikiRestClient) {
        this.restClient = osrsWikiRestClient;
    }

    public LivePriceResponse fetchLatestPrices() {
        return restClient.get()
                .uri("/latest")
                .retrieve()
                .body(LivePriceResponse.class);
    }

    public List<ItemMappingDto> fetchItemMappings() {
        return restClient.get()
                .uri("/mapping")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
