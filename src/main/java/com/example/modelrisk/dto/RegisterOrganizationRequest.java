package com.example.modelrisk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterOrganizationRequest(

        @NotBlank
        @Size(max = 150)
        String organizationName,

        @NotBlank
        @Pattern(
                regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
                message = "Organization slug must contain lowercase letters, numbers, or hyphens"
        )
        String organizationSlug,

        @NotBlank
        @Pattern(
                regexp = "^[A-Za-z]{2}$",
                message = "Country code must be a two-letter ISO code"
        )
        String countryCode,

        @NotBlank
        @Size(max = 150)
        String adminFullName,

        @NotBlank
        @Email
        String adminEmail,

        @NotBlank
        @Size(min = 10, max = 72)
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
                message = "Password must contain uppercase, lowercase, and a number"
        )
        String password
) {
}