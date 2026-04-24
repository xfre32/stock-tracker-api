package com.xfre32.stocktracker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_preferences")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserPreferences {
    @Id
    private Long userId;     // Same as User.id (shared PK)

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 20)
    private String theme;    // "light-theme" or "dark-theme"
}
