package com.xfre32.stocktracker.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    private final AppProperties props;

    @Bean
    public WebClient finnhubWebClient() {
        return WebClient.builder()
                .baseUrl(props.finnhub().baseUrl())
                .defaultHeader("X-Finnhub-Token", props.finnhub().apiKey())
                .build();
    }

    @Bean
    public WebClient twelveDataWebClient() {
        return WebClient.builder()
                .baseUrl(props.twelveData().baseUrl())
                .build();
    }
}