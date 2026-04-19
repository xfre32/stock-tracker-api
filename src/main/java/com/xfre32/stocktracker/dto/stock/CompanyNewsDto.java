package com.xfre32.stocktracker.dto.stock;

public record CompanyNewsDto(
        String category,
        long datetime,
        String headline,
        long id,
        String image,
        String related,
        String source,
        String summary,
        String url
) {}