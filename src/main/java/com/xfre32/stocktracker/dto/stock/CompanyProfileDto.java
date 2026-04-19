package com.xfre32.stocktracker.dto.stock;

public record CompanyProfileDto(
        String country,
        String currency,
        String exchange,
        String finnhubIndustry,
        String ipo,
        String logo,
        double marketCapitalization,
        String name,
        String phone,
        double shareOutstanding,
        String ticker,
        String weburl
) {}