package com.xfre32.stocktracker.controller;

import com.xfre32.stocktracker.dto.preferences.UserPreferencesDto;
import com.xfre32.stocktracker.service.PreferencesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class PreferencesController {
    private final PreferencesService preferencesService;

    @GetMapping
    public UserPreferencesDto getPreferences(@AuthenticationPrincipal UserDetails user) {
        return preferencesService.getPreferences(user.getUsername());
    }

    @PutMapping
    public UserPreferencesDto updatePreferences(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody UserPreferencesDto dto) {
        return preferencesService.updatePreferences(user.getUsername(), dto);
    }
}
