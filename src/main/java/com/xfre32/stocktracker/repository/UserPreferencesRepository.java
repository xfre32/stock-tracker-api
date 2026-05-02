package com.xfre32.stocktracker.repository;

import com.xfre32.stocktracker.entity.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {}
