package com.xfre32.stocktracker.controller;

import com.xfre32.stocktracker.dto.watchlist.*;
import com.xfre32.stocktracker.service.WatchlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchlistController {
    private final WatchlistService watchlistService;

    @GetMapping
    public List<WatchlistItemResponse> getWatchlist(@AuthenticationPrincipal UserDetails user) {
        return watchlistService.getWatchlist(user.getUsername());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WatchlistItemResponse addSymbol(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody WatchlistItemRequest request) {
        return watchlistService.addSymbol(user.getUsername(), request);
    }

    @DeleteMapping("/{symbol}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeSymbol(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable String symbol) {
        watchlistService.removeSymbol(user.getUsername(), symbol);
    }
}
