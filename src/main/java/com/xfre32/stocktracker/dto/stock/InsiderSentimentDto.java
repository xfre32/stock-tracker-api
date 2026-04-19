package com.xfre32.stocktracker.dto.stock;

import java.util.List;

public record InsiderSentimentDto(List<SentimentData> data, String symbol) {
    public record SentimentData(String symbol, int year, int month, double change, double mspr) {}
}