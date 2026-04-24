package com.xfre32.stocktracker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;      // BCrypt hash

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant lastLoginAt;

    @PrePersist
    void prePersist() {
        this.createdAt = Instant.now();
    }
}
