package com.example.modelrisk.controller;

import com.example.modelrisk.dto.AuthenticationResponse;
import com.example.modelrisk.dto.LoginRequest;
import com.example.modelrisk.dto.OrganizationRegistrationResponse;
import com.example.modelrisk.dto.RegisterOrganizationRequest;
import com.example.modelrisk.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(
            AuthenticationService authenticationService
    ) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/organizations/register")
    public ResponseEntity<OrganizationRegistrationResponse>
    registerOrganization(
            @Valid @RequestBody RegisterOrganizationRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        authenticationService.registerOrganization(
                                request
                        )
                );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(
                authenticationService.login(request)
        );
    }
}