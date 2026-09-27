package com.example.modelrisk.controller;

import com.example.modelrisk.dto.CreateUserRequest;
import com.example.modelrisk.dto.UserResponse;
import com.example.modelrisk.service.UserManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    private final UserManagementService userManagementService;

    public UserManagementController(
            UserManagementService userManagementService
    ) {
        this.userManagementService = userManagementService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateUserRequest request
    ) {
        UUID organizationId = UUID.fromString(
                jwt.getClaimAsString("organizationId")
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        userManagementService.createUser(
                                organizationId,
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID organizationId = UUID.fromString(
                jwt.getClaimAsString("organizationId")
        );

        return ResponseEntity.ok(
                userManagementService.getOrganizationUsers(
                        organizationId
                )
        );
    }
}