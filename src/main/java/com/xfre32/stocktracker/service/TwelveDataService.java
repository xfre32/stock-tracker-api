package com.xfre32.stocktracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xfre32.stocktracker.config.AppProperties;
import com.xfre32.stocktracker.dto.stock.CandleDataDto;
import com.xfre32.stocktracker.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TwelveDataService {
    private final WebClient twelveDataWebClient;
    private final AppProperties props;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Cacheable(value = "timeseries", key = "#symbol + '-' + #interval + '-' + #outputsize")
    public List<CandleDataDto> getTimeSeries(String symbol, String interval, int outputsize) {
        String rawResponse = blockWithTimeout(twelveDataWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/time_series")
                        .queryParam("symbol", symbol)
                        .queryParam("interval", interval)
                        .queryParam("outputsize", outputsize)
                        .queryParam("apikey", props.twelveData().apiKey())
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .onErrorMap(e -> new ExternalApiException("Failed to fetch time series for: " + symbol)),
                "Failed to fetch time series for: " + symbol);

        JsonNode response;
        try {
            response = objectMapper.readTree(rawResponse);
        } catch (Exception e) {
            throw new ExternalApiException("Failed to parse API response: " + e.getMessage());
        }

        if (response == null || "error".equals(response.path("status").asText())) {
            throw new ExternalApiException("Twelve Data error: " +
                    (response != null ? response.path("message").asText() : "null response"));
        }

        return transformToCandles(response.path("values"));
    }

    private List<CandleDataDto> transformToCandles(JsonNode values) {
        if (values == null || !values.isArray())
            return List.of();

        List<CandleDataDto> candles = new ArrayList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (JsonNode v : values) {
            String datetimeStr = v.path("datetime").asText();
            long timestamp;

            try {
                if (datetimeStr.contains(" ")) {
                    timestamp = LocalDateTime.parse(datetimeStr, dateTimeFormatter).atZone(ZoneId.of("America/New_York"))
                            .toEpochSecond();
                } else {
                    timestamp = LocalDate.parse(datetimeStr, dateFormatter).atStartOfDay(ZoneId.of("America/New_York"))
                            .toEpochSecond();
                }
            } catch (Exception e) {
                log.warn("Failed to parse datetime: {}", datetimeStr);
                continue;
            }

            candles.add(new CandleDataDto(
                    timestamp,
                    v.path("open").asDouble(),
                    v.path("high").asDouble(),
                    v.path("low").asDouble(),
                    v.path("close").asDouble(),
                    v.path("volume").asLong()));
        }
        Collections.reverse(candles); // Twelve Data returns newest-first
        return candles;
    }

    private <T> T blockWithTimeout(Mono<T> mono, String fallbackMessage) {
        try {
            return mono.block(Duration.ofMillis(props.externalApi().responseTimeoutMs()));
        } catch (ExternalApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalApiException(fallbackMessage);
        }
    }
}
