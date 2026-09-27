package com.example.modelrisk.service;

import com.example.modelrisk.dto.CreateUserRequest;
import com.example.modelrisk.dto.UserResponse;
import com.example.modelrisk.entity.Organization;
import com.example.modelrisk.entity.PlatformUser;
import com.example.modelrisk.exception.ConflictException;
import com.example.modelrisk.exception.ResourceNotFoundException;
import com.example.modelrisk.repository.OrganizationRepository;
import com.example.modelrisk.repository.PlatformUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserManagementService {

    private final PlatformUserRepository platformUserRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(
            PlatformUserRepository platformUserRepository,
            OrganizationRepository organizationRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.platformUserRepository = platformUserRepository;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse createUser(
            UUID organizationId,
            CreateUserRequest request
    ) {
        Organization organization = organizationRepository
                .findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organization not found"
                ));

        String email = request.email()
                .trim()
                .toLowerCase();

        if (platformUserRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException(
                    "Email address is already registered"
            );
        }

        PlatformUser user = PlatformUser.builder()
                .fullName(request.fullName().trim())
                .email(email)
                .passwordHash(
                        passwordEncoder.encode(request.password())
                )
                .role(request.role())
                .organization(organization)
                .active(true)
                .build();

        return toResponse(platformUserRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getOrganizationUsers(
            UUID organizationId
    ) {
        return platformUserRepository
                .findAllByOrganizationIdOrderByCreatedAtDesc(
                        organizationId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private UserResponse toResponse(PlatformUser user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getOrganization().getId(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}