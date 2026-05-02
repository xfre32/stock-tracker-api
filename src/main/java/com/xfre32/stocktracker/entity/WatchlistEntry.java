package com.xfre32.stocktracker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "watchlist_entries", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "symbol" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchlistEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 10)
    private String symbol;

    @Column(nullable = false, updatable = false)
    private Instant addedAt;

    @PrePersist
    void prePersist() {
        this.addedAt = Instant.now();
    }
}
