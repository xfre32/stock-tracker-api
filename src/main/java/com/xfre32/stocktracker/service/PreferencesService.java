package com.xfre32.stocktracker.service;

import com.xfre32.stocktracker.dto.preferences.UserPreferencesDto;
import com.xfre32.stocktracker.entity.UserPreferences;
import com.xfre32.stocktracker.exception.ResourceNotFoundException;
import com.xfre32.stocktracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PreferencesService {
    private final UserPreferencesRepository preferencesRepository;
    private final UserRepository userRepository;

    public UserPreferencesDto getPreferences(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var prefs = preferencesRepository.findById(user.getId())
                .orElse(UserPreferences.builder().theme("dark-theme").build());
        return new UserPreferencesDto(prefs.getTheme());
    }

    @Transactional
    public UserPreferencesDto updatePreferences(String username, UserPreferencesDto dto) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var prefs = preferencesRepository.findById(user.getId())
                .orElse(UserPreferences.builder().user(user).build());
        prefs.setTheme(dto.theme());
        preferencesRepository.save(prefs);
        return new UserPreferencesDto(prefs.getTheme());
    }
}
