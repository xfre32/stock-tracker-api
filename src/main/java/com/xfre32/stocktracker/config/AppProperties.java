package com.xfre32.stocktracker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.List;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        Finnhub finnhub,
        TwelveData twelveData,
        Jwt jwt,
        Cors cors,
        RateLimit rateLimit,
        ExternalApi externalApi
) {
    public record Finnhub(String apiKey, String baseUrl, String wsUrl) {}
    public record TwelveData(String apiKey, String baseUrl) {}
    public record Jwt(String secret, long accessTokenExpiration, long refreshTokenExpiration) {}
    public record Cors(List<String> allowedOrigins) {}
    public record RateLimit(int capacity, int refillTokens, int refillDuration, long bucketTtlSeconds, long maxBuckets) {}
    public record ExternalApi(int connectTimeoutMs, int readTimeoutMs, int writeTimeoutMs, int responseTimeoutMs) {}
}
