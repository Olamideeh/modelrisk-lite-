package com.example.modelrisk.repository;

import com.example.modelrisk.entity.PlatformUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlatformUserRepository
        extends JpaRepository<PlatformUser, UUID> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<PlatformUser> findByEmailIgnoreCase(String email);
    List<PlatformUser> findAllByOrganizationIdOrderByCreatedAtDesc(
            UUID organizationId
    );
}