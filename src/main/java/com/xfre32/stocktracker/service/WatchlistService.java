package com.xfre32.stocktracker.service;

import com.xfre32.stocktracker.dto.watchlist.*;
import com.xfre32.stocktracker.entity.*;
import com.xfre32.stocktracker.exception.*;
import com.xfre32.stocktracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchlistService {
    private final WatchlistRepository watchlistRepository;
    private final UserRepository userRepository;

    public List<WatchlistItemResponse> getWatchlist(String username) {
        var user = findUser(username);
        return watchlistRepository.findByUserIdOrderByAddedAtAsc(user.getId())
                .stream()
                .map(e -> new WatchlistItemResponse(e.getSymbol(), e.getAddedAt().toString()))
                .toList();
    }

    @Transactional
    public WatchlistItemResponse addSymbol(String username, WatchlistItemRequest request) {
        var user = findUser(username);
        String symbol = request.symbol().toUpperCase().trim();

        if (watchlistRepository.existsByUserIdAndSymbol(user.getId(), symbol)) {
            throw new DuplicateResourceException(symbol + " is already in your watchlist");
        }

        var entry = WatchlistEntry.builder().user(user).symbol(symbol).build();
        watchlistRepository.save(entry);
        return new WatchlistItemResponse(entry.getSymbol(), entry.getAddedAt().toString());
    }

    @Transactional
    public void removeSymbol(String username, String symbol) {
        var user = findUser(username);
        watchlistRepository.deleteByUserIdAndSymbol(user.getId(), symbol.toUpperCase());
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }
}
