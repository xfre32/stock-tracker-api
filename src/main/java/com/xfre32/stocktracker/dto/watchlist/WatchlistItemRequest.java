package com.xfre32.stocktracker.dto.watchlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record WatchlistItemRequest(
    @NotBlank
    @Size(max = 10, message = "Symbol must be 10 characters or fewer")
    @Pattern(regexp = "^[A-Z0-9.\\-]+$", message = "Invalid stock symbol format")
    String symbol
) {}
