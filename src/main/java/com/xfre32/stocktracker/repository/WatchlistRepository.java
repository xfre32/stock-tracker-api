package com.xfre32.stocktracker.repository;

import com.xfre32.stocktracker.entity.WatchlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<WatchlistEntry, Long> {
    List<WatchlistEntry> findByUserIdOrderByAddedAtAsc(Long userId);
    Optional<WatchlistEntry> findByUserIdAndSymbol(Long userId, String symbol);
    boolean existsByUserIdAndSymbol(Long userId, String symbol);
    void deleteByUserIdAndSymbol(Long userId, String symbol);
}
