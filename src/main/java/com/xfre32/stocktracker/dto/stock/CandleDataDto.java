package com.xfre32.stocktracker.dto.stock;

public record CandleDataDto(
        Object time,      // String ("YYYY-MM-DD") or number (unix timestamp)
        double open,
        double high,
        double low,
        double close,
        Long volume
) {}