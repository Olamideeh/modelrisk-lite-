package com.example.modelrisk.service;

import com.example.modelrisk.dto.AuthenticationResponse;
import com.example.modelrisk.dto.LoginRequest;
import com.example.modelrisk.dto.OrganizationRegistrationResponse;
import com.example.modelrisk.dto.RegisterOrganizationRequest;
import com.example.modelrisk.entity.Organization;
import com.example.modelrisk.entity.PlatformUser;
import com.example.modelrisk.enums.UserRole;
import com.example.modelrisk.exception.ConflictException;
import com.example.modelrisk.exception.InvalidCredentialsException;
import com.example.modelrisk.repository.OrganizationRepository;
import com.example.modelrisk.repository.PlatformUserRepository;
import com.example.modelrisk.security.JwtTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;

@Service
public class AuthenticationService {

    private static final Set<String> ISO_COUNTRY_CODES =
            Set.of(Locale.getISOCountries());

    private final OrganizationRepository organizationRepository;
    private final PlatformUserRepository platformUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthenticationService(
            OrganizationRepository organizationRepository,
            PlatformUserRepository platformUserRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService
    ) {
        this.organizationRepository = organizationRepository;
        this.platformUserRepository = platformUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public OrganizationRegistrationResponse registerOrganization(
            RegisterOrganizationRequest request
    ) {
        String slug = request.organizationSlug()
                .trim()
                .toLowerCase();

        String email = request.adminEmail()
                .trim()
                .toLowerCase();

        String countryCode = request.countryCode()
                .trim()
                .toUpperCase();

        if (!ISO_COUNTRY_CODES.contains(countryCode)) {
            throw new IllegalArgumentException(
                    "Invalid ISO country code: " + countryCode
            );
        }

        if (organizationRepository.existsBySlugIgnoreCase(slug)) {
            throw new ConflictException(
                    "Organization slug is already registered"
            );
        }

        if (platformUserRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException(
                    "Email address is already registered"
            );
        }

        Organization organization = Organization.builder()
                .name(request.organizationName().trim())
                .slug(slug)
                .countryCode(countryCode)
                .active(true)
                .build();

        organization = organizationRepository.save(organization);

        PlatformUser admin = PlatformUser.builder()
                .fullName(request.adminFullName().trim())
                .email(email)
                .passwordHash(
                        passwordEncoder.encode(request.password())
                )
                .role(UserRole.ADMIN)
                .organization(organization)
                .active(true)
                .build();

        admin = platformUserRepository.save(admin);

        return new OrganizationRegistrationResponse(
                organization.getId(),
                organization.getName(),
                organization.getSlug(),
                organization.getCountryCode(),
                admin.getId(),
                admin.getFullName(),
                admin.getEmail(),
                admin.getRole()
        );
    }

    @Transactional(readOnly = true)
    public AuthenticationResponse login(LoginRequest request) {
        PlatformUser user = platformUserRepository
                .findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Invalid email or password"
                ));

        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        )) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        if (!user.isActive()) {
            throw new InvalidCredentialsException(
                    "User account is inactive"
            );
        }

        if (!user.getOrganization().isActive()) {
            throw new InvalidCredentialsException(
                    "Organization account is inactive"
            );
        }

        return jwtTokenService.generateToken(user);
    }
}