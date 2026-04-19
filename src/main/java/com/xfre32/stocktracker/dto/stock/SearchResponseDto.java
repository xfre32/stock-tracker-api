package com.xfre32.stocktracker.dto.stock;

import java.util.List;

public record SearchResponseDto(int count, List<SearchResultDto> result) {}