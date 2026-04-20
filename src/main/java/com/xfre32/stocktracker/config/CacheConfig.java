package com.xfre32.stocktracker.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of(
                buildCache("quote",      30, TimeUnit.SECONDS,  500),
                buildCache("profile",    24, TimeUnit.HOURS,     200),
                buildCache("search",      2, TimeUnit.MINUTES,   100),
                buildCache("news",        5, TimeUnit.MINUTES,   100),
                buildCache("sentiment",   5, TimeUnit.MINUTES,   100),
                buildCache("timeseries",  5, TimeUnit.MINUTES,   200)
        ));
        return manager;
    }

    private CaffeineCache buildCache(String name, long duration, TimeUnit unit, int maxSize) {
        return new CaffeineCache(name, Caffeine.newBuilder()
                .expireAfterWrite(duration, unit)
                .maximumSize(maxSize)
                .recordStats()
                .build());
    }
}