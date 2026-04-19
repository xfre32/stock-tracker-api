package com.xfre32.stocktracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.xfre32.stocktracker.config.AppProperties;
import com.xfre32.stocktracker.dto.stock.CandleDataDto;
import com.xfre32.stocktracker.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TwelveDataService {
    private final WebClient twelveDataWebClient;
    private final AppProperties props;

    @Cacheable(value = "timeseries", key = "#symbol + '-' + #interval + '-' + #outputsize")
    public List<CandleDataDto> getTimeSeries(String symbol, String interval, int outputsize) {
        JsonNode response = twelveDataWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/time_series")
                        .queryParam("symbol", symbol)
                        .queryParam("interval", interval)
                        .queryParam("outputsize", outputsize)
                        .queryParam("apikey", props.twelveData().apiKey())
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .onErrorMap(e -> new ExternalApiException("Failed to fetch time series for: " + symbol))
                .block();

        if (response == null || "error".equals(response.path("status").asText())) {
            throw new ExternalApiException("Twelve Data error: " +
                    (response != null ? response.path("message").asText() : "null response"));
        }

        return transformToCandles(response.path("values"));
    }

    private List<CandleDataDto> transformToCandles(JsonNode values) {
        if (values == null || !values.isArray()) return List.of();

        List<CandleDataDto> candles = new ArrayList<>();
        for (JsonNode v : values) {
            long timestamp = java.time.LocalDateTime.parse(
                            v.path("datetime").asText(),
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd[ HH:mm:ss]")
                    ).atZone(java.time.ZoneId.of("America/New_York"))
                    .toEpochSecond();

            candles.add(new CandleDataDto(
                    timestamp,
                    v.path("open").asDouble(),
                    v.path("high").asDouble(),
                    v.path("low").asDouble(),
                    v.path("close").asDouble(),
                    v.path("volume").asLong()
            ));
        }
        Collections.reverse(candles);  // Twelve Data returns newest-first
        return candles;
    }
}