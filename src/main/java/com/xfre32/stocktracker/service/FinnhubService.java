package com.xfre32.stocktracker.service;

import com.xfre32.stocktracker.dto.stock.*;
import com.xfre32.stocktracker.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinnhubService {
    private final WebClient finnhubWebClient;

    @Cacheable(value = "search", key = "#query")
    public SearchResponseDto searchStock(String query) {
        return finnhubWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search").queryParam("q", query).build())
                .retrieve()
                .bodyToMono(SearchResponseDto.class)
                .doOnError(e -> log.error("Finnhub search failed for '{}': {}", query, e.getMessage()))
                .onErrorMap(e -> new ExternalApiException("Failed to search Finnhub for: " + query))
                .block();
    }

    @Cacheable(value = "quote", key = "#symbol")
    public StockQuoteDto getQuote(String symbol) {
        return finnhubWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/quote").queryParam("symbol", symbol).build())
                .retrieve()
                .bodyToMono(StockQuoteDto.class)
                .onErrorMap(e -> new ExternalApiException("Failed to fetch quote for: " + symbol))
                .block();
    }

    @Cacheable(value = "profile", key = "#symbol")
    public CompanyProfileDto getCompanyProfile(String symbol) {
        return finnhubWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/stock/profile2").queryParam("symbol", symbol)
                        .build())
                .retrieve()
                .bodyToMono(CompanyProfileDto.class)
                .onErrorMap(e -> new ExternalApiException("Failed to fetch profile for: " + symbol))
                .block();
    }

    @Cacheable(value = "news", key = "#symbol + '-' + #from + '-' + #to")
    public List<CompanyNewsDto> getCompanyNews(String symbol, String from, String to) {
        return finnhubWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/company-news")
                        .queryParam("symbol", symbol)
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .build())
                .retrieve()
                .bodyToFlux(CompanyNewsDto.class)
                .collectList()
                .onErrorMap(e -> new ExternalApiException("Failed to fetch news for: " + symbol))
                .block();
    }

    @Cacheable(value = "sentiment", key = "#symbol + '-' + #from + '-' + #to")
    public InsiderSentimentDto getInsiderSentiment(String symbol, String from, String to) {
        return finnhubWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/stock/insider-sentiment")
                        .queryParam("symbol", symbol)
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .build())
                .retrieve()
                .bodyToMono(InsiderSentimentDto.class)
                .onErrorMap(e -> new ExternalApiException("Failed to fetch sentiment for: " + symbol))
                .block();
    }

}
