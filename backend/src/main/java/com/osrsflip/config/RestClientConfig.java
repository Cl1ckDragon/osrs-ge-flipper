package com.osrsflip.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${osrs.wiki.base-url}")
    private String baseUrl;

    @Value("${osrs.wiki.user-agent}")
    private String userAgent;

    @Bean
    public RestClient osrsWikiRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
                .build();
    }
}
