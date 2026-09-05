package com.magnet.ecommerce.identity_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.magnet.ecommerce.identity_service.entity.Permission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    Optional<Permission> findByName(String name);

    boolean existsByName(String name);
}