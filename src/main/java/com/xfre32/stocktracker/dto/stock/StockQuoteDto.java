package com.xfre32.stocktracker.dto.stock;

public record StockQuoteDto(
        double c,   // current price
        double d,   // change
        double dp,  // percent change
        double h,   // high
        double l,   // low
        double o,   // open
        double pc,  // previous close
        long t      // timestamp
) {}