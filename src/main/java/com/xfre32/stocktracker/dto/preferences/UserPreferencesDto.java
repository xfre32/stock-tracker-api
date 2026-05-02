package com.xfre32.stocktracker.dto.preferences;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserPreferencesDto(
    @NotBlank @Pattern(regexp = "^(light-theme|dark-theme)$") String theme
) {}
