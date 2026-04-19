package com.xfre32.stocktracker.dto.stock;

public record SearchResultDto(
        String description,
        String displaySymbol,
        String symbol,
        String type
) {}