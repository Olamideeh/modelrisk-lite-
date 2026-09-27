package com.example.modelrisk.security;

import com.example.modelrisk.dto.AuthenticationResponse;
import com.example.modelrisk.entity.PlatformUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final long expirationMinutes;

    public JwtTokenService(
            JwtEncoder jwtEncoder,
            @Value("${modelrisk.jwt.expiration-minutes}")
            long expirationMinutes
    ) {
        this.jwtEncoder = jwtEncoder;
        this.expirationMinutes = expirationMinutes;
    }

    public AuthenticationResponse generateToken(PlatformUser user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(
                expirationMinutes,
                ChronoUnit.MINUTES
        );

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("modelrisk")
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .claim(
                        "organizationId",
                        user.getOrganization().getId().toString()
                )
                .build();

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .type("JWT")
                .build();

        String accessToken = jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();

        return new AuthenticationResponse(
                accessToken,
                "Bearer",
                expiresAt,
                user.getId(),
                user.getOrganization().getId(),
                user.getEmail(),
                user.getRole()
        );
    }
}